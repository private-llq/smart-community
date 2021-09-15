package com.jsy.community.vo.property;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CarEntranceVO implements Serializable {
    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @ApiModelProperty(value = "车道名称")
    private String  laneName;

    @ApiModelProperty(value = "进闸时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime openTime;

    @ApiModelProperty(value = "来访人姓名")
    private String name;

    @ApiModelProperty(value = "来访人联系方式")
    private String contact;

    @ApiModelProperty(value = "来访地址")
    private String address;
}
