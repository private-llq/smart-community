package com.jsy.community.qo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author DKS
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="合同管理查询对象", description="合同管理")
public class ContractQO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "合同类型1.房屋租赁合同 2.车位购买合同")
    private Integer contractType;
    
    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;
    
    @ApiModelProperty(value = "甲方/乙方名称")
    private String name;
    
    @ApiModelProperty(value = "签约状态;1:未签约;2:签约中;3已签约;4已过期")
    private Integer contractStatus;
}
