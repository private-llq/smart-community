package com.jsy.community.vo.property;

import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @Author: DKS
 * @Description: 物业端充值余额返参
 * @Date: 2021/8/13 17:09
 * @Version: 1.0
 **/
public class AdvanceDepositImportErrorVO extends BaseVO {

    public AdvanceDepositImportErrorVO(Integer successNumber, Integer failNumber, String failExcelDetailsAddress) {
        this.successNumber = successNumber;
        this.failNumber = failNumber;
        this.failExcelDetailsAddress = failExcelDetailsAddress;
    }

    public AdvanceDepositImportErrorVO() {}

    @ApiModelProperty("excel导入成功条数")
    private Integer successNumber;

    @ApiModelProperty("excel导入失败条数")
    private Integer failNumber;

    @ApiModelProperty("excel导入失败文件下载地址")
    private String failExcelDetailsAddress;

    @ApiModelProperty("错误信息备注,方便标记excel导入错误信息的回显")
    private String remark;

    @ApiModelProperty("姓名")
    private String name;
    
    @ApiModelProperty("手机号")
    private String mobile;
    
    @ApiModelProperty("房屋地址")
    private String houseAddress;
    
    @ApiModelProperty("房屋号码")
    private String door;
    
    @ApiModelProperty("付款金额")
    private BigDecimal payAmount;
    
    @ApiModelProperty("到账金额")
    private BigDecimal receivedAmount;
    
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getHouseAddress() {
        return houseAddress;
    }
    
    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }
    
    public String getDoor() {
        return door;
    }
    
    public void setDoor(String door) {
        this.door = door;
    }
    
    public BigDecimal getPayAmount() {
        return payAmount;
    }
    
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
    
    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }
    
    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AdvanceDepositImportErrorVO{");
        sb.append("successNumber=").append(successNumber);
        sb.append(", failNumber=").append(failNumber);
        sb.append(", failExcelDetailsAddress='").append(failExcelDetailsAddress).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", mobile='").append(mobile).append('\'');
        sb.append(", houseAddress='").append(houseAddress).append('\'');
        sb.append(", door='").append(door).append('\'');
        sb.append(", payAmount='").append(payAmount);
        sb.append(", receivedAmount='").append(receivedAmount);
        sb.append('}');
        return sb.toString();
    }
}
