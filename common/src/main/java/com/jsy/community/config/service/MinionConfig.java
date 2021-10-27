package com.jsy.community.config.service;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Author: Pipi
 * @Description: minio配置
 * @Date: 2021/10/21 10:06
 * @Version: 1.0
 **/
//@Configuration
public class MinionConfig {
    // ip
    public static String endPoint;

    //端口
    public static int port;

    //ACCESS_KEY
    public static String accessKey;

    //SECRET_KEY
    public static String secretKey;

    @Value("${minio.end-point}")
    public void setEndPoint(String endPoint) {
        MinionConfig.endPoint = endPoint;
    }

    @Value("${minio.port}")
    public void setPort(int port) {
        MinionConfig.port = port;
    }

    @Value("${minio.access-key}")
    public void setAccessKey(String accessKey) {
        MinionConfig.accessKey = accessKey;
    }

    @Value("${minio.secret-key}")
    public void setSecretKey(String secretKey) {
        MinionConfig.secretKey = secretKey;
    }
}
