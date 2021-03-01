package com.jsy.community.qo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-11 09:50
 **/
@Data
@ApiModel("生活缴费接收参数对象")
public class LivingPaymentQO implements Serializable {
    @ApiModelProperty(value = "缴费类型,如水电气")
    @NotNull(groups = {LivingPaymentValidated.class},message = "缴费类型不能为空")
    private Long typeId;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @Length(groups = {LivingPaymentValidated.class},max = 12,min = 8,message = "请输入8~12数字")
    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "户名")
    @NotNull(groups = {LivingPaymentValidated.class},message = "缴费单位不能为空")
    private String familyName;

    @ApiModelProperty(value = "缴费单位ID")
    private Long companyId;

    @ApiModelProperty(value = "用户ID",hidden = true)
    private String userID;

    @ApiModelProperty(value = "付款方式，1微信支付，2支付宝支付，3账户余额，4其他银行卡")
    private Integer payTpye;

    @ApiModelProperty(value = "付款方式名称")
    private String payTypeName;

    @ApiModelProperty(value = "户主余额")
    private BigDecimal accountBalance;

    @ApiModelProperty(value = "缴费金额")
    @NotNull(groups = {LivingPaymentValidated.class},message = "缴费金额不能为空")
    private BigDecimal paymentBalance;

    @ApiModelProperty(value = "缴费地址")
    private String address;


    public interface LivingPaymentValidated{}


}
