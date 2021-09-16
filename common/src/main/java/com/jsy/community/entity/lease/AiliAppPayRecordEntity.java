package com.jsy.community.entity.lease;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Description: 支付宝流水实体类
 * @Author: chq459799974
 * @Date: 2021/1/8
**/
@Data
@TableName("t_alipay_record")
public class AiliAppPayRecordEntity extends BaseEntity {
	
	@TableField(exist = false)
	private Long id;
	
	@ApiModelProperty(value = "系统订单号")
	private String orderNo;
	
	@ApiModelProperty(value = "支付宝渠道单号")
	private String tradeNo;
	
	@ApiModelProperty(value = "用户ID")
	private String userid;
	
	@ApiModelProperty(value = "用户姓名")
	@TableField(exist = false)
	private String realName;
	
	@ApiModelProperty(value = "用户手机号")
	private String phonenumber;
	
	@ApiModelProperty(value = "交易金额(RMB)")
	private BigDecimal tradeAmount;
	
	@ApiModelProperty(value = "交易名称 1.充值提现2.商城购物3.水电缴费4.物业管理5.房屋租金6.红包7.红包退回")
	private Integer tradeName;
	
	@ApiModelProperty(value = "交易类型 1.充值 2.提现")
	private Integer tradeType;
	
	@ApiModelProperty(value = "交易状态 1.已下单")
	private Integer tradeStatus;
	
	@ApiModelProperty(value = "系统类型 1.安卓 2.IOS")
	private Integer sysType;
	
	@ApiModelProperty(value = "其他服务订单号 如 商城")
	private String serviceOrderNo;

	@ApiModelProperty(value = "物业公司ID")
	private String companyId;
	
}
