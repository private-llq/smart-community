package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author qq459799974
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "HouseMember对象", description = "房间成员表")
@TableName("t_house_member")
public class HouseMemberEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社区ID")
    @NotNull(groups = {addHouseMemberValidatedGroup.class}, message = "缺少社区ID")
    private Long communityId;

    @ApiModelProperty(value = "房间ID")
    @NotNull(groups = {addHouseMemberValidatedGroup.class}, message = "缺少房间ID")
    private Long houseId;

    @ApiModelProperty(value = "房主ID")
    @NotNull(groups = {updateHouseMemberValidatedGroup.class}, message = "缺少社区ID")
    private String householderId;

    @ApiModelProperty(value = "家属名称")
    @NotNull(groups = {addHouseMemberValidatedGroup.class}, message = "缺少家属名称")
    private String name;
    @ApiModelProperty(value = "家属性别")
    private Integer sex;
    @ApiModelProperty(value = "家属电话")
    private String mobile;
    @ApiModelProperty(value = "家属身份证号码")
    @NotNull(groups = {addHouseMemberValidatedGroup.class}, message = "缺少家属身份证号码")
    private String idCard;
    @ApiModelProperty(value = "与业主关系 1.夫妻 2.父子 3.母子 4.父女 5.母女 6.亲属")
    private Integer relation;


    /**
     * 新增访客验证组
     */
    public interface addHouseMemberValidatedGroup {
    }

    /**
     * 修改访客验证组
     */
    public interface updateHouseMemberValidatedGroup {
    }

}
