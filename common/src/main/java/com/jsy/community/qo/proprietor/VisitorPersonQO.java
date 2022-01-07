package com.jsy.community.qo.proprietor;

import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author chq459799974
 * @description 随行人员
 * @since 2020-11-28 16:22
 **/
@Data
public class VisitorPersonQO extends BaseEntity implements Serializable {
	
	@ApiModelProperty(value = "随行人姓名")
	@NotBlank(message = "缺少随行人员姓名")
	private String name;
	
	@ApiModelProperty(value = "随行人手机号")
	@Pattern(regexp = "^1[3|4|5|6|7|8|9][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
	private String mobile;
}
