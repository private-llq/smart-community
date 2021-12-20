package com.jsy.community.qo.lease;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
* @Description: 支付宝接参
 * @Author: chq459799974
 * @Date: 2021/1/8
**/
@Data
public class AliAppPayQO implements Serializable {
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {addOrderGroup.class}, message = "社区ID不能为空")
	private Long communityId;
	
	@ApiModelProperty(value = "系统订单号",hidden = true)
	private String outTradeNo;
	
	@ApiModelProperty(value = "条目")
	private String subject;
	
	@ApiModelProperty(value = "交易金额(RMB)")
//	@NotNull(groups = addOrderGroup.class, message = "缺少交易金额")
	private BigDecimal totalAmount;
	
	@ApiModelProperty(value = "支付类型 1.APP 2.H5")
	private int payType;
	
	@ApiModelProperty(value = "交易来源 1.充值提现2.商城购物3.水电缴费4.物业管理5.房屋租金6.红包7.红包退回.8停车缴费.9房屋租赁.10临时车辆缴费")
	@NotNull(groups = addOrderGroup.class, message = "缺少交易来源")
	@Range(min = 1, max = 7, message = "缴费类型错误")
	private Integer tradeFrom;
	
	@ApiModelProperty(value = "订单详情")
	private Map<String,Object> orderData;
	
	@ApiModelProperty(value = "其他服务订单号 如 商城")
	@NotBlank(groups = addOrderGroup.class, message = "服务订单号不能为空")
	private String serviceOrderNo;
	
	@ApiModelProperty(value = "物业缴费账单数据id，逗号分隔")
	private String ids;

	/**
	 * 收款方id
	 */
	@NotBlank(groups = BalanceInvolvedGroup.class, message = "收款方id不能为空")
	private Long receiveUid;
	
	/**
	 * 添加订单验证组
	 */
	public interface addOrderGroup{}

	/**
	 * 涉及余额的订单验证组
	 */
	public interface BalanceInvolvedGroup{}
}
