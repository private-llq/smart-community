package com.jsy.community.callback;

import com.jsy.community.sdk.HCNetSDK;

/**
 * @author lihao
 * @create 2021-04-13-15:23
 * 功能：透传接口实现，透传ISAPI命令
 */
public class ISAPI {
    private int space = 0;
    
    public void Isapi(int lUserID, String url, String inputXml) {
        HCNetSDK.NET_DVR_STRING_POINTER stringRequest = new HCNetSDK.NET_DVR_STRING_POINTER(1024);
        stringRequest.read();
        //输入ISAPI协议命令   批量查询人脸库命令：GET /ISAPI/Intelligent/FDLib
        stringRequest.byString = url.getBytes();
        stringRequest.write();
        
        HCNetSDK.NET_DVR_STRING_POINTER stringInBuffer = new HCNetSDK.NET_DVR_STRING_POINTER(5 * 1024);
        stringInBuffer.read();
        //输入XML文本，GET命令不传输入文本
        String strInbuffer = inputXml;
        stringInBuffer.byString = strInbuffer.getBytes();
        stringInBuffer.write();
        
        HCNetSDK.NET_DVR_XML_CONFIG_INPUT struXMLInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        struXMLInput.read();
        struXMLInput.dwSize = struXMLInput.size();
        struXMLInput.lpRequestUrl = stringRequest.getPointer();
        struXMLInput.dwRequestUrlLen = stringRequest.byString.length;
        struXMLInput.lpInBuffer = stringInBuffer.getPointer();
        struXMLInput.dwInBufferSize = stringInBuffer.byString.length;
        struXMLInput.write();
        
        HCNetSDK.NET_DVR_STRING_POINTER stringXMLOut = new HCNetSDK.NET_DVR_STRING_POINTER(8 * 1024);
        stringXMLOut.read();
        HCNetSDK.NET_DVR_STRING_POINTER struXMLStatus = new HCNetSDK.NET_DVR_STRING_POINTER(1024);
        struXMLStatus.read();
        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struXMLOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        struXMLOutput.read();
        struXMLOutput.dwSize = struXMLOutput.size();
        struXMLOutput.lpOutBuffer = stringXMLOut.getPointer();
        struXMLOutput.dwOutBufferSize = stringXMLOut.size();
        struXMLOutput.lpStatusBuffer = struXMLStatus.getPointer();
        struXMLOutput.dwStatusSize = struXMLStatus.size();
        struXMLOutput.write();
        if (!FaceMain.hCNetSDK.NET_DVR_STDXMLConfig(lUserID, struXMLInput, struXMLOutput)) {
            int iErr = FaceMain.hCNetSDK.NET_DVR_GetLastError();
            System.err.println("NET_DVR_STDXMLConfig失败，错误号" + iErr);
            return;
        } else {
            stringXMLOut.read();
            System.out.println("输出文本大小：" + struXMLOutput.dwReturnedXMLSize);
            //打印输出XML文本
            String strOutXML = new String(stringXMLOut.byString).trim();
            System.out.println(strOutXML);
            struXMLStatus.read();
            String strStatus = new String(struXMLStatus.byString).trim();
            System.out.println(strStatus);
        }
    }
}
