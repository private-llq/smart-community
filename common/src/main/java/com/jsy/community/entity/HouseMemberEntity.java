package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private Long houseId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别，0未知，1男，2女
     */
    private Integer sex;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 与业主关系 1.业主 6.亲属，7租户
     */
    private Integer relation;
    /**
     * 出生日期
     */
    private LocalDate birthday;
    /**
     * 银行卡号
     */
    private String creditCard;
    /**
     * 入驻时间
     */
    private LocalDateTime enterTime;
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
     * 家庭电话
     */
    private String homeMobile;
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
     * 详细地址
     */
    private String site;
    /**
     * 暂住号码
     */
    private String stayNum;
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


}
