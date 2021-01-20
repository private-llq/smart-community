package com.jsy.community.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 红包(单发/转账、群发)
 * @since 2021-01-15 17:15
 **/
@Data
public class RedbagQO implements Serializable {
	private String data;
	
	@ApiModelProperty(value = "发送人ID")
	private String userUuid;
	
	@ApiModelProperty(value = "接收人ID")
	@NotBlank(groups = {singleRedbagValidated.class,receiveSingleValidated.class,receiveGroupValidated.class} ,message = "缺少接收人ID")
	private String receiveUserUuid;
	
	@ApiModelProperty(value = "红包UUID", hidden = true)
	@NotBlank(groups = {receiveSingleValidated.class,receiveGroupValidated.class}, message = "缺少红包UUID")
	private String uuid;
	
	@ApiModelProperty(value = "红包名称")
	private String name;
	
	@ApiModelProperty(value = "币种", hidden = true)
	private Integer type;
	
	@ApiModelProperty(value = "总金额")
	@NotNull(groups = {singleRedbagValidated.class, groupRedbagValidated.class}, message = "缺少总金额")
	private BigDecimal money;
	
	//===================================群发增加参数==============================================
	@ApiModelProperty(value = "红包个数")
	@NotNull(groups = {groupRedbagValidated.class}, message = "缺少红包个数")
	private Integer number;
	
	@ApiModelProperty(value = "群id")
	@NotBlank(groups = {groupRedbagValidated.class}, message = "缺少群ID")
	private String groupUuid;
	
	@ApiModelProperty(value = "红包类型 1.私包 2.群红包", hidden = true)
	private Integer redbagType;
	
	@ApiModelProperty(value = "来源类型 1.个人 2.官方", hidden = true)
	private Integer fromType;
	
	/**
	 * 单发红包参数验证
	 */
	public interface singleRedbagValidated{}
	
	/**
	 * 群发红包参数验证
	 */
	public interface groupRedbagValidated{}
	
	/**
	 * 私包领取参数验证
	 */
	public interface receiveSingleValidated{}
	
	/**
	 * 群红包领取参数验证
	 */
	public interface receiveGroupValidated{}
}
