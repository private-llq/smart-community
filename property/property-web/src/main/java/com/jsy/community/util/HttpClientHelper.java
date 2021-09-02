package com.jsy.community.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpClientHelper {


    private HttpClientHelper() {

    }

    /**
     * 发起POST请求
     *
     * @param url       url
     * @param paramJson 参数的json格式
     */
    public static String sendPost(String url, String paramJson) {


        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(url);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");
        try {
            //json格式的参数解析
            RequestEntity entity = new StringRequestEntity(paramJson, "application/json", "UTF-8");
            postMethod.setRequestEntity(entity);

            httpClient.executeMethod(postMethod);
            String result = postMethod.getResponseBodyAsString();
            postMethod.releaseConnection();
            return result;
        } catch (IOException e) {

        }
        return null;
    }

    /**
     * 发起GET请求
     *
     * @param urlParam url请求，包含参数
     */
    public static String sendGet(String urlParam) {

        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建GET请求方法实例对象
        GetMethod getMethod = new GetMethod(urlParam);
        // 设置post请求超时时间
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        getMethod.addRequestHeader("Content-Type", "application/json");
        try {
            httpClient.executeMethod(getMethod);
            String result = getMethod.getResponseBodyAsString();
            getMethod.releaseConnection();

            return result;
        } catch (IOException e) {

        }
        return null;
    }

    public static void main(String[] args) {
        String url = "https://jiashubing.cn/tencenttest";
        String param = "{\"aaa\":\"bbbbbbb\"}";
        sendPost(url, param);
        String urlParam = "https://jiashubing.cn/talk/document?fileid=1234";
        sendGet(urlParam);
    }

}