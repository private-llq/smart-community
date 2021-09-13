package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 支付宝支付配置
 * @author: DKS
 * @create: 2021-09-09 09:39
 **/
@Data
@TableName("t_pay_configure")
public class PayConfigureEntity extends BaseEntity {
	@ApiModelProperty(value = "物业id")
	private Long companyId;
	
	@ApiModelProperty(value = "支付宝appId")
	private String appId;
	
	@ApiModelProperty(value = "支付宝身份秘钥")
	private String privateKey;
	
	@ApiModelProperty(value = "应用公钥证书路径")
	private String certPath;
	
	@ApiModelProperty(value = "支付宝公钥证书路径")
	private String alipayPublicCertPath;
	
	@ApiModelProperty(value = "支付宝根证书路径")
	private String rootCertPath;
	
	@ApiModelProperty(value = "商户ID")
	private String sellerId;
	
	@ApiModelProperty(value = "商户账号(邮箱)")
	private String sellerEmail;
	
	@ApiModelProperty(value = "商户账号(PID)")
	private String sellerPid;
	
	@ApiModelProperty(value = "是否允许退款，默认为1 1允许退款，2不允许退款")
	private Integer refundStatus;
	
	@ApiModelProperty(value = "应用公钥证书路径上传状态,0表示未上传，1表示已上传")
	@TableField(exist = false)
	private Integer certPathStatus;
	
	@ApiModelProperty(value = "支付宝公钥证书路径上传状态,0表示未上传，1表示已上传")
	@TableField(exist = false)
	private Integer alipayPublicCertPathStatus;
	
	@ApiModelProperty(value = "支付宝根证书路径上传状态,0表示未上传，1表示已上传")
	@TableField(exist = false)
	private Integer rootCertPathStatus;
}
