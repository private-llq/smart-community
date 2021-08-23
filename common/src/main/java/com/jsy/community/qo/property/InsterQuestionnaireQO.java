package com.jsy.community.qo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;


@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("创建调查问卷")
@NoArgsConstructor
@AllArgsConstructor
public class InsterQuestionnaireQO implements Serializable {



    private static final long serialVersionUID = 1L;
    /**
     * 标题
     */
    private String title;
    /**
     * 说明
     */
    private String explains;
    /**
     * 统计总数量
     */
    private Integer statisticalNum;
    /**
     * 截止时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
    /**
     * 开启时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime opening;

    /**
     * 楼栋id集合用;隔开
     */
    private String buildings;

    /**
     * 问卷范围（0全部，1部分楼宇）
     */
    private Integer ranges;

    /**
     * 调查问卷连带问题的选项集合
     */
    private ArrayList<WProblemQO> problemList;

}
