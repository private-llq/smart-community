package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 注册VO
 * @Date: 2021/11/13 15:14
 * @Version: 1.0
 **/
@Data
public class CebLoginVO implements Serializable {
    // 渠道标识
    private String canal;

    // 用户标识
    private String sessionId;

    // 用户开户状态
    // 1新开户的用户;0之前开户的用户
    private Integer userStatus;
}
