package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 获取凭据返参
 * @Date: 2021/5/7 17:53
 * @Version: 1.0
 **/
@Data
public class CredentialVO extends UnionPayBaseVO implements Serializable {

    /**
     * 跳转URL
     */
    private String jumpUrl;

    /**
     * 凭据
     */
    private String ticket;

    /**
     * 注册号
     */
    private String registerNo;
}
