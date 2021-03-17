package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-17 11:29
 **/
@Data
@ApiModel("物业投诉")
public class PropertyComplainQO implements Serializable {

    @ApiModelProperty(value = "投诉原因,1质量投诉，2维修投诉，3扰民投诉，4安全投诉，" +
            "5停车管理投诉，6环境投诉，7设备设施，8服务投诉，9费用投诉，10其他投诉")
    private Integer type;
    @ApiModelProperty(value = "投诉内容")
    private String content;
    @ApiModelProperty(value = "编号")
    private String serialNumber;
    @ApiModelProperty(value = "投诉人",hidden = true)
    private String uid;
    @ApiModelProperty(value = "地点位置")
    private String location;
    @ApiModelProperty(value = "图片地址")
    private String images;

}
