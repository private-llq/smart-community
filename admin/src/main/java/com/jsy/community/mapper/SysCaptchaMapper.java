package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.SysCaptchaEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 验证码
 */
@Mapper
public interface SysCaptchaMapper extends BaseMapper<SysCaptchaEntity> {

	@Insert("replace into t_sys_captcha(uuid,code,expire_time,create_time) values(#{entity.uuid},#{entity.code},#{entity.expireTime},now())")
	void updateCaptcha(@Param("entity") SysCaptchaEntity entity);
	
}
