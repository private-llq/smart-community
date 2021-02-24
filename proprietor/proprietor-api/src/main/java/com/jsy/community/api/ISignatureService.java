package com.jsy.community.api;

import com.jsy.community.dto.signature.SignatureUserDTO;

import java.util.Map;

/**
 * @author chq459799974
 * @description 签章相关 Service
 * @since 2021-02-24 09:47
 **/
public interface ISignatureService {
	//POST 新增用户信息
	boolean insertUser(SignatureUserDTO signatureUserDTO);
	
	//POST 批量新增用户信息(物业端批量导入时)
	boolean batchInsertUser(SignatureUserDTO signatureUserDTO);
	
	//PUT 实名认证后修改签章用户信息
	boolean realNameUpdateUser(SignatureUserDTO signatureUserDTO);
	
	//PUT 修改用户普通信息
	boolean updateUser(SignatureUserDTO signatureUserDTO);
}
