package com.jsy.community.util;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpUtil {



    private static HttpClient httpClient = HttpClientBuilder.create().build();
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

}