package com.jsy.community.vo.property;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("查询问卷的返回对象")
public class SelectQuestionnaireVO implements Serializable {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;



    /**
     * 标题
     */
    private String title;

    /**
     * 社区id
     */
    private Long communityId;

    /**
     * 说明
     */
    private String explains;

    /**
     * 统计总数量
     */
    private Integer statisticalNum;
    /**
     * 已经统计调查的数量
     */
    private Integer statisticalNumED;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 开启时间
     */
    private LocalDateTime opening;
    /**
     * 楼栋id集合用;隔开
     */
    private String buildings;
    /**
     * 问卷范围（0全部，1部分楼宇）
     */
    private Integer ranges;


}
