package com.jsy.community.util.facility;

import com.jsy.community.api.PropertyException;
import com.jsy.community.callback.FMSGCallBack_V31Impl;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.sdk.HCNetSDK;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
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
	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	// 人脸与车牌的回调函数
	private static FMSGCallBack_V31Impl dVRMessageCallBack;
	
	// 人脸比对
	private static final Long EFFECT_FACE = 41565L;
	// 车牌抓拍
	private static final Long EFFECT_CAR = 456L;
	
	/**
	 * @return java.util.Map<java.lang.String, java.lang.Integer>
	 * @Author lihao
	 * @Description 设备登录, 并更新设备在线状态信息
	 * @Date 2021/4/21 16:43
	 * @Param [ip, port, username, password]
	 **/
	public static Map<String, Integer> login(String ip, short port, String username, String password, int lUserID) {
		log.info(ip + "：开始登录");
		// 对接多台设备，是否需要调用多次NET_DVR_Init接口分别初始化？
		// 答：不是 初始化接口全局的，一次即可。NET_DVR_Init和NET_DVR_Cleanup需要配对使用，程序运行开始调用NET_DVR_Init，程序退出时调用NET_DVR_Cleanup释放资源，都只需要调用一次即可。
		// 为什么登录设备这里不需要初始化  答：因为我在 ApplicationRunnerImpl 【项目一启动的时候已经初始化】
		
		//2. 用户注册设备    返回值：-1表示失败，其他值表示返回的用户ID值。该用户ID具有唯一性，后续对设备的操作都需要通过此ID实现。
		// 登录之前先判断是否已经登录
		if (lUserID > -1) { // lUserID：用户句柄   （每个设备都需要单独登录NET_DVR_Login_V30，登录成功返回唯一的userID）;
			//先注销
			hCNetSDK.NET_DVR_Logout(lUserID);
			lUserID = -1;
		}
		// 登录设备函数：NET_DVR_Login_V30   返回值：-1表示失败，其他值表示返回的用户ID值。该用户ID具有唯一性，后续对设备的操作都需要通过此ID实现
		// 参数1：设备IP地址或是静态域名，字符数不大于128个
		// 参数2：设备端口号
		// 参数3：登录的用户名
		// 参数4：用户密码
		// 参数5：设备信息
		HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		lUserID = hCNetSDK.NET_DVR_Login_V30(ip, port, username, password, m_strDeviceInfo);
		if (lUserID == -1) {
			log.info(ip + "：登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			log.info(ip + "：登录成功！");
		}
		// 设备在线状态函数：NET_DVR_RemoteControl  返回值：true表示成功[在线]  false表示失败[不在线]
		// 参数1：用户 ID 号，NET_DVR_Login_V40 的返回值
		// 参数2：控制命令
		// 参数3：输入参数
		// 参数4：输入参数长度
		Pointer pointer = null;
		boolean flag = hCNetSDK.NET_DVR_RemoteControl(lUserID, 20005, pointer, 0);
		int status = flag ? 1 : 0;
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
		log.info("开始开启设备功能ing...");
		//1. 判断是否登录成功
		if (loginStatus == 1) {//0不在线 1在线
			if (!facilityEffectId.equals(EFFECT_FACE) && !facilityEffectId.equals(EFFECT_CAR)) {
				throw new PropertyException("该功能没有实现");
			}
			
			int lAlarmHandle = -1;//报警布防句柄
			//尚未布防,需要布防
			if (lAlarmHandle < 0) {
				// 4.1 判断是否设置报警回调函数 若没有，则设置报警回调函数
				if (dVRMessageCallBack == null) {
					dVRMessageCallBack = new FMSGCallBack_V31Impl();
					Pointer pUser = null;
					// 报警回调函数：NET_DVR_SetDVRMessageCallBack_V31   返回值：true表示成功，false表示失败
					//             用于注册回调函数，接收设备报警消息等
					//                  参数1：fMessageCallBack //[in] 回调函数
					//                  参数2：pUser  //[in] 用户数据
					//              注册回调函数，接收设备报警消息等。true：成功  false：失败
					//              回调函数里面接收数据之后可以通过报警设备信息(NET_DVR_ALARMER)判断区分设备。
					
					if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(dVRMessageCallBack, pUser)) {
						log.info("设置报警布防回调函数失败!");
					} else {
						log.info("设置报警布防回调函数成功!");
					}
				}
				
				// 4.2 设置报警布防
				// NET_DVR_SETUPALARM_PARAM：报警布防参数的结构体。  用于设置报警布防的参数信息
				HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
				m_strAlarmInfo.dwSize = m_strAlarmInfo.size();// 结构体大小
				m_strAlarmInfo.byLevel = 1;// 布防优先级，0-一等级（高），1-二等级（中），2-三等级（低）
				m_strAlarmInfo.byAlarmInfoType = 1;// 上传报警信息类型（抓拍机支持），0-老报警信息（NET_DVR_PLATE_RESULT），1-新报警信息(NET_ITS_PLATE_RESULT)2012-9-28
				m_strAlarmInfo.byDeployType = 1;// 布防类型：0-客户端布防，1-实时布防
				m_strAlarmInfo.write();
				
				// =====NET_DVR_SetupAlarmChan_V41：这个方法才是设置报警布防  用于建立报警上传通道，获取报警等信息。======
				// 布防：即建立设备跟客户端之间报警上传的连接通道，这样设备发生报警之后通过该连接上传报警信息，SDK在报警回调函数中接收和处理报警信息数据即可。
				// 撤防：程序退出前或者不需要接收报警信息时调用NET_DVR_CloseAlarmChan_V30进行撤防，释放资源，此时连接断开，设备将不再上传报警信息
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
			log.info("开启设备功能成功");
		}
		log.info("开启设备功能失败，因为设备没有登录或已掉线");
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
	 * 1. 当某设备掉线了，此时拿着他的用户句柄去判断是否在线，返回false。
	 * 2. 当设备重新连接了，此时拿着他在线时的用户句柄去判断是否在线，返回true。因为这个用户句柄我们没有注销过，它相当于一直给这个设备留着的，等你重新上线了，拿着这个句柄去判断是否在线，就返回true了
	 * @Date 2021/4/23 18:46
	 * @Param [lUserID]
	 **/
	public static int isOnline(int lUserID) {
		boolean b = hCNetSDK.NET_DVR_RemoteControl(lUserID, 20005, null, 0);
		if (b) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * @return long
	 * @Author 91李寻欢
	 * @Description ************上传图片至人脸库************
	 * 上传接口调用流程：
	 * 1) 调用NET_DVR_UploadFile_V40开始上传数据。
	 * 2) 调用NET_DVR_UploadSend发送一张人脸图片数据和附加信息。
	 * 3) 上传过程中循环调用NET_DVR_GetUploadState获取上传状态和进度。
	 * 4) 上传成功之后调用NET_DVR_GetUploadResult获取结果信息（图片ID等）。
	 * 5) 重复步骤2、3、4，顺序上传其他人脸数据。
	 * 6) 调用NET_DVR_UploadClose停止上传，释放资源。
	 * @Date 2021/4/28 9:09
	 * @Param []
	 **/
	public static void uploadFaceLibrary(int lUserID, String FDID, UserEntity user) throws IOException {
		String realName = user.getRealName();
		String faceUrl = user.getFaceUrl();
		Integer sex = user.getSex();
		String sexStr = "";
		if (sex == 1) {
			sexStr = "male";
		} else if (sex == 2) {
			sexStr = "female";
		}
		LocalDateTime birthdayTimeStr = null;
		LocalDateTime birthdayTime = user.getBirthdayTime();
		if (birthdayTime != null) {
			birthdayTimeStr = birthdayTime;
		}
		Integer cityId = user.getCityId();
		String cityStr = "未知";
		if (cityId != null) {
			cityStr = String.valueOf(cityId).substring(2, 4);
		}
		Integer provinceId = user.getProvinceId();
		String provinceStr = null;
		if (provinceId != null) {
			provinceStr = String.valueOf(provinceId).substring(0, 2);
		}
		
		Integer ficationType = user.getIdentificationType();
		String ficationTypeStr = "未知";
		if (ficationType == 1) {
			ficationTypeStr = "ID";
		} else if (ficationType == 2) {
			ficationTypeStr = "passportID";
		} else {
			ficationTypeStr = "other";
		}
		String idCard = user.getIdCard();
		
		
		//1. 调用NET_DVR_UploadFile_V40开始上传数据(命令:IMPORT_DATA_TO_FACELIB)开始上传数据。
		//上传条件里面byConcurrent赋值为0时不开启并发处理，设备会自动对上传的图片进行建模，如果批量上传大量图片，可以赋值为1开启并发处理，提高速度，但是上传之后需要自己建模
		log.info("调用NET_DVR_UploadFile_V40开始上传数据");
		HCNetSDK.NET_DVR_FACELIB_COND struFaceLibCond = new HCNetSDK.NET_DVR_FACELIB_COND();
		struFaceLibCond.read();
		struFaceLibCond.dwSize = struFaceLibCond.size();
		struFaceLibCond.szFDID = FDID.getBytes(); //人脸库ID
		struFaceLibCond.byConcurrent = 0; //设备是否并发处理：0- 不开启(设备自动会建模)，1- 开始(设备不会自动进行建模)
		struFaceLibCond.byCover = 0;  //是否覆盖式导入(人脸库存储满的情况下强制覆盖导入时间最久的图片数据)：0- 否，1- 是    PS：客服说每个摄像机可以存30000张人脸
		struFaceLibCond.write();
		Pointer pStruFaceLibCond = struFaceLibCond.getPointer();
		int iUploadHandle = hCNetSDK.NET_DVR_UploadFile_V40(lUserID, HCNetSDK.IMPORT_DATA_TO_FACELIB, pStruFaceLibCond, struFaceLibCond.size(), null, Pointer.NULL, 0);
		if (iUploadHandle <= -1) {
			log.info("NET_DVR_UploadFile_V40失败，错误号" + hCNetSDK.NET_DVR_GetLastError());
			return;
		} else {
			log.info("NET_DVR_UploadFile_V40成功");
		}
		
		//2. 调用NET_DVR_UploadSend发送一张人脸图片数据和附加信息
		// 通过该接口将人脸数据(人脸图片+图片附件信息)发送到设备的人脸库。图片格式要求：JPG或者JPEG，像素在40x40以上，大小在300KB以下
		HCNetSDK.NET_DVR_SEND_PARAM_IN struSendParam = new HCNetSDK.NET_DVR_SEND_PARAM_IN();
		struSendParam.read();

//		String relativelyPath = System.getProperty("user.dir");
//		byte[] picbyte = toByteArray(relativelyPath + "\\property\\property-web\\src\\main\\resources\\face\\small.jpg");
		byte[] picbyte = getFileStream(faceUrl);
		
		HCNetSDK.BYTE_ARRAY arraybyte = new HCNetSDK.BYTE_ARRAY(picbyte.length);
		arraybyte.read();
		arraybyte.byValue = picbyte;
		arraybyte.write();
		
		struSendParam.pSendData = arraybyte.getPointer();
		struSendParam.dwSendDataLen = picbyte.length;
		struSendParam.byPicType = 1; //图片格式：1- jpg，2- bmp，3- png，4- SWF，5- GIF
		struSendParam.sPicName = "哇哈哈".getBytes(); //图片名称
		byte[] byFDLibName = realName.getBytes("UTF-8");
//		2014-12-12T00:00:00Z
		String strInBuffer1 = new String("<FaceAppendData version=\"2.0\" xmlns=\"http://www.hikvision.com/ver20/XMLSchema\"><bornTime>" + birthdayTimeStr + "</bornTime><name>");
		String strInBuffer2 = new String("</name><sex>" + sexStr + "</sex><province>" + provinceStr + "</province><city>" + cityStr + "</city><certificateType>" + ficationTypeStr + "</certificateType><certificateNumber>" + idCard + "</certificateNumber><PersonInfoExtendList><PersonInfoExtend><id>1</id><enable>false</enable><name>test1</name><value>test2</value></PersonInfoExtend></PersonInfoExtendList></FaceAppendData>");
		int iStringSize = byFDLibName.length + strInBuffer1.length() + strInBuffer2.length();
		HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(iStringSize);
		System.arraycopy(strInBuffer1.getBytes(), 0, ptrByte.byValue, 0, strInBuffer1.length());
		System.arraycopy(byFDLibName, 0, ptrByte.byValue, strInBuffer1.length(), byFDLibName.length);
		System.arraycopy(strInBuffer2.getBytes(), 0, ptrByte.byValue, strInBuffer1.length() + byFDLibName.length, strInBuffer2.length());
		ptrByte.write();
		struSendParam.pSendAppendData = ptrByte.getPointer();
		struSendParam.dwSendAppendDataLen = ptrByte.byValue.length;
		
		struSendParam.write();
		int iSendData = hCNetSDK.NET_DVR_UploadSend(lUserID, struSendParam, Pointer.NULL);
		if (iSendData <= -1) {
			System.err.println("NET_DVR_UploadSend失败，错误号" + hCNetSDK.NET_DVR_GetLastError());
			return;
		}
		
		while (true) {
			IntByReference Pint = new IntByReference(0);
			int state = hCNetSDK.NET_DVR_GetUploadState(iSendData, Pint.getPointer());
			if (state == 1) {
				System.out.println("上传成功");
				//获取图片ID
				HCNetSDK.NET_DVR_UPLOAD_FILE_RET struUploadRet = new HCNetSDK.NET_DVR_UPLOAD_FILE_RET();
				boolean bUploadResult = hCNetSDK.NET_DVR_GetUploadResult(iUploadHandle, struUploadRet.getPointer(), struUploadRet.size());
				if (!bUploadResult) {
					int iErr = hCNetSDK.NET_DVR_GetLastError();
					System.err.println("NET_DVR_GetUploadResult失败，错误号" + iErr);
				} else {
					struUploadRet.read();
					System.out.println("图片ID：" + new String(struUploadRet.sUrl, "UTF-8"));
				}
				break;
			} else if (state == 2) {
				System.out.println("进度：" + Pint.getValue());
				continue;
			}
			System.err.println("返回值" + state);
			break;
		}
		//关闭图片上传连接
		boolean b_Close = hCNetSDK.NET_DVR_UploadClose(iUploadHandle);
		if (!b_Close) {
			int iErr = hCNetSDK.NET_DVR_GetLastError();
			System.err.println("NET_DVR_UploadSend失败，错误号" + iErr);
			return;
		}
	}
	
	/**
	 * @return byte[]
	 * @Author 91李寻欢
	 * @Description 返回读取到的数据到 byte数组
	 * @Date 2021/4/28 13:59
	 * @Param [filename] 本地文件
	 **/
	public static byte[] toByteArray(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			throw new FileNotFoundException(filename);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		try {
			byte[] buffer = new byte[1024];
			int len;
			while (-1 != (len = in.read(buffer, 0, buffer.length))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			bos.close();
			in.close();
		}
	}
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 以图搜图     设备不支持该功能
	 * @Date 2021/5/11 15:52
	 * @Param []
	 **/
	public void search(){
	
	}
	
	/**
	 * 得到文件流
	 *
	 * @param url 网络图片URL地址
	 * @return
	 */
	public static byte[] getFileStream(String url) {
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
			byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
			return btImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 从输入流中获取数据
	 *
	 * @param inStream 输入流
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
	
	public static void main(String[] args) {
		CLibrary.INSTANCE.printf("Hello, World\n");
		for (int i = 0; i < args.length; i++) {
			CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
		}
	}
}
