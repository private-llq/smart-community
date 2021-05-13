package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;
import com.sun.jna.Pointer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * &#x5e03;&#x9632;&#x56de;&#x8c03;&#x51fd;&#x6570;
 *
 * @author jiangxin
 * @create 2021-03-11-11:09
 */
public class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {
	
	//报警信息回调函数
	@Override
	public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
		AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
		return true;
	}
	
	public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
		System.out.println("报警事件发生，进入回调");
		String hexString = Integer.toHexString(lCommand);
		System.out.println("报警类型 = " + hexString);
		String sAlarmType = new String();
		String[] newRow = new String[3];
		//报警时间
		Date today = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String[] sIP = new String[2];
		sAlarmType = new String("lCommand=") + lCommand;
		switch (lCommand) {
			case HCNetSDK.COMM_SNAP_MATCH_ALARM:
				//人脸黑名单比对报警
				System.out.println("SNAP_MATCH_ALARM");
				HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM();
				strFaceSnapMatch.write();
				Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
				pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0, strFaceSnapMatch.size());
				strFaceSnapMatch.read();
				//比对结果
				System.out.println("比对结果：" + (strFaceSnapMatch.byContrastStatus == 1 ? "比对成功" : "比对失败"));
				//人脸库ID
				if (strFaceSnapMatch.struBlockListInfo.dwFDIDLen > 0) {
					long offset1 = 0;
					ByteBuffer buffers1 = strFaceSnapMatch.struBlockListInfo.pFDID.getByteBuffer(offset1, strFaceSnapMatch.struBlockListInfo.dwFDIDLen);
					byte[] bytes1 = new byte[strFaceSnapMatch.struBlockListInfo.dwFDIDLen];
					buffers1.get(bytes1);
					System.out.println("人脸库ID:" + new String(bytes1));
				}
				//人脸库图片ID
				if (strFaceSnapMatch.struBlockListInfo.dwPIDLen > 0) {
					long offset2 = 0;
					ByteBuffer buffers2 = strFaceSnapMatch.struBlockListInfo.pPID.getByteBuffer(offset2, strFaceSnapMatch.struBlockListInfo.dwPIDLen);
					byte[] bytes2 = new byte[strFaceSnapMatch.struBlockListInfo.dwPIDLen];
					buffers2.get(bytes2);
					System.out.println("图片ID：" + new String(bytes2));
				}
				//保存抓拍人脸图，图片数据传输方式: 0- 二进制
				if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
					String newName = sf.format(new Date());
					FileOutputStream fout;
					try {
						String filename = ".//pic//" + newName + "_pSnapPicBuffer" + ".jpg";
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
				//保存抓拍人脸图URL，图片数据传输方式: 1- URL 注：超脑设备人脸比对报警图片一般按照URL上传，根据URL去下载设备中存储的图片，认证方式选择：摘要认证
				if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 1)) {
					long offset = 0;
					ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
					byte[] bytes = new byte[strFaceSnapMatch.dwSnapPicLen];
					buffers.rewind();
					buffers.get(bytes);
					System.out.println("人脸抓拍图片 URL:" + new String(bytes));
				}
				if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
					String newName = sf.format(new Date());
					FileOutputStream fout;
					try {
						String filename = ".//pic//" + newName + "_struSnapInfo_pBuffer1" + ".jpg";
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
				if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 1)) {
					long offset = 0;
					ByteBuffer buffers = strFaceSnapMatch.struSnapInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen);
					byte[] bytes = new byte[strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen];
					buffers.rewind();
					buffers.get(bytes);
					System.out.println("人脸抓拍图片1 URL:" + new String(bytes));
				}
				//保存人脸库图片，图片数据传输方式: 0- 二进制
				if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
					String newName = sf.format(new Date());
					FileOutputStream fout;
					try {
						String filename = ".//pic//" + newName + "_fSimilarity_" + strFaceSnapMatch.fSimilarity + "_struBlackListInfo_pBuffer1" + ".jpg";
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
				//保存人脸库图片，图片数据传输方式: 1-URL
				if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 1)) {
					long offset = 0;
					ByteBuffer buffers = strFaceSnapMatch.struBlockListInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen);
					byte[] bytes = new byte[strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen];
					buffers.rewind();
					buffers.get(bytes);
					System.out.println("人脸库图片 URL:" + new String(bytes));
				}
				////抓拍库附加信息数据指针，附加信息的XML文本中包含人脸温度数据
				if (strFaceSnapMatch.struBlockListInfo.struBlockListInfo.dwFCAdditionInfoLen > 0) {
					long offset = 0;
					ByteBuffer buffers = strFaceSnapMatch.struBlockListInfo.struBlockListInfo.pFCAdditionInfoBuffer.getByteBuffer(offset, strFaceSnapMatch.struBlockListInfo.struBlockListInfo.dwFCAdditionInfoLen);
					byte[] bytes = new byte[strFaceSnapMatch.struBlockListInfo.struBlockListInfo.dwFCAdditionInfoLen];
					buffers.rewind();
					buffers.get(bytes);
					System.out.println("抓拍库附加信息:" + new String(bytes));
				}
				break;
			default:
				System.out.println("报警类型" + lCommand);
				break;
		}
	}
}
