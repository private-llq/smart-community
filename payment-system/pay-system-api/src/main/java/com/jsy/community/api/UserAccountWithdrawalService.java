package com.jsy.community.api;

import com.jsy.community.vo.WithdrawalResulrVO;

public interface UserAccountWithdrawalService {
    /**
     * 调用微信提现（商家账户转账至个人账户）
     * ◆ 当返回错误码为“SYSTEMERROR”时，请不要更换商户订单号，一定要使用原商户订单号重试，否则可能造成重复支付等资金风险。
     * ◆ XML具有可扩展性，因此返回参数可能会有新增，而且顺序可能不完全遵循此文档规范，如果在解析回包的时候发生错误，请商户务必不要换单重试，
     * 请商户联系客服确认付款情况。如果有新回包字段，会更新到此API文档中。
     * ◆ 因为错误代码字段err_code的值后续可能会增加，所以商户如果遇到回包返回新的错误码，请商户务必不要换单重试，请商户联系客服确认付款情况。如果有新的错误码，会更新到此API文档中。
     * ◆ 错误代码描述字段err_code_des只供人工定位问题时做参考，系统实现时请不要依赖这个字段来做自动化处理。
     * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
     *
     * @param serialNumber 转账订单号，唯一
     * @param openid       收款方openid
     * @param amount       转账金额（分）
     * @return true：成功；false：失败
     */
    WithdrawalResulrVO weiXinWithdrawal(String serialNumber, String openid, String amount);

    /**
     * 说明：单笔转账到支付宝账户
     * 文档地址：https://opendocs.alipay.com/open/02byuo
     *
     * @param serialNumber 商家侧唯一订单号
     * @param amount       订单总金额，单位为元，精确到小数点后两位
     * @param realName     收款方信息->真实姓名
     * @param identity     收款方信息->参与方的唯一标识
     * @param identityType 收款方信息->参与方的标识类型，目前支持如下类型：
     *                     1、ALIPAY_USER_ID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
     *                     2、ALIPAY_LOGON_ID：支付宝登录号，支持邮箱和手机号格式。
     */
    WithdrawalResulrVO zhiFuBaoWithdrawal(String serialNumber, String amount, String realName, String identity, String identityType);
}
