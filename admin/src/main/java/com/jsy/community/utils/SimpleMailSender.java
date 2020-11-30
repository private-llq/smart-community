package com.jsy.community.utils;

import com.jsy.community.entity.SysUserEntity;
import com.jsy.community.exception.JSYException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author chq459799974
 * @description 注册邮件发送与确认激活
 * @since 2020-11-26 16:43
 **/
@Slf4j
@Service
public class SimpleMailSender {
	
	@Value("${email.host}")
	private String host;
	
	@Value("${email.baseAccount}")
	private String from;
	
	@Value("${email.authPass}")
	private String authPass;
	
	private JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
	
	@Autowired
	private TemplateEngine templateEngine;
	
	public void sendRegisterEmail(String template, SysUserEntity sysUserEntity, String invitor)  {
		
		String to = sysUserEntity.getEmail();
		
		javaMailSender.setHost(host);
		javaMailSender.setPort(25);
		javaMailSender.setUsername(from);
		javaMailSender.setPassword(authPass);
		javaMailSender.setDefaultEncoding("UTF-8");
		
		// 使用Mime消息体
		MimeMessage message = javaMailSender.createMimeMessage();
		
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject("【智慧社区后台】用户注册");
			// 根据模板、变量生成内容
			// 数据模型
//			List<Pet> pets = new ArrayList<Pet>();
//			pets.add(new Pet("Polly", "Bird", 2));
//			pets.add(new Pet("Tom", "Cat", 5));
//			pets.add(new Pet("Badboy", "Dog", 3));
			
			Context context = new Context();
//			context.setVariable("pets", pets);
			context.setVariable("id", sysUserEntity.getId());
			context.setVariable("invitor", invitor);
			context.setVariable("invitee", sysUserEntity.getRealName());
			context.setVariable("email", sysUserEntity.getEmail());
			context.setVariable("password", sysUserEntity.getPassword());
			
			String text = templateEngine.process(template, context);
			helper.setText(text, true);
		} catch (MessagingException e) {
			log.error("邮件构建失败",e);
			throw new JSYException();
		}
		
		javaMailSender.send(message);
	}
}
