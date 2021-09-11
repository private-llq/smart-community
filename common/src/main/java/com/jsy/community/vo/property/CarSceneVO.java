package com.jsy.community.vo.property;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.converters.string.StringImageConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.print.DocFlavor;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
@Data
public class CarSceneVO implements Serializable {
    @ExcelProperty("车牌号")
    private String carNumber;


    @ExcelProperty("车辆类型")
    private String carType;

//    @ExcelProperty("进闸时间")
//    private Date openTime;

    @ExcelProperty("车子进闸全景图")
    private String outImage;
    @ExcelProperty("图")
    private InputStream file;

    @ExcelProperty("url图片")
    private URL url;

    @ExcelProperty(value = "string图",converter = StringImageConverter.class)
    private String string;

    @ColumnWidth(40)
    @ExcelProperty(value = "车牌进闸特写图")
    private String outPic;

}
