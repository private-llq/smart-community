package com.jsy.community.config.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
// 你的WebSocket访问地址
@ServerEndpoint("/webSocket")
@Slf4j
public class WebSocket {
	
	private Session session;
	
	//定义Websocket容器，储存session
	private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();
	
	//建立连接
	@OnOpen
	public void opOpen(Session session) {
		this.session = session;
		webSocketSet.add(this);
		log.info("建立连接，当前连接数为" + webSocketSet.size());
	}
	
	//关闭连接
	@OnClose
	public void onClose() {
		webSocketSet.remove(this);
	}
	
	//接收消息
	@OnMessage
	public void onMessage(String message) {
		System.out.println(message);
		log.info("接收到的消息为：" + message);
	}
	
	//服务器广播 发送消息【给每一个建立连接的用户】
	public void sendMessage(String message) {
		//遍历储存的Websocket
		for (WebSocket webSocket : webSocketSet) {
			try {
				//发送
				webSocket.session.getBasicRemote().sendText(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
