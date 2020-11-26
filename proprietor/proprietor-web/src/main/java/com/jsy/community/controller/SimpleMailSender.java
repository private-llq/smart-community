package com.jsy.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chq459799974
 * @description TODO
 * @since 2020-11-26 16:43
 **/
@Service
public class SimpleMailSender {
	/**
	 * 日志工具
	 */
	
	
	private JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
	
	@Autowired
	private TemplateEngine templateEngine;
	
//	@Autowired
//	private Environment environment;
	
	public void sendTemplateMail() throws MessagingException {
		
		String host = "smtp.qq.com";
		String from = "459799974@qq.com";
		String to = "441055441@qq.com";
		
		
		
		javaMailSender.setHost(host);
		javaMailSender.setPort(25);
		javaMailSender.setUsername(from);
		javaMailSender.setPassword("nfezhwyeduksbjjh");
		javaMailSender.setDefaultEncoding("UTF-8");
		
		// 使用Mime消息体
		MimeMessage message = javaMailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setFrom(from);
		helper.setTo(to);
		
		helper.setSubject("测试邮件标题");
		
		// 根据模板、变量生成内容
		
		// 数据模型
		List<Pet> pets = new ArrayList<Pet>();
		pets.add(new Pet("Polly", "Bird", 2));
		pets.add(new Pet("Tom", "Cat", 5));
		pets.add(new Pet("Badboy", "Dog", 3));
		
		Context context = new Context();
		context.setVariable("customer", "LiLei");
		context.setVariable("pets", pets);
		
		String text = templateEngine.process("mail/template.html", context);
		helper.setText(text, true);
		
		javaMailSender.send(message);
	}
}
