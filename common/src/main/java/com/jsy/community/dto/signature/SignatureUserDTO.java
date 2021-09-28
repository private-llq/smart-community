package com.jsy.community.dto.signature;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 签章用户
 * @since 2021-02-23 18:22
 **/
@Data
public class SignatureUserDTO implements Serializable {
	private String uuid;//用户uid
	private String imId;//用户聊天的imId
	private String nickName;//昵称
	private String telephone;//电话
	private String email;//邮箱
	private String image;//头像url
	
	private String idCardName;//实名
	private String idCardNumber;//身份证号
	private String idCardAddress;//身份证地址
}
