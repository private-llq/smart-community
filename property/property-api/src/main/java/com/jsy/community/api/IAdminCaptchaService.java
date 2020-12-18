package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminCaptchaEntity;

/**
 * 验证码
 */
public interface IAdminCaptchaService extends IService<AdminCaptchaEntity> {
	
	/**
	* @Description: 保存验证码
	 * @Param: [captchaEntity]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/18
	**/
	void saveCaptcha(AdminCaptchaEntity captchaEntity);
	
	/**
	 * 验证码效验
	 *
	 * @param uuid uuid
	 * @param code 验证码
	 * @return true：成功  false：失败
	 */
	boolean validate(String uuid, String code);
}
