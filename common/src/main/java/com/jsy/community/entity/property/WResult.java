package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("t_w_result")
@Accessors(chain = true)
public class WResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 问卷id
     */
    private Long questionnaireId;

    /**
     * 问题id
     */
    private Long problemId;

    /**
     * 用户id
     */
    private String userUuid;

    /**
     * 答案
     */
    private String result;
    /**
     * 问题类型
     */
    private Integer resultType;
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
