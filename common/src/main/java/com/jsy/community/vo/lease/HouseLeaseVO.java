package com.jsy.community.vo.lease;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jsy.community.entity.UserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 房屋租售数据返回对象
 * 用于视图层返回显示
 * @author YuLF
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋出租返回对象", description="返回后端查询参数")
public class HouseLeaseVO implements Serializable {

    @ApiModelProperty(value = "业务数据唯一标识")
    private Long id;
    
    @ApiModelProperty(value = "发布人uid")
    @TableField(exist = false)
    private String uid;

    @ApiModelProperty(value = "房屋租售标题")
    private String houseTitle;


    @ApiModelProperty(value = "社区id")
    private Long houseCommunityId;

    @ApiModelProperty(value = "社区名称")
    private String houseCommunityName;

    @ApiModelProperty(value = "房源id")
    private Long houseId;

    @ApiModelProperty(value = "房屋租售所属城市ID")
    private Long houseCityId;

    @ApiModelProperty(value = "房屋所属城市名称")
    private String houseCityName;

    @ApiModelProperty(value = "房屋租售所属区ID")
    private Long houseAreaId;

    @ApiModelProperty(value = "房屋租售详细地址")
    private String houseAddress;

    @ApiModelProperty(value = "房屋租售优势标签ID")
    private Long houseAdvantageId;

    @ApiModelProperty(value = "房屋租售家具标签ID")
    private Long houseFurnitureId;

    @ApiModelProperty(value = "房屋租售优势标签")
    private Map<String, Long> houseAdvantageCode;

    @ApiModelProperty(value = "房屋标签冗余熟悉")
    private Map<String, Long> redundancy;

    @ApiModelProperty(value = "房屋租售家具标签")
    private List<String> houseFurniture;

    @ApiModelProperty(value = "房屋预约时间")
    private String houseReserveTime;

    @ApiModelProperty(value = "房屋租售价格")
    private BigDecimal housePrice;


    @ApiModelProperty(value = "房屋租售平方米")
    private BigDecimal houseSquareMeter;

    @ApiModelProperty(value = "房主称呼")
    private String appellation;

    @ApiModelProperty(value = "房屋出租单位/年/月/周/日")
    private String houseUnit;

    /**
     * 值是变动  需要存id至数据库 对应 名称 有后台人员管理
     */
    @ApiModelProperty(value = "房屋出租方式id /1.压一付一/2.压一付三/3.压一付六")
    private Integer houseLeasedepositId;


    @ApiModelProperty(value = "房屋出租方式文本 /1.压一付一/2.压一付三/3.压一付六")
    private String houseLeaseDeposit;

    @ApiModelProperty(value = "房屋户型Code：如040202 表示 4室2厅2卫")
    private String houseTypeCode;

    @ApiModelProperty(value = "房屋户型文本：1.四室一厅、2.二室一厅...")
    private String houseType;

    @ApiModelProperty(value = "房屋所属楼层")
    private String houseFloor;

    @ApiModelProperty(value = "房屋朝向ID 1.东.2.西 3.南 4.北. 4.东南 5. 东北 6.西北 7.西南")
    private Integer houseDirectionId;

    /**
     * 非 经常变动，常量存 BusinessEnum
     */
    @ApiModelProperty(value = "房屋朝向1.东.2.西 3.南 4.北. 4.东南 5. 东北 6.西北 7.西南")
    private String houseDirection;


    @ApiModelProperty(value = "房屋联系人电话")
    private String houseContact;


    /**
     * 值是变动  需要存id至数据库 对应 名称 由后台人员管理
     */
    @ApiModelProperty(value = "房源类型ID、73不限(默认) 74可短租 75邻地铁  76压一付一  77配套齐全  78精装修 79南北通透  80有阳台")
    private Long houseSourcetypeId;


    @ApiModelProperty(value = "房屋介绍内容")
    private String houseIntroduce;

    @ApiModelProperty(value = "房屋图片数组地址")
    private List<String> houseImage;

    @ApiModelProperty(value = "图片id")
    private Long houseImageId;

    @ApiModelProperty(value = "卧室类型、主卧、次卧、其他")
    private String bedroomType;

    /**
     * 65不限 66普通住宅 67别墅 68公寓
     */
    @ApiModelProperty(value = "房屋出租类型ID")
    private Integer houseLeasetypeId;


    @ApiModelProperty(value = "房屋出租类型文本")
    private String houseLeaseType;
    /**
     * 69不限 70整租，71合租
     */
    @ApiModelProperty(value = "房屋出租方式ID")
    private Long houseLeasemodeId;

    @ApiModelProperty(value = "房屋出租方式文本")
    private String houseLeaseMode;

    @ApiModelProperty(value = "经度")
    private Double houseLon;

    @ApiModelProperty(value = "纬度")
    private Double houseLat;

    @ApiModelProperty(value = "是否被当前用户已收藏")
    private Boolean favorite;

    @ApiModelProperty(value = "是出租房吗/false为商铺")
    private Boolean leaseHouse;

    @ApiModelProperty( value = "出租要求Code：1.一家人 2.不养宠物 4.作息正常 8.组合稳定 16.禁烟")
    private List<Long> leaseRequireCode;

    @ApiModelProperty( value = "出租要求位运算后的id")
    private Long leaseRequireId;

    @ApiModelProperty( value = "公共设施Code")
    private Map<String, Long> commonFacilitiesCode;

    @ApiModelProperty( value = "公共设施位运算后的id")
    private Long commonFacilitiesId;

    @ApiModelProperty( value = "房间设施Code")
    private Map<String, Long> roomFacilitiesCode;

    @ApiModelProperty( value = "房间设施位运算后的id")
    private Long roomFacilitiesId;

    @ApiModelProperty( value = "装修情况codeId：1.简单装修 2.精装修 4.豪华装修")
    private Long decorationTypeId;

    @ApiModelProperty( value = "装修情况文本：1.简单装修 2.精装修 4.豪华装修")
    private String decorationType;

    @ApiModelProperty( value = "室友期望Code：1.一个人住,2.不养宠物,4.作息正常")
    private List<Long> roommateExpectCode;

    @ApiModelProperty( value = "室友期望Code运算后的Id")
    private Long roommateExpectId;

    @ApiModelProperty( value = "室友性别: 1.限女生 2.限男生 4.男女不限")
    private String roommateSex;

    @ApiModelProperty("发布人信息")
    private UserEntity user;

    // 签约操作状态;0;没有签约;1:(租客)发起租赁申请;2:接受申请;3:拟定合同;4:等待支付房租;5:支付完成;6:完成签约;7:取消申请;8:拒绝申请;9:重新发起;31:(房东)发起签约/重新发起;32:取消发起;
    @TableField(exist = false)
    private Integer operation;
    // 签约ID
    @TableField(exist = false)
    private String contractId;
    
}
