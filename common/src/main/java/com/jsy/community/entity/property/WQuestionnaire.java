package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Arli
 * @since 2021-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_w_questionnaire")
@Accessors(chain = true)
public class WQuestionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    /**
     * 发布状态（0未发布，1已发布）
     */
    private Integer releaseStatus;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Long deleted;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;


}
