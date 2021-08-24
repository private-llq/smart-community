package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 业主投票
 * @author: Hu
 * @create: 2021-08-23 16:39
 **/
@Data
@TableName("t_vote")
public class VoteEntity extends BaseEntity {
    /**
     * 社区id
     */
    private Long communityId;
    /**
     * 主题
     */
    private String theme;
    /**
     * 开始时间
     */
    private LocalDateTime beginTime;
    /**
     * 结束时间
     */
    private LocalDateTime overTime;
    /**
     * 图片集合，以逗号分割
     */
    private String picture;
    /**
     * 1单选，2多选
     */
    private Integer choose;
    /**
     * @Description: 投票选项
     * @author: Hu
     * @since: 2021/8/23 16:50
     * @Param:
     * @return:
     */
    @TableField(exist = false)
    private List<VoteOptionEntity> options;

}
