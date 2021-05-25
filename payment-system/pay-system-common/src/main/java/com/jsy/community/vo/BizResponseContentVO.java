package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * User: chenhf
 * Date: 2018/10/31
 * Time: 19:56
 */
@Data
public class BizResponseContentVO implements Serializable {
    private String subcode;
    private String submsg;
    private String name;
    private Integer age;

    private String rspCode;
    private String rspResult;
    private String list;
    private String plugRandomKey;
    private String msgBody;
}
