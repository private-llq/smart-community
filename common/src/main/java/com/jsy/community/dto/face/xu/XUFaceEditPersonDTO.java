package com.jsy.community.dto.face.xu;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description 炫优人脸识别二维码刷卡一体机(mqtt) 人员实体类 需结合EditPerson.json模板
 * @since 2021-01-27 10:56
 **/
@Data
public class XUFaceEditPersonDTO extends XUFaceBaseDTO {
	
	//人员唯一ID(厂家建议使用身份证号) (此为机器上新增还是修改操作的依据，不存在则新增，存在则修改)
	private String customId;
	//姓名
	private String name;
	//人脸头像Base64
	private String pic;
	//性别0.男1.女
	private Integer gender;
	//0:白名单1:黑名单
	private Integer personType;

	//名单类型
	//0:永久名单
	//1:临时名单1(时间段有效)
	//2:临时名单2(每天同一时间段有效)
	//3临时名单3(次数有效)
	private Integer tempCardType;

	// 临时名单开始时间
	private LocalDateTime cardValidBegin;

	// 临时名单结束时间
	private LocalDateTime cardValidEnd;

	// 临时名单3的有效次数
	private Integer EffectNumber;

	// 人员图片（URI地址）和pic2选1
	private String picURI;

	// 操作的设备序列号列表
	private Set<String> hardwareIds;

	// 社区ID
	private String communityId;

	// ID 卡(门禁卡)卡号
	private String RFIDCard;

}
