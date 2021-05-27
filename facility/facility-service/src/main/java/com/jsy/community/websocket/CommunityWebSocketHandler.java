package com.jsy.community.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("CommunityWebSocketHandler")
public class CommunityWebSocketHandler implements WebSocketHandler {

    @Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
		System.out.println(webSocketSession.getRemoteAddress() + "连接成功");
	}

	public static Map<String,WebSocketSession> SOCKET_SESSION_MAP = new ConcurrentHashMap<>();

	/**
	 * 处理方法
	 */
	@Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
    	System.out.println("接收msg");
    	String msg = webSocketMessage.getPayload().toString();
    	System.out.println("from session：" + webSocketSession.getId() + "，msg：" + msg);
//	    if("bye".equals(msg)){
//		    closeByServer(SOCKET_SESSION_MAP.get("AdminUserId"));
//	    }

    	//根据用户ID放入Session
		SOCKET_SESSION_MAP.put("AdminUserId",webSocketSession);
		System.out.println(SOCKET_SESSION_MAP);

	    webSocketSession.sendMessage(new TextMessage("start send"));

	    
//	    byte[] bytes = { 0x03,0x02,0x01 };
//	    webSocketSession.sendMessage(new BinaryMessage(bytes));

//	    System.out.println("用户手动暂停接收");
//	    closeByServer(webSocketSession);

//	    webSocketSession.sendMessage(new TextMessage("xxx"));
    }

	/**
	 * 服务器主动断开连接
	 */
    public void closeByServer(WebSocketSession webSocketSession) throws Exception{
	    System.out.println("请求主动断开");
    	if(webSocketSession != null){
		    SOCKET_SESSION_MAP.remove("AdminUserId");
            webSocketSession.close();
            System.out.println("主动断开");
    	}
    }

	/**
	 * 关闭后执行
	 */
	@Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
    	if(webSocketSession.isOpen()){
		    SOCKET_SESSION_MAP.remove("AdminUserId");
    		webSocketSession.close();
		    System.out.println("已关闭");
    	}
    }

	/**
	 * 异常断开
	 */
	@Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if(webSocketSession.isOpen()){
	        SOCKET_SESSION_MAP.remove("AdminUserId");
            webSocketSession.close();
            System.out.println("异常断开");
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
