package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 访客随行人员
 * </p>
 *
 * @author jsy
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="VisitorPerson对象", description="访客随行人员")
@TableName("t_visitor_person")
public class VisitorPerson extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "访客记录ID")
    private Long visitorId;

    @ApiModelProperty(value = "随行人名")
    private String name;

    @ApiModelProperty(value = "随行人身份证")
    private String idCard;

    @ApiModelProperty(value = "随行人手机号")
    private String mobile;

}
