package com.jsy.community.dto.face.xu;

import lombok.Data;

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
	
}
