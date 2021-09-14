package com.jsy.community.vo.property;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CarAccessVO implements Serializable {
    @ExcelProperty("车牌号")
    private String carNumber;

    @ExcelProperty("车辆类型")
    private String carType;

    @ExcelProperty("入场时间")
    private String openTime;

    @ExcelProperty("出场时间")
    private LocalDateTime stopTime;

    @ExcelProperty("停车时长")
    private String stopCarTime;

    @ExcelProperty("入场全景图")
    private String image;

    @ExcelProperty("入场特写图")
    private String closeupPic;

    @ExcelProperty("入场全景图")
    private String outImage;

    @ExcelProperty("入场特写图")
    private String outPic;

}
