package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;
import com.sun.jna.Pointer;

/**
 * @author lihao
 * @ClassName FExceptionCallBackImpl
 * @Date 2021/4/20  15:14
 * @Description TODO
 * @Version 1.0
 **/
public class FExceptionCallBackImpl implements HCNetSDK.FExceptionCallBack {
	
	@Override
	public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser) {
		fException(dwType, lUserID, lHandle, pUser);
	}
	
	public void fException(int dwType, int lUserID, int lHandle, Pointer pUser) {
		switch (dwType) {
			case HCNetSDK.EXCEPTION_EXCHANGE: //用户交互时异常（注册心跳超时，心跳间隔为2分钟）
				System.out.println("掉线了");
				break;
			case HCNetSDK.EXCEPTION_PREVIEW:
				System.out.println("123");
				break;
			case HCNetSDK.NET_DVR_REALPLAYEXCEPTION:
				System.out.println("111");
				break;
		}
	}
	
}
