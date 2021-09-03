package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.utils.RegexUtils;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author qq459799974
 * @since 2020-11-23
 */
@Data
@TableName("t_house_member")
public class HouseMemberEntity extends BaseEntity {
    /**
     * 1迁入，2迁出
     */
    private Integer status;
    /**
     * 未注册为空，已注册则填入，注册时也需要填入
     */
    private String uid;
    /**
     * 房屋地址
     */
    @TableField(exist = false)
    private String houseSite;
    /**
     * 业主ID
     */
    private String householderId;
    /**
     * 社区ID
     */
    private Long communityId;
    /**
     * 房间ID
     */
    @NotNull(message = "房间不能为空！",groups = {SaveVerification.class})
    @Min(message = "房间不能为空",value = 1,groups = {SaveVerification.class})
    private Long houseId;

    /**
     * 房间ID
     */
    @TableField(exist = false)
    private String houseIdStr;

    public String getHouseIdStr() {
        return String.valueOf(houseId);
    }

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空",groups = {SaveVerification.class})
    private String name;
    /**
     * 性别，0未知，1男，2女
     */
    private Integer sex;
    /**
     * 手机号码
     */
    @Pattern(message = "手机号格式不正确！",regexp = RegexUtils.REGEX_MOBILE,groups = {SaveVerification.class})
    private String mobile;
    /**
     * 与业主关系 0.临时，1.业主，6.亲属，7租户
     */
    private Integer relation;
    /**
     * 与业主关系 0.临时，1.业主，6.亲属，7租户
     */
    @TableField(exist = false)
    private String relationName;
    /**
     * 标签：1独居，2孤寡，3残疾，4留守
     */
    private Integer tally;
    /**
     * 出生日期
     */
    private LocalDate birthday;
    /**
     * 银行卡号
     */
    private String creditCard;

    /**
     * 单位
     */
    private String unit;
    /**
     * 入驻时间
     */
    private LocalDate enterTime;
    /**
     * 入驻理由
     */
    private String enterReason;
    /**
     * 入驻照片
     */
    private String enterPicture;
    /**
     * qq
     */
    private String qq;
    /**
     * 微信
     */
    private String wechat;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 家庭电话
     */
    private String homeMobile;
    /**
     * 紧急联系人
     */
    private String exigencyName;
    /**
     * 紧急联系人
     */
    private String exigencyMobile;
    /**
     * 民族
     */
    private String nation;
    /**
     * 政治面貌
     */
    private String politicsStatus;
    /**
     * 婚姻状况0保密，1已婚，2未婚
     */
    private Integer maritalStatus;
    /**
     * 证件类型1.身份证 2.护照
     */
    private Integer identificationType;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 户口类型1农村户口，2城镇户口
     */
    private Integer familyType;
    /**
     * 户口地址
     */
    private String familySite;
    /**
     * 详细地址
     */
    private String site;
    /**
     * 暂住号码
     */
    private String stayNum;
    /**
     * 居住类型
     */
    private Integer liveType;
    /**
     * 宠物
     */
    private String pet;
    /**
     * 备注
     */
    private String remark;
    /**
     * 身份证照片
     */
    private String idCardPicture;
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 有效时间
     */
    private LocalDateTime validTime;


    //新增修改验证
    public interface SaveVerification{}


}
