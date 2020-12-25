package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 房屋报修
 * </p>
 *
 * @author lihao
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_repair")
@ApiModel(value="Repair对象", description="房屋报修")
public class RepairEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "业主id")
    @NotBlank(groups = {addRepairValidate.class},message = "业主id不能为空")
    private String userId;
    
    @ApiModelProperty(value = "社区id")
    @NotNull(groups = {addRepairValidate.class},message = "社区不能为空")
    private Long communityId;

    @ApiModelProperty(value = "报修地址")
    @NotBlank(groups = {addRepairValidate.class},message = "报修地址不能为空")
    private String address;

    @ApiModelProperty(value = "报修状态 0 待处理 1 处理中 2 已处理")
    private Integer status;

    @ApiModelProperty(value = "报修类别 0 抹灰 1 防水 2 墙面 3 门窗 4 排水")
    private Integer type;

    @ApiModelProperty(value = "报修人姓名")
    @NotBlank(groups = {addRepairValidate.class},message = "请输入报修人姓名")
    private String name;

    @ApiModelProperty(value = "联系电话")
    @NotBlank(groups = {addRepairValidate.class},message = "请输入联系电话")
    private String phone;

    @ApiModelProperty(value = "报修内容")
    @NotBlank(groups = {addRepairValidate.class},message = "请输入报修内容")
    private String problem;

    @ApiModelProperty(value = "图片地址")
    @NotBlank(groups = {addRepairValidate.class},message = "请添加图片")
    private String repairImg;
    
    
    
    public interface addRepairValidate {
    }
    
    public interface updateRepairValidate {
    }

}
