package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PaymentException;
import com.jsy.community.api.UnionPayService;
import com.jsy.community.api.UnionPayWalletService;
import com.jsy.community.config.UnionPayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.UnionPayTradeRecordEntity;
import com.jsy.community.entity.payment.UnionPayWalletBankEntity;
import com.jsy.community.entity.payment.UnionPayWalletEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.UnionPayBApplyRecordMapper;
import com.jsy.community.mapper.UnionPayTradeRecordMapper;
import com.jsy.community.mapper.UnionPayWalletBankMapper;
import com.jsy.community.mapper.UnionPayWalletMapper;
import com.jsy.community.qo.unionpay.*;
import com.jsy.community.untils.UnionPayUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.unionpay.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Pipi
 * @Description: 银联支付服务实现
 * @Date: 2021/4/8 10:56
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class UnionPayWalletServiceImpl extends ServiceImpl<UnionPayWalletMapper, UnionPayWalletEntity> implements UnionPayWalletService {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private UnionPayUtils UnionPayUtils;

    @Autowired
    private UnionPayWalletBankMapper unionPayWalletBankMapper;

    @Autowired
    private UnionPayTradeRecordMapper tradeRecordMapper;

    @Autowired
    private UnionPayBApplyRecordMapper bApplyRecordMapper;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false, timeout = 1200000)
    private UnionPayService unionPayService;

    // 银联成功状态码
    private static final String SUCCESS_STATUS_CODE = "00000";

    /**
     * @Author: Pipi
     * @Description: C端用户开户
     * @Param: openAccountForCQO: 银联C端开户请求参数
     * @Return: java.lang.Boolean
     * @Date: 2021/4/8 10:49
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean openAccountForC(OpenAccountForCQO openAccountForCQO, String uid) {
        // 查询在平台是否已有账户
        QueryWrapper<UnionPayWalletEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile_no", openAccountForCQO.getMobileNo());
        queryWrapper.eq("id_card", openAccountForCQO.getIdCard());
        queryWrapper.or().eq("uid", uid);
        UnionPayWalletEntity unionPayWalletEntity1 = baseMapper.selectOne(queryWrapper);
        if (unionPayWalletEntity1 != null) {
            throw new PaymentException("该用户已经");
        }
        // 调用接口请求
        OpenApiResponseVO response =  unionPayService.openAccountForC(openAccountForCQO);
        // 判断业务
        if (response.getResponse() == null || !UnionPayConfig.SUCCESS_CODE.equals(response.getCode())) {
            log.info("开户失败!");
            return false;
        }
        OpenAccountForCVO openAccountForCVO = JSON.parseObject(response.getResponse().getMsgBody(), OpenAccountForCVO.class);
        if (openAccountForCVO == null || !UnionPayConfig.SUCCESS_CODE.equals(openAccountForCVO.getRspCode())) {
            return false;
        }
        log.info("开户失败!{}", openAccountForCVO.getRspResult());
        if ("1".equals(openAccountForCVO.getIsExistedAcct())) {
            log.info("重复的开户操作,直接返回成功!");
            return true;
        }
        // 银联钱包实体
        UnionPayWalletEntity unionPayWalletEntity = new UnionPayWalletEntity();
        BeanUtils.copyProperties(openAccountForCQO, unionPayWalletEntity);
        unionPayWalletEntity.setId(SnowFlake.nextId());
        unionPayWalletEntity.setUid(uid);
        unionPayWalletEntity.setUserUuid(openAccountForCVO.getUserUuid());
        unionPayWalletEntity.setWalletId(openAccountForCVO.getWalletId());
        unionPayWalletEntity.setBankAcctNo(openAccountForCQO.getBankAcctNo());
        unionPayWalletEntity.setDeleted(0L);
        unionPayWalletEntity.setCreateTime(new Date());
        unionPayWalletEntity.setUpdateTime(new Date());
        baseMapper.insert(unionPayWalletEntity);
        return true;
    }

    /**
     * @Author: Pipi
     * @Description: 获取控件随机因子
     * @Param: :
     * @Return: java.lang.String
     * @Date: 2021/4/10 13:57
     **/
    @Override
    public String getPlugRandomKey() {
        // 从缓存中取出随机因子
        Set unionPayPlugRandomKey = redisTemplate.opsForSet().members("union_pay_plugRandomKey");
        // 判断是否还有可用的随机因子
        if (!CollectionUtils.isEmpty(unionPayPlugRandomKey)) {
            Object plugRandomKey = unionPayPlugRandomKey.iterator().next();
            redisTemplate.boundSetOps("union_pay_plugRandomKey").remove(plugRandomKey);
            return plugRandomKey.toString();
        }
        // 如果缓存中没有,则调用接口获取随机因子
        OpenApiResponseVO response = unionPayService.getPlugRandomKey(100);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            throw new JSYException("获取控件随机因子失败!请稍候重试!");
        }
        PlugRandomMsgBodyVO msgBodyVO = JSON.parseObject(response.getResponse().getMsgBody(), PlugRandomMsgBodyVO.class);
        if (msgBodyVO.getList().isEmpty()) {
            throw new JSYException("获取控件随机因子失败!请稍候重试!");
        }
        // 获取第一个随机因子在随后返回
        PlugRandomKeyVO plugRandomKeyVO = msgBodyVO.getList().get(0);
        // 删除第一个随机因子
        msgBodyVO.getList().remove(0);
        // 将随机因子存入缓存
        msgBodyVO.getList().forEach(plugRandomKey -> {
            redisTemplate.opsForSet().add("union_pay_plugRandomKey", plugRandomKey.getPlugRandomKey());
        });
        // 设置缓存24小时之后过期
        redisTemplate.expire("union_pay_plugRandomKey", 24, TimeUnit.HOURS);
        return plugRandomKeyVO.getPlugRandomKey();
    }

    /**
     *@Author: Pipi
     *@Description: 账户绑定/解绑/设置默认银行卡
     *@Param: bindBankCardQO: 账户绑定/解绑/设置默认银行卡接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/12 15:11
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean bindBankCard(BindBankCardQO bindBankCardQO) {
        // 判断操作类型,进行相关处理
        if (bindBankCardQO.getOprtType() == 1 || bindBankCardQO.getOprtType() == 2) {
            // 绑定银行卡,则新增绑定银行卡数据
            UnionPayWalletBankEntity unionPayWalletBankEntity = new UnionPayWalletBankEntity();
            unionPayWalletBankEntity.setId(SnowFlake.nextId());
            unionPayWalletBankEntity.setWalletId(bindBankCardQO.getWalletId());
            unionPayWalletBankEntity.setBankAcctNo(bindBankCardQO.getBankAcctNo());
            unionPayWalletBankEntity.setAccountBank(bindBankCardQO.getBankAcctName());
            unionPayWalletBankEntity.setBankId(bindBankCardQO.getElecBankNo());
            unionPayWalletBankEntity.setThreeBankNo(bindBankCardQO.getBankNo());
            if (bindBankCardQO.getOprtType() == 2) {
                unionPayWalletBankEntity.setIsDefault(1);
                // 设置所有的为非默认
                unionPayWalletBankMapper.updateIsDefaultByWalletId(bindBankCardQO.getWalletId());
            } else {
                unionPayWalletBankEntity.setIsDefault(0);
            }
            unionPayWalletBankEntity.setDeleted(0L);
            unionPayWalletBankEntity.setCreateTime(new Date());
            unionPayWalletBankEntity.setUpdateTime(new Date());
            unionPayWalletBankMapper.insert(unionPayWalletBankEntity);
        }
        if (bindBankCardQO.getOprtType() == 3) {
            // 解绑,则软删数据
            Long id = SnowFlake.nextId();
            unionPayWalletBankMapper.updateDeleted(id, bindBankCardQO.getWalletId(), bindBankCardQO.getBankAcctNo());
        }
        if (bindBankCardQO.getOprtType() == 4) {
            // 设置默认
            // 设置所有的为非默认
            unionPayWalletBankMapper.updateIsDefaultByWalletId(bindBankCardQO.getWalletId());
            // 设置这张卡为默认
            unionPayWalletBankMapper.updateIsDefaultByWalletIdAndBankAcctNo(bindBankCardQO.getWalletId(), bindBankCardQO.getBankAcctNo());
        }
        OpenApiResponseVO response = unionPayService.bindBankCard(bindBankCardQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            throw new JSYException("银行卡操作失败!");
        }
        UnionPayBaseVO unionPayBaseVO = JSON.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unionPayBaseVO == null) {
            throw new JSYException("银行卡操作失败!");
        }
        if (!SUCCESS_STATUS_CODE.equals(unionPayBaseVO.getRspCode())) {
            log.info("银行卡操作失败!{}", unionPayBaseVO.getRspResult());
            throw new JSYException("银行卡操作失败!");
        }
        return true;
    }

    /**
     *@Author: Pipi
     *@Description: 银联发送短信验证码
     *@Param: sendSmsAuthCodeQO: 发送短信验证码接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/12 17:59
     **/
    @Override
    public Boolean sendSmsAuthCode(SendSmsAuthCodeQO sendSmsAuthCodeQO) {
        OpenApiResponseVO response = unionPayService.sendSmsAuthCode(sendSmsAuthCodeQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            return false;
        }
        UnionPayBaseVO unionPayBaseVO = JSON.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unionPayBaseVO == null) {
            return false;
        }
        if (!SUCCESS_STATUS_CODE.equals(unionPayBaseVO.getRspCode())) {
            log.info("短信验证码发送失败!{}", unionPayBaseVO.getRspResult());
            return false;
        }
        return true;
    }

    /**
     *@Author: Pipi
     *@Description: 修改用户手机号
     *@Param: modifyUserMobileQO: 修改用户手机号接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/14 9:36
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyUserMobile(ModifyUserMobileQO modifyUserMobileQO) {
        // 更新数据库信息
        baseMapper.updateMobileNoByWalletId(modifyUserMobileQO.getMobileNo(), modifyUserMobileQO.getWalletId());
        // 向银联发送更新信息
        OpenApiResponseVO response = unionPayService.modifyUserMobile(modifyUserMobileQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            throw new JSYException("修改用户手机号失败!");
        }
        UnionPayBaseVO unionPayBaseVO = JSON.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unionPayBaseVO == null) {
            throw new JSYException("修改用户手机号失败!");
        }
        if (!SUCCESS_STATUS_CODE.equals(unionPayBaseVO.getRspCode())) {
            log.info("修改用户手机号失败!{}", unionPayBaseVO.getRspResult());
            throw new JSYException("修改用户手机号失败!");
        }
        return true;
    }

    /**
     *@Author: Pipi
     *@Description: 获取钱包账户信息
     *@Param: walletIdQO: 钱包ID接参
     *@Return: com.jsy.community.vo.unionpay.AcctInfoVO
     *@Date: 2021/4/14 13:44
     **/
    @Override
    public AcctInfoVO queryAcctInfo(WalletIdQO walletIdQO) {
        AcctInfoVO acctInfoVO = new AcctInfoVO();
        // 获取钱包基本信息
        OpenApiResponseVO response = unionPayService.queryAcctInfo(walletIdQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            return acctInfoVO;
        }
        AcctInfoVO baseAcctInfo = JSON.parseObject(response.getResponse().getMsgBody(), AcctInfoVO.class);
        if (baseAcctInfo == null) {
            return acctInfoVO;
        }
        if (!SUCCESS_STATUS_CODE.equals(baseAcctInfo.getRspCode())) {
            log.info("获取钱包基本账户信息失败!{}", baseAcctInfo.getRspResult());
            return acctInfoVO;
        }
        BeanUtils.copyProperties(baseAcctInfo, acctInfoVO);
        //获取钱包关联信息(只允许查询C端)
        String relationMsgBody = UnionPayUtils.buildMsgBody(walletIdQO);
        OpenApiResponseVO relationResponse = UnionPayUtils.queryApi(relationMsgBody, UnionPayConfig.QUERY_ACCT_RELATED_INFO);
        if (relationResponse.getResponse() == null || !SUCCESS_STATUS_CODE.equals(relationResponse.getCode())) {
            return acctInfoVO;
        }
        AcctInfoVO relationAcctInfo = JSON.parseObject(relationResponse.getResponse().getMsgBody(), AcctInfoVO.class);
        if (relationAcctInfo == null) {
            return acctInfoVO;
        }
        if (!SUCCESS_STATUS_CODE.equals(relationAcctInfo.getRspCode())) {
            log.info("获取钱包账户关联信息失败!{}", relationAcctInfo.getRspResult());
            return acctInfoVO;
        }
        acctInfoVO.setUserName(relationAcctInfo.getUserName());
        acctInfoVO.setMobileNo(relationAcctInfo.getMobileNo());
        acctInfoVO.setUserUuid(relationAcctInfo.getUserUuid());
        acctInfoVO.setCustId(relationAcctInfo.getCustId());
        acctInfoVO.setCustName(relationAcctInfo.getCustName());
        acctInfoVO.setRechargeCode(acctInfoVO.getAcctAttribute().substring(0,1));
        acctInfoVO.setConsumeCode(acctInfoVO.getAcctAttribute().substring(1,2));
        acctInfoVO.setTransferCode(acctInfoVO.getAcctAttribute().substring(2,3));
        acctInfoVO.setWithdrawCode(acctInfoVO.getAcctAttribute().substring(3,4));
        acctInfoVO.setRemittanceCode(acctInfoVO.getAcctAttribute().substring(4,5));
        acctInfoVO.setFreezeCode(acctInfoVO.getAcctAttribute().substring(5,6));
        acctInfoVO.setThawCode(acctInfoVO.getAcctAttribute().substring(6,7));
        return acctInfoVO;
    }

    /**
     *@Author: Pipi
     *@Description: 获取钱包账户绑定的银行卡列表
     *@Param: walletIdQO: 钱包ID接参
     *@Return: java.util.List<com.jsy.community.vo.unionpay.BindBankCardVO>
     *@Date: 2021/4/14 17:35
     **/
    @Override
    public List<BindBankCardVO> queryBindBankCardList(WalletIdQO walletIdQO) {
        List<BindBankCardVO> bindBankCardVOS = new ArrayList<>();
        OpenApiResponseVO response = unionPayService.queryBindBankCardList(walletIdQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            return bindBankCardVOS;
        }
        BindBankCardListVO bindBankCardListVO = JSON.parseObject(response.getResponse().getMsgBody(), BindBankCardListVO.class);
        return bindBankCardListVO.getRowList();
    }

    /**
     *@Author: Pipi
     *@Description: 修改银联支付密码
     *@Param: modifyPwdQO: 修改银联支付密码接参
     *@Return: java.lang.Boolean
     *@Date: 2021/4/15 11:31
     **/
    @Override
    public Boolean modifyPwd(ModifyPwdQO modifyPwdQO) {
        OpenApiResponseVO response = unionPayService.modifyPwd(modifyPwdQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            log.info("修改支付密码失败!");
            return false;
        }
        UnionPayBaseVO unioPayBaseVO = JSON.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unioPayBaseVO == null) {
            return false;
        }
        if (!SUCCESS_STATUS_CODE.equals(unioPayBaseVO.getRspCode())) {
            log.info("修改支付密码失败!{}", unioPayBaseVO.getRspResult());
            return false;
        }
        return true;
    }

    /**
     * @Author: Pipi
     * @Description: 查询钱包余额
     * @Param: balanceQO:
     * @Return: com.jsy.community.vo.unionpay.BalanceVO
     * @Date: 2021/4/28 17:46
     */
    @Override
    public BalanceVO queryBalance(BalanceQO balanceQO) {
        BalanceVO balanceVO = new BalanceVO();
        OpenApiResponseVO response = unionPayService.queryBalance(balanceQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            log.info("查询钱包余额失败!");
            return balanceVO;
        }
        return JSON.parseObject(response.getResponse().getMsgBody(), BalanceVO.class);
    }


    /**
     *@Author: Pipi
     *@Description: 查询开B端开户情况
     *@Param: bizLicNoQO:
     *@Return: com.jsy.community.vo.unionpay.BEndAccountOpeningVO
     *@Date: 2021/5/10 9:18
     **/
    @Override
    public BEndAccountOpeningVO queryWalletByBizLicNo(BizLicNoQO bizLicNoQO) {
        BEndAccountOpeningVO openingVO = new BEndAccountOpeningVO();
        OpenApiResponseVO response = unionPayService.queryWalletByBizLicNo(bizLicNoQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            log.info("查询开B端开户情况失败!");
            return openingVO;
        }
        openingVO = JSON.parseObject(response.getResponse().getMsgBody(), BEndAccountOpeningVO.class);
        return openingVO;
    }

    /**
     *@Author: Pipi
     *@Description: 发送提现申请
     *@Param: withdrawQO:
     *@Return: com.jsy.community.vo.unionpay.WithdrawVO
     *@Date: 2021/5/10 10:38
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WithdrawVO withdrawApply(WithdrawQO withdrawQO, String uid) {
        BigDecimal amount = new BigDecimal(withdrawQO.getAmount());
        // 添加交易记录
        UnionPayTradeRecordEntity tradeRecordEntity = new UnionPayTradeRecordEntity();
        tradeRecordEntity.setUid(uid);
        tradeRecordEntity.setWalletId(withdrawQO.getWalletId());
        tradeRecordEntity.setAmount(amount);
        tradeRecordEntity.setFeeAmt(new BigDecimal("0"));
        tradeRecordEntity.setFeeIntoWalletId(withdrawQO.getFeeIntoWalletId());
        tradeRecordEntity.setBankAcctNo(withdrawQO.getBankAcctNo());
        tradeRecordEntity.setRemark(withdrawQO.getRemark());
        tradeRecordEntity.setAbst(withdrawQO.getAbst());
        tradeRecordEntity.setPostscript(withdrawQO.getPostscript());
        tradeRecordEntity.setTradeStatue(1);
        tradeRecordEntity.setId(SnowFlake.nextId());
        tradeRecordMapper.insert(tradeRecordEntity);

        withdrawQO.setMctOrderNo(tradeRecordEntity.getId().toString());
        // 发送提现申请
        withdrawQO.setTradeWayCode("c_pass");
        withdrawQO.setTradeWayFeilds("{\"encryptPwd\":\"" + withdrawQO.getEncryptPwd() + "\",\"encryptType\":\"" +
                withdrawQO.getEncryptType() + "\",\"plugRandomKey\":\"" + withdrawQO.getPlugRandomKey() + "\"}");
        amount = amount.multiply(BigDecimal.valueOf(100)).setScale(0);
        withdrawQO.setAmount(amount.toString());
        OpenApiResponseVO response = unionPayService.withdrawApply(withdrawQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            log.info("提现申请失败!{}", response.getResponse());
            throw new JSYException("提现申请失败!");
        }
        WithdrawVO withdrawVO = JSON.parseObject(response.getResponse().getMsgBody(), WithdrawVO.class);
        if (withdrawVO == null) {
            throw new PaymentException("提现申请失败!");
        }
        if (!SUCCESS_STATUS_CODE.equals(withdrawVO.getRspCode())) {
            log.info("提现申请失败!{}", withdrawVO.getRspResult());
            throw new PaymentException("提现申请失败!" + withdrawVO.getRspResult());
        }
        return withdrawVO;
    }

    /**
     * @Author: Pipi
     * @Description: 激活账户
     * @Param: activeAcctQO:
     * @Return: com.jsy.community.vo.unionpay.ActiveAcctVO
     * @Date: 2021/5/12 17:22
     */
    @Override
    public ActiveAcctVO activeAcct(ActiveAcctQO activeAcctQO) {
        OpenApiResponseVO response = unionPayService.activeAcct(activeAcctQO);
        if (response.getResponse() == null || !SUCCESS_STATUS_CODE.equals(response.getCode())) {
            log.info("激活账户失败!{}", response.getResponse());
            throw new JSYException("激活账户失败!");
        }
        ActiveAcctVO activeAcctVO = JSON.parseObject(response.getResponse().getMsgBody(), ActiveAcctVO.class);
        if (activeAcctVO == null) {
            throw new PaymentException("激活账户失败!");
        }
        if (!SUCCESS_STATUS_CODE.equals(activeAcctVO.getRspCode())) {
            log.info("激活账户失败!{}", activeAcctVO.getRspResult());
            throw new PaymentException("激活账户失败!" + activeAcctVO.getRspResult());
        }
        return activeAcctVO;
    }
}
