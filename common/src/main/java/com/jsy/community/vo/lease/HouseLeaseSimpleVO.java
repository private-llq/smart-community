package com.jsy.community.vo.lease;/**
 * @author YuLF
 * @since 2021-03-27 16:00
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 房屋出租简略数据返回对象
 * @Date: 2021/3/27 16:00
 * @Version: 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "房屋出租简略数据返回对象", description = "返回后端查询参数")
public class HouseLeaseSimpleVO implements Serializable {

    @ApiModelProperty(value = "业务数据唯一标识")
    private Long id;

    @ApiModelProperty(value = "房屋租售标题")
    private String houseTitle;

    @ApiModelProperty(value = "社区id")
    private Long houseCommunityId;

    @ApiModelProperty(value = "社区名称")
    private String houseCommunityName;

    @ApiModelProperty(value = "房源id")
    private Long houseId;

    @ApiModelProperty(value = "房屋租售详细地址")
    private String houseAddress;

    @ApiModelProperty(value = "房屋租售价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "房屋租售平方米")
    private BigDecimal houseSquareMeter;

    @ApiModelProperty(value = "房屋出租单位/年/月/周/日")
    private String houseUnit;

    @ApiModelProperty(value = "房屋户型Code：如040202 表示 4室2厅2卫")
    private String houseTypeCode;

    @ApiModelProperty(value = "房屋户型文本：1.四室一厅、2.二室一厅...")
    private String houseType;

    @ApiModelProperty(value = "房屋图片数组地址")
    private List<String> houseImage;

    @ApiModelProperty(value = "图片id")
    private Long houseImageId;

    /**
     * 69不限 70整租，71合租
     */
    @ApiModelProperty(value = "房屋出租方式ID")
    private Long houseLeasemodeId;

    @ApiModelProperty(value = "房屋出租方式文本")
    private String houseLeaseMode;
}
