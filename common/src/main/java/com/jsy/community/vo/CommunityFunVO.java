package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-15 17:31
 **/
@Data
public class CommunityFunVO implements Serializable {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "社区趣事标题")
    private String titleName;
    @ApiModelProperty(value = "社区趣事浏览次数")
    private Integer viewCount;
    @ApiModelProperty(value = "社区趣事内容")
    private String content;
    @ApiModelProperty(value = "社区趣事缩略图地址")
    private String smallImageUrl;
    @ApiModelProperty(value = "社区趣事封面图地址")
    private String coverImageUrl;
    @ApiModelProperty(value = "社区趣事状态1表示已上线，2二表示为上线")
    private Integer status;
    @ApiModelProperty(value = "上线时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "下线时间")
    private LocalDateTime outTime;
    @ApiModelProperty(value = "标签")
    private String[] tallys;
}
