//package com.jsy.community.util.facility;
//
//import com.jsy.community.sdk.HCNetSDK;
//import com.jsy.community.sdk.PlayCtrl;
//import com.sun.jna.Native;
//import com.sun.jna.Pointer;
//import com.sun.jna.examples.win32.W32API;
//import com.sun.jna.ptr.ByteByReference;
//import com.sun.jna.ptr.NativeLongByReference;
//
//public class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
//
//	private static PlayCtrl playControl = PlayCtrl.INSTANCE;
//
//	NativeLongByReference m_lPort;//回调预览时播放库端口指针
//
//	@Override
//	public void invoke(int lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
//
//		switch (dwDataType) {
//			case HCNetSDK.NET_DVR_SYSHEAD: //系统头
//
//				//获取播放库未使用的通道号
//				if (!playControl.PlayM4_GetPort(m_lPort)) {
//					break;
//				}
//
//				if (dwBufSize > 0) {
//					//设置实时流播放模式
//					if (!playControl.PlayM4_SetStreamOpenMode(m_lPort.getValue(), PlayCtrl.STREAME_REALTIME)) {
//						break;
//					}
//
//					//打开流接口
//					if (!playControl.PlayM4_OpenStream(m_lPort.getValue(), pBuffer, dwBufSize, 1024 * 1024)) {
//						break;
//					}
//
//					//播放开始
//					if (!playControl.PlayM4_Play(m_lPort.getValue(), hwnd)) {
//						break;
//					}
//				}
//			case HCNetSDK.NET_DVR_STREAMDATA:   //码流数据
//				if ((dwBufSize > 0) && (m_lPort.getValue().intValue() != -1)) {
//					//输入流数据
//					if (!playControl.PlayM4_InputData(m_lPort.getValue(), pBuffer, dwBufSize)) {
//						break;
//					}
//				}
//		}
//	}
//
//
//}
//}