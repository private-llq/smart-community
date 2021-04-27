package com.jsy.community.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-23 15:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplainVO extends BaseVO {
    @ApiModelProperty(value = "编号")
    private String serialNumber;
    @ApiModelProperty(value = "类型，1投诉，2建议")
    private Integer type;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "图片地址")
    private String images;
    @ApiModelProperty(value = "投诉人名称")
    private String name;
    @ApiModelProperty(value = "投诉人电话")
    private String mobile;
    @ApiModelProperty(value = "投诉时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime complainTime;
    @ApiModelProperty(value = "反馈内容")
    private String feedbackContent;
    @ApiModelProperty(value = "回复人名称")
    private String feedbackName;
    @ApiModelProperty(value = "反馈内容")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime feedbackTime;
    @ApiModelProperty(value = "1，已回复，0未回复")
    private Integer status;
}
