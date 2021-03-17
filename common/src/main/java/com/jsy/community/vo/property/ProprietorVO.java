package com.jsy.community.vo.property;

import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  物业端VO业主查询信息返回实体类
 * @author YuLF
 * @since  2020/11/30 11:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("业主信息查询信息返回")
public class ProprietorVO extends BaseVO {

	/**
	 * excel 导入返回对象构造函数
	 * @param successNumber				成功数量
	 * @param failNumber				失败数量
	 * @param failExcelDetailsAddress	失败excel明细文件下载地址
	 */
	public ProprietorVO (Integer successNumber, Integer failNumber, String failExcelDetailsAddress){
		this.successNumber = successNumber;
		this.failNumber = failNumber;
		this.failExcelDetailsAddress = failExcelDetailsAddress;
	}

	public ProprietorVO(){}

	@ApiModelProperty("用户ID")
	private String uid;

	@ApiModelProperty("社区ID")
	private Long communityId;

	@ApiModelProperty("业主ID")
	private Long householderId;

	@ApiModelProperty("房屋ID")
	private Long houseId;

	@ApiModelProperty("房屋编号")
	private String houseNumber;

	@ApiModelProperty("昵称")
	private String nickname;

	@ApiModelProperty("头像地址")
	private String avatarUrl;

	@ApiModelProperty("电话号码")
	private String mobile;

	@ApiModelProperty("性别，0未知，1男，2女")
	private Integer sex;

	@ApiModelProperty("性别，未知，男，女")
	private String gender;

	@ApiModelProperty("年龄")
	private String age;

	@ApiModelProperty("真实姓名")
	private String realName;

	@ApiModelProperty("身份证")
	private String idCard;

	@ApiModelProperty("微信")
	private String wechat;

	@ApiModelProperty("腾讯qq")
	private String qq;

	@ApiModelProperty("联系邮箱")
	private String email;

	@ApiModelProperty("是否实名认证")
	private Integer isRealAuth;

	@ApiModelProperty("省ID")
	private Integer provinceId;

	@ApiModelProperty("市ID")
	private Integer cityId;

	@ApiModelProperty("区ID")
	private Integer areaId;

	@ApiModelProperty("详细地址")
	private String detailAddress;

	@ApiModelProperty("省名")
	private String provinceName;

	@ApiModelProperty("市名")
	private String cityName;

	@ApiModelProperty("区名")
	private String areaName;

	@ApiModelProperty("创建时间")
	private String createTime;

	@ApiModelProperty("创建人")
	private String createBy;

	@ApiModelProperty("最近更新时间")
	private String updateTime;

	@ApiModelProperty("更新人")
	private String updateBy;

	@ApiModelProperty("房屋合并后的字符串")
	private String houseMergeName;

	@ApiModelProperty("错误信息备注,方便标记excel导入错误信息的回显")
	private String remark;

	@ApiModelProperty("excel导入成功条数")
	private Integer successNumber;

	@ApiModelProperty("excel导入失败条数")
	private Integer failNumber;

	@ApiModelProperty("excel导入失败文件下载地址")
	private String failExcelDetailsAddress;

}
