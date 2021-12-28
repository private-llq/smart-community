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
 * @description 广告
 * @since 2021-12-25 11:00
 **/
@Data
@TableName("t_advert")
@Accessors(chain = true)
public class AdvertEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 广告id
     */
    private String advertId;
    /**
     * 广告名称
     */
    private String name;
    /**
     * 广告位值
     */
    private Integer displayPosition;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 广告类型
     */
    private Integer advertType;
    /**
     * 文件路径
     */
    private String fileUrl;
    /**
     * 跳转类型
     */
    private Integer jumpType;
    /**
     * 跳转地址
     */
    private String jumpAddress;
    /**
     * 展示状态
     */
    private Integer state;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 描述
     */
    private String notes;
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
