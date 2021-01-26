package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: chq459799974
 * @Date: 2020/12/10
**/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="随行人员记录", description="随行人员记录")
@TableName("t_visitor_person_record")
public class VisitorPersonRecordEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //访客记录ID
    private Long visitorId;

    @ApiModelProperty(value = "随行人员姓名")
    @NotBlank(message = "缺少随行人员姓名")
    private String name;
    
    @ApiModelProperty(value = "随行人员手机号")
    @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    private String mobile;
    
}
