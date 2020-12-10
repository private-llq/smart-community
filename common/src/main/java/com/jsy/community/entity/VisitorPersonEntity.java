package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author qq459799974
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="随行人员", description="随行人员")
@TableName("t_visitor_person")
public class VisitorPersonEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    @ApiModelProperty(value = "随行人员姓名")
    @NotBlank(groups = {addPersonValidatedGroup.class,updatePersonValidatedGroup.class}, message = "缺少随行人员姓名")
    private String name;
    
    @ApiModelProperty(value = "随行人员手机号")
    @Pattern(groups = {addPersonValidatedGroup.class,updatePersonValidatedGroup.class}, regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    @NotBlank(groups = {addPersonValidatedGroup.class,updatePersonValidatedGroup.class}, message = "缺少随行人员手机号")
    private String mobile;
    
    /**
     * 添加随行人员前端参数验证接口
     */
    public interface addPersonValidatedGroup{}
    
    /**
     * 修改随行人员前端参数验证接口
     */
    public interface updatePersonValidatedGroup{}

}
