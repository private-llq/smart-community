package com.jsy.community.vo.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class LeaseReleasePageVO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("主键Str")
    private String idStr;

    @ApiModelProperty("房源标题")
    private String tTitle;

    @ApiModelProperty("房屋类型")
    private String tType;

    @ApiModelProperty("社区id")
    private Long tCommunityId;

    @ApiModelProperty("社区")
    private String community;

    @ApiModelProperty("发布人姓名")
    private String tName;

    @ApiModelProperty("发布人电话")
    private String tMobile;

    @ApiModelProperty("发布时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("状态编号")
    private Integer tLeaseStatus;

    @ApiModelProperty("状态")
    private String leaseStatus;

}
