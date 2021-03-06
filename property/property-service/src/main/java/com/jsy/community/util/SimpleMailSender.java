package com.jsy.community.util;

import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
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
@Component
public class SimpleMailSender {
	
//	@Value("${email.host}")
	private String host = "smtp.qq.com";
	
//	@Value("${email.baseAccount}")
	private String from = "459799974@qq.com";
	
//	@Value("${email.authPass}")
	private String authPass = "nfezhwyeduksbjjh";
	
	private JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
	
	@Autowired
	private TemplateEngine templateEngine;
	
	public void sendRegisterEmail(String template, AdminUserEntity adminUserEntity)  {
		
		String to = adminUserEntity.getEmail();
		
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
			helper.setSubject("【智慧社区物业】用户注册");
			
			// 根据模板、变量生成内容
			Context context = new Context();
			context.setVariable("uid", adminUserEntity.getCreateUserId());
			context.setVariable("invitor", adminUserEntity.getCreateUserName());
			context.setVariable("invitee", adminUserEntity.getRealName());
			context.setVariable("email", adminUserEntity.getEmail());
			context.setVariable("password", adminUserEntity.getPassword());
			
			String text = templateEngine.process(template, context);
			helper.setText(text, true);
		} catch (MessagingException e) {
			log.error("邮件构建失败",e);
			throw new JSYException();
		}
		
		javaMailSender.send(message);
	}
}
