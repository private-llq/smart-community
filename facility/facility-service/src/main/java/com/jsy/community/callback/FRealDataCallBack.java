package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;
import com.jsy.community.websocket.CommunityWebSocketHandler;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @return
 * @Author lihao
 * @Description 实时预览回调函数
 * @Date 2021/5/18 16:33
 * @Param
 **/
@Component
public class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
	
	//TODO 定时批量把这个对象格式化输出到文件
	private static Map ERROR_MSG_MAP = new HashMap();
	
	@Override
	public void invoke(NativeLong lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
		WebSocketSession webSocketSession = CommunityWebSocketHandler.SOCKET_SESSION_MAP.get("AdminUserId");
		if(webSocketSession != null){
//			System.out.println(new String(bytes));
			try {
//				int i=1/0;
				byte[] bytes = pBuffer.getPointer().getByteArray(0, dwBufSize);
				webSocketSession.sendMessage(new BinaryMessage(bytes));
			} catch (Exception e) {
				ERROR_MSG_MAP.put(e.getClass().getSimpleName(),e.getMessage());
			}
		}
		// 在回调函数里面写业务逻辑
	}
}