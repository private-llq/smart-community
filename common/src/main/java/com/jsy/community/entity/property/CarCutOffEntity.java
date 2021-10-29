package com.jsy.community.entity.property;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringImageConverter;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel("车禁模块-开闸记录")
@TableName("t_car_cut_off")
public class CarCutOffEntity  extends BaseEntity {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @ApiModelProperty(value = "车辆类型")
    private String carType;


    @ApiModelProperty(value = "进场车道")
    private String  laneName;

    @ApiModelProperty(value = "出场车道")
    private String  outLane;

    @ApiModelProperty(value = "进出方向")
    private String access;

    @ApiModelProperty(value = "完成状态  0未完成 1已完成")
    private Integer state;

    @ApiModelProperty(value = "车辆子品牌")
    private String carSublogo;

    @ApiModelProperty(value = "触发模式表示视频触发，hwtriger 表示地感触发，swtriger 表示软触发")
    private String trigerType;
    @ApiModelProperty(value = "车牌底色")
    private String plateColor;

    @ApiModelProperty(value = "车子进闸全景图")
    private String image;
    @ApiModelProperty(value = "车牌进闸特写图")
    private String closeupPic;

    @ApiModelProperty(value = "车子出闸全景图")
    private String outImage;
    @ApiModelProperty(value = "车牌出闸特写图")
    @ExcelProperty(converter = StringImageConverter.class)
    private String outPic;

    @ApiModelProperty(value = "车辆所属类型  1-临时 2-包月  3-业主")
    private Integer belong;
    /**
     *  订单编号
     */
    private String orderNum;


    @ApiModelProperty(value = "进闸时间")
    //@TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime openTime;


    @ApiModelProperty(value = "出闸时间")
    //@TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime stopTime;




    @ApiModelProperty(value = "创建时间")
    //@TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    // @TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 停车时长
     */
    @TableField(exist = false)
    private String stopCarTime;
}
