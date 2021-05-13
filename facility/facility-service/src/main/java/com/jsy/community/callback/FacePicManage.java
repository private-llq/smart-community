package com.jsy.community.callback;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.*;


public class FacePicManage {
    ISAPI isapi = new ISAPI();
    //查询人脸比对库里的图片数据
    public void searchFaceLibData(int lUserID) {
        //POST /ISAPI/Intelligent/FDLib/FDSearch
        isapi.Isapi(lUserID, "POST /ISAPI/Intelligent/FDLib/FDSearch", XmlCreat("1", "黄启云"));
    }
    //获取人脸比对库图片数据附加信息
    public void getFaceLibPicInfo(int lUserID, String FDID, String PID) {
        //GET /ISAPI/Intelligent/FDLib/<FDID>/picture/<PID>
        String requestUrl = "GET /ISAPI/Intelligent/FDLib/" + FDID + "/picture/" + PID;
        isapi.Isapi(lUserID, requestUrl, "");
    }
    //删除人脸比对库图片数据(包含附加信息)
    public void deleteFaceLibPicData(int lUserID, String FDID, String PID) {
        //DELETE /ISAPI/Intelligent/FDLib/<FDID>/picture/<PID>
        String requestUrl = "DELETE /ISAPI/Intelligent/FDLib/" + FDID + "/picture/" + PID;
        isapi.Isapi(lUserID, requestUrl, "");
    }
    //获取指定人脸库剩余空间（支持导入人脸图片的剩余张数）
    public void getFaceLibRemainSpace(int lUserID, String FDID) {
        // GET /ISAPI/Intelligent/FDLib/<FDID>/picture/surplusCapacity
        String requestUrl = "GET /ISAPI/Intelligent/FDLib/" + FDID + "/picture/surplusCapacity";
        System.out.println(requestUrl);
        isapi.Isapi(lUserID, requestUrl, "");
    }
    //查询的人员输入报文
    //ID:人脸库ID
    // Name： 人员姓名
    public static String XmlCreat(String ID, String Name) {
/*        <FDSearchDescription>
        <searchID>C929433A-AD10-0001-CA62-1A701E0015F2</searchID>
        <maxResults>50</maxResults>
        <searchResultPosition>0</searchResultPosition>
        <FDID>1135C03401404CC696F02B03F649ACFE</FDID>
        <name>test</name>
        <sex>male</sex>
        <province>21</province>
        <city>01</city>
        </FDSearchDescription>*/
        Document document1;
        Element root = DocumentHelper.createElement("FDSearchDescription");
        document1 = DocumentHelper.createDocument(root);
        Element searchID = root.addElement("searchID");
        searchID.setText("C929433A-AD10-0001-CA62-1A701E0015F2");
        Element maxResults = root.addElement("maxResults");
        maxResults.setText("50");
        Element searchResultPosition = root.addElement("searchResultPosition");
        searchResultPosition.setText("0");
        Element FDID = root.addElement("FDID");
        FDID.setText(ID);
        Element name = root.addElement("name");
        name.setText(Name);
        Element sex = root.addElement("sex");
        sex.setText("male");
        Element province = root.addElement("province");
        province.setText("11");
        Element city = root.addElement("city");
        city.setText("01");
        String requestXml = document1.asXML();
        System.out.println(requestXml);
        return requestXml;
    }
    //添加人脸附加信息报文
    public static String XmlFaceAppendData() throws UnsupportedEncodingException {
/*        <FDSearchDescription>
        <searchID>C929433A-AD10-0001-CA62-1A701E0015F2</searchID>
        <maxResults>50</maxResults>
        <searchResultPosition>0</searchResultPosition>
        <FDID>1135C03401404CC696F02B03F649ACFE</FDID>
        <name>test</name>
        <sex>male</sex>
        <province>21</province>
        <city>01</city>
        </FDSearchDescription>*/
        Document document1;
        Element root = DocumentHelper.createElement("FaceAppendData");
        document1 = DocumentHelper.createDocument(root);
        Element bornTime = root.addElement("bornTime");
        bornTime.setText("2020-12-12T00:00:00Z");
        Element name = root.addElement("name");
        name.setText("test");
        Element sex = root.addElement("sex");
        sex.setText("male");
        Element province = root.addElement("province");
        province.setText("11");
        Element city = root.addElement("city");
        city.setText("01");
        Element certificateType = root.addElement("certificateType");
        certificateType.setText("officerID");
        Element certificateNumber = root.addElement("certificateNumber");
        certificateNumber.setText("1123123123");
        Element PersonInfoExtendList = root.addElement("PersonInfoExtendList");
        Element PersonInfoExtend = PersonInfoExtendList.addElement("PersonInfoExtend");
        Element id = PersonInfoExtend.addElement("id");
        id.setText("1");
        Element enable = PersonInfoExtend.addElement("enable");
        enable.setText("1");
        Element name1 = PersonInfoExtend.addElement("name");
        name1.setText("1");
        Element value = PersonInfoExtend.addElement("value");
        value.setText("1");
        String requestXml = document1.asXML();
        System.out.println(requestXml);
        return requestXml;
    }
    /**
     * 读取本地文件到数组中
     *
     * @param filename 本地文件
     * @return 返回读取到的数据到 byte数组
     * @throws IOException
     */
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
}
