package com.jsy.community.dto.face.xu;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 炫优人脸识别二维码刷卡一体机(mqtt) 基础实体类
 * @since 2021-01-27 11:09
 **/
@Data
public class XUFaceBaseDTO implements Serializable {
	//通用messageId
	private String messageId;

	// 操作
	private String operator;
}
