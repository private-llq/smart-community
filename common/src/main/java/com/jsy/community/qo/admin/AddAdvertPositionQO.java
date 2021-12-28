package com.jsy.community.qo.admin;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author xrq
 * @version 1.0
 * @Description: 新增广告位置参数
 * @date 2021/12/25 16:08
 */
@Data
public class AddAdvertPositionQO {

    @NotBlank
    private String name;

    @NotNull
    private Integer pid;

    @Min(value = 1)
    private Integer level;

    @Min(value = 1)
    private Integer sort;

    @NotBlank
    private String pixelSize;

    private Integer state;
}
