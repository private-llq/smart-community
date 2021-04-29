package com.jsy.community.entity.hk;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author lihao
 * @since 2021-03-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_facility")
@ApiModel(value = "Facility对象", description = "设备信息")
public class FacilityEntity extends BaseEntity {
	
	@ApiModelProperty(value = "设备作用id")
	// TODO: 2021/4/27 目前更新设备的时候不让其可以更改设备作用  ***************************************************************************************
//	@NotNull(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "设备作用id不能为空")
	@NotNull(groups = {FacilityEntity.addFacilityValidate.class,}, message = "设备作用id不能为空")
	private Long facilityEffectId;
	
	@ApiModelProperty(value = "设备在线状态")
	@TableField(exist = false)
	// TODO: 2021/4/27 这个地方暂时不让其可以修改 ***************************************************************************************
	@Range(groups = {updateFacilityValidate.class}, min = 0, max = 1, message = "请选择正确的设备在线状态")
	private Integer status;
	
	@ApiModelProperty(value = "创建人id")
	private String personId;
	
	@ApiModelProperty(value = "创建人")
	private String createPerson;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "设备分类id")
	@NotNull(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "设备分类id不能为空")
	private Long facilityTypeId;
	
	@ApiModelProperty(value = "是否已同步数据 0 未同步 1 已同步 ")
	private Integer isConnectData;
	
	@ApiModelProperty(value = "设备分类")
	@NotNull(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "设备分类id不能为空")
	private String facilityTypeName;
	
	@ApiModelProperty(value = "设备编号")
	@NotBlank(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "编号不能为空")
	@Length(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, min = 1, max = 20)
	private String number;
	
	@ApiModelProperty(value = "设备名称")
	@NotBlank(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "设备名称不能为空")
	@Length(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, min = 1, max = 20)
	private String name;
	
	@ApiModelProperty(value = "序列号")
	@NotBlank(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "序列号不能为空")
	@Length(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, min = 1, max = 20)
	private String serialNumber;
	
	@ApiModelProperty(value = "ip地址")
	@NotBlank(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "IP地址不能为空")
	@Length(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, min = 1, max = 20)
	private String ip;
	
	@ApiModelProperty(value = "端口号")
	@NotNull(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "端口号不能为空")
	private Short port;
	
	@ApiModelProperty(value = "设备账号")
	private String username;
	
	@ApiModelProperty(value = "设备密码")
	private String password;
	
	@ApiModelProperty(value = "设备型号")
	@Length(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, min = 0, max = 20)
	private String modelNumber;
	
	@ApiModelProperty(value = "设备所在地址")
	private String address;
	
	@ApiModelProperty(value = "备注")
	@Length(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, min = 0, max = 100)
	private String remark;
	
	@ApiModelProperty(value = "数据同步时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime dataConnectTime;
	
	public interface addFacilityValidate {
	}
	
	public interface updateFacilityValidate {
	}
	
}
