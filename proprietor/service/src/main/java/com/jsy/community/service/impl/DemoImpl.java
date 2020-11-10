package com.jsy.community.service.impl;

import com.jsy.community.api.Demo;
import com.jsy.community.constant.Const;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = Const.version, group = Const.group)
public class DemoImpl implements Demo {
	@Override
	public String sayHello() {
		return "hello, cloud";
	}
}
