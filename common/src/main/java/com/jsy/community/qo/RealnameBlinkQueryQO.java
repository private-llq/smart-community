package com.jsy.community.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chq459799974
 * @description 三要素实人验证(眨眼版) 查询认证结果 所需参数
 * @since 2021-03-02 16:01
 **/
@Data
public class RealnameBlinkQueryQO {
	@NotBlank(message = "缺少参数bizId")
	private String bizId; //业务ID (app端传入)
	@NotBlank(message = "缺少参数certifyId")
	private String certifyId; //认证ID (app端传入)
	@NotBlank(message = "缺少参数身份证地址")
	private String detailAddress; //身份证地址 (身份证识别后，APP最后一起提交)
}
