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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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
    
    @ApiModelProperty(value = "编号")
    @NotBlank(groups = {addHouseValidatedGroup.class,addRoomValidatedGroup.class}, message = "缺少编号")
    @Length(groups = {addHouseValidatedGroup.class,addRoomValidatedGroup.class,updateHouseValidatedGroup.class}, max = 20, message = "编号过长")
    private String number;
    
    @ApiModelProperty(value = "房间code",hidden = true)
    private String code;

    @ApiModelProperty(value = "社区ID")
    private Long communityId;
    
    @ApiModelProperty(value = "是否有电梯 0.无 1.有")
    private Integer hasElevator;
    
    @ApiModelProperty(value = "名称")
    @TableField(exist = false)
    @Length(groups = {addHouseValidatedGroup.class,updateHouseValidatedGroup.class}, max = 10, message = "名称过长")
    @NotBlank(groups = {addHouseValidatedGroup.class}, message = "缺少名称")
    private String name;
    
    @ApiModelProperty(value = "楼栋名",hidden = true)
//    @Length(groups = addHouseValidatedGroup.class, max = 10, message = "楼栋名称过长")
    private String building;

    @ApiModelProperty(value = "单元名",hidden = true)
//    @Length(groups = addHouseValidatedGroup.class, max = 10, message = "单元名称过长")
    private String unit;

    @ApiModelProperty(value = "楼层名",hidden = true)
    @Length(groups = {addRoomValidatedGroup.class,updateHouseValidatedGroup.class}, max = 20, message = "楼层名称过长")
    @NotBlank(groups = addRoomValidatedGroup.class, message = "缺少楼层名称")
    private String floor;

    @ApiModelProperty(value = "门牌名",hidden = true)
//    @Length(groups = addHouseValidatedGroup.class, max = 10, message = "门牌名称过长")
    private String door;

    @ApiModelProperty(value = "房屋id")
    @TableField(exist = false)
    private Long houseId;

    @ApiModelProperty(value = "父级id")
//    @NotNull(groups = {addHouseValidatedGroup.class}, message = "缺少父级ID")
    @NotNull(groups = {addRoomValidatedGroup.class}, message = "缺少父级ID")
    private Long pid;

    @ApiModelProperty(value = "1.楼栋 2.单元 3.楼层 4.门牌")
    @NotNull(groups = {addHouseValidatedGroup.class,addRoomValidatedGroup.class}, message = "缺少类型")
    @Range(groups = {addHouseValidatedGroup.class,addRoomValidatedGroup.class}, min = 1, max = 4, message = "楼宇类型错误")
    private Integer type;
    
    @ApiModelProperty(value = "建筑面积(㎡)")
    @NotNull(groups = {addRoomValidatedGroup.class}, message = "缺少建筑面积")
    private Double buildArea;
    
    @ApiModelProperty(value = "房屋类型1.商铺 2.住宅")
    @NotNull(groups = addRoomValidatedGroup.class, message = "缺少房屋类型")
    @Range(groups = {addRoomValidatedGroup.class,updateHouseValidatedGroup.class}, min = 1, max = 2 , message = "非法房屋类型")
    private Integer houseType;
    
    @ApiModelProperty(value = "房屋类型字符串")
    @TableField(exist = false)
    private String houseTypeStr;
    
    @ApiModelProperty(value = "房产类型1.商品房 2.房改房 3.集资房 4.经适房 5.廉租房 6.公租房 7.安置房 8.小产权房")
    // @NotNull(groups = addRoomValidatedGroup.class, message = "缺少房产类型")
    // @Range(groups = {addRoomValidatedGroup.class,updateHouseValidatedGroup.class}, min = 1, max = 8 , message = "非法房产类型")
    private Integer propertyType;
    
    @ApiModelProperty(value = "房产类型字符串")
    @TableField(exist = false)
    private String propertyTypeStr;
    
    @ApiModelProperty(value = "装修情况1.样板间 2.毛坯 3.简装 4.精装")
    // @NotNull(groups = addRoomValidatedGroup.class, message = "请填写装修情况")
    private Integer decoration;
    
    @ApiModelProperty(value = "装修情况字符串")
    @TableField(exist = false)
    private String decorationStr;
    
    @ApiModelProperty(value = "户型code：单间配套00000000  如01020304代表1室2厅3厨4卫")
    @NotBlank(groups = addRoomValidatedGroup.class, message = "缺少户型")
    private String houseTypeCode;
    
    @ApiModelProperty(value = "户型字符串")
    @TableField(exist = false)
    private String houseTypeCodeStr;

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
    
    @ApiModelProperty(value = "创建人")
    private String createBy;
    
    @ApiModelProperty(value = "最近更新人")
    private String updateBy;
    
    @ApiModelProperty(value = "总层数")
    private Integer totalFloor;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "单元ID列表")
    private List<Long> unitIdList;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "已绑定单元数")
    private Long bindUnitCount;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "业主姓名")
    private String owner;


    @TableField(exist = false)
    @ApiModelProperty(value = "业主uid")
    private String uid;

    @TableField(exist = false)
    @ApiModelProperty(value = "楼栋编号")
    private String buildingNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "单元编号")
    private String unitNumber;
    
    /**
     * 新增house验证组
     */
    public interface addHouseValidatedGroup{}
    
    /**
     * 新增房屋验证组
     */
    public interface addRoomValidatedGroup{}
    
    /**
     * 修改house验证组
     */
    public interface updateHouseValidatedGroup{}
}