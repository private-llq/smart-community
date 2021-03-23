//package com.jsy.community.controller;
//
//import com.jsy.community.annotation.ApiJSYController;
//import com.jsy.community.callback.FMSGCallBack_V31Impl;
//import com.jsy.community.sdk.HCNetSDK;
//import com.sun.jna.Pointer;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author lihao
// * @ClassName HkController
// * @Date 2021/3/3  10:00
// * @Description TODO
// * @Version 1.0
// **/
//@Api(tags = "单个摄像头控制器")
//@Slf4j
////@Scope(value = "prototype")
//@ApiJSYController
//@RestController
//@RequestMapping("/hk")
//public class HkController {
//	// 加载HK库
//	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
//	int lUserID = -1;//用户句柄
//	int lAlarmHandle = -1;//报警布防句柄
//	private FMSGCallBack_V31Impl dVRMessageCallBack;
//
//	@ApiOperation("开启实时人脸比对")
//	@GetMapping("/openFace")
//	public void openFace() {
//		//1. 初始化SDK    TRUE表示成功，FALSE表示失败。
//		if (!hCNetSDK.NET_DVR_Init()) {
//			log.info("初始化失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
//		}
//
//		//2. 设置网络连接超时时间(毫秒)和连接尝试次数。 [可选]
//		hCNetSDK.NET_DVR_SetConnectTime(5000, 2);
//
//		//3. 用户注册
//		Login();
//
//		//4. 设置报警回调函数与报警布防
//		SetAlarm();
//	}
//
//	@ApiOperation("关闭实时人脸比对")
//	@GetMapping("/closeFace")
//	public void closeFace() {
//		//1. 初始化
//		hCNetSDK.NET_DVR_Init();
//
//		//2. 用户注册
//		Login();
//
//		//3. 撤防【关闭人脸比对功能，不是关闭摄像头】
//		// 程序退出前或者不需要接收报警信息时调用NET_DVR_CloseAlarmChan_V30进行撤防，释放资源，此时连接断开，设备将不再上传报警信息。
//		if (lAlarmHandle > -1) {
//			if (hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
//				lAlarmHandle = -1;
//				log.info("已关闭实时人脸比对");
//			}
//
//			//4. 注销用户
//			hCNetSDK.NET_DVR_Logout_V30(lUserID);
//			log.info("注销用户成功");
//		}
//
//	}
//
//	@ApiOperation("开启实时车辆抓拍")
//	@GetMapping("/openCar")
//	public void openHK() {
//		//1. 初始化
//		hCNetSDK.NET_DVR_Init();
//
//		//2. 用户注册
//		Login();
//
//		//3. 设置回调函数
//		SetAlarm();
//	}
//
//
//	/**
//	 * @return void
//	 * @Author lihao
//	 * @Description
//	 * 报警可分为“布防”和“监听”两种方式。采用两种报警方式都可以接收到设备上传的移动侦测报警、视频信号丢失报警、遮挡报警和信号量报警等信息。
//	 * “布防”报警方式：是指SDK主动连接设备，并发起报警上传命令，设备发生报警立即发送给SDK。
//	 * 报警布防方式获取设备上传的人脸比对结果的实现方法：
//	 *      1) 先调用NET_DVR_SetDVRMessageCallBack_V31设置报警回调函数，在SDK初始化之后即可以调用，多台设备对接时也只需要调用一次设置一个回调函数，回调函数里面接收数据之后可以通过报警设备信息(NET_DVR_ALARMER)判断区分设备。
//	 *      2) 每台设备分别登录，分别调用NET_DVR_SetupAlarmChan_V41进行布防，布防即建立设备跟客户端之间报警上传的连接通道，这样设备发生报警之后通过该连接上传报警信息，SDK在报警回调函数中接收和处理报警信息数据即可。
//	 *      3) 程序退出前或者不需要接收报警信息时调用NET_DVR_CloseAlarmChan_V30进行撤防，释放资源，此时连接断开，设备将不再上传报警信息。
//	 * @Date 2021/3/8 10:45
//	 * @Param []
//	 **/
//	public void SetAlarm() {
//		//判断是否布防。尚未布防,需要布防
//		if (lAlarmHandle < 0) {
//			// 4.1 判断是否设置报警回调函数
//			if (dVRMessageCallBack == null) {
//				dVRMessageCallBack = new FMSGCallBack_V31Impl();
//				Pointer pUser = null;
//				// NET_DVR_SetDVRMessageCallBack_V31：注册回调函数，接收设备报警消息等。true：成功  false：失败   他需要两个参数： fMessageCallBack：回调函数     pUser：用户数据				// 回调函数里面接收数据之后可以通过报警设备信息(NET_DVR_ALARMER)判断区分设备。
//				// ps：官网提供的sdk 这里写错了  他写的需要一个MSGCallBack_V31和pUser的参数，  其实是需要一个 FMSGCallBack_V31(接口) 和 pUser的参数
//				if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(dVRMessageCallBack, pUser)) {
//					log.info("设置回调函数失败!");
//				} else {
//					log.info("设置回调函数成功!");
//				}
//			}
//
//			// 4.2 设置报警布防
//			// NET_DVR_SETUPALARM_PARAM：报警布防参数结构体。
//			HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
//			m_strAlarmInfo.dwSize = m_strAlarmInfo.size();// 结构体大小
//			m_strAlarmInfo.byLevel = 1;// 布防优先级，0-一等级（高），1-二等级（中），2-三等级（低）
//			m_strAlarmInfo.byAlarmInfoType = 1;// 上传报警信息类型（抓拍机支持），0-老报警信息（NET_DVR_PLATE_RESULT），1-新报警信息(NET_ITS_PLATE_RESULT)2012-9-28
//			m_strAlarmInfo.byDeployType = 1;// 布防类型：0-客户端布防，1-实时布防
//			m_strAlarmInfo.write();
//
//			// NET_DVR_SetupAlarmChan_V41：建立报警上传通道，获取报警等信息。
//			// 返回Long，    -1：失败，其他值：作为NET_DVR_CloseAlarmChan_V30函数的句柄参数。
//			// 参数：lUserID：NET_DVR_Login或者NET_DVR_Login_V30的返回值      lpSetupParam：报警布防参数
//			lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
//			if (lAlarmHandle == -1) {
//				log.info("开启人脸车牌比对/车牌抓拍失败，错误号：" + hCNetSDK.NET_DVR_GetLastError());
//			} else {
//				log.info("开启人脸比对/车牌抓拍成功");
//			}
//		}
//	}
//
//	public void Login() {
//		/**
//		 * @return void
//		 * @Author lihao
//		 * @Description
//		 *              LONG NET_DVR_Login_V30(
//		 *                  char                       *sDVRIP,        设备IP地址或是静态域名，字符数不大于128个
//		 *                  WORD                       wDVRPort,       设备端口号
//		 *                  char                       *sUserName,     登录的用户名
//		 *                  char                       *sPassword,     用户密码
//		 *                  LPNET_DVR_DEVICEINFO_V30   lpDeviceInfo    设备信息
//		 *              );
//		 *              -1表示失败，其他值表示返回的用户ID值。该用户ID具有唯一性，后续对设备的操作都需要通过此ID实现。
//		 *              接口返回失败请调用NET_DVR_GetLastError获取错误码，通过错误码判断出错原因。
//		 * @Date 2021/3/8 10:32
//		 * @Param []
//		 **/
//		HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
//		// - 用户注册设备
//		lUserID = hCNetSDK.NET_DVR_Login_V30("192.168.12.188", (short) 8000, "admin", "root1234", m_strDeviceInfo);
//		if (lUserID == -1) {
//			log.info("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
//		} else {
//			log.info("登录成功！");
//		}
//	}
//}
//
