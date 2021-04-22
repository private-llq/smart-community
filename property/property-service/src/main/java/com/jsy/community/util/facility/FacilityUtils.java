package com.jsy.community.util.facility;

import com.jsy.community.callback.FMSGCallBack_V31Impl;
import com.jsy.community.sdk.HCNetSDK;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lihao
 * @ClassName FacilityUtils
 * @Date 2021/4/20  16:40
 * @Description TODO
 * @Version 1.0
 **/
@Slf4j
public class FacilityUtils {
	
	// 加载HK库
	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private FMSGCallBack_V31Impl dVRMessageCallBack;
	
	/**
	 * @return java.util.Map<java.lang.String, java.lang.Integer>
	 * @Author lihao
	 * @Description 设备登录
	 * @Date 2021/4/21 16:43
	 * @Param [ip, port, username, password]
	 **/
	public static Map<String, Integer> login(String ip, short port, String username, String password, int lUserID) {
		//1. 初始化SDK    TRUE表示成功，FALSE表示失败。
		if (!hCNetSDK.NET_DVR_Init()) {
			log.info("初始化SDK失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
		}
		
		//2. 用户注册设备    返回值：-1表示失败，其他值表示返回的用户ID值。该用户ID具有唯一性，后续对设备的操作都需要通过此ID实现。
		// 注册之前先注销已注册的设备
		if (lUserID > -1) { // lUserID：用户句柄   （每个设备都需要单独登录NET_DVR_Login_V30，登录成功返回唯一的userID1）;
			//先注销
			hCNetSDK.NET_DVR_Logout(lUserID);
			lUserID = -1;
		}
		HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		lUserID = hCNetSDK.NET_DVR_Login_V30(ip, port, username, password, m_strDeviceInfo);
		if (lUserID == -1) {
			log.info(ip + "登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			log.info(ip + "登录成功！");
		}
		
		// 设备在线状态检测
		boolean flag = hCNetSDK.NET_DVR_RemoteControl(lUserID, 20005, null, 0);
		int status = flag ? 0 : 1;
		
		Map<String, Integer> map = new HashMap<>();
		map.put("status", status);
		map.put("facilityHandle", lUserID);
		return map;
	}
	
}
