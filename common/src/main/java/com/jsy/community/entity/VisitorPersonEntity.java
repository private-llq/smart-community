package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author qq459799974
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="VisitorPerson对象", description="访客随行人员")
@TableName("t_visitor_person")
public class VisitorPersonEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "访客记录ID")
    private Long visitorId;

    @ApiModelProperty(value = "随行人名")
    private String name;
    
    @JsonIgnore
    @ApiModelProperty(value = "随行人身份证")
    private String idCard;

    @ApiModelProperty(value = "随行人手机号")
    @Pattern(groups = {addPersonValidatedGroup.class,updatePersonValidatedGroup.class}, regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    private String mobile;
    
    /**
     * 添加随行车辆前端参数验证接口
     */
    public interface addPersonValidatedGroup{}
    
    /**
     * 修改随行车辆前端参数验证接口
     */
    public interface updatePersonValidatedGroup{}

}
