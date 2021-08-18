package com.jsy.community.vo.property;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
@Data
@ApiModel("分页查询车位信息")
public class SelectCarPositionPagingVO {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属小区
     */
    private Long communityId;

    /**
     * 所属业主
     */
    private String uid;

    /**
     * 关联车位类型id
     */
    private Long typeId;

    /**
     * 车位号
     */
    private String carPosition;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;



    /**
     * 车位状态（0空置，1业主自用，2租赁）
     */
    private Integer carPosStatus;

    /**
     * 产权面积
     */
    private Double area;

    /**
     * 所属房屋
     */
    private String belongHouse;

    /**
     * 业主电话
     */
    private String ownerPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 绑定状态（0没有绑定1,绑定）
     */
    private Integer bindingStatus;

    /**
     * 起始时间
     */
    private Date beginTime;

    /**
     * 到期时间
     */
    private Date endTime;
}
