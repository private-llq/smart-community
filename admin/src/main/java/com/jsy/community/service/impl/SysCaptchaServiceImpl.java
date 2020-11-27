package com.jsy.community.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.jsy.community.entity.SysCaptchaEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.SysCaptchaMapper;
import com.jsy.community.service.ISysCaptchaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * 验证码
 */
@Service
public class SysCaptchaServiceImpl extends ServiceImpl<SysCaptchaMapper, SysCaptchaEntity> implements ISysCaptchaService {
	@Resource
	private Producer producer;
	
	@Override
	public BufferedImage getCaptcha(String uuid) {
		if (StrUtil.isBlank(uuid)) {
			throw new JSYException("uuid不能为空");
		}
		// 生成文字验证码
		String code = producer.createText();
		
		SysCaptchaEntity captchaEntity = new SysCaptchaEntity();
		captchaEntity.setUuid(uuid);
		captchaEntity.setCode(code);
		// 5分钟后过期
		captchaEntity.setExpireTime(DateUtil.offset(new Date(), DateField.HOUR, 5));
		this.save(captchaEntity);
		
		return producer.createImage(code);
	}
	
	@Override
	public boolean validate(String uuid, String code) {
		SysCaptchaEntity captchaEntity = this.getOne(new QueryWrapper<SysCaptchaEntity>().eq("uuid", uuid));
		if (captchaEntity == null) {
			return false;
		}
		
		//删除验证码
		//this.remove(new QueryWrapper<SysCaptchaEntity>().eq("uuid",uuid));
		
		return captchaEntity.getCode().equalsIgnoreCase(code) && captchaEntity.getExpireTime().getTime() >= System.currentTimeMillis();
	}
}
