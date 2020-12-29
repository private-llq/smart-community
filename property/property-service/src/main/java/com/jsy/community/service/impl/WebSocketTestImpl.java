package com.jsy.community.service.impl;

import com.jsy.community.api.IWebSocketTest;
import com.jsy.community.config.impl.WebSocket;
import com.jsy.community.constant.Const;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lihao
 * @ClassName WebSocketTestImpl
 * @Date 2020/12/29  11:15
 * @Description TODO
 * @Version 1.0
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class WebSocketTestImpl implements IWebSocketTest {
	
	@Autowired
	private WebSocket webSocket;
	
	@Override
	public void senMsg(String uid) {
		webSocket.sendMessage(uid+"：我下单了");
	}
}
