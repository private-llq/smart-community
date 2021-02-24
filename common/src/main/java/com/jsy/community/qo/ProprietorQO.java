package com.jsy.community.qo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.UserHouseEntity;
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

    @NotNull(groups = {PropertyUpdateValid.class}, message = "用户id为空")
    @ApiModelProperty("数据id")
    private Long id;

    @ApiModelProperty("用户id")
    private String uid;

    @Range(groups = {PropertyUpdateValid.class}, min = 1, message = "非法业主Id")
    @ApiModelProperty("业主ID")
    private Long householderId;

    @Length(groups = {PropertyUpdateValid.class}, min = 1, max = 32, message = "昵称长度请在1~32之间")
    @ApiModelProperty("昵称")
    private String nickname;

    @Pattern(groups = {PropertyUpdateValid.class}, regexp = RegexUtils.REGEX_URL, message = "头像地址不正确")
    @ApiModelProperty("头像地址")
    private String avatarUrl;

    @Pattern(groups = {ProprietorRegister.class}, regexp = RegexUtils.REGEX_URL, message = "人脸URL地址不正确")
    @NotBlank(groups = {ProprietorRegister.class}, message = "人脸未上传!")
    @ApiModelProperty("人脸头像地址")
    private String faceUrl;

    @Pattern(groups = {PropertyUpdateValid.class}, regexp = RegexUtils.REGEX_MOBILE, message = "电话号码错误，只支持电信|联通|移动")
    @ApiModelProperty("电话号码")
    private String mobile;

    @Range(groups = {PropertyUpdateValid.class, ProprietorRegister.class}, min = 0, max = 2, message = "性别不可用")
    @NotNull(groups = {ProprietorRegister.class}, message = "性别不能为空")
    @ApiModelProperty("性别，0未知，1男，2女")
    private Integer sex;

    @Pattern(groups = {PropertyUpdateValid.class, ProprietorRegister.class}, regexp = RegexUtils.REGEX_REAL_NAME, message = "请输入一个正确的姓名")
    @ApiModelProperty("真实姓名")
    @NotBlank(groups = {ProprietorRegister.class}, message = "姓名未填写!")
    private String realName;

    @Pattern(groups = {PropertyUpdateValid.class, ProprietorRegister.class}, regexp = RegexUtils.REGEX_ID_CARD, message = "身份证错误")
    @NotBlank(groups = {ProprietorRegister.class}, message = "身份证号码未输入!")
    @ApiModelProperty("身份证")
    private String idCard;

    @Range(groups = {PropertyUpdateValid.class}, min = 0, max = 1, message = "非法实名认证信息")
    @ApiModelProperty("是否实名认证")
    private Integer isRealAuth;

    @Range(groups = {PropertyUpdateValid.class}, min = 1 , max = Integer.MAX_VALUE, message = "省ID不可用")
    @ApiModelProperty("省ID")
    private Integer provinceId;

    @Range(groups = {PropertyUpdateValid.class}, min = 1 , max = Integer.MAX_VALUE, message = "市ID不可用")
    @ApiModelProperty("市ID")
    private Integer cityId;

    @Range(groups = {PropertyUpdateValid.class}, min = 1 , max = Integer.MAX_VALUE, message = "区ID不可用")
    @ApiModelProperty("区ID")
    private Integer areaId;

    @Length(groups = {PropertyUpdateValid.class}, min = 1, max = 128, message = "详细地址字符长度请在1~128之间")
    @ApiModelProperty("详细地址")
    private String detailAddress;

    @ApiModelProperty("标记是否需要登记车辆")
    @TableField( exist = false )
    private Boolean hasCar;

    @ApiModelProperty("车辆集合")
    private List<CarQO> cars;

    @ApiModelProperty("用来存储House的id和社区id")
    private List<UserHouseQo> houses;

    /**
     * [物业]业主更新效验接口
     */
    public interface PropertyUpdateValid {}


    /**
     * [业主] 登记接口
     */
    public interface ProprietorRegister{}
}
