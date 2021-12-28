package com.jsy.community.qo.admin;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author xrq
 * @version 1.0
 * @Description: 新增广告参数
 * @date 2021/12/25 11:42
 */

@Data
public class AddAdvertQO {
    /**
     * 广告名称
     */
    @NotBlank
    private String name;
    /**
     * 广告位值
     */
    @NotNull
    private Integer displayPosition;
    /**
     * 排序
     */
    @Min(value = 1)
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
     * 描述
     */
    private String notes;
}
