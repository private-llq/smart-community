package com.jsy.community.qo.lease;

import com.jsy.community.annotation.FieldValid;
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

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author YuLF
 * @since  2021/1/13 17:52
 * 房屋租售数据传输对象
 * 用于业务层之间的数据传递
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋出租收参对象", description="接收前端参数")
public class HouseLeaseQO implements Serializable {

    @Range( groups = {UpdateWholeLeaseHouse.class, UpdateSingleRoomHouse.class}, min = 1, message = "房屋id无效!")
    @NotNull(groups = {UpdateWholeLeaseHouse.class, UpdateSingleRoomHouse.class}, message = "房屋id不能为空!")
    private Long id;

    @ApiModelProperty(value = "所属人ID")
    private String uid;

    @Range(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class, UpdateSingleRoomHouse.class}, min = 1 , message = "社区id不正确")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "社区id不能为空!")
    @ApiModelProperty(value = "社区id")
    private Long houseCommunityId;

    @Range(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,  UpdateWholeLeaseHouse.class}, min = 1 , message = "房屋id不正确")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "房屋id不能为空!")
    @ApiModelProperty(value = "房源id")
    private Long houseId;

    @ApiModelProperty(value = "房屋租售标题")
    @Length(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class}, min = 1, max = BusinessConst.HOUSE_TITLE_CHAR_MAX, message = "标题长度在1~32之间")
    @NotBlank(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "未填写租售标题!")
    private String houseTitle;

    @ApiModelProperty(value = "房屋租售所属省ID")
    private Long houseProvinceId;

    @ApiModelProperty(value = "房屋租售所属城市ID")
    // @Range(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class}, min = 1, max = BusinessConst.HOUSE_ID_RANGE_MAX, message = "请选择一个正确的城市Code!")
    // @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "城市未选择!")
    private Long houseCityId;


    @ApiModelProperty(value = "房屋租售所属区ID")
    private Long houseAreaId;

    @ApiModelProperty(value = "房屋租售详细地址")
    @Length(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,   UpdateWholeLeaseHouse.class}, min = 1, max =  BusinessConst.HOUSE_ADDRESS_CHAR_MAX, message = "房屋地址长度在1~128之间")
    @NotBlank(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "未填写房屋租售详细地址")
    private String houseAddress;

    @NotEmpty( groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class}, message = "房屋亮点标签至少一个!")
    @ApiModelProperty(value = "房屋租售亮点标签ID数组")
    private List<Long> houseAdvantageCode;

    @ApiModelProperty(value = "房屋租售优势ID")
    private Long houseAdvantageId;

//    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "经度不能为空!")
    @ApiModelProperty(value = "经度")
    private Double houseLon;

