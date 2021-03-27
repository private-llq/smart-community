package com.jsy.community.entity.lease;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
    @ApiModelProperty(value = "预约状态 0已取消 1预约中 2预约成功 3预约已拒绝")
    private Integer reserveStatus;


    @ApiModelProperty(value = "预约信息内容")
    private String reserveMsg;

    @NotNull(groups = {Add.class}, message = "看房时间不能为空!")
    @ApiModelProperty(value = "看房时间")
    private Date checkingTime;

    @Range(groups = {Add.class}, min = 1, max = 4, message = "预计入住时间错误!预计入住时间,1.一周内;2.1-2周内;3.2-4周内;4.一个月之后")
    @NotNull(groups = {Add.class}, message = "预计入住时间不能为空")
    @ApiModelProperty(value = "预计入住时间,1.一周内;2.1-2周内;3.2-4周内;4.一个月之后")
    private Integer checkInTime;

    /**
     * 提交预约信息验证接口
     */
    public interface Add{}

}
