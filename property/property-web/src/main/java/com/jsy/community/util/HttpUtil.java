package com.jsy.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class HttpUtil {



    private static HttpClient httpClient = HttpClientBuilder.create().build();
    // in milliseconds  连接超时的时间
    private final static int CONNECT_TIMEOUT = 5000;
    private final static String CHARSET = "UTF-8";

    public static String post(String url, String json) {
        HttpPost post = new HttpPost(url);
        post.addHeader(HTTP.CONTENT_TYPE, "application/json");
        StringEntity entity = new StringEntity(json, CHARSET);
        post.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return EntityUtils.toString(response.getEntity(), CHARSET);
                }
            }
            throw new IOException("请求出错");
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }
    
    public static String postData(String urlStr, String data){
        return postData(urlStr, data, null);
    }
    
    /**
     * post数据请求
     * @param urlStr
     * @param data
     * @param contentType
     * @return
     */
    public static String postData(String urlStr, String data, String contentType){
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(CONNECT_TIMEOUT);
            if(contentType != null)
                conn.setRequestProperty("content-type", contentType);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), CHARSET);
            if(data == null)
                data = "";
            writer.write(data);
            writer.flush();
            writer.close();
            
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            return sb.toString();
        } catch (IOException e) {
            log.info("Error connecting to " + urlStr + ": " + e.getMessage());
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
            }
        }
        return null;
    }
}