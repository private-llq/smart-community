package com.jsy.community.callback;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class FaceLibManage {
    ISAPI isapi = new ISAPI();

    // 查询人脸库
    public void getAllFaceLib(int lUserID) {
        //批量查询人脸库命令：GET /ISAPI/Intelligent/FDLib 输入为空
        isapi.Isapi(lUserID, "GET /ISAPI/Intelligent/FDLib", "");
    }

    //  创建人脸库输入XML报文
    public static String XmlCreat(String ID, String Name) {
        /*<CreateFDLibList version="2.0" xmlns="http://www.isapi.org/ver20/XMLSchema">
        <!--req,创建人脸比对库输入参数-->
        <CreateFDLib>
        <id>
        <!--req, xs:integer,"表示list中子项个数,从"1"开始赋值,依次增加" -->
        </id>
        <name>
        <!--opt, xs:string,"人脸比对库名称"-->
        </name>
        <thresholdValue>
        <!--opt, xs:integer, "检测阈值,阈值越大检测准确率越低, 范围[0,100]"-->
        </thresholdValue>
        <customInfo>
        <!--opt, xs:string, 人脸库附加信息-->
        </customInfo>
        <customFaceLibID>
        <!--opt, xs:string, "自定义人脸库ID, 由上层下发给设备, 该ID由上层维护并确保唯一性,
        设备侧需将自定义人脸库ID与设备生成的FDID进行关联, 确保上层可通过下发人脸库ID来替代下发FDID进行后续操作"-->
            </customFaceLibID>
        </CreateFDLib>
        </CreateFDLibList>*/
        Document document1;
        Element root = DocumentHelper.createElement("CreateFDLibList");
        document1 = DocumentHelper.createDocument(root);
        Element CreateFDLib = root.addElement("CreateFDLib");
        Element id = CreateFDLib.addElement("id");
        id.setText(ID);
        Element name = CreateFDLib.addElement("name");
        name.setText(Name);
        Element thresholdValue = CreateFDLib.addElement("thresholdValue");
        thresholdValue.setText("70");
        String requestXml = document1.asXML();
        return requestXml;
    }

    // 创建人脸库
    public void creatFaceLib(int lUserID) {   //POST /ISAPI/Intelligent/FDLib
        isapi.Isapi(lUserID, "POST /ISAPI/Intelligent/FDLib", XmlCreat("6", "test02"));
    }

    //删除指定人脸库
    public void deleteFaceLib(int lUserID) {
       /* DELETE /ISAPI/Intelligent/FDLib/<FDID>
        (FDID为设备自动生成的FDID*/
        isapi.Isapi(lUserID, "DELETE /ISAPI/Intelligent/FDLib/AAEEB3BB78424F81B44D74F516BE685E", ""); //AAEEB3BB78424F81B44D74F516BE685E是获取到的一个人脸库ID
    }

    //查询指定人脸库信息
    public void getOneFaceLib(int lUserID, String FDID) {
        //GET /ISAPI/Intelligent/FDLib/<FDID>
        String requestUrl = "GET /ISAPI/Intelligent/FDLib/" + FDID;
        isapi.Isapi(lUserID, requestUrl, "");
    }
}
