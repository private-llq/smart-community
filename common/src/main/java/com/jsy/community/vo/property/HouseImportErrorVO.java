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

    @ApiModelProperty("编号")
    private String number;

    @ApiModelProperty("楼层名")
    private String floor;

    @ApiModelProperty("楼栋编号")
    private String buildingNumber;

    @ApiModelProperty("楼栋名称")
    private String building;

    @ApiModelProperty("单元编号")
    private String unitNumber;

    @ApiModelProperty("单元名称")
    private String unit;

    @ApiModelProperty("建筑面积")
    private Double buildArea;

    @ApiModelProperty("房屋类型")
    private String houseType;

    @ApiModelProperty("房产类型")
    private String propertyType;

    @ApiModelProperty("装修情况")
    private String decoration;

    @ApiModelProperty("备注")
    private String comment;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getBuildArea() {
        return buildArea;
    }

    public void setBuildArea(Double buildArea) {
        this.buildArea = buildArea;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HouseImportErrorVO{");
        sb.append("successNumber=").append(successNumber);
        sb.append(", failNumber=").append(failNumber);
        sb.append(", failExcelDetailsAddress='").append(failExcelDetailsAddress).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", number='").append(number).append('\'');
        sb.append(", floor='").append(floor).append('\'');
        sb.append(", buildingNumber='").append(buildingNumber).append('\'');
        sb.append(", building='").append(building).append('\'');
        sb.append(", unitNumber='").append(unitNumber).append('\'');
        sb.append(", unit='").append(unit).append('\'');
        sb.append(", buildArea=").append(buildArea);
        sb.append(", houseType='").append(houseType).append('\'');
        sb.append(", propertyType='").append(propertyType).append('\'');
        sb.append(", decoration='").append(decoration).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
