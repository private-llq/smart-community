package com.jsy.community.qo.payment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联支付下单接参
 * @Date: 2021/4/26 15:55
 * @Version: 1.0
 **/
@Data
@ApiModel("银联支付下单接参")
public class GenerateOrderQO implements Serializable {

    @ApiModelProperty("商户网站唯一订单号-必填")
    @NotBlank(message = "商户网站唯一订单号不能为空")
    private String outTradeNo;

    @ApiModelProperty("钱包ID-必填")
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    @ApiModelProperty("商品编号-选填")
    private String goodsId;

    @ApiModelProperty("商品名称-选填")
    private String goodsName;

    @ApiModelProperty("商品摘要-必填")
    @Length(max = 256, message = "商品摘要过长,最多为256个字符")
    @NotBlank(message = "商品摘要不能为空")
    private String subject;

    @ApiModelProperty("订单金额-必填")
    @NotBlank(message = "订单金额不能为空")
    private String orderAmt;

    @ApiModelProperty("商户钱包ID-必填")
    @NotBlank(message = "商户钱包ID不能为空")
    private String merWalletId;

    @ApiModelProperty("订单有效时间(m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点)-选填")
    private String timeoutExpress;

    @ApiModelProperty("商户号-选填(交易商户号，电子券支付必填)")
    private String merNo;

    @ApiModelProperty("商户名称-必填")
    @NotBlank(message = "商户名称不能为空")
    private String merName;

    @ApiModelProperty("前端回调url-选填")
    private String frontCallbackUrl;

    @ApiModelProperty("消息推送url-选填")
    private String notifyUrl;

    @ApiModelProperty("备注")
    private String remark;
}
