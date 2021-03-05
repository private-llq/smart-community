package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;
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
 * @ClassName FMSGCallBack
 * @Date 2021/3/3  15:43
 * @Description TODO
 * @Version 1.0
 **/
@Slf4j
public class FMSGCallBack implements HCNetSDK.FMSGCallBack_V31 {

	@Override
	public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
		AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
		return true;
	}
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 人脸比对功能是设备实现的，设备配置好之后自动抓拍和人脸库里面人脸图片比对，比对相似度超过相似的阈值就是人脸比对成功，不是上传相似度最高的结果  ->来自技术客服回答
	 *              人脸抓拍：支持对运动人脸进行检测、跟踪、抓拍、评分、筛选，输出最优的人脸抓图，最多同时检测60个/帧，支持前端人脸比对，支持最多3个人脸库的管理，支持最多9万张人脸库，每个人脸库支持的人脸数量为30000个，单张人脸不超过300kb  ->来自产品客服回答
	 *
	 * @Date 2021/3/4 10:17
	 * @Param [lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser]
	 **/
	public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
			
			//lCommand是传的报警类型
			switch (lCommand) {
				case HCNetSDK.COMM_UPLOAD_FACESNAP_RESULT:
					log.info("");
					log.info("人脸抓拍事件开始触发");
					//实时人脸抓拍上传
					HCNetSDK.NET_VCA_FACESNAP_RESULT strFaceSnapInfo = new HCNetSDK.NET_VCA_FACESNAP_RESULT();
					strFaceSnapInfo.write();
					Pointer pFaceSnapInfo = strFaceSnapInfo.getPointer();
					pFaceSnapInfo.write(0, pAlarmInfo.getByteArray(0, strFaceSnapInfo.size()), 0, strFaceSnapInfo.size());
					strFaceSnapInfo.read();
					
					log.info("人脸抓拍上传触发，抓拍人脸评分："+strFaceSnapInfo.dwFaceScore+"，年龄段：" + strFaceSnapInfo.struFeature.byAgeGroup + "，性别：" + strFaceSnapInfo.struFeature.bySex);
					
					//人脸图片写文件
					try {
						log.info("触发抓拍人脸保存事件");
						SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); //设置日期格式
						String time = df.format(new Date()); // new Date()为获取当前系统时间
						
						FileOutputStream small = new FileOutputStream(System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + time + "small.jpg");
						FileOutputStream big = new FileOutputStream(System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + time + "big.jpg");
						
						if (strFaceSnapInfo.dwFacePicLen > 0) { // dwFacePicLen：人脸子图的长度，为0表示没有图片，大于0表示有图片
							try {
								//pBuffer1：人脸子图的图片数据                                            dwFacePicLen：人脸子图的长度，为0表示没有图片，大于0表示有图片
								small.write(strFaceSnapInfo.pBuffer1.getByteArray(0, strFaceSnapInfo.dwFacePicLen), 0, strFaceSnapInfo.dwFacePicLen);
								small.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
						if (strFaceSnapInfo.dwFacePicLen > 0) {
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
				 * 1. 实时抓拍与人脸名单中的图片进行比对，同时输出抓拍信息和人脸名单相关信息（包括比对出的相似度最高的人脸图片）。
				 * 2. 人脸比对的匹配图片有多张的时候，多次回调分配获取每一张图片，通过struSnapInfo中的dwUIDLen和pUIDBuffer判断是否是同一次比对结果，当接收到同一个pUIDbuffer的报警匹配图片的张数等于byMatchPicNum时，表示这个报警的匹配图片信息接收结束。
				 *    如果在一段时间内接收到的匹配图片的张数不等于byMatchPicNum时，上层应该设置一个超时时间，不再继续等待这个报警后续的匹配信息，建议超时时间1分钟。
				 * 3. dwUIDLen为0的情况，上层则不做匹配信息的流程处理，则认为是老设备上传的报警按照以前的处理流程接口报警信息。
				 * 4. 这个结构体扩展的时差，是对应NET_VCA_FACESNAP_INFO_ALARM中的绝对时标
				 * @Date 2021/3/4 9:51
				 * @Param [lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser]
				 **/
				case HCNetSDK.COMM_SNAP_MATCH_ALARM:
					log.info("");
					log.info("人脸名单比对报警事件开始触发");
					//人脸名单比对报警
					HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM();
					strFaceSnapMatch.write();
					Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
					pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0, strFaceSnapMatch.size());
					strFaceSnapMatch.read();
					
					// dwSnapPicLen：设备识别抓拍图片长度
					// byPicTransType：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
					// ----------------大图[远距离图]
					if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
						SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); //设置日期格式
						String time = df.format(new Date()); // new Date()为获取当前系统时间
						FileOutputStream fout;
						try {
//							String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + time + "_pSnapPicBuffer" + ".jpg";
							String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + time + "远距离" + ".jpg";
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
					if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {  // struSnapInfo：人脸抓拍上传信息  dwSnapFacePicLen：抓拍人脸子图的长度，为0表示没有图片，大于0表示有图片  byPicTransType ：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
						String newName = sf.format(new Date());
						FileOutputStream fout;
						try {
//							String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + newName + "_struSnapInfo_pBuffer1" + ".jpg";
							String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + newName + "近距离" + ".jpg";
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
					// struBlockListInfo：人脸比对报警信息
					// dwBlockListPicLen：黑名单人脸子图的长度，为0表示没有图片，大于0表示有图片
					// byPicTransType：图片数据传输方式: 0- 二进制，1- URL路径(HTTP协议的图片URL)
					if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) { // struBlockListInfo：人脸比对报警信  dwBlockListPicLen：黑名单人脸子图的长度，为0表示没有图片，大于0表示有图片
						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
						String newName = sf.format(new Date());
						FileOutputStream fout;
						try {
//							String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + newName + "_fSimilarity_" + strFaceSnapMatch.fSimilarity + "_struBlockListInfo_pBuffer1" + ".jpg";
							String filename = System.getProperty("user.dir") + "\\property\\property-web\\src\\main\\resources\\pic\\" + newName + "_fSimilarity_" + strFaceSnapMatch.fSimilarity + "_struBlockListInfo_pBuffer1" + ".jpg";
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
						log.info("人脸名单比对报警，相识度：" + strFaceSnapMatch.fSimilarity + "，人脸库姓名：" + new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byName, "GBK").trim() + "，人脸库证件信息：" + new String(strFaceSnapMatch.struBlockListInfo.struBlockListInfo.struAttribute.byCertificateNumber).trim());
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
			}
	}
}
