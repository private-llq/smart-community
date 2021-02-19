package com.jsy.community.entity.lease;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 房屋最近浏览数据 与 数据库映射关系
 * @author YuLF
 * @since 2021-02-18 13:41
 */
@Data
@Builder
@ApiModel(value="房屋最近浏览", description="房屋最近浏览数据字段实体")
@TableName("t_house_recent")
public class HouseRecentEntity implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "业务数据唯一ID")
    private Long houseId;

    @ApiModelProperty(value = "用户Id")
    private String uid;

    @ApiModelProperty(value = "浏览数据标题")
    private String browseTitle;

    @ApiModelProperty(value = "浏览数据类型：0出租房屋、1商铺")
    private Integer browseType;

    @ApiModelProperty(value = "租赁面积")
    private Double acreage;

    @ApiModelProperty(value = "房屋地址")
    private String address;

    @ApiModelProperty(value = "价格/单位 如：15700/年、1500/月")
    private String price;

    @ApiModelProperty(value = "房屋标签")
    private Long tag;

    @ApiModelProperty(value = "房屋图片")
    private String houseImage;

    private LocalDateTime createTime;

}
