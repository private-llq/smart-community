package com.jsy.community.qo.lease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 房屋预约参数对象
 * YuLF
 * 数据传值对象：这个类主要用于对应数据库表t_house_reserve的数据字段的前端接收，
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋租售预约参数对象", description="主要用于接收前端请求参数")
public class HouseReserveQO implements Serializable {

    @Range(groups = {Cancel.class,Reject.class}, min = 0, message = "id范围错误!")
    @NotNull(groups = {Cancel.class,Reject.class}, message = "预约信息id不能为空!")
    @ApiModelProperty(value = "预约信息ID")
    private Long id;

    @ApiModelProperty(value = "预约所属人ID")
    private String reserveUid;

    @ApiModelProperty(value = "预约状态 0已取消 1预约中 2预约成功 3.预约已拒绝")
    private Integer reserveStatus;

    @ApiModelProperty(value = "预约信息内容")
    private String reserveMsg;


    /**
     * 取消预约信息参数验证接口
     */
    public interface Cancel {}

    /**
     * 拒绝预约信息参数验证接口
     */
    public interface Reject {}

}
