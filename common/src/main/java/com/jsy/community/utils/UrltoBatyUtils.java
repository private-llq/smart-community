package com.jsy.community.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrltoBatyUtils {
    /**
     * @Description: 根据url路劲转换为byte
     * @Param: [UrlPath]
     * @Return: byte[]
     * @Author: Tian
     * @Date: 2021/9/23-15:22
     **/
    public static byte[] getDate(String UrlPath) throws IOException {
        URL url = new URL(UrlPath);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(4 * 1000);
        InputStream inStream = con .getInputStream();    //通过输入流获取图片数据
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        byte[] data =  outStream.toByteArray();
        return data;
    }
}