//    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "纬度不能为空!")
    @ApiModelProperty(value = "纬度")
    private Double houseLat;

    @Size(groups = {AddWholeLeaseHouse.class, UpdateWholeLeaseHouse.class}, min = 1, max = 64,message = "家具至少需要一个!")
    @NotNull( groups = {AddWholeLeaseHouse.class}, message = "房屋家具至少一个!")
    @ApiModelProperty(value = "房屋家具code")
    private List<Long> houseFurnitureCode;

    @ApiModelProperty(value = "房屋预约时间")
    private String houseReserveTime;

    @ApiModelProperty(value = "房屋家具code换算过的id")
    private Long houseFurnitureId;

    @ApiModelProperty(value = "房屋租售价格")
    @Range(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,   UpdateWholeLeaseHouse.class}, min = 1, max = Integer.MAX_VALUE, message = "价格没有在指定范围之内!")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class }, message = "未输入价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "房屋租售价格最小值")
    private BigDecimal housePriceMin;

    @ApiModelProperty(value = "房屋租售价格最大值")
    private BigDecimal housePriceMax;



    @ApiModelProperty(value = "房屋联系人电话")
    @Pattern(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,   UpdateWholeLeaseHouse.class}, regexp = RegexUtils.REGEX_MOBILE, message = "手机号不正确! 电信|联通|移动")
    @NotBlank(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class }, message = "手机号不能为空")
    private String houseContact;

    @ApiModelProperty(value = "房屋租售平方米")
    @Range(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class}, min = 1, max =BusinessConst.HOUSE_SQUARE_METER_MAX, message = "房屋面积请输入一个有效的值")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "未输入房屋面积")
    private BigDecimal houseSquareMeter;

    @ApiModelProperty(value = "房屋租售平方最小值")
    private BigDecimal houseSquareMeterMin;

    @ApiModelProperty(value = "房屋租售平方最大值")
    private BigDecimal houseSquareMeterMax;

    /**
     * 值是变动  需要存id至数据库 对应 名称 有后台人员管理
     */
    @ApiModelProperty(value = "房屋出租方式 1.押1付1  2.押1付3  4.押1付6 8.押2付3   16.半年付  32.年付")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "押金方式是必须选择的")
    private Integer houseLeasedepositId;

    /**
     * 值是变动  需要存id至数据库 对应 名称 有后台人员管理
     * 1:一室;2:两室;3:三室;4:四室及以上
     */
    @ApiModelProperty(value = "房屋户型id：1.四室一厅、2.二室一厅...")
    // @Pattern(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,  UpdateWholeLeaseHouse.class}, regexp = "^[0-9]{6}$", message = "房屋类型Code必须为6位数字!")
    @NotBlank(groups = {AddWholeLeaseHouse.class,  AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "未选择房屋户型!")
    private String houseTypeCode;


    @ApiModelProperty(value = "房屋所属楼层")
    @Length(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class}, min = 1, max = BusinessConst.HOUSE_FLOOR_CHAR_MAX, message = "楼层字符超过上限")
    @NotBlank(groups = {AddWholeLeaseHouse.class,  AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "房屋楼层未填写")
    private String houseFloor;


    /**
     * 非 经常变动，常量存 BusinessEnum
     */
    @ApiModelProperty(value = "房屋朝向1.东.2.西 3.南 4.北. 4.东南 5. 东北 6.西北 7.西南")
    @Range(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class}, min = BusinessEnum.HouseDirectionEnum.min, max = BusinessEnum.HouseDirectionEnum.max, message = "房屋朝向未选择正确!可用范围：1.东 2.西 3.南 4.北. 4.东南 5.北 6.西北 7.西南")
    @NotNull(groups = {AddWholeLeaseHouse.class,  AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "房屋朝向未选择!")
    private Integer houseDirectionId;



    @ApiModelProperty(value = "卧室类型、主卧、次卧、其他")
    @FieldValid(groups = {AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class}, value = {"主卧", "次卧", "其他"}, message = "卧室类型错误!")
    @NotBlank( groups = {AddCombineLeaseHouse.class}, message = "卧室类型不能为空!")
    private String bedroomType;


    @ApiModelProperty(value = "房屋介绍内容")
    @Length(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class, UpdateWholeLeaseHouse.class}, min = 1, max = BusinessConst.HOUSE_INTRODUCE_CHAR_MAX, message = "房屋介绍内容字符在1~1000之间")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "房屋没有介绍!对你的房屋来一句简介")
    private String houseIntroduce;


    @ApiModelProperty(value = "房屋图片数组地址")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "您的房屋没有图片!请上传至少一张实景")
    @Size(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, min = 1, max = 8, message = "至少需要一张房屋实景图!")
    private String[] houseImage;


    @ApiModelProperty(value = "房主称呼")
    @Length(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,  UpdateWholeLeaseHouse.class}, min = 2, max = 4, message = "请输入一个正确的称呼!2~4字符")
    @NotBlank(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "房主称呼不能为空")
    private String appellation;

    /**
     * t_house_lease  数据库house_image_id保存图片的id
     */
    private Long houseImageId;


    @ApiModelProperty(value = "房源搜索文本")
    @Length(groups = {SearchLeaseHouse.class}, max = 32, message = "搜索最多32个字符!")
    private String searchText;


    @ApiModelProperty(value = "房屋出租类型ID：1不限(默认) 2普通住宅 4别墅 8公寓")
    @FieldValid( groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,  UpdateWholeLeaseHouse.class}, value = {"1", "2", "4", "8"}, message = "房屋出租类型ID不在可用范围内!")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "未填写出租类型!")
    private Integer houseLeasetypeId;


    @ApiModelProperty(value = "房屋出租方式ID: 1不限(默认) 2整租，4合租, 8合租 ")
    @FieldValid( groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class,  UpdateWholeLeaseHouse.class}, value = {"1", "2", "4", "8"}, message = "房屋出租方式ID不在可用范围内!")
    @NotNull(groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "未填写出租方式!")
    private Integer houseLeasemodeId;


    @ApiModelProperty( value = "出租要求Code：1.一家人 2.不养宠物 4.作息正常 8.组合稳定 16.禁烟")
    @Size( groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class,   UpdateWholeLeaseHouse.class}, min = 1, message = "出租要求至少选择一个!")
    @NotNull( groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class}, message = "出租要求不能为空!")
    private List<Long> leaseRequireCode;


    @ApiModelProperty( value = "出租要求位运算后的id")
    private Long leaseRequireId;


    @ApiModelProperty( value = "公共设施Code")
    private List<Long> commonFacilitiesCode;


    @ApiModelProperty( value = "公共设施位运算后的id")
    private Long commonFacilitiesId;


    @ApiModelProperty( value = "房间设施Code")
    @Size( groups = {AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, min = 1, message = "房间设施至少选择一个!")
    @NotNull( groups = {AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "房间设施不能为空!")
    private List<Long> roomFacilitiesCode;


    @ApiModelProperty( value = "房间设施位运算后的id")
    private Long roomFacilitiesId;


    @ApiModelProperty( value = "装修情况codeId：1.简单装修 2.精装修 4.豪华装修")
    @NotNull( groups = {AddWholeLeaseHouse.class, AddSingleRoomLeaseHouse.class, AddCombineLeaseHouse.class}, message = "装修情况未选择!")
    private Long decorationTypeId;


    @ApiModelProperty( value = "室友期望Code：1.一个人住,2.不养宠物,4.作息正常")
    private List<Long> roommateExpectCode;


    @ApiModelProperty( value = "室友期望Code运算后的Id")
    private Long roommateExpectId;


    @ApiModelProperty( value = "室友性别文本: 1.限女生 2.限男生 4.男女不限")
    @FieldValid(groups = {AddCombineLeaseHouse.class,   UpdateWholeLeaseHouse.class}, value = {"男女不限","限女生","限男生"})
    @NotNull( groups = {AddCombineLeaseHouse.class}, message = "室友性别未选择!")
    private String roommateSex;

    /**
     * 类型
     */
    private String shopTypeId;
    private Long[] shopTypeIdArrays;

    /**
     * 行业
     */
    private String shopBusinessId;
    private Long[] shopBusinessIdArrays;
    /**
     * 来源
     */
    private Short houseSourceId;


    /**
     * 【合租】新增房屋租售验证接口参数验证
     */
    public interface AddCombineLeaseHouse {}
    /**
     * 【单间】单间新增房屋租售验证接口参数验证
     */
    public interface AddSingleRoomLeaseHouse {}

    /**
     * 【整租】整租新增房屋租售验证接口参数验证
     */
    public interface AddWholeLeaseHouse {}

    /**
     * 【整租】修改房屋参数验证接口
     */
    public interface UpdateWholeLeaseHouse {}

    /**
     * 【单间】修改房屋参数验证接口
     */
    public interface UpdateSingleRoomHouse {}

    /**
     * 搜索文本参数验证
     */
    public interface SearchLeaseHouse {}



}
