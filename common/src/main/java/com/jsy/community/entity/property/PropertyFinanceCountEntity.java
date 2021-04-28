package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author chq459799974
 * @description 财务统计
 * @since 2021-04-26 17:28
 **/
@Data
public class PropertyFinanceCountEntity implements Serializable {
    
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    
    @ApiModelProperty(value = "查询条件 - 收款开始日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate startDate;
    
    @ApiModelProperty(value = "查询条件 - 收款结束日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate endDate;

    @ApiModelProperty("查询类型")
    @Range(groups = {QueryValidate.class}, min = 1, max = 3, message = "查询类型超出范围,1:查询缴费统计,2:查询应收统计,3:查询结算统计")
    @NotNull(groups = {QueryValidate.class}, message = "查询类型不能为空")
    private Integer queryType;

    public interface QueryValidate {}

}
