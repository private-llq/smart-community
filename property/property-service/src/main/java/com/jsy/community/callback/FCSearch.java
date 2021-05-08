package com.jsy.community.callback;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class FCSearch {
    ISAPI isapi = new ISAPI();
    //查询设备中人脸比对数据结果
    public void faceMatchListSearch(int lUserID) {
/*        POST /ISAPI/Intelligent/FDLib/FCSearch ：查询设备端存储的人脸比对结果信息(包括抓拍图片)
        POST /ISAPI/Intelligent/FDLib/FCSearch/channels/<ID>(ID表示通道号，从1开始) : 按通道查询设备端存储的人脸比对结果信息(包括抓拍图片)*/
        isapi.Isapi(lUserID, "POST /ISAPI/Intelligent/FDLib/FCSearch", XmlCreat());
    }
    
    //  查询人脸比对数据的输入XML报文
    public static String XmlCreat() {
        Document document1;
        Element root = DocumentHelper.createElement("FCSearchDescription");
        document1 = DocumentHelper.createDocument(root);
        Element searchID = root.addElement("searchID");
        searchID.setText("C929433A-AD10-0001-CA62-1A701E0015F9");
        Element searchResultPosition = root.addElement("searchResultPosition");
        searchResultPosition.setText("0");
        Element maxResults = root.addElement("maxResults");
        maxResults.setText("50");
        Element FDID = root.addElement("FDID");
        FDID.setText("6C5791A3708B4C418388E05FCD6F6BD9");
        Element snapStartTime = root.addElement("snapStartTime");
        snapStartTime.setText("2021-04-14T17:00:00Z");
        Element snapEndTime = root.addElement("snapEndTime");
        snapEndTime.setText("2021-04-15T11:53:00Z");
        Element faceMatchInfoEnable = root.addElement("faceMatchInfoEnable");
        faceMatchInfoEnable.setText("true");
        Element eventType = root.addElement("eventType");
        eventType.setText("whiteFaceContrast");
        Element sortord = root.addElement("sortord");
        sortord.setText("time");
        String requestXml = document1.asXML();
        return requestXml;
    }
    
    // 获取指定人脸库剩余空间
}
