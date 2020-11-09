package com.jsy.service;

import com.jsy.community.api.Demo;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0", group = "test")
public class DemoImpl implements Demo {
	@Override
	public String sayHello() {
		return "hello, cloud";
	}
}
