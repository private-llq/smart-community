package com.jsy.community.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @Author: Pipi
 * @Description: 结算单查询返参
 * @Date: 2021/4/23 16:47
 * @Version: 1.0
 **/
@Data
public class StatementVO implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("结算单号")
    private String statementNum;

    @ApiModelProperty("结算时间段-开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    @ApiModelProperty("结算时间段-开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    @ApiModelProperty("结算状态1.待审核 2.结算中 3.已结算 4.驳回")
    private Integer statementStatus;

    @ApiModelProperty("结算金额")
    private BigDecimal totalMoney;

    @ApiModelProperty("开户账户名称")
    private String accountName;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("开户行所在城市")
    private String bankCity;

    @ApiModelProperty("开户支行名称")
    private String bankBranchName;

    @ApiModelProperty("银行卡号")
    private String bankNo;

    @ApiModelProperty("驳回原因")
    private String rejectReason;

    @ApiModelProperty("收款账户类型")
    private String receiptAccountType;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    @ApiModelProperty("结算单的账单列表")
    List<StatementOrderVO> orderVOList;
}
