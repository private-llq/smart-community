package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 访客进出记录实体类
 * @since 2021-04-13 13:45
 **/
@Data
@TableName("t_visitor_history")
public class VisitorHistoryEntity extends BaseEntity{
	
	private Long communityId;//社区ID
	private Long visitorId;//访客登记记录关联ID
	private String name;//登记主访客姓名
	private Integer accessType;//本次进出方式1.二维码 2.人脸识别
	private LocalDate startTime;//预计来访开始时间
	
	//其他表数据
	@TableField(exist = false)
	private String contact;//来访人联系方式
	
	@TableField(exist = false)
	private String carPlate;//来访车辆车牌
	
	@TableField(exist = false)
	private Integer reason;//来访事由ID 1.一般来访 2.应聘来访 3.走亲访友 4.客户来访
	
	@TableField(exist = false)
	private String address;//来访地址
	
	@TableField(exist = false)
	private LocalDateTime vCreateTime;//访客邀请登记时间(t_visitor创建时间)
	
	@TableField(exist = false)
	private Integer isCommunityAccess;//是否授予来访人社区门禁权限，0无，1二维码通行证，2人脸识别
	
	@TableField(exist = false)
	private Integer isBuildingAccess;//是否授予来访人楼栋门禁权限，0无，1二维码通行证，2可视对讲
	
	@TableField(exist = false)
	private Integer status;//状态 1.待入园 2.已入园 3.已出园 4.已失效
	
	@TableField(exist = false)
	private Long followCount;//随行人员统计
	
}
