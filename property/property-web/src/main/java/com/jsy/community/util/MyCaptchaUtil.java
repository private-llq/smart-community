package com.jsy.community.util;

import com.google.code.kaptcha.Producer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 验证码工具类
 * @since 2020-12-18 16:08
 **/
@Component
public class MyCaptchaUtil {
	@Resource
	private Producer producer;
	
	public Map<String,Object> getCaptcha() {
		Map<String, Object> map = new HashMap<>();
		// 生成文字验证码
		String code = producer.createText();
		//生成图片
		BufferedImage image = producer.createImage(code);
		map.put("code",code);
		map.put("image",image);
		return map;
	}
}
