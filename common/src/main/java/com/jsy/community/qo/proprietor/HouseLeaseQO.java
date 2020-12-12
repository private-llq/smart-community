package com.jsy.community.qo.proprietor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房屋租售数据传输对象
 * 用于业务层之间的数据传递
 * YuLF
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋出租收参对象", description="接收前端参数")
public class HouseLeaseQO implements Serializable {

    private Long id;

    @ApiModelProperty(value = "业务数据唯一标识")
    private String rowGuid;

    @ApiModelProperty(value = "所属人ID")
    private String uid;

    @ApiModelProperty(value = "房屋租售标题")
    @Length(groups = {addLeaseSaleHouse.class}, min = 1, max = BusinessConst.HOUSE_TITLE_CHAR_MAX, message = "标题长度在1~32之间")
    @NotBlank(groups = addLeaseSaleHouse.class, message = "未填写租售标题!")
    private String houseTitle;

    @ApiModelProperty(value = "房屋租售所属省ID")
    private Long houseProvinceId;

    @ApiModelProperty(value = "房屋租售所属城市ID")
    @Range(groups = {addLeaseSaleHouse.class}, min = 1, max = BusinessConst.HOUSE_ID_RANGE_MAX, message = "请选择一个正确的城市Code!")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "城市未选择!")
    private Long houseCityId;

    @ApiModelProperty(value = "房屋租售所属区ID")
    @Range(groups = {addLeaseSaleHouse.class}, min = 1, max = BusinessConst.HOUSE_ID_RANGE_MAX, message = "请选择一个正确的区域Code!")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "请选择区域!")
    private Long houseAreaId;

    @ApiModelProperty(value = "房屋租售详细地址")
    @Length(groups = {addLeaseSaleHouse.class}, min = 1, max =  BusinessConst.HOUSE_ADDRESS_CHAR_MAX, message = "房屋地址长度在1~128之间")
    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "未填写房屋租售详细地址")
    private String houseAddress;

    @ApiModelProperty(value = "房屋租售优势标签ID数组")
    private Short[] houseAdvantage;

    //在把houseAdvantage添加至中间表之后 返回 中间表存储的id
    private String houseAdvantageId;

    @ApiModelProperty(value = "房屋租售价格")
    @Range(groups = {addLeaseSaleHouse.class}, min = 1, max = Integer.MAX_VALUE, message = "价格没有在指定范围之内!")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未输入价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "房屋出租单位/年/月/周/日")
    @Pattern(groups = {addLeaseSaleHouse.class}, regexp = RegexUtils.REGEX_DATE, message = "指定的日期单位没有在范围之内")
    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "日期单位错误")
    private String houseUnit;

    @ApiModelProperty(value = "房屋租售平方米")
    @Range(groups = {addLeaseSaleHouse.class}, min = 1, max =BusinessConst.HOUSE_SQUARE_METER_MAX, message = "房屋面积请输入一个有效的值")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未输入房屋面积")
    private BigDecimal houseSquareMeter;

    //值是变动  需要存id至数据库 对应 名称 有后台人员管理
    @ApiModelProperty(value = "房屋出租方式id /1.压一付一/2.压一付三/3.压一付六")
    private Short houseLeasedepositId;

    //值是变动  需要存id至数据库 对应 名称 有后台人员管理
    @ApiModelProperty(value = "房屋类型id：1.四室一厅、2.二室一厅...")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未选择房屋类型")
    private Short houseTypeId;

    @ApiModelProperty(value = "房屋所属楼层")
    @Length(groups = {addLeaseSaleHouse.class}, min = 1, max = BusinessConst.HOUSE_FLOOR_CHAR_MAX, message = "楼层字符超过上限")
    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "房屋楼层未填写")
    private String houseFloor;

    //非 经常变动，常量存 BusinessEnum
    @ApiModelProperty(value = "房屋朝向1.东.2.西 3.南 4.北. 4.东南 5. 东北 6.西北 7.西南")
    @Range(groups = {addLeaseSaleHouse.class}, min = BusinessEnum.HouseDirectionEnum.min, max = BusinessEnum.HouseDirectionEnum.max, message = "房屋朝向未选择正确!可用范围：1.东 2.西 3.南 4.北. 4.东南 5.北 6.西北 7.西南")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "房屋朝向未选择!")
    private String houseDirection;

    @ApiModelProperty(value = "房屋是否有电梯：1有 0无")
    @Range(groups = {addLeaseSaleHouse.class}, min = 0, max = 1, message = "电梯选择可用范围不正确")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "电梯未选择!")
    private Short houseHasElevator;

    //值是变动  需要存id至数据库 对应 名称 由后台人员管理
    @ApiModelProperty(value = "房屋装修样式ID、1、精装修2、现代风格、3.古典风格、4.欧美风")
    private Short houseStyleId;

    //值是变动  需要存id至数据库 对应 名称 由后台人员管理
    @ApiModelProperty(value = "房源类型ID、73不限(默认) 74可短租 75邻地铁  76压一付一  77配套齐全  78精装修 79南北通透  80有阳台")
    private Short houseSourcetypeId;

    @ApiModelProperty(value = "房屋年代")
    @Pattern(groups = {addLeaseSaleHouse.class}, regexp = RegexUtils.REGEX_YEAR , message = "房屋年代过于久远!")
    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "房屋年代未选择!")
    private String houseYear;

    //非经常变动，值存BusinessEnum
    @ApiModelProperty(value = "房屋用途ID、1住宅、2工商业、3仓库")
    @Range(groups = {addLeaseSaleHouse.class}, min = BusinessEnum.HouseUsageEnum.min, max = BusinessEnum.HouseUsageEnum.max, message = "没有这种房屋用途")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "房屋用途未选择")
    private Short houseUsageId;

    //非经常变动，值存BusinessEnum
    @ApiModelProperty(value = "房屋种类id 1.商品房、2.经济适用房、3.央产房、4.军产房、5.公房、6.小产权房、7.自建住房")
    @Range(groups = {addLeaseSaleHouse.class}, min = BusinessEnum.HouseKindEnum.min, max = BusinessEnum.HouseKindEnum.max, message = "房屋种类错误")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "房屋类型未选择")
    private Short houseKindId;

    @ApiModelProperty(value = "房屋介绍内容")
    @Length(groups = {addLeaseSaleHouse.class}, min = 1, max = BusinessConst.HOUSE_INTRODUCE_CHAR_MAX, message = "房屋介绍内容字符在1~1000之间")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "房屋没有介绍!对你的房屋来一句简介")
    private String houseIntroduce;

    @ApiModelProperty(value = "房屋图片数组地址")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "您的房屋没有图片!请上传至少一张实景")
    private String[] houseImage;

    //t_house_lease  数据库house_image_id保存图片的id
    private String houseImageId;

    //65不限 66普通住宅 67别墅 68公寓
    @ApiModelProperty(value = "房屋出租类型ID")
    //业务层效验值的有效性
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未填写出租类型!")
    private Integer houseLeasetypeId;


    //69不限 70整租，71合租
    @ApiModelProperty(value = "房屋出租方式ID")
    //业务层效验 值有效性
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未填写出租方式!")
    private Integer houseLeaseymodeId;

    /**
     * 新增房屋租售验证接口
     */
    public interface addLeaseSaleHouse{};


}