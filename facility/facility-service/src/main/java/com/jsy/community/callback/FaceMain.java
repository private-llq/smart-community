package com.jsy.community.callback;


import com.jsy.community.sdk.HCNetSDK;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.Scanner;

/**
 * @return
 * @Author lihao
 * @Description 这个是我扭着客服找他私人要的 他自己写的  他做了人脸库相关的功能   可以打debug看他怎么做出来人脸库(增删改查)的操作
 * @Date 2021/5/8 11:53
 * @Param
 **/
public class FaceMain {

    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
    
    static int lUserID = -1;//用户句柄
    public static void main(String[] args) throws InterruptedException, IOException, DocumentException {
        FaceMain faceMain=new FaceMain();
        FaceLibManage faceLibManage=new FaceLibManage();
        FDSearch fdSearch=new FDSearch();
        Alarm alarm=new Alarm();

        /**初始化*/
        hCNetSDK.NET_DVR_Init();

        /**登录*/
        faceMain.Login();

        /**获取所有人脸库信息:ID*/
        faceLibManage.getAllFaceLib(lUserID);
        
        /**获取指定人脸库的信息*/
        faceLibManage.getOneFaceLib(lUserID,"1");

        /**创建一个人脸库*/
//        faceLibManage.creatFaceLib(lUserID);

        /**删除一个人脸库*/
//        faceLibManage.deleteFaceLib(lUserID);

        /**上传人脸图片到人脸库*/
//        facePicManage.uploadPic(lUserID,"1");
        
        /**查询指定人脸库的人脸信息*/
//        facePicManage.searchFaceLibData(lUserID);

        /**获取人脸比对库图片数据附加信息*/
/*                FDID=A5F41447CB5E41E296C92369CF1873F5
                PID=3E0D576B7675466FBF500D2B6A362809*/
//        facePicManage.getFaceLibPicInfo(lUserID,"A5F41447CB5E41E296C92369CF1873F5","3E0D576B7675466FBF500D2B6A362809");

        /**删除人脸比对库图片数据(包含附加信息)*/
//        facePicManage.deleteFaceLibPicData(lUserID,"A5F41447CB5E41E296C92369CF1873F5","3E0D576B7675466FBF500D2B6A362809");

        /**以图搜图*/
//        fdSearch.analysisImage(lUserID);
        
        
//        fdSearch.faceLibSearch(lUserID);

        /**查询设备中存储的人脸比对结果信息*/
//        fcSearch.faceMatchListSearch(lUserID);
    
        fdSearch.getFaceLibSpace(lUserID);
        

        /**报警布防实时接收人脸比对的结果信息*/
        alarm.SetAlarm(lUserID);

        while (true) {
            //这里加入控制台输入控制，是为了保持连接状态，当输入Y表示布防结束
            System.out.print("请选择是否撤出布防(Y/N)：");
            Scanner input = new Scanner(System.in);
            String str = input.next();
            if (str.equals("Y")) {
                break;
            }
        }

        faceMain.Logout(alarm);

    }


    /**
     * 登录
     */
    public void Login() {
        //注册
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        String m_sDeviceIP = "192.168.12.188";//设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());
        String m_sUsername = "admin";//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());
        String m_sPassword = "root1234";//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());
        m_strLoginInfo.wPort = 8000;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.write();
        HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息
        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID == -1) {
            System.out.println("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
            return;
        } else {
            System.out.println("登录成功！");

        }
    }


    /**
     * 登出操作
     * @param alarm 传入报警监听句柄
     */
    public void Logout(Alarm alarm){

        /**退出之前判断布防监听状态，并做撤防和停止监听操作*/
        if (alarm.lAlarmHandle >= 0){
            if (!hCNetSDK.NET_DVR_CloseAlarmChan_V30(alarm.lAlarmHandle)){
                System.out.println("撤防失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
            }else{
                System.out.println("撤防成功！！！");
            }
        }
        if (alarm.lListenHandle >= 0){
            if (!hCNetSDK.NET_DVR_StopListen_V30(alarm.lListenHandle)){
                System.out.println("取消监听失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
            }else{
                System.out.println("停止监听成功！！！");
            }
        }

        /**登出和清理，释放SDK资源*/
        if (lUserID>=0)
        {
            hCNetSDK.NET_DVR_Logout(lUserID);
        }
        hCNetSDK.NET_DVR_Cleanup();
    }
}
