package com.jsy.community.service.impl;

import com.jsy.community.api.IHkFaceService;
import com.jsy.community.constant.Const;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author lihao
 * @ClassName HkFaceServiceImpl
 * @Date 2021/3/13  14:43
 * @Description TODO
 * @Version 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class HkFaceServiceImpl implements IHkFaceService {
	
	@Override
	public boolean openFaceCompare(String ip, short port, String sUsername, String sPassword) {
		return false;
	}
}
