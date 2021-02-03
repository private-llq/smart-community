package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author qq459799974
 * @since 2020-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="House对象", description="社区楼栋")
@TableName("t_house")
public class HouseEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "房间code",hidden = true)
    private String code;

    @ApiModelProperty(value = "社区ID")
    @NotNull(groups = {addHouseValidatedGroup.class}, message = "缺少社区ID")
    private Long communityId;

    @ApiModelProperty(value = "楼栋名")
    @Length(groups = addHouseValidatedGroup.class, max = 10, message = "楼栋名称过长")
    private String building;

    @ApiModelProperty(value = "单元名")
    @Length(groups = addHouseValidatedGroup.class, max = 10, message = "单元名称过长")
    private String unit;

    @ApiModelProperty(value = "楼层名")
    @Length(groups = addHouseValidatedGroup.class, max = 10, message = "楼层名称过长")
    private String floor;

    @ApiModelProperty(value = "门牌名")
    @Length(groups = addHouseValidatedGroup.class, max = 10, message = "门牌名称过长")
    private String door;

    @ApiModelProperty(value = "房屋id")
    @TableField(exist = false)
    private Long houseId;

    @ApiModelProperty(value = "父级id")
    @NotNull(groups = {addHouseValidatedGroup.class}, message = "缺少父级ID")
    private Long pid;

    @ApiModelProperty(value = "1.楼栋 2.单元 3.楼层 4.门牌")
    @NotNull(groups = {addHouseValidatedGroup.class,updateHouseValidatedGroup.class}, message = "缺少类型")
    @Range(groups = {addHouseValidatedGroup.class,updateHouseValidatedGroup.class}, min = 1, max = 4, message = "楼宇类型错误")
    private Integer type;

    @ApiModelProperty(value = "备注")
    private String comment;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "社区名称", hidden = true)
    private String communityName;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "房间地址拼接", hidden = true)
    private String address;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "顶级父节点id(楼栋/单元之类)", hidden = true)
    private Long buildingId;
    
    /**
     * 新增house验证组
     */
    public interface addHouseValidatedGroup{}
    
    /**
     * 新增house验证组
     */
    public interface updateHouseValidatedGroup{}

}
