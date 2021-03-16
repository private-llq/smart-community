package com.jsy.community.entity.lease;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

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

    public HouseRecentEntity(){}

    public HouseRecentEntity(Long id, Long houseId, String uid, String browseTitle, Integer browseType, String leaseType, Double acreage, String address, String price, String tag, String houseImage, String houseTypeCode, LocalDateTime createTime) {
        this.id = id;
        this.houseId = houseId;
        this.uid = uid;
        this.browseTitle = browseTitle;
        this.browseType = browseType;
        this.leaseType = leaseType;
        this.acreage = acreage;
        this.address = address;
        this.price = price;
        this.tag = tag;
        this.houseImage = houseImage;
        this.houseTypeCode = houseTypeCode;
        this.createTime = createTime;
    }

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

    @ApiModelProperty(value = "房屋出租方式：合租、整租")
    private String leaseType;

    @ApiModelProperty(value = "租赁面积")
    private Double acreage;

    @ApiModelProperty(value = "房屋地址")
    private String address;

    @ApiModelProperty(value = "价格/单位 如：15700/年、1500/月")
    private String price;

    @ApiModelProperty(value = "房屋标签")
    private String tag;

    @ApiModelProperty(value = "房屋图片")
    private String houseImage;

    @ApiModelProperty(value = "房屋户型")
    private String houseTypeCode;

    private LocalDateTime createTime;

}
