package com.jsy.community.vo.property;

import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: DKS
 * @Description: 物业端楼栋信息返参
 * @Date: 2021/8/10 10:41
 * @Version: 1.0
 **/
public class BuildingImportErrorVO extends BaseVO {

    public BuildingImportErrorVO(Integer successNumber, Integer failNumber, String failExcelDetailsAddress) {
        this.successNumber = successNumber;
        this.failNumber = failNumber;
        this.failExcelDetailsAddress = failExcelDetailsAddress;
    }

    public BuildingImportErrorVO() {}

    @ApiModelProperty("excel导入成功条数")
    private Integer successNumber;

    @ApiModelProperty("excel导入失败条数")
    private Integer failNumber;

    @ApiModelProperty("excel导入失败文件下载地址")
    private String failExcelDetailsAddress;

    @ApiModelProperty("错误信息备注,方便标记excel导入错误信息的回显")
    private String remark;

    @ApiModelProperty("楼栋名称")
    private String building;
    
    @ApiModelProperty("总楼层")
    private Integer totalFloor;
    
    @ApiModelProperty("楼宇分类名称")
    private String buildingTypeName;
    
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
    
    public String getBuilding() {
        return building;
    }
    
    public void setBuilding(String building) {
        this.building = building;
    }
    
    public Integer getTotalFloor() {
        return totalFloor;
    }
    
    public void setTotalFloor(Integer totalFloor) {
        this.totalFloor = totalFloor;
    }
    
    public String getBuildingTypeName() {
        return buildingTypeName;
    }
    
    public void setBuildingTypeName(String buildingTypeName) {
        this.buildingTypeName = buildingTypeName;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BuildingImportErrorVO{");
        sb.append("successNumber=").append(successNumber);
        sb.append(", failNumber=").append(failNumber);
        sb.append(", failExcelDetailsAddress='").append(failExcelDetailsAddress).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", building='").append(building).append('\'');
        sb.append(", totalFloor='").append(totalFloor);
        sb.append(", buildingTypeName='").append(buildingTypeName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
