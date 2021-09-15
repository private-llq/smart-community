package com.jsy.community.vo.property;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
@Data
@ContentRowHeight(53)
public class CarSceneVO implements Serializable {
    @ColumnWidth(9)
    @ExcelProperty("车牌号")
    private String carNumber;

    @ColumnWidth(12)
    @ExcelProperty("车辆类型")
    private String carType;

    @ColumnWidth(20)
    @ExcelProperty("进闸时间")
    private Date openTime;

    @ColumnWidth(21)
    @ExcelProperty(value = "车牌进闸特写图")
    private byte[] closeupPic;

//    @ColumnWidth(21)
//    @ExcelProperty(value = "车子进闸全景图")
//    private byte[] image;

}
