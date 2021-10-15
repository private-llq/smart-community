package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description: 物业公司
 * @author: Hu
 * @create: 2021-08-20 14:58
 **/
@Data
@TableName("t_property_company")
public class PropertyCompanyEntity extends BaseEntity {
    
    @ApiModelProperty(value = "公司名称")
    private String name;
    
    @ApiModelProperty(value = "联系人")
    private String contacts;
    
    @ApiModelProperty(value = "联系人电话")
    private String contactsMobile;
    
    @ApiModelProperty(value = "地区")
    private String region;
    
    @ApiModelProperty(value = "详细地址")
    private String address;
    
    @ApiModelProperty(value = "开始时间")
    private LocalDate beginTime;
    
    @ApiModelProperty(value = "到期时间")
    private LocalDate overTime;
    
    @ApiModelProperty(value = "经度")
    private String longitude;
    
    @ApiModelProperty(value = "纬度")
    private String latitude;
    
    @ApiModelProperty(value = "公司简介")
    private String profile;
    
    @ApiModelProperty(value = "公司图片 以逗号分割")
    private String picture;
    
    @ApiModelProperty(value = "短信剩余数量")
    private Integer messageQuantity;
    
    @ApiModelProperty(value = "用户注册状态（1.开启 2.关闭）")
    private Integer userRegisterStatus;
    
    @ApiModelProperty(value = "物业缴费状态（1.开启 2.关闭）")
    private Integer propertyPayStatus;
    
    @ApiModelProperty(value = "物业通知状态（1.开启 2.关闭）")
    private Integer propertyNoticeStatus;
    
    @ApiModelProperty(value = "小区公告状态（1.开启 2.关闭）")
    private Integer communityAnnouncementStatus;
    
    @ApiModelProperty(value = "车禁月租车到期状态（1.开启 2.关闭）")
    private Integer carBanExpiresStatus;
    
    @ApiModelProperty(value = "挪车通知状态（1.开启 2.关闭）")
    private Integer carMovingNoticeStatus;
    
    @ApiModelProperty(value = "报修通知状态（1.开启 2.关闭）")
    private Integer repairNoticeStatus;

}
