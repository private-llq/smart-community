package com.jsy.community.api;

import com.jsy.community.qo.payment.UnionPay.*;
import com.jsy.community.vo.livingpayment.UnionPay.OpenApiResponseVO;

/**
 * @Author: Pipi
 * @Description: 银联支付服务
 * @Date: 2021/5/13 13:55
 * @Version: 1.0
 **/
public interface UnionPayService {

    /**
     *@Author: Pipi
     *@Description: 获取银联支付凭据
     *@Param: credentialsQO:
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.CredentialsVO
     *@Date: 2021/5/7 17:55
     **/
    OpenApiResponseVO getCredential(CredentialQO credentialQO);

    /**
     * @Author: Pipi
     * @Description: B端钱包重置支付密码
     * @Param: resetBtypeAcctPwdQO:
     * @Return: java.lang.Boolean
     * @Date: 2021/5/11 17:48
     */
    OpenApiResponseVO resetBtypeAcctPwd(ResetBtypeAcctPwdQO resetBtypeAcctPwdQO);

    /**
     * @Author: Pipi
     * @Description: C端用户开户
     * @Param: openAccountForCQO: 银联C端开户请求参数
     * @Return: java.lang.Boolean
     * @Date: 2021/4/8 10:49
     */
    OpenApiResponseVO openAccountForC(OpenAccountForCQO openAccountForCQO);

    /**
     * @Author: Pipi
     * @Description: 获取控件随机因子
     * @Param: :
     * @Return: java.lang.String
     * @Date: 2021/4/10 13:57
     **/
    OpenApiResponseVO getPlugRandomKey(Integer num);

    /**
     * @Author: Pipi
     * @Description: 账户绑定/解绑/设置默认银行卡
     * @Param: bindBankCardQO: 账户绑定/解绑/设置默认银行卡接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/12 15:11
     **/
    OpenApiResponseVO bindBankCard(BindBankCardQO bindBankCardQO);

    /**
     * @Author: Pipi
     * @Description: 银联发送短信验证码
     * @Param: sendSmsAuthCodeQO: 发送短信验证码接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/12 17:59
     **/
    OpenApiResponseVO sendSmsAuthCode(SendSmsAuthCodeQO sendSmsAuthCodeQO);

    /**
     * @Author: Pipi
     * @Description: 修改用户手机号
     * @Param: modifyUserMobileQO: 修改用户手机号接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/14 9:36
     **/
    OpenApiResponseVO modifyUserMobile(ModifyUserMobileQO modifyUserMobileQO);

    /**
     * @Author: Pipi
     * @Description: 获取钱包账户信息
     * @Param: walletIdQO: 钱包ID接参
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.AcctInfoVO
     * @Date: 2021/4/14 13:44
     **/
    OpenApiResponseVO queryAcctInfo(WalletIdQO walletIdQO);

    /**
     * @Author: Pipi
     * @Description: 获取钱包账户绑定的银行卡列表
     * @Param: walletIdQO: 钱包ID接参
     * @Return: java.util.List<com.jsy.community.vo.livingpayment.UnionPay.BindBankCardVO>
     * @Date: 2021/4/14 17:35
     **/
    OpenApiResponseVO queryBindBankCardList(WalletIdQO walletIdQO);

    /**
     * @Author: Pipi
     * @Description: 修改银联支付密码
     * @Param: modifyPwdQO: 修改银联支付密码接参
     * @Return: java.lang.Boolean
     * @Date: 2021/4/15 11:31
     **/
    OpenApiResponseVO modifyPwd(ModifyPwdQO modifyPwdQO);

    /**
     * @Author: Pipi
     * @Description: 查询钱包余额
     * @Param: balanceQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.BalanceVO
     * @Date: 2021/4/28 17:46
     */
    OpenApiResponseVO queryBalance(BalanceQO balanceQO);

    /**
     * @Author: Pipi
     * @Description: 查询开B端开户情况
     * @Param: bizLicNoQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.BEndAccountOpeningVO
     * @Date: 2021/5/10 9:18
     **/
    OpenApiResponseVO queryWalletByBizLicNo(BizLicNoQO bizLicNoQO);

    /**
     * @Author: Pipi
     * @Description: 发送提现申请
     * @Param: withdrawQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.WithdrawVO
     * @Date: 2021/5/10 10:38
     **/
    OpenApiResponseVO withdrawApply(WithdrawQO withdrawQO);

    /**
     * @Author: Pipi
     * @Description: 激活账户
     * @Param: activeAcctQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.ActiveAcctVO
     * @Date: 2021/5/12 17:22
     */
    OpenApiResponseVO activeAcct(ActiveAcctQO activeAcctQO);

    /**
     *@Author: Pipi
     *@Description: 银联消费下单
     *@Param: unionPayOrderRecordEntity:
     *@Return: com.jsy.community.vo.livingpayment.UnionPay.UnionPayOrderVO
     *@Date: 2021/4/26 16:56
     **/
    OpenApiResponseVO generateConsumeOrder(GenerateOrderQO generateOrderQO);

    /**
     * @Author: Pipi
     * @Description: 查询交易明细
     * @Param: queryTransListQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.UnionPayTransListVO
     * @Date: 2021/5/12 10:08
     */
    OpenApiResponseVO queryTransList(QueryTransListQO queryTransListQO);

    /**
     * @Author: Pipi
     * @Description: 账单查询
     * @Param: queryBillInfoQO:
     * @Return: com.jsy.community.vo.livingpayment.UnionPay.QueryBillInfoListVO
     * @Date: 2021/5/12 11:27
     */
    OpenApiResponseVO queryBillInfo(QueryBillInfoQO queryBillInfoQO);
}
