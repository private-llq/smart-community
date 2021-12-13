package com.jsy.community.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.UserTicketQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.qo.proprietor.UserWithdrawalQ0;
import com.jsy.community.qo.proprietor.ZhiFuBaoAccountBindingQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.imutils.open.StringUtils;
import com.jsy.community.vo.UserAccountVO;
import com.jsy.community.vo.WithdrawalResulrVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.BaseWallet;
import com.zhsj.base.api.rpc.IBaseWalletRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 用户账户实现类
 * @since 2021-01-08 11:14
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserAccountServiceImpl implements IUserAccountService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private IUserAccountRecordService userAccountRecordService;

    @Autowired
    private UserTicketMapper userTicketMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private UserThirdPlatformMapper userThirdPlatformMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserAccountRecordMapper userAccountRecordMapper;


    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private UserAccountWithdrawalService userAccountWithdrawalService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER)
    private IBaseWalletRpcService baseWalletRpcService;

    private static final String ZHIFUBAO_WITHDRAWAL_INFO_KEY = "ZHIFUBAO_WITHDRAWAL_INFO_KEY";

    /**
     * @Description: 创建用户账户
     * @Param: [uid]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    @Override
    public boolean createUserAccount(String uid) {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setUid(uid);
        entity.setBalance(new BigDecimal(0));
        return userAccountMapper.insert(entity) == 1;
    }

    /**
     * @Description: 查询余额
     * @Param: [uid]
     * @Return: com.jsy.community.vo.UserAccountVO
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    @Override
    public UserAccountVO queryBalance(String uid) {
        BaseWallet wallet = baseWalletRpcService.getWalletByCon(uid, "RMB");
        // UserAccountEntity userAccountEntity = userAccountMapper.selectOne(new QueryWrapper<UserAccountEntity>().select("uid", "balance").eq("uid", uid));
        UserAccountVO userAccountVO = new UserAccountVO();
        userAccountVO.setUid(uid);
        if (wallet != null) {
            userAccountVO.setBalance(wallet.getBalance());
        }
        return userAccountVO;
    }

    /**
     * @Description: 账户交易
     * @Param: [userid, uAccountRecordQO]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/1/8
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void trade(UserAccountTradeQO tradeQO) {
        int updateResult = 0;
        //处理(加或减)
        if (PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex().equals(tradeQO.getTradeType())) { //收入
            updateResult = userAccountMapper.updateBalance(tradeQO.getTradeAmount(), tradeQO.getUid());
        } else if (PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex().equals(tradeQO.getTradeType())) { //支出
            UserAccountVO userAccountVO = queryBalance(tradeQO.getUid());
            BigDecimal balance = userAccountVO.getBalance();
            if (balance.compareTo(tradeQO.getTradeAmount()) == -1) {
                throw new ProprietorException("余额不足");
            }
            updateResult = userAccountMapper.updateBalance(tradeQO.getTradeAmount().negate(), tradeQO.getUid());
        } else {
            throw new ProprietorException("非法交易类型");
        }
        if (updateResult != 1) {
            throw new ProprietorException("系统异常，交易失败");
        }
        //写流水
        UserAccountRecordEntity ucoinRecordEntity = new UserAccountRecordEntity();
        BeanUtils.copyProperties(tradeQO, ucoinRecordEntity);
        ucoinRecordEntity.setId(SnowFlake.nextId());
        ucoinRecordEntity.setBalance(queryBalance(tradeQO.getUid()).getBalance());//交易后余额
        boolean b = userAccountRecordService.addAccountRecord(ucoinRecordEntity);
        if (!b) {
            throw new ProprietorException("因账户流水记录失败，交易取消");
        }
    }

    /**
     * @Description: 统计用户可用券张数
     * @Param: [uid]
     * @Return: java.lang.Integer
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @Override
    public Integer countTicketByUid(String uid) {
        return userTicketMapper.countAvailableTickets(uid);
    }

    /**
     * @Description: 查用户拥有的所有券
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.UserTicketEntity>
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @Override
    public PageInfo<UserTicketEntity> queryTickets(BaseQO<UserTicketQO> baseQO) {
        Page<UserTicketEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        UserTicketQO query = baseQO.getQuery();
        Page<UserTicketEntity> pageResult = userTicketMapper.queryUserTicketPage(page, query);
        //若是查询未过期的
        if (UserTicketQO.TICKET_UNEXPIRED.equals(query.getExpired())) {
            for (UserTicketEntity ticketEntity : pageResult.getRecords()) {
                ticketEntity.setMoneyStr(ticketEntity.getMoney().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                ticketEntity.setLeastConsumeStr(ticketEntity.getLeastConsume().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                //减少循环判断，直接设为未过期
                ticketEntity.setExpired(UserTicketQO.TICKET_UNEXPIRED);
            }
        } else if (UserTicketQO.TICKET_EXPIRED.equals(query.getExpired())) {
            //若是查询已过期的
            for (UserTicketEntity ticketEntity : pageResult.getRecords()) {
                ticketEntity.setMoneyStr(ticketEntity.getMoney().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                ticketEntity.setLeastConsumeStr(ticketEntity.getLeastConsume().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                //减少循环判断，直接设为已过期
                ticketEntity.setExpired(UserTicketQO.TICKET_EXPIRED);
            }
        } else {
            //若是查询全部
            for (UserTicketEntity ticketEntity : pageResult.getRecords()) {
                ticketEntity.setMoneyStr(ticketEntity.getMoney().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                ticketEntity.setLeastConsumeStr(ticketEntity.getLeastConsume().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                if (LocalDateTime.now().isBefore(ticketEntity.getExpireTime())) {
                    //未过期
                    ticketEntity.setExpired(UserTicketQO.TICKET_UNEXPIRED);
                } else {
                    //已过期
                    ticketEntity.setExpired(UserTicketQO.TICKET_EXPIRED);
                }
            }
        }
        PageInfo<UserTicketEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageResult, pageInfo);
        return pageInfo;
    }

    //TODO 券相关操作改为支付(账户操作)时抵用 结果由本平台计算 展示项目结束后修改

    /**
     * @Description: id单查
     * @Param: [id, uid]
     * @Return: com.jsy.community.entity.UserTicketEntity
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @Override
    public UserTicketEntity queryTicketById(Long id, String uid) {
        //查用户券t_user_ticket
        UserTicketEntity userTicketEntity = userTicketMapper.selectOne(new QueryWrapper<UserTicketEntity>().select("ticket_id as id,status,expire_time").eq("id", id).eq("uid", uid));
        if (userTicketEntity == null) {
            throw new ProprietorException(JSYError.BAD_REQUEST.getCode(), "查无此券");
        }
        //查券信息t_ticket
        UserTicketEntity ticketEntity = userTicketMapper.queryTicketById(userTicketEntity.getId());
        if (ticketEntity == null) {
            throw new ProprietorException(JSYError.BAD_REQUEST.getCode(), "平台已无该券，请联系管理员");
        }
        ticketEntity.setStatus(userTicketEntity.getStatus());
        ticketEntity.setExpireTime(userTicketEntity.getExpireTime());
        return ticketEntity;
    }

    //TODO 券相关操作改为支付(账户操作)时抵用 结果由本平台计算 展示项目结束后修改

    /**
     * @Description: 使用
     * @Param: [id, uid]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @Override
    public boolean useTicket(Long id, String uid) {
        //检查
        UserTicketEntity userTicketEntity = userTicketMapper.checkExpired(id);
        if (userTicketEntity == null) {
            throw new ProprietorException("券已过期");
        }
        if (userTicketEntity.getStatus() == 1) {
            throw new ProprietorException("券已使用");
        }
        return userTicketMapper.useTicket(id, uid) == 1;
    }

    //TODO 券相关操作改为支付(账户操作)时抵用 结果由本平台计算 展示项目结束后修改

    /**
     * @Description: 退回
     * @Param: [id, uid]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2021/1/28
     **/
    @Override
    public boolean rollbackTicket(Long id, String uid) {
        return userTicketMapper.rollbackTicket(id, uid) == 1;
    }

    /**
     * @Description: 清理过期券(超期30天)
     * @Param: []
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/1/29
     **/
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteExpiredTicket() {
        Long id = SnowFlake.nextId();
        userTicketMapper.deleteExpiredTicket(id);
    }

    /**
     * 校验支付密码
     *
     * @param uid         用户ID
     * @param payPassword 用户输入的支付密码
     * @return
     */
    @Override
    public boolean checkPayPassword(String uid, String payPassword) {
        if (StringUtils.isEmpty(payPassword)) {
            throw new ProprietorException("支付密码未填写！");
        }
        QueryWrapper<UserAuthEntity> eq = new QueryWrapper<UserAuthEntity>().select("pay_password", "pay_salt").eq("uid", uid).eq("deleted", 0);
        UserAuthEntity userAuthEntity = userAuthMapper.selectOne(eq);
        if (userAuthEntity == null) {
            return false;
        }
        if (StringUtils.isEmpty(userAuthEntity.getPayPassword())) {
            throw new ProprietorException("还未设置支付密码！");
        }
        String salt = userAuthEntity.getPaySalt();
        String encryptedPassword = SecureUtil.sha256(payPassword + salt);
        return userAuthEntity.getPayPassword().equals(encryptedPassword);
    }

    /**
     * 钱包余额提现至微信
     *
     * @param userWithdrawalQ0 提现金额相关
     * @param uid              用户
     */
    @Override
    public WithdrawalResulrVO wechatWithdrawal(UserWithdrawalQ0 userWithdrawalQ0, String uid) {
        //检查能否允许转账，并返回一个当前余额
        BigDecimal balance = checkAmountAndPassword(userWithdrawalQ0, uid);
        //得到一个唯一的编号作为转账流水号
        long serialNumber = SnowFlake.nextId();
        //查询要转账去的个人微信账户
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("uid", uid).eq("third_platform_type", 2));
        if (entity == null || StringUtils.isEmpty(entity.getThirdPlatformId())) {
            throw new ProprietorException("还未绑定微信，请先绑定微信再进行提现！");
        }
        return transactionWithdrawal(serialNumber, userWithdrawalQ0.getAmount(), entity.getThirdPlatformId(), uid, balance, 1, null, null);
    }

    /**
     * 钱包余额提现至支付宝
     *
     * @param userWithdrawalQ0 提现金额相关
     * @param uid              用户
     */
    @Override
    public WithdrawalResulrVO zhiFuBaoWithdrawal(UserWithdrawalQ0 userWithdrawalQ0, String uid) {
        //检查能否允许转账，并返回一个当前余额
        BigDecimal balance = checkAmountAndPassword(userWithdrawalQ0, uid);
        //得到一个唯一的编号作为转账流水号
        long serialNumber = SnowFlake.nextId();
        String platform;
        String realName;
        String identityType;
        //查询要转账去的个人支付宝账户
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("uid", uid).eq("third_platform_type", 5));
        if (entity == null || StringUtils.isEmpty(entity.getThirdPlatformId())) {
            throw new ProprietorException("还未绑定支付宝，请先绑定支付宝再进行提现！");
        }
        platform = entity.getThirdPlatformId();
        realName = entity.getRealname();
        identityType = "ALIPAY_LOGON_ID";
        return transactionWithdrawal(serialNumber, userWithdrawalQ0.getAmount(), platform, uid, balance, 2, realName, identityType);
    }

    /**
     * 查询绑定的支付宝账户
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public ZhiFuBaoAccountBindingQO queryZhiFuBaoAccount(String uid) {
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("uid", uid).eq("third_platform_type", 5));
        ZhiFuBaoAccountBindingQO zhiFuBaoAccountBindingQO = new ZhiFuBaoAccountBindingQO();
        if (entity != null) {
            zhiFuBaoAccountBindingQO.setAccount(entity.getThirdPlatformId());
            zhiFuBaoAccountBindingQO.setRealname(entity.getRealname());
        }
        return zhiFuBaoAccountBindingQO;
    }

    /**
     * 解绑支付宝提现账户
     *
     * @param uid
     */
    @Override
    public void unbundlingZhiFuBaoAccount(String uid) {
        userThirdPlatformMapper.deleteZhiFuBaoBinDing(uid);
    }

    /**
     * 绑定支付宝账户
     *
     * @param uid              用户ID
     * @param accountBindingQO 绑定的支付宝账户信息
     */
    @Override
    public void bindingZhiFuBaoAccount(String uid, ZhiFuBaoAccountBindingQO accountBindingQO) {
        String account = accountBindingQO.getAccount();
        String realname = accountBindingQO.getRealname();
        log.info("绑定支付宝账号：" + account);
        log.info("绑定支付宝账号名称：" + realname);
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(realname)) {
            throw new ProprietorException("账户和真实姓名填写完整。");
        }
        UserThirdPlatformEntity entity = userThirdPlatformMapper.selectOne(new QueryWrapper<UserThirdPlatformEntity>()
                .eq("uid", uid).eq("third_platform_type", 5));
        if (entity != null) {
            if (!account.equals(entity.getThirdPlatformId()) || !realname.equals(entity.getRealname())) {
                UserThirdPlatformEntity userThirdPlatform = new UserThirdPlatformEntity();
                userThirdPlatform.setId(entity.getId());
                userThirdPlatform.setThirdPlatformId(account);
                userThirdPlatform.setRealname(realname);
                userThirdPlatformMapper.updateById(userThirdPlatform);
            }
        } else {
            UserThirdPlatformEntity userThirdPlatform = new UserThirdPlatformEntity();
            userThirdPlatform.setId(SnowFlake.nextId());
            userThirdPlatform.setDeleted(0L);
            userThirdPlatform.setUid(uid);
            userThirdPlatform.setThirdPlatformId(account);
            userThirdPlatform.setThirdPlatformType(5);
            userThirdPlatform.setRealname(realname);
            userThirdPlatformMapper.insert(userThirdPlatform);
        }
    }

    /**
     * @param conId  : 合同ID,传合同ID的原因是防止重复加金额
     * @param amount : 入账金额
     * @param uid    : 房东ID
     * @author: Pipi
     * @description: 签约支付后租金入账房东账户
     * @return: java.lang.Integer
     * @date: 2021/10/15 11:43
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer rentalIncome(String conId, BigDecimal amount, String uid) {
        //查询合同对应的流水记录是否存在.合同ID对应goods_id
        QueryWrapper<UserAccountRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();
        recordEntityQueryWrapper.eq("goods_id", conId);
        recordEntityQueryWrapper.last(" limit 1");
        UserAccountRecordEntity userAccountRecordEntity = userAccountRecordMapper.selectOne(recordEntityQueryWrapper);
        if (userAccountRecordEntity != null) {
            // 已经有入账数据,说明入过账,跳过
            return 0;
        }
        // 没有入账,则入账
        // 查询余额
        QueryWrapper<UserAccountEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        UserAccountEntity userAccountEntity = userAccountMapper.selectOne(queryWrapper);
        if (userAccountEntity == null) {
            throw new ProprietorException("未找到用户余额信息,合同编号:" + conId);
        }
        // 修改用户余额
        UpdateWrapper<UserAccountEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("balance", userAccountEntity.getBalance().add(amount));
        int result = userAccountMapper.update(new UserAccountEntity(), updateWrapper);
        if (result == 1) {
            // 添加入账记录
            UserAccountRecordEntity ucoinRecordEntity = new UserAccountRecordEntity();
            ucoinRecordEntity.setId(SnowFlake.nextId());
            ucoinRecordEntity.setUid(uid);
            ucoinRecordEntity.setTradeFrom(5);
            ucoinRecordEntity.setTradeType(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex());
            ucoinRecordEntity.setTradeAmount(amount);
            ucoinRecordEntity.setBalance(userAccountEntity.getBalance().add(amount));//交易后余额
            ucoinRecordEntity.setComment("租金入账");
            ucoinRecordEntity.setDeleted(0L);
            ucoinRecordEntity.setSerialNumber(Long.toString(ucoinRecordEntity.getId()));
            userAccountRecordMapper.insert(ucoinRecordEntity);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public WithdrawalResulrVO transactionWithdrawal(long serialNumber, BigDecimal amount, String thirdPlatformId,
                                                    String uid, BigDecimal balance, Integer type, String realname, String identityType) {
        String typeStr = type == 1 ? "微信" : "支付宝";
        WithdrawalResulrVO result;
        if (type == 1) {
            // 开始发起微信提现，实质上是微信商户对个人用户付款
            //得到以分为单位的金额
            String amountStr = amount.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_UP).toString();
            result = userAccountWithdrawalService.weiXinWithdrawal(Long.toString(serialNumber), thirdPlatformId, amountStr);
        } else {
            //发起支付宝提现
            result = userAccountWithdrawalService.zhiFuBaoWithdrawal(Long.toString(serialNumber), amount.toString(), realname, thirdPlatformId, identityType);
        }
        if (!result.getSuccess()) {
            log.error("提现到{}失败。用户id：{},流水号：{},提现金额：{},发起提现方三方平台ID：{},", typeStr, uid, serialNumber, amount, thirdPlatformId);
            return result;
        }
        log.info("提现到{}成功。用户id：{},流水号：{},提现金额：{},发起提现方三方平台ID：{},", typeStr, uid, serialNumber, amount, thirdPlatformId);
        //计算出转账成功之后的余额
        BigDecimal subtract = balance.subtract(amount);
        //修改余额
        int i = userAccountMapper.updateBalanceByBalance(subtract, uid, balance);
        if (i != 1) {
            throw new PropertyException("修改余额异常。");
        }
        //写提现流水记录
        UserAccountRecordEntity ucoinRecordEntity = new UserAccountRecordEntity();
        ucoinRecordEntity.setId(SnowFlake.nextId());
        ucoinRecordEntity.setUid(uid);
        ucoinRecordEntity.setTradeFrom(1);
        ucoinRecordEntity.setTradeType(PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex());
        ucoinRecordEntity.setTradeAmount(amount);
        ucoinRecordEntity.setBalance(subtract);//交易后余额
        ucoinRecordEntity.setComment(typeStr + "提现");
        ucoinRecordEntity.setDeleted(0L);
        ucoinRecordEntity.setSerialNumber(Long.toString(serialNumber));
        userAccountRecordService.addAccountRecord(ucoinRecordEntity);
        return result;
    }

    /**
     * 检查支付密码和余额
     */
    private BigDecimal checkAmountAndPassword(UserWithdrawalQ0 userWithdrawalQ0, String uid) {
        if (StringUtils.isEmpty(uid)) {
            throw new ProprietorException("账户信息错误！");
        }
        //先要检查支付密码正确性
        String payPassword = userWithdrawalQ0.getPayPassword();
        if (StringUtils.isEmpty(payPassword)) {
            throw new ProprietorException("支付密码不能为空！");
        }
        if (!checkPayPassword(uid, payPassword)) {
            throw new ProprietorException("支付密码错误！");
        }
        //检查余额
        BigDecimal amount = userWithdrawalQ0.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP); //用户要提现金额，保留两位小数
        userWithdrawalQ0.setAmount(amount);
        if (amount.compareTo(new BigDecimal("0.1")) < 0) {
            throw new ProprietorException("提现金额错误！提现金额不能小于0.1元。");
        }
        if (amount.compareTo(new BigDecimal("100000000")) > 0) {
            throw new ProprietorException("提现金额错误！提现金额不能大于100,000,000元。");
        }
        UserAccountVO userAccountVO = queryBalance(uid);
        if (userAccountVO == null) {
            throw new ProprietorException("用户还未拥有钱包账户！");
        }
        BigDecimal balance = userAccountVO.getBalance(); //用户余额
        if (balance.compareTo(amount) < 0) {
            throw new ProprietorException("余额不足。");
        }
        return balance;
    }

}
