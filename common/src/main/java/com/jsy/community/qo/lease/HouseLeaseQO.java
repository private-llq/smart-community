package com.jsy.community.qo.lease;

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
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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

    @NotNull(groups = {updateLeaseSaleHouse.class}, message = "房屋id不能为空!")
    private Long id;

    @ApiModelProperty(value = "所属人ID")
    private String uid;

    @Range(groups = {addLeaseSaleHouse.class}, min = 1 , message = "社区id不正确")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "社区id不能为空!")
    @ApiModelProperty(value = "社区id")
    private Integer houseCommunityId;

    @Range(groups = {addLeaseSaleHouse.class}, min = 1 , message = "房屋id不正确")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "房屋id不能为空!")
    @ApiModelProperty(value = "房源id")
    private Integer houseId;

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


    @Size(groups = {addLeaseSaleHouse.class}, min = 1,  message = "房屋标签至少需要一个!")
    @ApiModelProperty(value = "房屋租售优势标签ID数组")
    private List<Long> houseAdvantage;


    @ApiModelProperty(value = "房屋租售优势ID")
    private Long houseAdvantageId;


    @NotNull(groups = {addLeaseSaleHouse.class}, message = "经度不能为空!")
    @ApiModelProperty(value = "经度")
    private Double houseLon;

    @NotNull(groups = {addLeaseSaleHouse.class}, message = "纬度不能为空!")
    @ApiModelProperty(value = "纬度")
    private Double houseLat;

    @Size(groups = {addLeaseSaleHouse.class}, min = 1, max = 64,message = "家具至少需要一个!")
    @ApiModelProperty(value = "房屋家具code")
    private List<Long> houseFurniture;

    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "房屋可预约时间未选择!")
    @ApiModelProperty(value = "房屋预约时间")
    private String houseReserveTime;

    @ApiModelProperty(value = "房屋家具code换算过的id")
    private Long houseFurnitureId;

    @ApiModelProperty(value = "房屋租售价格")
    @Range(groups = {addLeaseSaleHouse.class}, min = 1, max = Integer.MAX_VALUE, message = "价格没有在指定范围之内!")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未输入价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "房屋租售价格最小值")
    private BigDecimal housePriceMin;

    @ApiModelProperty(value = "房屋租售价格最大值")
    private BigDecimal housePriceMax;

    @ApiModelProperty(value = "房屋出租单位/年/月/周/日")
    @Pattern(groups = {addLeaseSaleHouse.class}, regexp = RegexUtils.REGEX_DATE, message = "指定的日期单位没有在范围之内")
    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "日期单位错误")
    private String houseUnit;

    @ApiModelProperty(value = "房屋联系人电话")
    @Pattern(groups = {addLeaseSaleHouse.class}, regexp = RegexUtils.REGEX_MOBILE, message = "手机号不正确! 电信|联通|移动")
    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "手机号不能为空")
    private String houseContact;

    @ApiModelProperty(value = "房屋租售平方米")
    @Range(groups = {addLeaseSaleHouse.class}, min = 1, max =BusinessConst.HOUSE_SQUARE_METER_MAX, message = "房屋面积请输入一个有效的值")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未输入房屋面积")
    private BigDecimal houseSquareMeter;

    @ApiModelProperty(value = "房屋租售平方最小值")
    private BigDecimal houseSquareMeterMin;

    @ApiModelProperty(value = "房屋租售平方最大值")
    private BigDecimal houseSquareMeterMax;

    //值是变动  需要存id至数据库 对应 名称 有后台人员管理
    @ApiModelProperty(value = "房屋出租方式id /1.压一付一/2.压一付三/3.压一付六")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "押金方式是必须选择的")
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



    @ApiModelProperty(value = "房屋来源类型ID、1.个人 2.经纪人 3.不限")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "房屋来源类型ID未选择!")
    private Short houseSourceId;

    @ApiModelProperty(value = "房屋年代")
    @Pattern(groups = {addLeaseSaleHouse.class}, regexp = RegexUtils.REGEX_YEAR , message = "房屋年代过于久远!")
    @NotBlank(groups = {addLeaseSaleHouse.class}, message = "房屋年代未选择!")
    private String houseYear;


    @ApiModelProperty(value = "房屋介绍内容")
    @Length(groups = {addLeaseSaleHouse.class}, min = 1, max = BusinessConst.HOUSE_INTRODUCE_CHAR_MAX, message = "房屋介绍内容字符在1~1000之间")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "房屋没有介绍!对你的房屋来一句简介")
    private String houseIntroduce;

    @ApiModelProperty(value = "房屋图片数组地址")
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "您的房屋没有图片!请上传至少一张实景")
    @Size(groups = {addLeaseSaleHouse.class}, min = 1, max = 8, message = "至少需要一张房屋实景图,最大8张!")
    private String[] houseImage;

    //t_house_lease  数据库house_image_id保存图片的id
    private Long houseImageId;

    //65不限 66普通住宅 67别墅 68公寓
    @ApiModelProperty(value = "房屋出租类型ID")
    //业务层效验值的有效性
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未填写出租类型!")
    private Integer houseLeasetypeId;


    //69不限 70整租，71合租
    @ApiModelProperty(value = "房屋出租方式ID")
    //业务层效验 值有效性
    @NotNull(groups = {addLeaseSaleHouse.class}, message = "未填写出租方式!")
    private Integer houseLeasemodeId;

    /**
     * 新增房屋租售验证接口参数验证
     */
    public interface addLeaseSaleHouse{}

    /**
     * 修改房屋参数验证接口
     */
    public interface updateLeaseSaleHouse{}

}
