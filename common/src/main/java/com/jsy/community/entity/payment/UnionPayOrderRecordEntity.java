package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: 银联支付订单实体
 * @Date: 2021/4/26 15:47
 * @Version: 1.0
 **/
@Data
@ApiModel("银联支付订单实体")
@TableName("t_union_pay_order_record")
public class UnionPayOrderRecordEntity extends BaseEntity {

    @ApiModelProperty("内部订单号(其他服务订单号)")
    @NotBlank(groups = {GenerateOrderValidate.class}, message = "订单号不能为空")
    private String serviceOrderNo;

    @ApiModelProperty("电子钱包唯一流水号。标识唯一一笔交易")
    private String transOrderNo;

    @ApiModelProperty("商品摘要")
    @NotBlank(groups = {GenerateOrderValidate.class}, message = "商品摘要不能为空")
    private String subject;

    @ApiModelProperty("订单金额")
    @NotNull(groups = {GenerateOrderValidate.class}, message = "订单金额不能为空")
    private BigDecimal orderAmt;

    @ApiModelProperty("折后金额")
    private BigDecimal transAmt;

    @ApiModelProperty("银联用户电子钱包ID")
    private String walletId;

    @ApiModelProperty("银联商户钱包ID")
    private String merWalletId;

    @ApiModelProperty("银联商户名称")
    private String merName;

    @ApiModelProperty("交易名称 1.充值提现2.商城购物3.水电缴费4.物业管理5.房屋租金6.红包7.红包退回")
    @Range(groups = {GenerateOrderValidate.class}, min = 1, max = 7, message = "交易类型超出范围,交易名称 1.充值提现2.商城购物3.水电缴费4.物业管理5.房屋租金6.红包7.红包退回")
    @NotNull(groups = {GenerateOrderValidate.class}, message = "交易类型不能为空")
    private Integer tradeName;

    @ApiModelProperty("交易状态 1.待支付 2.支付完成")
    private Integer tradeStatus;

    @ApiModelProperty("系统类型 1.安卓 2.IOS,3:PC")
    private Integer sysType;

    @ApiModelProperty("商户订单号,下单接口返回值之一")
    private String mct_order_no;

    @ApiModelProperty("消费类支付（H5）url,下单接口返回值之一")
    private String pay_h5_url;

    @ApiModelProperty("支付类型：00：钱包余额支付；01：银行卡支付")
    private String payType;

    @ApiModelProperty("用户id")
    @TableField(exist = false)
    private String uid;

    public interface GenerateOrderValidate {}

}
