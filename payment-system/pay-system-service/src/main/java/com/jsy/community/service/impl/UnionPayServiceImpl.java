package com.jsy.community.service.impl;

import com.jsy.community.api.UnionPayService;
import com.jsy.community.config.UnionPayConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.*;
import com.jsy.community.untils.UnionPayUtils;
import com.jsy.community.vo.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 银联支付服务实现
 * @Date: 2021/5/13 13:55
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class UnionPayServiceImpl implements UnionPayService {

    @Autowired
    private UnionPayUtils unionPayUtils;

    /**
     * @Author: Pipi
     * @Description: 获取银联支付凭据
     * @Param: credentialsQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.CredentialsVO
     * @Date: 2021/5/7 17:55
     */
    @Override
    public OpenApiResponseVO getCredential(CredentialQO credentialQO) {
        // 构建请求json
        String msgBody = unionPayUtils.buildBizContent(credentialQO);
        return unionPayUtils.credentialApi(msgBody, UnionPayConfig.APPLY_TICKET);
    }

    /**
     * @Author: Pipi
     * @Description: B端钱包重置支付密码
     * @Param: resetBtypeAcctPwdQO:
     * @Return: java.lang.Boolean
     * @Date: 2021/5/11 17:48
     */
    @Override
    public OpenApiResponseVO resetBtypeAcctPwd(ResetBtypeAcctPwdQO resetBtypeAcctPwdQO) {
        // 构建请求json
        String msgBody = unionPayUtils.buildMsgBody(resetBtypeAcctPwdQO);
        return unionPayUtils.transApi(msgBody, UnionPayConfig.RESET_BTYPE_ACCT_PWD);
    }

    /**
     * @Author: Pipi
     * @Description: C端用户开户
     * @Param: openAccountForCQO: 银联C端开户请求参数
     * @Return: java.lang.Boolean
     * @Date: 2021/4/8 10:49
     */
    @Override
    public OpenApiResponseVO openAccountForC(OpenAccountForCQO openAccountForCQO) {
        // 构建请求json
        String msgBody = unionPayUtils.buildMsgBody(openAccountForCQO);
        // 调用接口请求
        return unionPayUtils.transApi(msgBody, UnionPayConfig.OPEN_ACCOUNT_FOR_C);
    }

    /**
     * @Author: Pipi
     * @Description: 获取控件随机因子
     * @Param: :
     * @Return: java.lang.String
     * @Date: 2021/4/10 13:57
     **/
    @Override
    public OpenApiResponseVO getPlugRandomKey(Integer num) {
        Map<String,Integer> bizMap = new HashMap<>();
        // 获取随机因子数量,最多100个
        bizMap.put("applyCount", num);
        String msgBody = unionPayUtils.buildMsgBody(bizMap);
        // 调用接口请求
        return unionPayUtils.transApi(msgBody, UnionPayConfig.CONTROL_RANDOM_FACTOR);
    }

    /**
     * @Author: Pipi
     * @Description: 账户绑定/解绑/设置默认银行卡
     * @Param: bindBankCardQO: 账户绑定/解绑/设置默认银行卡接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/12 15:11
     */
    @Override
    public OpenApiResponseVO bindBankCard(BindBankCardQO bindBankCardQO) {
        // 向银联发送处理请求
        String msgBody = unionPayUtils.buildMsgBody(bindBankCardQO);
        return unionPayUtils.transApi(msgBody, UnionPayConfig.SET_BANK_METHOD);
    }

    /**
     * @Author: Pipi
     * @Description: 银联发送短信验证码
     * @Param: sendSmsAuthCodeQO: 发送短信验证码接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/12 17:59
     */
    @Override
    public OpenApiResponseVO sendSmsAuthCode(SendSmsAuthCodeQO sendSmsAuthCodeQO) {
        String msgBody = unionPayUtils.buildMsgBody(sendSmsAuthCodeQO);
        return unionPayUtils.transApi(msgBody, UnionPayConfig.SEND_SMS_AUTH_CODE);
    }

    /**
     * @Author: Pipi
     * @Description: 修改用户手机号
     * @Param: modifyUserMobileQO: 修改用户手机号接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/14 9:36
     */
    @Override
    public OpenApiResponseVO modifyUserMobile(ModifyUserMobileQO modifyUserMobileQO) {
        String msgBody = unionPayUtils.buildMsgBody(modifyUserMobileQO);
        return unionPayUtils.transApi(msgBody, UnionPayConfig.MODIFY_USER_MOBILE);
    }

    /**
     * @Author: Pipi
     * @Description: 获取钱包账户信息
     * @Param: walletIdQO: 钱包ID接参
     * @Return: com.jsy.community.vo.AcctInfoVO
     * @Date: 2021/4/14 13:44
     */
    @Override
    public OpenApiResponseVO queryAcctInfo(WalletIdQO walletIdQO) {
        String msgBody = unionPayUtils.buildMsgBody(walletIdQO);
        return unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_ACCT_INFO);
    }

    /**
     * @Author: Pipi
     * @Description: 获取钱包账户绑定的银行卡列表
     * @Param: walletIdQO: 钱包ID接参
     * @Return: java.util.List<com.jsy.community.vo.BindBankCardVO>
     * @Date: 2021/4/14 17:35
     */
    @Override
    public OpenApiResponseVO queryBindBankCardList(WalletIdQO walletIdQO) {
        String msgBody = unionPayUtils.buildMsgBody(walletIdQO);
        return unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_BIND_BANK_CARD);
    }

    /**
     * @Author: Pipi
     * @Description: 修改银联支付密码
     * @Param: modifyPwdQO: 修改银联支付密码接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/15 11:31
     */
    @Override
    public OpenApiResponseVO modifyPwd(ModifyPwdQO modifyPwdQO) {
        String msgBody = unionPayUtils.buildMsgBody(modifyPwdQO);
        return unionPayUtils.transApi(msgBody, UnionPayConfig.MODIFY_PWD);
    }

    /**
     * @Author: Pipi
     * @Description: 查询钱包余额
     * @Param: balanceQO:
     * @Return: com.jsy.community.vo.BalanceVO
     * @Date: 2021/4/28 17:46
     */
    @Override
    public OpenApiResponseVO queryBalance(BalanceQO balanceQO) {
        String msgBody = unionPayUtils.buildMsgBody(balanceQO);
        return unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_ACCT_BAL);
    }

    /**
     * @Author: Pipi
     * @Description: 查询开B端开户情况
     * @Param: bizLicNoQO:
     * @Return: com.jsy.community.vo.BEndAccountOpeningVO
     * @Date: 2021/5/10 9:18
     */
    @Override
    public OpenApiResponseVO queryWalletByBizLicNo(BizLicNoQO bizLicNoQO) {
        String msgBody = unionPayUtils.buildMsgBody(bizLicNoQO);
        return unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_WALLET_BY_BIZ_LIC_NO);
    }

    /**
     * @Author: Pipi
     * @Description: 发送提现申请
     * @Param: withdrawQO:
     * @Return: com.jsy.community.vo.WithdrawVO
     * @Date: 2021/5/10 10:38
     */
    @Override
    public OpenApiResponseVO withdrawApply(WithdrawQO withdrawQO) {
        String msgBody = unionPayUtils.buildMsgBody(withdrawQO);
        return unionPayUtils.transApi(msgBody, UnionPayConfig.WITHDRAW);
    }

    /**
     * @Author: Pipi
     * @Description: 激活账户
     * @Param: activeAcctQO:
     * @Return: com.jsy.community.vo.ActiveAcctVO
     * @Date: 2021/5/12 17:22
     */
    @Override
    public OpenApiResponseVO activeAcct(ActiveAcctQO activeAcctQO) {
        String msgBody = unionPayUtils.buildMsgBody(activeAcctQO);
        return unionPayUtils.transApi(msgBody, UnionPayConfig.ACTIVE_ACCT);
    }

    /**
     * @Author: Pipi
     * @Description: 银联消费下单
     * @Param: unionPayOrderRecordEntity:
     * @Return: com.jsy.community.vo.UnionPayOrderVO
     * @Date: 2021/4/26 16:56
     */
    @Override
    public OpenApiResponseVO generateConsumeOrder(GenerateOrderQO generateOrderQO) {
        // 构建请求json
        String msgBody = unionPayUtils.buildMsgBody(generateOrderQO);
        // 调用接口请求
        return unionPayUtils.transApi(msgBody, UnionPayConfig.CONSUME_APPLY_ORDER);
    }

    /**
     * @Author: Pipi
     * @Description: 查询交易明细
     * @Param: queryTransListQO:
     * @Return: com.jsy.community.vo.UnionPayTransListVO
     * @Date: 2021/5/12 10:08
     */
    @Override
    public OpenApiResponseVO queryTransList(QueryTransListQO queryTransListQO) {
        String msgBody = unionPayUtils.buildMsgBody(queryTransListQO);
        return unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_TRANS_LIST);
    }

    /**
     * @Author: Pipi
     * @Description: 账单查询
     * @Param: queryBillInfoQO:
     * @Return: com.jsy.community.vo.QueryBillInfoListVO
     * @Date: 2021/5/12 11:27
     */
    @Override
    public OpenApiResponseVO queryBillInfo(QueryBillInfoQO queryBillInfoQO) {
        String msgBody = unionPayUtils.buildMsgBody(queryBillInfoQO);
        return unionPayUtils.queryApi(msgBody, UnionPayConfig.QUERY_BILL_INFO);
    }
}
