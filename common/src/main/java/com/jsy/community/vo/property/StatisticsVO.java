package com.jsy.community.vo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 统计返参
 * @Date: 2021/4/27 10:47
 * @Version: 1.0
 **/
@Data
@ApiModel("统计返参")
public class StatisticsVO implements Serializable {

    @ApiModelProperty("统计月份列表")
    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    private List<String> createTime;

    @ApiModelProperty("统计费用名称列表")
    private List<String> name;

    @ApiModelProperty("统计费用明细")
    private ContentVO content;
}
