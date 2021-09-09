package com.jsy.community.vo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author: DKS
 * @Description: 物业端历史账单返参
 * @Date: 2021/9/7 10:03
 * @Version: 1.0
 **/
public class FinanceImportErrorVO extends BaseVO {

    public FinanceImportErrorVO(Integer successNumber, Integer failNumber, String failExcelDetailsAddress) {
        this.successNumber = successNumber;
        this.failNumber = failNumber;
        this.failExcelDetailsAddress = failExcelDetailsAddress;
    }

    public FinanceImportErrorVO() {}

    @ApiModelProperty("excel导入成功条数")
    private Integer successNumber;

    @ApiModelProperty("excel导入失败条数")
    private Integer failNumber;

    @ApiModelProperty("excel导入失败文件下载地址")
    private String failExcelDetailsAddress;

    @ApiModelProperty("错误信息备注,方便标记excel导入错误信息的回显")
    private String remark;
    
    @ApiModelProperty("姓名")
    private String realName;
    
    @ApiModelProperty("手机号码")
    private String mobile;
    
    @ApiModelProperty("主体类型")
    private String targetType;

    @ApiModelProperty("账单主体")
    private String financeTarget;
    
    @ApiModelProperty("收费项目名称")
    private String feeRuleName;
    
    @ApiModelProperty(value = "账单开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate beginTime;
    
    @ApiModelProperty(value = "账单结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate overTime;
    
    @ApiModelProperty("物业费")
    private BigDecimal propertyFee;
    
    public Integer getSuccessNumber() {
        return successNumber;
    }
    
    public void setSuccessNumber(Integer successNumber) {
        this.successNumber = successNumber;
    }
    
    public Integer getFailNumber() {
        return failNumber;
    }
    
    public void setFailNumber(Integer failNumber) {
        this.failNumber = failNumber;
    }
    
    public String getFailExcelDetailsAddress() {
        return failExcelDetailsAddress;
    }
    
    public void setFailExcelDetailsAddress(String failExcelDetailsAddress) {
        this.failExcelDetailsAddress = failExcelDetailsAddress;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getFinanceTarget() {
        return financeTarget;
    }
    
    public void setFinanceTarget(String financeTarget) {
        this.financeTarget = financeTarget;
    }
    
    public String getFeeRuleName() {
        return feeRuleName;
    }
    
    public void setFeeRuleName(String feeRuleName) {
        this.feeRuleName = feeRuleName;
    }
    
    public LocalDate getBeginTime() {
        return beginTime;
    }
    
    public void setBeginTime(LocalDate beginTime) {
        this.beginTime = beginTime;
    }
    
    public LocalDate getOverTime() {
        return overTime;
    }
    
    public void setOverTime(LocalDate overTime) {
        this.overTime = overTime;
    }
    
    public BigDecimal getPropertyFee() {
        return propertyFee;
    }
    
    public void setPropertyFee(BigDecimal propertyFee) {
        this.propertyFee = propertyFee;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FinanceImportErrorVO{");
        sb.append("successNumber=").append(successNumber);
        sb.append(", failNumber=").append(failNumber);
        sb.append(", failExcelDetailsAddress='").append(failExcelDetailsAddress).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", realName='").append(realName).append('\'');
        sb.append(", mobile='").append(mobile).append('\'');
        sb.append(", targetType='").append(targetType).append('\'');
        sb.append(", financeTarget='").append(financeTarget).append('\'');
        sb.append(", feeRuleName='").append(feeRuleName).append('\'');
        sb.append(", beginTime='").append(beginTime);
        sb.append(", overTime=").append(overTime);
        sb.append(", propertyFee=").append(propertyFee);
        sb.append('}');
        return sb.toString();
    }
}
