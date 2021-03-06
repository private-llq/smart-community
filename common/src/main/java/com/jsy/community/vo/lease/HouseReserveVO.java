package com.jsy.community.vo.lease;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 房屋预约返回值对象
 * @author YuLF
 * @since  2021/2/20 15:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋租售预约参数返回对象", description="主要用于返回参数")
public class HouseReserveVO implements Serializable {

    @ApiModelProperty(value = "预约信息ID")
    private Long id;

    @ApiModelProperty(value = "房源信息ID")
    private Long houseLeaseId;

    @ApiModelProperty(value = "预约所属人ID")
    private String reserveUid;

    @ApiModelProperty(value = "预约所属推送ID")
    private String pushId;

    @ApiModelProperty(value = "预约状态 1.预约中 2.预约成功 3.已取消 4.已完成")
    private Integer reserveStatus;

    @ApiModelProperty(value = "预约状态文本 1.预约中 2.预约成功 3.已取消 4.已完成")
    private String reserveStatusText;

    @ApiModelProperty(value = "出租状态：合租、整租")
    private String houseLeaseMode;

    @ApiModelProperty(value = "出租押金方式")
    private String houseLeaseDeposit;

    @ApiModelProperty(value = "出租房屋类型：三室一厅.四室一厅...")
    private String houseType;

    @ApiModelProperty(value = "出租房屋平方米")
    private String houseSquareMeter;

    @ApiModelProperty(value = "出租房屋朝向Id")
    private Integer houseDirectionId;

    @ApiModelProperty(value = "出租房屋朝向")
    private String houseDirection;

    @ApiModelProperty(value = "出租房屋标题")
    private String houseTitle;

    @ApiModelProperty(value = "出租房屋价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "出租房屋价格单位：年 月 周")
    private String houseUnit;

    @ApiModelProperty(value = "出租房屋图片ID")
    private Long houseImageId;

    @ApiModelProperty(value = "出租房屋图片地址")
    private List<String> houseImageUrl;

    @ApiModelProperty(value = "预约信息内容")
    private String reserveMsg;

    @ApiModelProperty(value = "预约联系人用户名, 如果预约信息是预约我自己的，那这里的名称就是租客的，如果是我预约其他人的房源，那这里的用户名就是其他人的用户名")
    private String contactName;

    @ApiModelProperty(value = "预约联系人头像地址")
    private String contactAvatar;

    @ApiModelProperty(value = "看房时间")
    @JsonFormat(pattern = "MM-dd HH:mm", timezone = "GMT+8")
    private Date checkingTime;

    @ApiModelProperty(value = "是房东还是租客,true为房东,false为租客")
    private Boolean proprietor;

    @ApiModelProperty(value = "社区id")
    private Long houseCommunityId;

    @ApiModelProperty(value = "社区名称")
    private String houseCommunityName;

}
