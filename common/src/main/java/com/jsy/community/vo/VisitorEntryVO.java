package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 访客登记返回VO(前端生成二维码)
 * @since 2020-12-11 09:15
 **/
@Data
@ApiModel("访客门禁权限VO")
public class VisitorEntryVO implements Serializable {
	
	@ApiModelProperty("id")
	private Long id;
	
	@ApiModelProperty("业主id")
	private String uid;
	
	@ApiModelProperty("开门密码")
	private String password;
	
	@ApiModelProperty("权限有效时间(秒)")
	private Long timeLimit;
	
	@ApiModelProperty(value = "社区门禁验证方式，0无，1二维码通行证，2人脸识别")
	private Integer isCommunityAccess;
	
	@ApiModelProperty(value = "楼栋门禁验证方式，0无，1二维码通行证，2可视对讲")
	private Integer isBuildingAccess;
	
	@ApiModelProperty("token")
	private String token;
	
}
