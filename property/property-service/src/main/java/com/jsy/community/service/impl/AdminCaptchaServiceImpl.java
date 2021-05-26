package com.jsy.community.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminCaptchaService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminCaptchaEntity;
import com.jsy.community.mapper.AdminCaptchaMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 验证码
 */
@DubboService(version = Const.version, group = Const.group_property)
public class AdminCaptchaServiceImpl extends ServiceImpl<AdminCaptchaMapper, AdminCaptchaEntity> implements IAdminCaptchaService {

	@Resource
	private AdminCaptchaMapper adminCaptchaMapper;
	
	//保存验证码
	@Override
	public void saveCaptcha(AdminCaptchaEntity captchaEntity){
		//TODO 暂时5小时过期
		captchaEntity.setExpireTime(DateUtil.offset(new Date(), DateField.HOUR, 5));
		adminCaptchaMapper.updateCaptcha(captchaEntity);
	}
	
	@Override
	public boolean validate(String uuid, String code) {
		AdminCaptchaEntity captchaEntity = this.getOne(new QueryWrapper<AdminCaptchaEntity>().eq("uuid", uuid));
		if (captchaEntity == null) {
			return false;
		}
		
		//删除验证码
		//this.remove(new QueryWrapper<SysCaptchaEntity>().eq("uuid",uuid));
		
		return captchaEntity.getCode().equalsIgnoreCase(code) && captchaEntity.getExpireTime().getTime() >= System.currentTimeMillis();
	}
	
	
}
