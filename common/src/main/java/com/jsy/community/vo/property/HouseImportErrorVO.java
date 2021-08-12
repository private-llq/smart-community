package com.jsy.community.vo.property;

import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: Pipi
 * @Description: 物业端房屋信息返参
 * @Date: 2021/5/19 11:41
 * @Version: 1.0
 **/
public class HouseImportErrorVO extends BaseVO {

    public HouseImportErrorVO(Integer successNumber, Integer failNumber, String failExcelDetailsAddress) {
        this.successNumber = successNumber;
        this.failNumber = failNumber;
        this.failExcelDetailsAddress = failExcelDetailsAddress;
    }

    public HouseImportErrorVO() {}

    @ApiModelProperty("excel导入成功条数")
    private Integer successNumber;

    @ApiModelProperty("excel导入失败条数")
    private Integer failNumber;

    @ApiModelProperty("excel导入失败文件下载地址")
    private String failExcelDetailsAddress;

    @ApiModelProperty("错误信息备注,方便标记excel导入错误信息的回显")
    private String remark;

//    @ApiModelProperty("编号")
//    private String number;
    
    @ApiModelProperty("房屋号码")
    private String door;

//    @ApiModelProperty("楼栋编号")
//    private String buildingNumber;

    @ApiModelProperty("楼栋名称")
    private String building;
    
    @ApiModelProperty("总楼层")
    private Integer totalFloor;

//    @ApiModelProperty("单元编号")
//    private String unitNumber;

    @ApiModelProperty("单元名称")
    private String unit;
    
    @ApiModelProperty("楼层名")
    private Integer floor;

    @ApiModelProperty("建筑面积")
    private Double buildArea;
    
    @ApiModelProperty("实用面积")
    private Double practicalArea;
    
    @ApiModelProperty("房屋状态")
    private String status;

//    @ApiModelProperty("房屋类型")
//    private String houseType;

//    @ApiModelProperty("房产类型")
//    private String propertyType;

//    @ApiModelProperty("装修情况")
//    private String decoration;

    @ApiModelProperty("备注")
    private String comment;
    
    @ApiModelProperty("房屋地址")
    private String address;
    
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
    
    public String getDoor() {
        return door;
    }
    
    public void setDoor(String door) {
        this.door = door;
    }
    
    public String getBuilding() {
        return building;
    }
    
    public void setBuilding(String building) {
        this.building = building;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Integer getTotalFloor() {
        return totalFloor;
    }
    
    public void setTotalFloor(Integer totalFloor) {
        this.totalFloor = totalFloor;
    }
    
    public Integer getFloor() {
        return floor;
    }
    
    public void setFloor(Integer floor) {
        this.floor = floor;
    }
    
    public Double getBuildArea() {
        return buildArea;
    }
    
    public void setBuildArea(Double buildArea) {
        this.buildArea = buildArea;
    }
    
    public Double getPracticalArea() {
        return practicalArea;
    }
    
    public void setPracticalArea(Double practicalArea) {
        this.practicalArea = practicalArea;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HouseImportErrorVO{");
        sb.append("successNumber=").append(successNumber);
        sb.append(", failNumber=").append(failNumber);
        sb.append(", failExcelDetailsAddress='").append(failExcelDetailsAddress).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", door='").append(door).append('\'');
        sb.append(", building='").append(building).append('\'');
        sb.append(", totalFloor='").append(totalFloor).append('\'');
        sb.append(", unit='").append(unit).append('\'');
        sb.append(", floor='").append(floor).append('\'');
        sb.append(", buildArea=").append(buildArea);
        sb.append(", practicalArea=").append(practicalArea);
        sb.append(", status='").append(status).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
