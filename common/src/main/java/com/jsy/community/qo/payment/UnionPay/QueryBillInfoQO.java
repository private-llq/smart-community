package com.jsy.community.qo.payment.UnionPay;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @Author: Pipi
 * @Description: 分页查询账单明细接参
 * @Date: 2021/5/12 10:47
 * @Version: 1.0
 **/
@Data
@ApiModel("分页查询账单明细接参")
public class QueryBillInfoQO implements Serializable {

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("商户订单号")
    private String mctOrderNo;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("钱包id")
    private String walletId;

    @ApiModelProperty("授权钱包ID")
    private String grantWalletId;

    @ApiModelProperty("被授权钱包ID")
    private String onGrantWalletId;

    @ApiModelProperty("共管子账号")
    private String coadminAcctNo;

    @ApiModelProperty("开始日期-必填,查询区间最长为3个月")
    @NotNull(message = "开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate localDateStartDate;

    private String startDate;

    @ApiModelProperty("结束日期-必填,查询区间最长为3个月")
    @NotNull(message = "结束日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate localDateEndDate;

    private String endDate;

    @ApiModelProperty("收支类型,C：收入；D：支出；")
    @Pattern(regexp = "^C|D$", message = "收支类型错误,C：收入；D：支出；")
    private String loanMark;

    @ApiModelProperty("交易代码")
    private String transCode;

    @ApiModelProperty("最低交易金额")
    private String minTransAmt;

    @ApiModelProperty("最高交易金额")
    private String maxTransAmt;

    @ApiModelProperty("是否分页-必填,0分页,1不分页（返回所有查询结果）")
    @Range(min = 0, max = 1, message = "是否分页区间只能为0或1")
    @NotBlank(message = "是否分页不能为空")
    private String pageType;

    @ApiModelProperty("第几页")
    @Range(min = 1, message = "页数最小为1")
    private String pageNumber;

    @ApiModelProperty("每页显示数")
    @Range(min = 1, max = 50, message = "页面大小最小为1,最大为50")
    private String pageSize;
}