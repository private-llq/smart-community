package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;
import com.jsy.community.utils.HKCarTypeUtils;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lihao
 * @ClassName FMSGCallBack_V31Impl
 * @Date 2021/3/3  15:43
 * @Description 人脸识别/车牌识别回调函数
 * @Version 1.0
 **/
@Slf4j
public class FMSGCallBack_V31Impl implements HCNetSDK.FMSGCallBack_V31 {
	
	/**
	 * @return boolean
	 * @Author lihao
	 * @Description
	 * @Date 2021/4/20 10:36
	 * @Param [
	 * lCommand：上传的消息类型，不同的报警信息对应不同的类型，通过类型区分是什么报警信息，详见“Remarks”中列表
	 * pAlarmer：报警设备信息，包括设备序列号、IP地址、登录IUserID句柄等
	 * pAlarmInfo：报警信息，通过lCommand值判断pAlarmer对应的结构体，详见“Remarks”中列表
	 * dwBufLen：报警信息缓存大小
	 * pUser：用户数据
	 * ]
	 **/
	@Override
	public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
		AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
		return true;
	}
	
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 人脸比对功能是设备实现的，设备配置好之后自动抓拍和人脸库里面人脸图片比对，比对相似度超过相似的阈值就是人脸比对成功，不是上传相似度最高的结果  ->来自技术客服回答
	 * 人脸抓拍：支持对运动人脸进行检测、跟踪、抓拍、评分、筛选，输出最优的人脸抓图，最多同时检测60个/帧，支持前端人脸比对，支持最多3个人脸库的管理，支持最多9万张人脸库，每个人脸库支持的人脸数量为30000个，单张人脸不超过300kb  ->来自产品客服回答
	 * @Date 2021/3/4 10:17
	 * @Param [lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser]
	 **/
	public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
		//lCommand是传的报警类型
		switch (lCommand) {
			case HCNetSDK.COMM_UPLOAD_FACESNAP_RESULT: //人脸识别结果上传
				log.info("");
				log.info("设备：" + new String(pAlarmer.sDeviceIP) + ",人脸抓拍事件开始触发");
				//实时人脸抓拍上传
				// NET_VCA_FACESNAP_RESULT: 人脸抓拍结果结构体。
				HCNetSDK.NET_VCA_FACESNAP_RESULT strFaceSnapInfo = new HCNetSDK.NET_VCA_FACESNAP_RESULT();
				strFaceSnapInfo.write();
				Pointer pFaceSnapInfo = strFaceSnapInfo.getPointer();
				pFaceSnapInfo.write(0, pAlarmInfo.getByteArray(0, strFaceSnapInfo.size()), 0, strFaceSnapInfo.size());
				strFaceSnapInfo.read();
				// strFaceSnapInfo.struFeature  获取人体属性参数结构体。    NET_VCA_HUMAN_FEATURE
				log.info("抓拍人脸评分[0-100]：" + strFaceSnapInfo.dwFaceScore + "，年龄段：" + strFaceSnapInfo.struFeature.byAgeGroup + "，性别：" + strFaceSnapInfo.struFeature.bySex + "，是否戴眼镜[1不戴，2戴]：" + strFaceSnapInfo.struFeature.byEyeGlass + "，是否戴帽子[0不支持，1不戴，2戴]：" + strFaceSnapInfo.struFeature.byHat);
				
				// 将抓拍人脸图片写出到存储设备[或上传到服务器]
				// 人脸抓拍一般只能上传背景图（dwBackgroundPicLen、pBuffer2）和人脸子图区域（struRect），人脸子图需要应用层根据区域从背景图截取。
				try {
					log.info("触发抓拍人脸保存事件触发");
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); //设置日期格式
					String time = df.format(new Date()); // new Date()为获取当前系统时间
					
					// 人脸子图
					FileOutputStream small = new FileOutputStream(System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\face\\" + time + "(人脸抓拍)人脸子图.jpg");
					// 人脸子图加背景图
					FileOutputStream big = new FileOutputStream(System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\face\\" + time + "(人脸抓拍)带背景的人脸子图.jpg");
					
					// dwFacePicLen：人脸子图的长度，为0表示没有图片，大于0表示有图片
					if (strFaceSnapInfo.dwFacePicLen > 0) {
						try {
							//pBuffer1：人脸子图的图片数据                                            dwFacePicLen：人脸子图的长度，为0表示没有图片，大于0表示有图片
							small.write(strFaceSnapInfo.pBuffer1.getByteArray(0, strFaceSnapInfo.dwFacePicLen), 0, strFaceSnapInfo.dwFacePicLen);
							small.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
					// dwBackgroundPicLen：背景图的长度，为0表示没有图片，大于0表示有图片(保留)
					if (strFaceSnapInfo.dwBackgroundPicLen > 0) {
						try {
							//pBuffer2：背景图的图片数据                                             dwBackgroundPicLen：背景图的长度，为0表示没有图片，大于0表示有图片(保留)
							big.write(strFaceSnapInfo.pBuffer2.getByteArray(0, strFaceSnapInfo.dwBackgroundPicLen), 0, strFaceSnapInfo.dwBackgroundPicLen);
							big.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				}
				log.info("人脸抓拍事件结束");
				break;
			
			/**
			 * @return void
			 * @Author lihao
			 * @Description
			 * 人脸库管理和相关参数配置等也可以直接通过WEB网页或者客户端软件访问设备进行配置。
			 *
			 * 1. 实时抓拍与人脸名单中的图片进行比对，同时输出抓拍信息和人脸名单相关信息（包括比对出的相似度最高的人脸图片）。
			 * 2. 人脸比对的匹配图片有多张的时候，多次回调分配获取每一张图片，通过struSnapInfo中的dwUIDLen和pUIDBuffer判断是否是同一次比对结果，当接收到同一个pUIDbuffer的报警匹配图片的张数等于byMatchPicNum时，表示这个报警的匹配图片信息接收结束。
			 *    如果在一段时间内接收到的匹配图片的张数不等于byMatchPicNum时，上层应该设置一个超时时间，不再继续等待这个报警后续的匹配信息，建议超时时间1分钟。
			 * 3. dwUIDLen为0的情况，上层则不做匹配信息的流程处理，则认为是老设备上传的报警按照以前的处理流程接口报警信息。
			 * 4. 这个结构体扩展的时差，是对应NET_VCA_FACESNAP_INFO_ALARM中的绝对时标
			 *
			 * @Date 2021/3/4 9:51
			 * @Param [lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser]
			 **/
			case HCNetSDK.COMM_SNAP_MATCH_ALARM:  //人脸比对结果上传
				log.info("");
				log.info("人脸名单比对报警事件开始触发");
				//人脸名单比对报警
				// NET_VCA_FACESNAP_MATCH_ALARM：人脸比对结果报警上传结构体。
				HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM();
				strFaceSnapMatch.write();
				Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
				pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0, strFaceSnapMatch.size());
				strFaceSnapMatch.read();
				
				// dwSnapPicLen：设备识别抓拍图片长度
				// byPicTransType：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
				// ----------------大图[远距离图]
				// 将设备抓拍的图片写出到存储设备[或上传到服务器]
				// dwSnapPicLen：设备识别抓拍图片长度     byPicTransType：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
				if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); //设置日期格式
					String time = df.format(new Date()); // new Date()为获取当前系统时间
					FileOutputStream fout;
					try {
						String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\face\\" + time + "(人脸比对)设备识别抓拍图片" + ".jpg";
						fout = new FileOutputStream(filename);
						//将字节写入文件
						long offset = 0;
						ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
						byte[] bytes = new byte[strFaceSnapMatch.dwSnapPicLen];
						buffers.rewind();
						buffers.get(bytes);
						fout.write(bytes);
						fout.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// struSnapInfo：人脸抓拍上传信息
				// dwSnapFacePicLen：抓拍人脸子图的长度，为0表示没有图片，大于0表示有图片
				// byPicTransType：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
				// ----------------小图[近距离图]
				// 将设备抓拍的人脸子图图片写出到存储设备[或上传到服务器]
				if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {  // struSnapInfo：人脸抓拍上传信息  dwSnapFacePicLen：抓拍人脸子图的长度，为0表示没有图片，大于0表示有图片  byPicTransType ：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
					String newName = sf.format(new Date());
					FileOutputStream fout;
					try {
						String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\face\\" + newName + "(人脸比对)抓拍人脸子图" + ".jpg";
						fout = new FileOutputStream(filename);
						//将字节写入文件
						long offset = 0;
						ByteBuffer buffers = strFaceSnapMatch.struSnapInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen);
						byte[] bytes = new byte[strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen];
						buffers.rewind();
						buffers.get(bytes);
						fout.write(bytes);
						fout.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// struBlockListInfo：人脸比对报警信息  dwBlockListPicLen：黑名单人脸子图的长度，为0表示没有图片，大于0表示有图片       byPicTransType：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
				// ps：这里的黑名单报警  我的理解就是：黑名单指的就是从人脸库中获取到的最符合当前抓拍的人脸
				// 将人脸库中最匹配的人脸图片写出到存储设备[或上传到服务器]
				if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
					String newName = sf.format(new Date());
					FileOutputStream fout;
					try {
						String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\face\\" + newName + "(人脸比对)最符合的人脸" + strFaceSnapMatch.fSimilarity + ".jpg";
						fout = new FileOutputStream(filename);
						//将字节写入文件
						long offset = 0;
						ByteBuffer buffers = strFaceSnapMatch.struBlockListInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen);
						byte[] bytes = new byte[strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen];
						buffers.rewind();
						buffers.get(bytes);
						fout.write(bytes);
						fout.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					log.info("人脸名单比对报警，相识度[0.001-1]：" + strFaceSnapMatch.fSimilarity + "，人脸库姓名：" + new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byName, "GBK").trim() + "，人脸库证件信息：" + new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byCertificateNumber).trim());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				//获取人脸库ID
				// struBlockListInfo：人脸比对报警信息
				// dwFDIDLen：人脸库ID长度
				// pFDID：人脸库ID数据缓冲区指针
				byte[] FDIDbytes;
				if ((strFaceSnapMatch.struBlockListInfo.dwFDIDLen > 0) && (strFaceSnapMatch.struBlockListInfo.pFDID != null)) {
					ByteBuffer FDIDbuffers = strFaceSnapMatch.struBlockListInfo.pFDID.getByteBuffer(0, strFaceSnapMatch.struBlockListInfo.dwFDIDLen);
					FDIDbytes = new byte[strFaceSnapMatch.struBlockListInfo.dwFDIDLen];
					FDIDbuffers.rewind();
					FDIDbuffers.get(FDIDbytes);
					log.info("最匹配该抓拍人脸的人脸模型所在的人脸库ID:" + new String(FDIDbytes).trim());
				}
				
				//获取人脸图片ID
				byte[] PIDbytes;
				if ((strFaceSnapMatch.struBlockListInfo.dwPIDLen > 0) && (strFaceSnapMatch.struBlockListInfo.pPID != null)) {
					ByteBuffer PIDbuffers = strFaceSnapMatch.struBlockListInfo.pPID.getByteBuffer(0, strFaceSnapMatch.struBlockListInfo.dwPIDLen);
					PIDbytes = new byte[strFaceSnapMatch.struBlockListInfo.dwPIDLen];
					PIDbuffers.rewind();
					PIDbuffers.get(PIDbytes);
					log.info("最匹配该抓拍人脸的人脸模型所在人脸图片ID:" + new String(PIDbytes).trim());
				}
				log.info("人脸名单比对报警事件结束");
				break;
			
			
			//交通抓拍的终端图片上传
			case HCNetSDK.COMM_ITS_PLATE_RESULT:
				HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();
				strItsPlateResult.write();
				Pointer pItsPlateInfo = strItsPlateResult.getPointer();
				pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
				strItsPlateResult.read();
				
				try {
					// struPlateInfo：车牌信息结构   sLicense：车牌号码，注：中东车牌需求把小字也纳入车牌号码，小字和车牌号中间用空格分隔
					String carNumber = new String(strItsPlateResult.struPlateInfo.sLicense, "GBK");
					// byVehicleType：车辆类型，定义详见VTR_RESULT
					log.info("抓拍的车辆类型：" + HKCarTypeUtils.getCarType(strItsPlateResult.byVehicleType) + ",车牌号：" + carNumber);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				// 将设备抓拍图片写出到存储设备[或上传到服务器]
				// dwPicNum：图片数量（与byGroupNum不同，代表本条信息附带的图片数量）
				for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
					// struPicInfo：图片信息，单张回调，最多6张图，由序号区分
					if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
						String newName = sf.format(new Date());
						FileOutputStream fout;
						try {
							// sDeviceIP：设备IP地址
							String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\car\\" + new String(pAlarmer.sDeviceIP).trim() + "_"
								+ newName + "_type[" + strItsPlateResult.struPicInfo[i].byType + "]_ItsPlate.jpg";
							fout = new FileOutputStream(filename);
							//将字节写入文件
							long offset = 0;
							ByteBuffer buffers = strItsPlateResult.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[i].dwDataLen);
							byte[] bytes = new byte[strItsPlateResult.struPicInfo[i].dwDataLen];
							buffers.rewind();
							buffers.get(bytes);
							fout.write(bytes);
							fout.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
		}
	}
	
	
	/**
	 * byte[]转int
	 *
	 * @param bytes 需要转换成int的数组
	 * @return int值
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (3 - i) * 8;
			value += (bytes[i] & 0xFF) << shift;
		}
		return value;
	}
	
}


