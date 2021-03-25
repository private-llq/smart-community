package com.jsy.community.entity.hk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
	
	@ApiModelProperty(value = "创建人id")
	@NotNull(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "创建人id不能为空")
	private Long personId;
	
	@ApiModelProperty(value = "创建人")
	@NotBlank(groups = {FacilityEntity.addFacilityValidate.class}, message = "创建人不能为空")
	private String createPerson;
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "社区id不能为空")
	private Long communityId;
	
	@ApiModelProperty(value = "设备分类id")
	@NotNull(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "设备分类id不能为空")
	private Long facilityTypeId;
	
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
	@NotBlank(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, message = "端口号不能为空")
	private String port;
	
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
	@Length(groups = {FacilityEntity.addFacilityValidate.class, FacilityEntity.updateFacilityValidate.class}, min = 1, max = 100)
	private String remark;
	
	@ApiModelProperty(value = "数据同步时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dataConnectTime;
	
	public interface addFacilityValidate {
	}
	
	public interface updateFacilityValidate {
	}
	
}
