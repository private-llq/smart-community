package com.jsy.community.qo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 获取凭据接参
 * @Date: 2021/5/7 17:43
 * @Version: 1.0
 **/
@Data
public class CredentialQO implements Serializable {

    // 跳转类型,跳转类型说明:1、个人钱包注册,2、修改支付密码,3、忘记支付密码,4、银行卡管理,5、个人钱包充值,6、个人钱包提现,7、钱包间转账,8、转账到银行卡,9、H5 收银台,10、PC企业注册开户,H5企业注册开户
    @NotBlank(message = "跳转类型不能为空,跳转类型说明:1、个人钱包注册,2、修改支付密码,3、忘记支付密码,4、银行卡管理,5、个人钱包充值,6、个人钱包提现,7、钱包间转账,8、转账到银行卡,9、H5 收银台,10、PC企业注册开户,H5企业注册开户")
    @Range(min = 1, max = 11, message = "跳转类型超出范围,跳转类型说明:1、个人钱包注册,2、修改支付密码,3、忘记支付密码,4、银行卡管理,5、个人钱包充值,6、个人钱包提现,7、钱包间转账,8、转账到银行卡,9、H5 收银台,10、PC企业注册开户,H5企业注册开户")
    private String jumpType;

    // 客户端IP
    private String clientIp;

    // 外部用户ID
    private String extUserId;

    // 钱包ID（非注册必填）
    private String walletId;

    // 跳转外部参数
    private String extData;

    // 商户前端(返回)跳转
    private String callbackUrl;

    // 注册号(企业用户首次注册时不需要携带，同用户第二次跳转需要携带此参数，否则会生成新的注册号)
    private String registerNo;

    // 异步通知地址(异步通知地址（暂企业注册使用）)
    private String notifyUrl;
}
