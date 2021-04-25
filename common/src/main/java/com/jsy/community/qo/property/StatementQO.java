package com.jsy.community.qo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 结算单查询接参
 * @Date: 2021/4/23 16:29
 * @Version: 1.0
 **/
@Data
@ApiModel("结算单查询接参")
public class StatementQO implements Serializable {

    @ApiModelProperty("结算状态")
    private Integer statementStatus;

    @ApiModelProperty("结算时间开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate statementStartDate;

    @ApiModelProperty("结算时间结算时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate statementEndDate;

    @ApiModelProperty("创建时间开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate createStartTime;

    @ApiModelProperty("创建时间结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate createEndTime;

    @ApiModelProperty("结算单号")
    private String statementNum;

    @ApiModelProperty("账单号")
    private String orderNum;

    @ApiModelProperty("社区ID")
    private Long communityId;

    @ApiModelProperty("导出选项, 1:仅导出主数据, 2:导出主数据和从数据")
    @Range(min = 1, max = 2, message = "导出选项值超出范围, 1:仅导出主数据, 2:导出主数据和从数据")
    @NotNull(groups = {ExportValiadate.class}, message = "导出选项不能为空")
    private Integer exportType;

    private List<String> statementNumS;

    public interface ExportValiadate {}
}
