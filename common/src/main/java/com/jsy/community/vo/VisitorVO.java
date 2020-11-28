package com.jsy.community.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.entity.VisitorPersonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chq459799974
 * @description 访客VO
 * @since 2020-11-28 10:58
 **/
@Data
public class VisitorVO extends BaseVO {
	
	@ApiModelProperty(value = "来访人姓名")
	private String name;
	
	@ApiModelProperty(value = "来访人联系方式")
	@Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
	private String contact;
	
	@ApiModelProperty(value = "来访事由")
	private String reason;
	
	@ApiModelProperty(value = "预期来访时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expectTime;
	
	@ApiModelProperty(value = "是否授予来访人社区门禁权限，0无，1二维码通行证，2人脸识别")
	private Integer isCommunityAccess;
	
	@ApiModelProperty(value = "是否授予来访人楼栋门禁权限，0无，1二维码通行证，2可视对讲")
	private Integer isBuildingAccess;
	
}
