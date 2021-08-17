package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * <p>
 * 车位类型表
 * </p>
 *
 * @author Arli
 * @since 2021-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_car_position_type")
public class CarPositionTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 车位类型id
     */
    private String typeId;
    /**
     * 小区id
     */
    private Long communityId;
    /**
     * 车位类型名称
     */
    private String description;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
