package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.PaymentException;
import com.jsy.community.api.UnionPayWalletService;
import com.jsy.community.config.service.UnionPayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.UnionPayTradeRecordEntity;
import com.jsy.community.entity.payment.UnionPayWalletBankEntity;
import com.jsy.community.entity.payment.UnionPayWalletEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.UnionPayBApplyRecordMapper;
import com.jsy.community.mapper.UnionPayTradeRecordMapper;
import com.jsy.community.mapper.UnionPayWalletBankMapper;
import com.jsy.community.mapper.UnionPayWalletMapper;
import com.jsy.community.qo.payment.UnionPay.*;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UnionPayUtils;
import com.jsy.community.vo.livingpayment.UnionPay.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UnionPayUtils unionPayUtils;

    @Autowired
    private UnionPayWalletBankMapper unionPayWalletBankMapper;

    @Autowired
    private UnionPayTradeRecordMapper tradeRecordMapper;

    @Autowired
    private UnionPayBApplyRecordMapper bApplyRecordMapper;
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
        // 构建请求json
        String msgBody = unionPayUtils.buildMsgBody(openAccountForCQO);
        // 调用接口请求
        OpenApiResponseVO response =  unionPayUtils.transApi(msgBody, UnionPayConfig.OPEN_ACCOUNT_FOR_C);
        // 判断业务
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            log.info("开户失败!");
            return false;
        }
        OpenAccountForCVO openAccountForCVO = JSONObject.parseObject(response.getResponse().getMsgBody(), OpenAccountForCVO.class);
        if (openAccountForCVO == null || !"00000".equals(openAccountForCVO.getRspCode())) {
            log.info("开户失败!{}", openAccountForCVO.getRspResult());
            return false;
        }
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
        unionPayWalletEntity.setEncryptPwd(openAccountForCQO.getEncryptPwd());
        unionPayWalletEntity.setEncryptType(openAccountForCQO.getEncryptType());
        unionPayWalletEntity.setBankAcctNo(openAccountForCQO.getBankAcctNo());
        unionPayWalletEntity.setDeleted(0);
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
        Set union_pay_plugRandomKey = redisTemplate.opsForSet().members("union_pay_plugRandomKey");
        // 判断是否还有可用的随机因子
        if (union_pay_plugRandomKey != null && union_pay_plugRandomKey.size() > 0) {
            for (Object plugRandomKey : union_pay_plugRandomKey) {
                redisTemplate.boundSetOps("union_pay_plugRandomKey").remove(plugRandomKey);
                return plugRandomKey.toString();
            }
        }
        // 如果缓存中没有,则调用接口获取随机因子
        Map<String,Integer> bizMap = new HashMap<>();
        // 获取随机因子数量,最多100个
        bizMap.put("applyCount", 10);
        String msgBody = unionPayUtils.buildMsgBody(bizMap);
        // 调用接口请求
        OpenApiResponseVO response = unionPayUtils.transApi(msgBody, UnionPayConfig.CONTROL_RANDOM_FACTOR);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            throw new JSYException("获取控件随机因子失败!请稍候重试!");
        }
        PlugRandomMsgBodyVO msgBodyVO = JSONArray.parseObject(response.getResponse().getMsgBody(), PlugRandomMsgBodyVO.class);
        if (msgBodyVO.getList() == null || msgBodyVO.getList().size() <= 0) {
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
            unionPayWalletBankEntity.setDeleted(0);
            unionPayWalletBankEntity.setCreateTime(new Date());
            unionPayWalletBankEntity.setUpdateTime(new Date());
            unionPayWalletBankMapper.insert(unionPayWalletBankEntity);
        }
        if (bindBankCardQO.getOprtType() == 3) {
            // 解绑,则软删数据
            unionPayWalletBankMapper.updateDeleted(bindBankCardQO.getWalletId(), bindBankCardQO.getBankAcctNo());
        }
        if (bindBankCardQO.getOprtType() == 4) {
            // 设置默认
            // 设置所有的为非默认
            unionPayWalletBankMapper.updateIsDefaultByWalletId(bindBankCardQO.getWalletId());
            // 设置这张卡为默认
            unionPayWalletBankMapper.updateIsDefaultByWalletIdAndBankAcctNo(bindBankCardQO.getWalletId(), bindBankCardQO.getBankAcctNo());
        }

        // 向银联发送处理请求
        String msgBody = unionPayUtils.buildMsgBody(bindBankCardQO);
        OpenApiResponseVO response = unionPayUtils.transApi(msgBody, UnionPayConfig.SET_BANK_METHOD);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            throw new JSYException("银行卡操作失败!");
        }
        UnionPayBaseVO unionPayBaseVO = JSONObject.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unionPayBaseVO == null || !"00000".equals(unionPayBaseVO.getRspCode())) {
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
        String msgBody = unionPayUtils.buildMsgBody(sendSmsAuthCodeQO);
        OpenApiResponseVO response = unionPayUtils.transApi(msgBody, UnionPayConfig.SEND_SMS_AUTH_CODE);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            return false;
        }
        UnionPayBaseVO unionPayBaseVO = JSONObject.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unionPayBaseVO == null || !"00000".equals(unionPayBaseVO.getRspCode())) {
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
        String msgBody = unionPayUtils.buildMsgBody(modifyUserMobileQO);
        OpenApiResponseVO response = unionPayUtils.transApi(msgBody, UnionPayConfig.MODIFY_USER_MOBILE);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            throw new JSYException("修改用户手机号失败!");
        }
        UnionPayBaseVO unionPayBaseVO = JSONObject.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unionPayBaseVO == null || !"00000".equals(unionPayBaseVO.getRspCode())) {
            log.info("修改用户手机号失败!{}", unionPayBaseVO.getRspResult());
            throw new JSYException("修改用户手机号失败!");
        }
        return true;
    }

    /**
     *@Author: Pipi
     *@Description: 获取钱包账户信息
     *@Param: walletIdQO: 钱包ID接参
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.AcctInfoVO
     *@Date: 2021/4/14 13:44
     **/
    @Override
    public AcctInfoVO queryAcctInfo(WalletIdQO walletIdQO) {
        AcctInfoVO acctInfoVO = new AcctInfoVO();
        // 获取钱包基本信息
        String msgBody = unionPayUtils.buildMsgBody(walletIdQO);
        OpenApiResponseVO response = unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_ACCT_INFO);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            return acctInfoVO;
        }
        AcctInfoVO baseAcctInfo = JSONObject.parseObject(response.getResponse().getMsgBody(), AcctInfoVO.class);
        if (baseAcctInfo == null || !"00000".equals(baseAcctInfo.getRspCode())) {
            log.info("获取钱包基本账户信息失败!{}", baseAcctInfo.getRspResult());
            return acctInfoVO;
        }
        BeanUtils.copyProperties(baseAcctInfo, acctInfoVO);
        //获取钱包关联信息(只允许查询C端)
        String relationMsgBody = unionPayUtils.buildMsgBody(walletIdQO);
        OpenApiResponseVO relationResponse = unionPayUtils.queryApi(relationMsgBody, UnionPayConfig.QUERY_ACCT_RELATED_INFO);
        if (relationResponse.getResponse() == null || !"00000".equals(relationResponse.getCode())) {
            return acctInfoVO;
        }
        AcctInfoVO relationAcctInfo = JSONObject.parseObject(relationResponse.getResponse().getMsgBody(), AcctInfoVO.class);
        if (relationAcctInfo == null || !"00000".equals(relationAcctInfo.getRspCode())) {
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
     *@Return: java.util.List<com.jsy.community.vo.livingpayment.UnionPay.BindBankCardVO>
     *@Date: 2021/4/14 17:35
     **/
    @Override
    public List<BindBankCardVO> queryBindBankCardList(WalletIdQO walletIdQO) {
        List<BindBankCardVO> bindBankCardVOS = new ArrayList<>();
        String msgBody = unionPayUtils.buildMsgBody(walletIdQO);
        OpenApiResponseVO response = unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_BIND_BANK_CARD);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            return bindBankCardVOS;
        }
        BindBankCardListVO bindBankCardListVO = JSONObject.parseObject(response.getResponse().getMsgBody(), BindBankCardListVO.class);
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
        String msgBody = unionPayUtils.buildMsgBody(modifyPwdQO);
        OpenApiResponseVO response = unionPayUtils.transApi(msgBody, UnionPayConfig.MODIFY_PWD);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            log.info("修改支付密码失败!");
            return false;
        }
        UnionPayBaseVO unioPayBaseVO = JSONObject.parseObject(response.getResponse().getMsgBody(), UnionPayBaseVO.class);
        if (unioPayBaseVO == null || !"00000".equals(unioPayBaseVO.getRspCode())) {
            log.info("修改支付密码失败!{}", unioPayBaseVO.getRspResult());
            return false;
        }
        return true;
    }

    /**
     * @Author: Pipi
     * @Description: 查询钱包余额
     * @Param: balanceQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.BalanceVO
     * @Date: 2021/4/28 17:46
     */
    @Override
    public BalanceVO queryBalance(BalanceQO balanceQO) {
        BalanceVO balanceVO = new BalanceVO();
        String msgBody = unionPayUtils.buildMsgBody(balanceQO);
        OpenApiResponseVO response = unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_ACCT_BAL);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            log.info("查询钱包余额失败!");
            return balanceVO;
        }
        return JSONObject.parseObject(response.getResponse().getMsgBody(), BalanceVO.class);
    }

    /**
     *@Author: Pipi
     *@Description: 查询开B端开户情况
     *@Param: bizLicNoQO:
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.BEndAccountOpeningVO
     *@Date: 2021/5/10 9:18
     **/
    @Override
    public BEndAccountOpeningVO queryWalletByBizLicNo(BizLicNoQO bizLicNoQO) {
        BEndAccountOpeningVO openingVO = new BEndAccountOpeningVO();
        String msgBody = unionPayUtils.buildMsgBody(bizLicNoQO);
        OpenApiResponseVO response = unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_WALLET_BY_BIZ_LIC_NO);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            log.info("查询开B端开户情况失败!");
            return openingVO;
        }
        openingVO = JSONObject.parseObject(response.getResponse().getMsgBody(), BEndAccountOpeningVO.class);
        return openingVO;
    }

    /**
     *@Author: Pipi
     *@Description: 发送提现申请
     *@Param: withdrawQO:
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.WithdrawVO
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
        String msgBody = unionPayUtils.buildMsgBody(withdrawQO);
        OpenApiResponseVO response = unionPayUtils.transApi(msgBody, UnionPayConfig.WITHDRAW);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            log.info("提现申请失败!{}", response.getResponse());
            throw new JSYException("提现申请失败!");
        }
        WithdrawVO withdrawVO = JSONObject.parseObject(response.getResponse().getMsgBody(), WithdrawVO.class);
        if (withdrawVO == null || !"00000".equals(withdrawVO.getRspCode())) {
            log.info("提现申请失败!{}", withdrawVO.getRspResult());
            throw new PaymentException("提现申请失败!" + withdrawVO.getRspResult());
        }
        return withdrawVO;
    }

    /**
     * @Author: Pipi
     * @Description: 激活账户
     * @Param: activeAcctQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.ActiveAcctVO
     * @Date: 2021/5/12 17:22
     */
    @Override
    public ActiveAcctVO activeAcct(ActiveAcctQO activeAcctQO) {
        String msgBody = unionPayUtils.buildMsgBody(activeAcctQO);
        OpenApiResponseVO response = unionPayUtils.transApi(msgBody, UnionPayConfig.ACTIVE_ACCT);
        if (response.getResponse() == null || !"00000".equals(response.getCode())) {
            log.info("激活账户失败!{}", response.getResponse());
            throw new JSYException("激活账户失败!");
        }
        ActiveAcctVO activeAcctVO = JSONObject.parseObject(response.getResponse().getMsgBody(), ActiveAcctVO.class);
        if (activeAcctVO == null || !"00000".equals(activeAcctVO.getRspCode())) {
            log.info("激活账户失败!{}", activeAcctVO.getRspResult());
            throw new PaymentException("激活账户失败!" + activeAcctVO.getRspResult());
        }
        return activeAcctVO;
    }
}
