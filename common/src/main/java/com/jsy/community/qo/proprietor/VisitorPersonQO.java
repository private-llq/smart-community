package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author chq459799974
 * @description 随行人员
 * @since 2020-11-28 16:22
 **/
@Data
public class VisitorPersonQO {
	
	@ApiModelProperty(value = "随行人姓名")
	private String name;
	
	@ApiModelProperty(value = "随行人手机号")
	@Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
	private String mobile;
}