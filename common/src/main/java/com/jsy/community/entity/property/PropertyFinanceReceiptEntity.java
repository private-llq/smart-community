package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import com.jsy.community.qo.property.StatementQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chq459799974
 * @description 物业财务-收款单 实体类
 * @since 2021-04-21 16:52
 **/
@Data
@TableName("t_property_finance_receipt")
public class PropertyFinanceReceiptEntity extends BaseEntity {
    
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    
    @ApiModelProperty(value = "收款单号")
    private String receiptNum;
    
    @ApiModelProperty(value = "收款渠道单号")
    private String transactionNo;
    
    @ApiModelProperty(value = "收款渠道1.支付宝 2.微信")
    private Integer transactionType;
    
    @ApiModelProperty(value = "收款金额")
    private BigDecimal receiptMoney;
    
    @ApiModelProperty(value = "账单列表",hidden = true)
    @TableField(exist = false)
    private List<PropertyFinanceOrderEntity> orderList;
    
    @ApiModelProperty(value = "查询条件 - 收款开始日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate startDate;
    
    @ApiModelProperty(value = "查询条件 - 收款结束日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate endDate;
    
    @ApiModelProperty(value = "查询条件 - 账单号")
    @TableField(exist = false)
    private String orderNum;

    @ApiModelProperty("导出选项, 1:仅导出主数据, 2:导出主数据和从数据")
    @Range(min = 1, max = 2, message = "导出选项值超出范围, 1:仅导出主数据, 2:导出主数据和从数据")
    @NotNull(groups = {ExportValiadate.class}, message = "导出选项不能为空")
    @TableField(exist = false)
    private Integer exportType;

    public interface ExportValiadate {}
}
