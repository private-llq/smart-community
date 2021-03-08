package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.callback.FMSGCallBack;
import com.jsy.community.sdk.HCNetSDK;
import com.sun.jna.Pointer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihao
 * @ClassName HkController
 * @Date 2021/3/3  10:00
 * @Description TODO
 * @Version 1.0
 **/
@Api(tags = "摄像头控制器")
@Slf4j
@ApiJSYController
@RestController
@RequestMapping("/hk")
public class HkController {
	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	int lUserID = -1;//用户句柄
	int lAlarmHandle = -1;//报警布防句柄
	FMSGCallBack dVRMessageCallBack;
	
	@ApiOperation("开启实时人脸比对")
	@GetMapping("/openFace")
	public void openFace(){
		//1. 初始化
		hCNetSDK.NET_DVR_Init();
		//2. 用户注册
		Login();
		//3. 设置回调函数
		SetAlarm();
//		while (true) ;
	
	}
	
	@ApiOperation("关闭实时人脸比对")
	@GetMapping("/closeFace")
	public void closeFace(){
		//1. 初始化
		hCNetSDK.NET_DVR_Init();
		//2. 用户注册
		Login();
		//3. 撤防【关闭人脸比对功能，不是关闭摄像头】
		if (lAlarmHandle > -1) {
			if (hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
				lAlarmHandle = -1;
				log.info("已关闭实时人脸比对");
			}
		}
	
	}
	
	@ApiOperation("开启实时车辆抓拍")
	@GetMapping("/openCar")
	public void openHK(){
		//1. 初始化
		hCNetSDK.NET_DVR_Init();
		//2. 用户注册
		Login();
		//3. 设置回调函数
		SetAlarm();
//		while (true) ;
	
	}
	
	public void SetAlarm() {
		//判断是否布防。——尚未布防,需要布防
		if (lAlarmHandle < 0){
			// 判断是否设置报警回调函数
			if (dVRMessageCallBack == null) {
				dVRMessageCallBack = new FMSGCallBack();
				Pointer pUser = null;
				if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(dVRMessageCallBack, pUser)) {
					log.info("设置回调函数失败!");
				} else {
					log.info("设置回调函数成功!");
				}
			}
			
			HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
			m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
			m_strAlarmInfo.byLevel = 1;//布防优先级，0-一等级（高），1-二等级（中），2-三等级（低）
			m_strAlarmInfo.byAlarmInfoType = 1;//上传报警信息类型（抓拍机支持），0-老报警信息（NET_DVR_PLATE_RESULT），1-新报警信息(NET_ITS_PLATE_RESULT)2012-9-28
			m_strAlarmInfo.byDeployType = 1;//布防类型：0-客户端布防，1-实时布防
			m_strAlarmInfo.write();
			
			// 设置报警布防
			lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
			if (lAlarmHandle == -1) {
				log.info("开启人脸比对失败，错误号："+hCNetSDK.NET_DVR_GetLastError());
			} else {
				log.info("开启人脸比对成功");
			}
		}
	}
	
	public void Login() {
		HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		lUserID = hCNetSDK.NET_DVR_Login_V30("192.168.12.188", (short) 8000, "admin", "root1234", m_strDeviceInfo); // - // - 用户注册设备（NET_DVR_Login_V40）：实现用户的注册功能，注册成功后，返回的用户ID作为其他功能操作的唯一标识，SDK允许最大注册个数为2048个。
		if (lUserID == -1) {
			log.info("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			log.info("登录成功！");
		}
	}
}

