package com.jsy.community.vo.property;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("小区车位分类")
public class SelectCartPositionTypeVO implements Serializable {
    private static final long serialVersionUID = 1L;


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
    private LocalDateTime createTime;

}
