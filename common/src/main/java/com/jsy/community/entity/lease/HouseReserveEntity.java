package com.jsy.community.entity.lease;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author YuLF
 * @since  2021/1/13 17:49
 * 房屋预约实体对象
 * 数据访问对象：这个类主要用于对应数据库表t_house_reserve的数据字段的映射关系，
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋租售预约对象", description="房屋租售预约数据字段实体")
@TableName("t_house_reserve")
public class HouseReserveEntity extends BaseEntity {

    @ApiModelProperty(value = "预约所属人ID")
    private String reserveUid;

    @Range(groups = {Add.class}, min = 0, message = "房屋id范围错误!")
    @NotNull(groups = {Add.class}, message = "预约房屋信息不能为空!")
    @ApiModelProperty(value = "预约出租房屋id")
    private Long houseLeaseId;

    @Range(groups = {Add.class}, min = 0, message = "社区Id范围错误!")
    @NotNull(groups = {Add.class}, message = "社区id不能为空!")
    @ApiModelProperty(value = "社区id")
    private Long communityId;

    @Range(groups = {Add.class}, min = 0, max = 2, message = "预约范围错误! 0已取消 1预约中 2预约成功")
    @ApiModelProperty(value = "预约状态 0已取消 1预约中 2预约成功")
    private Integer reserveStatus;


    @ApiModelProperty(value = "预约信息内容")
    private String reserveMsg;

    @Length(groups = {Add.class}, max =  12, message = "预约日期长度错误!")
    @NotBlank(groups = {Add.class}, message = "预约日期不能为空!")
    @ApiModelProperty(value = "预约日期")
    private String reserveDate;


    @Length(groups = {Add.class},  max =  12, message = "预约时间长度错误!")
    @NotBlank(groups = {Add.class}, message = "预约时间不能为空!")
    @ApiModelProperty(value = "预约时间")
    private String reserveTime;

    /**
     * 提交预约信息验证接口
     */
    public interface Add{}

}
