package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
    
    @ApiModelProperty(value = "保修状态字符串形式")
    @TableField(exist = false)
    private String statusString;

    @ApiModelProperty(value = "报修类别id")
    @NotNull(groups = {addRepairValidate.class},message = "报修类别不能为空")
    private Long type;
    
    @ApiModelProperty(value = "报修类别字符串形式")
    @TableField(exist = false)
    @NotNull(groups = {addRepairValidate.class},message = "报修类别不能为空")
    private String typeName;

    @ApiModelProperty(value = "报修人姓名")
    @NotBlank(groups = {addRepairValidate.class},message = "请输入报修人姓名")
    private String name;

    @ApiModelProperty(value = "联系电话")
    @NotBlank(groups = {addRepairValidate.class},message = "请输入联系电话")
    @Pattern(groups = {addRepairValidate.class},regexp = RegexUtils.REGEX_MOBILE,message = "您输入的联系电话不符合要求")
    private String phone;

    @ApiModelProperty(value = "报修内容")
    @NotBlank(groups = {addRepairValidate.class},message = "请描述您的报修内容")
    @Size(groups = {addRepairValidate.class},max = 200,message = "报修内容只允许200字以内")
    private String problem;

    @ApiModelProperty(value = "图片地址")
    @NotBlank(groups = {addRepairValidate.class},message = "请添加图片,以供物业人员审核")
    @Size(groups = {addRepairValidate.class},max = 500,message = "您的图片文件名过长或您上传的文件太多")
    private String repairImg;
    
    
    
    public interface addRepairValidate {
    }
    
}
