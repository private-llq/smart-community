package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import org.springframework.stereotype.Component;


/**
 * @return
 * @Author lihao
 * @Description 实时预览回调函数
 * @Date 2021/5/18 16:33
 * @Param
 **/
@Component
public class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
	
	@Override
	public void invoke(NativeLong lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
//		byte[] bytes = pBuffer.getPointer().getByteArray(0, dwBufSize);
		// 在回调函数里面写业务逻辑
	}
}