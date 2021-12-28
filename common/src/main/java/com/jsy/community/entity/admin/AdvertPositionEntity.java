package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告位置表
 * @date 2021/12/25 15:59
 */
@Data
@Accessors(chain = true)
@TableName("t_advert_position")
public class AdvertPositionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String positionName;

    private String fullName;

    private Integer pid;

    private Integer level;

    private Integer sort;

    private String pixelSize;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer state;

    @TableLogic
    private Integer deleted;
}
