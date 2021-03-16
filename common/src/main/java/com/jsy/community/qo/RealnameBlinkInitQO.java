package com.jsy.community.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chq459799974
 * @description 三要素实人验证(眨眼版) 初始化 所需参数
 * @since 2021-03-02 15:53
 **/
@Data
public class RealnameBlinkInitQO {
	@NotBlank(message = "缺少参数identityParam")
	private String identityParam; //客户端采用的RSA加密传输的身份信息Base64编码字符串
	@NotBlank(message = "缺少参数metaInfo")
	private String metaInfo; //metainfo 环境参数，需要通过客户端 SDK 获取
	@NotBlank(message = "缺少参数packageName")
	private String packageName; //包名
	@NotBlank(message = "缺少参数platform")
	private String platform; //平台：android,ios
}
