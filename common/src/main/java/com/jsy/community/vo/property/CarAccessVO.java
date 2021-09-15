package com.jsy.community.vo.property;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ContentRowHeight(53)
public class CarAccessVO implements Serializable {
    @ColumnWidth(9)
    @ExcelProperty("车牌号")
    private String carNumber;


    @ColumnWidth(21)
    @ExcelProperty("入场特写图")
    private byte[] closeupPic;

    @ColumnWidth(12)
    @ExcelProperty("车辆类型")
    private String carType;


    @ColumnWidth(12)
    @ExcelProperty("进场车道")
    private String laneName;


    @ColumnWidth(12)
    @ExcelProperty("出场车道")
    private String outLane;

    @ColumnWidth(20)
    @ExcelProperty("进闸时间")
    private Date openTime;

    @ColumnWidth(20)
    @ExcelProperty("出场时间")
    private Date stopTime;

    @ColumnWidth(22)
    @ExcelProperty("停车时长")
    private String stopCarTime;


    @ColumnWidth(21)
    @ExcelProperty("出场特写图")
    private byte[] outPic;

}
