package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;
import com.sun.jna.Pointer;

public class Alarm {
    public int lAlarmHandle = -1; //布防句柄
    public int lListenHandle = -1; //监听句柄
    public static FMSGCallBack_V31 fMSFCallBack_V31 = null;
    /**
     * 报警布防 （布防和监听选其一）
     *
     * @param lUserID 用户登录句柄
     */
    public void SetAlarm(int lUserID) {
        //尚未布防,需要布防
        if (lAlarmHandle < 0) {
            if (fMSFCallBack_V31 == null) {
                fMSFCallBack_V31 = new FMSGCallBack_V31();
                Pointer pUser = null;
                if (!FaceMain.hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser)) {
                    System.out.println("设置回调函数失败!");
                } else {
                    System.out.println("设置回调函数成功!");
                }
            }
            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 1;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 1;   //布防类型 1：客户端布防 2：实时布防
            m_strAlarmInfo.write();
            lAlarmHandle = FaceMain.hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
            System.out.println("lAlarmHandle: " + lAlarmHandle);
            if (lAlarmHandle == -1) {
                System.out.println("布防失败，错误码为" + FaceMain.hCNetSDK.NET_DVR_GetLastError());
            } else {
                System.out.println("布防成功");
            }
        }
    }
}
