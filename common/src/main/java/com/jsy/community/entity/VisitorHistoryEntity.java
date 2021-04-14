package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 访客进出记录实体类
 * @since 2021-04-13 13:45
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_visitor_history")
public class VisitorHistoryEntity extends BaseEntity{
	
	private Long communityId;//社区ID
	private Long visitorId;//访客登记记录关联ID
	//兼容H5，使用字符串格式， 针对js long型长度不够的问题
	@TableField(exist = false)
	private String visitorIdStr;
	public String getVisitorIdStr(){
		return String.valueOf(visitorId);
	}
	private Integer accessType;//本次进出方式 1.二维码 2.人脸识别 (备用)
	private Integer accessDirection;//进出方向 1.入方向 2.出方向  (备用)
	private LocalDateTime inTime;//入园时间
	private LocalDateTime outTime;//出园时间
	
	//冗余字段
	private String name;//登记主访客姓名
	private String contact;//主访客联系方式
	private String carPlate;//来访车辆车牌
	
	//其他表数据(后期主表为t_visitor_history时，复制数据用)
	@TableField(exist = false)
	private Integer reason;//来访事由ID 1.一般来访 2.应聘来访 3.走亲访友 4.客户来访
	
	@TableField(exist = false)
	private String address;//来访地址
	
	@TableField(exist = false)
	private LocalDateTime vCreateTime;//访客邀请登记时间(t_visitor创建时间)
	
	@TableField(exist = false)
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private LocalDate startTime;//预计来访开始时间
	
	@TableField(exist = false)
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private LocalDate endTime;//预计来访开始时间
	
	@TableField(exist = false)
	private Integer isCommunityAccess;//是否授予来访人社区门禁权限，0无，1二维码通行证，2人脸识别
	
	@TableField(exist = false)
	private Integer isBuildingAccess;//是否授予来访人楼栋门禁权限，0无，1二维码通行证，2可视对讲
	
	@TableField(exist = false)
	private Integer status;//状态 1.待入园 2.已入园 3.已出园 4.已失效
	
	@TableField(exist = false)
	private Long followCount;//随行人员统计
	
	
	//查询用
	@TableField(exist = false)
	private Integer timeType;//1.按预计来访日期查询 2.按入园日期查询 3.按出园日期查询
	
}
