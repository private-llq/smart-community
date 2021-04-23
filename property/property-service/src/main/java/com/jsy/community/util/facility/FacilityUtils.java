package com.jsy.community.util.facility;

import com.jsy.community.api.PropertyException;
import com.jsy.community.callback.FMSGCallBack_V31Impl;
import com.jsy.community.sdk.HCNetSDK;
import com.sun.jna.Pointer;
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
	private static FMSGCallBack_V31Impl dVRMessageCallBack;
	
	// 人脸比对
	private static final Long EFFECT_FACE = 41565L;
	
	// 车牌抓拍
	private static final Long EFFECT_CAR = 456L;
	
	/**
	 * @return java.util.Map<java.lang.String, java.lang.Integer>
	 * @Author lihao
	 * @Description 设备登录
	 * @Date 2021/4/21 16:43
	 * @Param [ip, port, username, password]
	 **/
	public static Map<String, Integer> login(String ip, short port, String username, String password, int lUserID, boolean fffff) {
		log.info(ip+"：开始登录");
		// 对接多台设备，是否需要调用多次NET_DVR_Init接口分别初始化？
		// 答：不是 初始化接口全局的，一次即可。NET_DVR_Init和NET_DVR_Cleanup需要配对使用，程序运行开始调用NET_DVR_Init，程序退出时调用NET_DVR_Cleanup释放资源，都只需要调用一次即可。
		// 为什么登录设备不需要初始化  答：因为我在 ApplicationRunnerImpl 【项目一启动的时候已经初始化】
		if (!fffff) {
			boolean flag = hCNetSDK.NET_DVR_RemoteControl(lUserID, 20005, null, 0);
			HashMap<String, Integer> map = new HashMap<>();
			int workStatus = flag ? 0 : 1;
			map.put("workStatus", workStatus);
			return map;
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
			log.info(ip + "：登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			log.info(ip + "：登录成功！");
		}
		
		// 设备在线状态检测
		boolean flag = hCNetSDK.NET_DVR_RemoteControl(lUserID, 20005, null, 0);
		int status = flag ? 0 : 1;
		
		Map<String, Integer> map = new HashMap<>();
		map.put("status", status);
		map.put("facilityHandle", lUserID);
		return map;
	}
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 根据不同的作用id开启不同的设备功能
	 * @Date 2021/4/23 13:59
	 * @Param [loginStatus, handle, facilityEffectId]
	 **/
	public static int toEffect(Integer loginStatus, Integer handle, Long facilityEffectId) {
		log.info("开始开启设备功能");
		//1. 判断是否登录成功
		if (loginStatus == 0) {//成功
			// TODO: 2021/4/23 这个报警布防句柄是由摄像头传过来的 [不是很确定]
			if (!facilityEffectId.equals(EFFECT_FACE) && !facilityEffectId.equals(EFFECT_CAR)) {
				throw new PropertyException("该功能没有实现");
			}
			
			int lAlarmHandle = -1;//报警布防句柄
			//判断是否布防。尚未布防,需要布防
			if (lAlarmHandle < 0) {
				// 4.1 判断是否设置报警回调函数 若没有，则设置报警回调函数
				if (dVRMessageCallBack == null) {
					dVRMessageCallBack = new FMSGCallBack_V31Impl();
					Pointer pUser = null;
					// NET_DVR_SetDVRMessageCallBack_V31： 用于注册回调函数，接收设备报警消息等
					//              两个参数：参数1：fMessageCallBack //[in] 回调函数    参数2：pUser  //[in] 用户数据
					//              注册回调函数，接收设备报警消息等。true：成功  false：失败
					//              回调函数里面接收数据之后可以通过报警设备信息(NET_DVR_ALARMER)判断区分设备。
					
					//              返回值：TRUE表示成功，FALSE表示失败。接口返回失败请调用NET_DVR_GetLastError获取错误码，通过错误码判断出错原因。
					// ps：官网提供的sdk 这里写错了  他写的需要一个MSGCallBack_V31和pUser的参数，  其实是需要一个 FMSGCallBack_V31(接口) 和 pUser的参数
					if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(dVRMessageCallBack, pUser)) {
						log.info("设置回调函数失败!");
					} else {
						log.info("设置回调函数成功!");
					}
				}
				
				// 4.2 设置报警布防
				// NET_DVR_SETUPALARM_PARAM：报警布防参数的结构体。
				HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
				m_strAlarmInfo.dwSize = m_strAlarmInfo.size();// 结构体大小
				m_strAlarmInfo.byLevel = 1;// 布防优先级，0-一等级（高），1-二等级（中），2-三等级（低）
				m_strAlarmInfo.byAlarmInfoType = 1;// 上传报警信息类型（抓拍机支持），0-老报警信息（NET_DVR_PLATE_RESULT），1-新报警信息(NET_ITS_PLATE_RESULT)2012-9-28
				m_strAlarmInfo.byDeployType = 1;// 布防类型：0-客户端布防，1-实时布防
				m_strAlarmInfo.write();
				
				// =====NET_DVR_SetupAlarmChan_V41：这个方法才是设置报警布防  用于建立报警上传通道，获取报警等信息。======
				// 返回Long，    -1：失败，其他值：作为NET_DVR_CloseAlarmChan_V30函数的句柄参数。
				// 参数1：lUserID：NET_DVR_Login或者NET_DVR_Login_V30的返回值             参数2：lpSetupParam：报警布防参数
				lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(handle, m_strAlarmInfo);
				if (lAlarmHandle == -1) {
					log.info("开启人脸车牌比对/车牌抓拍失败，错误号：" + hCNetSDK.NET_DVR_GetLastError());
				} else {
					log.info("开启人脸比对/车牌抓拍成功");
					return lAlarmHandle;
				}
			}
		}
		log.info("开启设备功能失败，因为没有登录连接成功");
		return -1;
	}
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 撤防
	 * @Date 2021/4/23 15:11
	 * @Param []
	 **/
	public static void cancel(int lAlarmHandle) {
		// 1. 撤防
		if (lAlarmHandle > -1) {
			if (hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
				log.info("撤防成功");
			}
		}
	}
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 注销
	 * @Date 2021/4/23 18:44
	 * @Param [lUserID]
	 **/
	public static void logOut(int lUserID) {
		if (lUserID > -1) {
			if (hCNetSDK.NET_DVR_Logout(lUserID)) {
				log.info("注销成功");
			}
		}
	}
	
	/**
	 * @return int
	 * @Author 91李寻欢
	 * @Description 根据用户句柄判断设备是否在线
	 * @Date 2021/4/23 18:46
	 * @Param [lUserID]
	 **/
	public static int isOnline(int lUserID){
		boolean b = hCNetSDK.NET_DVR_RemoteControl(lUserID, 20005, null, 0);
		if (b) {
			return 0;
		} else {
			return 1;
		}
	}
}
