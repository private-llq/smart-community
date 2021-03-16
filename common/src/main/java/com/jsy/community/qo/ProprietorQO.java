package com.jsy.community.qo;

import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.qo.proprietor.UserHouseQo;
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
import java.util.List;

/**
 * 业主更新接口 - 接收业主参数QO
 * @author YuLF
 * @since  2020/11/30 10:21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Proprietor查询对象", description="业主信息")
public class ProprietorQO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Range( groups = {PropertyUpdateValid.class}, min = 1, message = "数据id范围不正确!")
    @NotNull(groups = {PropertyUpdateValid.class}, message = "数据id为空")
    @ApiModelProperty("数据id")
    private Long id;


    @Range( groups = {PropertyAddValid.class,PropertyUpdateValid.class}, min = 1, message = "社区id范围不正确!")
    @NotNull(groups = {PropertyAddValid.class, PropertyUpdateValid.class}, message = "社区id为空")
    @ApiModelProperty("社区id")
    private Long communityId;


    @ApiModelProperty("业主ID")
    private Long householderId;


    @ApiModelProperty("用户id")
    private String uid;


    @Range( groups = {PropertyAddValid.class, PropertyUpdateValid.class, PropertySearchValid.class}, min = 1, message = "房屋id范围不正确!")
    @NotNull( groups = {PropertyAddValid.class, PropertyUpdateValid.class}, message = "房屋未选择!房屋Id为空")
    @ApiModelProperty("房屋id")
    private Long houseId;


    @Range( groups = {PropertySearchValid.class}, min = 1, message = "楼栋id范围不正确!")
    @ApiModelProperty("楼栋id")
    private Long buildingId;


    @Range( groups = {PropertySearchValid.class}, min = 1, message = "单元id范围不正确!")
    @ApiModelProperty("单元id")
    private Long unitId;


    @Pattern(groups = {PropertyAddValid.class, PropertyUpdateValid.class}, regexp = RegexUtils.REGEX_WE_CHAT, message = "微信号不正确!")
    @ApiModelProperty("微信")
    private String wechat;


    @Pattern(groups = {PropertyAddValid.class, PropertyUpdateValid.class}, regexp = RegexUtils.REGEX_QQ, message = "QQ号不正确!")
    @ApiModelProperty("qq")
    private String qq;


    @ApiModelProperty("性别")
    private Integer sex;


    @Pattern(groups = {PropertyAddValid.class, PropertyUpdateValid.class}, regexp = RegexUtils.REGEX_EMAIL, message = "邮箱号不正确!")
    @ApiModelProperty("邮箱")
    private String email;


    @Pattern(groups = {PropertyUpdateValid.class, PropertyAddValid.class}, regexp = RegexUtils.REGEX_MOBILE, message = "电话号码错误，只支持电信|联通|移动")
    @NotBlank( groups = {PropertyAddValid.class}, message = "电话号码不能为空!")
    @ApiModelProperty("电话号码")
    private String mobile;


    /**
     * 第一版  只按照汉族的姓名 验证
     */
    @Pattern(groups = {PropertyAddValid.class, PropertyUpdateValid.class}, regexp = RegexUtils.REGEX_REAL_NAME, message = "请输入一个正确的姓名")
    @Length( groups = {PropertyAddValid.class, PropertyUpdateValid.class}, min = 2, max = 20, message = "姓名长度不在正常范围之内!")
    @ApiModelProperty("真实姓名")
    @NotBlank(groups = {PropertyAddValid.class}, message = "姓名未填写!")
    private String realName;


    @Pattern(groups = {PropertyUpdateValid.class, PropertyAddValid.class}, regexp = RegexUtils.REGEX_ID_CARD, message = "身份证错误")
    @NotBlank(groups = {PropertyAddValid.class}, message = "证件号码未输入!")
    @ApiModelProperty("证件号码")
    private String idCard;


    @ApiModelProperty("证件类型：1.身份证 2.护照")
    private Integer identificationType = 1;


    @ApiModelProperty("人脸url")
    private String faceUrl;


    @ApiModelProperty("标记是否需要登记车辆")
    private Boolean hasCar;


    @ApiModelProperty("搜索字段")
    private String searchText;


    @ApiModelProperty("车辆集合")
    private List<CarQO> cars;


    @ApiModelProperty("用来存储House的id和社区id")
    private List<UserHouseQo> houses;


    /**
     * [物业]业主查询效验接口
     */
    public interface PropertySearchValid {}

    /**
     * [物业]业主更新效验接口
     */
    public interface PropertyUpdateValid {}

    /**
     * [物业]业主新增效验接口
     */
    public interface PropertyAddValid {}

}
