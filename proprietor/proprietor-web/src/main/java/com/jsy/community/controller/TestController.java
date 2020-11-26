package com.jsy.community.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.MessagingException;

/**
 * @author chq459799974
 * @description TODO
 * @since 2020-11-26 16:54
 **/
@RestController
@RequestMapping("test123")
public class TestController {
	@Resource
	private SimpleMailSender simpleMailSender;
	
	@GetMapping("test456")
	public String test456() throws MessagingException {
		simpleMailSender.sendTemplateMail();
		return "success...";
	}
}
