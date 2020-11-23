package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 房间成员表
 * </p>
 *
 * @author jsy
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HouseMember对象", description="房间成员表")
@TableName("t_house_member")
public class HouseMemberEntity extends  BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "人员ID")
    @NotNull(groups = {addHouseMemberValidatedGroup.class}, message = "缺少人员ID")
    private Long uid;

    @ApiModelProperty(value = "社区ID")
    @NotNull(groups = {addHouseMemberValidatedGroup.class}, message = "缺少社区ID")
    private Long communityId;

    @ApiModelProperty(value = "房间ID")
    @NotNull(groups = {addHouseMemberValidatedGroup.class}, message = "缺少房间ID")
    private Long houseId;

    @ApiModelProperty(value = "被操作人有无确认 0.否 1.是")
    private Integer isConfirm;
    
    @JsonIgnore
    @ApiModelProperty(value = "操作人ID")
    private Long createBy;
    
    /**
     * 新增访客验证组
     */
    public interface addHouseMemberValidatedGroup{}
    
    /**
     * 修改访客验证组
     */
    public interface updateHouseMemberValidatedGroup{}

}
