package com.jsy.community.qo.property;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value="编辑车位信息")
public class UpdateCarPositionQO implements Serializable {
    /**
     * 主键
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联车位类型id
     */
    @ExcelIgnore
    private Long typeId;

    /**
     * 车位号
     */
    @ExcelProperty("车位号")
    private String carPosition;



    /**
     * 产权面积
     */
    @ExcelProperty("产权面积(m²)")
    private Double area;


    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;

     /** 售价
     */
    @ExcelProperty("售价")
    private BigDecimal price;
    /**
     * 月租
     */
    @ExcelProperty("月租")
    private  BigDecimal     monthlyPrice;
}
