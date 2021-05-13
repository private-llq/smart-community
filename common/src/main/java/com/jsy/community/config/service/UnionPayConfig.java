package com.jsy.community.config.service;

/**
 * @Author: Pipi
 * @Description: 银联支付配置
 * @Date: 2021/4/8 9:36
 * @Version: 1.0
 **/
public class UnionPayConfig {
    // 生产地址
    public final static String PRODUCT_REQUEST_URL = "https://api.gnete.com/routejson";
    // 测试地址
    public final static String TEST_REQUEST_URL = "https://testapi.gnete.com:9083/routejson";
    // 支付回调地址
    public final static String NOTIFY_URL = "https://ymxd.cn.utools.club/api/v1/payment/unionPay/unionPayNotifyUrl";
    // 凭据回调地址
    public final static String CREDENTIAL_NOTIFY_URL = "https://ymxd.cn.utools.club/api/v1/payment/unionPay/credentialNotifyUrl";
    // 银联支付appid
    public final static String APP_ID = "5ed70f9c2c07873f0aa9e455560bc553";
    // 用于测试的商户钱包id
    public final static String MER_WALLET_ID = "2061521600215120369";
    // 用于测试的商户名称
    public final static String MER_NAME = "腾运测试";
    // 银联回调验签公钥
    public final static String VERIFY_PUBLIC_KEY = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100c15b0bb2626606e1c9514e2fe3f67965464459b9373f3e1b1ee3dcedd8e3670a7aff82afb63417a672ab2d5946122eb46ffbfde4ed2732f2dbb3ee3a0f8d9c764c306dfad4306934fd15629332c3645d33a305a8ccd95fed002aa3811170515f39f1ac5068a111159c1a4df5d0d3b4f4261ee7d27e9c38437ba17241a5aa863c11dc6a8f4ef2792c4dbb586c51811892edf1c864b5545f32bad2598778e8d0ad910b659687a057751b8a23b70e33bb4f98d95cab9950bd7b8de815b07da63b9adc5f65503574461d23ca49231cee206b4ae6fa2d129a86889aa3771d0668c56c2c04ba4dd916211e746537245af239d151dd284734702ea626f52f5fbd0a934f0203010001";
    // 银联支付公钥
    public final static String PUBLIC_KEY = "2048-cfca.der";
    // 银联支付私钥
    public final static String PRIVATE_KEY = "A0010000-0014.pfx";
    public final static String PRIVATE_KEY_PASS = "123456";
    // 交易请求方法头
    public final static String TRANS_METHOD_HEADER = "gnete.wallbc.WallbcOpenapi102TransRpcService";
    // 查询请求方法头
    public final static String QUERY_METHOD_HEADER = "gnete.wallbc.WallbcOpenapi102QueryRpcService";
    // 红包请求方法头
    public final static String RED_PACKET_METHOD_HEADER = "gnete.wallbc.WallbcOpenapi102RedPacketRpcService";
    // 凭据请求方法头
    public final static String CREDENTIALS_METHOD_HEADER = "gnete.wextbc.WextbcTradeRpcService";
    // 查询注册登记信息请求方法头
    public final static String EEEPMS_REG_METHOD_HEADER = "gnete.wextbc.EeepmsRegQueryRPCRpcService";
    // 外部用户查询请求方法头
    public final static String WEXTBC_QUERY_METHOD_HEADER = "gnete.wextbc.WextbcQueryRpcService";

    // 4.1.1.开户（C端）（报文编码2001）
    public final static String OPEN_ACCOUNT_FOR_C = "openAcct";
    // 4.7.2.开B端账户申请（报文编码2003）
    public final static String OPEN_BTYPE_ACCT_REG = "openBtypeAcctReg";
    // 4.1.3.1.账户绑定/解绑/设置默认银行卡（报文编码2019）
    public final static String SET_BANK_METHOD = "acctBindBankCard";
    // 获取控件随机因子（报文编码2046）
    public final static String CONTROL_RANDOM_FACTOR = "getPlugRandomKey";
    // 4.7.17.发送短信验证码（报文编码2020）
    public final static String SEND_SMS_AUTH_CODE = "sendSmsAuthCode";
    // 修改用户手机号（报文编码2033）
    public final static String MODIFY_USER_MOBILE = "modifyUserMobile";
    // 查询账户信息（报文编码1002）
    public final static String QUERY_ACCT_INFO = "queryAcctInfo";
    // 查询账户关联信息（报文编码1003）(只允许查询C端)
    public final static String QUERY_ACCT_RELATED_INFO = "queryAcctRelatedInfo";
    // 查询账户绑定银行卡
    public final static String QUERY_BIND_BANK_CARD = "queryBindBankCard";
    // 4.7.12.修改密码（报文编码2016）
    public final static String MODIFY_PWD = "modifyPwd";
    // 4.4.2.消费类下单接口（报文编码2056）
    public final static String CONSUME_APPLY_ORDER = "consumeApplyOrder";
    // 4.1.6.查询账户余额（报文编码1004）
    public final static String QUERY_ACCT_BAL = "queryAcctBal";
    // 获取凭据
    public final static String APPLY_TICKET = "applyTicket";
    // 查询注册登记信息
    public final static String QUERY_REG_INFO = "queryRegInfo";
    // 外部用户查询钱包id
    public final static String QUERY_WALLET_ID  = "queryWalletId";
    // 验证凭据
    public final static String VALIDATE_TICKET = "validateTicket";
    // 查询开B端开户情况(1032)
    public final static String QUERY_WALLET_BY_BIZ_LIC_NO = "queryWalletByBizLicNo";
    // 提现（报文编码2007）
    public final static String WITHDRAW = "withdraw";
    // 4.7.33.B端钱包重置支付密码（报文编码2045）
    public final static String RESET_BTYPE_ACCT_PWD = "resetBtypeAcctPwd";
    // 查询交易明细（报文编码1006）
    public final static String QUERY_TRANS_LIST = "queryTransList";
    // 4.1.10.账单查询（报文编码1013）
    public final static String QUERY_BILL_INFO = "queryBillInfo";
    // 4.7.1.激活账户（报文编码2002）
    public final static String ACTIVE_ACCT = "activeAcct";


    // 加密方式
    // SHA1_WITH_RSA
    public final static String SHA1_WITH_RSA = "0";
    // SHA256_WITH_RSA
    public final static String SHA256_WITH_RSA = "1";
    // SM3_WITH_SM2--暂不支持
    public final static String SM3_WITH_SM2 = "2";
    // HMAC_MD5--暂不支持
    public final static String HMAC_MD5 = "3";
    // HMAC_SHA256--暂不支持
    public final static String HMAC_SHA256 = "4";
    // MD5
    public final static String ENCRYPT_MD5 = "5";
}
