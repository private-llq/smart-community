package com.jsy.community.vo.property;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("根绝问卷id查询问卷相关数据（用户")
public class SelectQuestionnaireAllVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * uuid
     */
    private String uuid;

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

   private List<WProblemVO> wProblemVOList;



}
