package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 用户个人信息
 * @author: Hu
 * @create: 2021-03-11 13:42
 **/
@Data
public class UserDataVO implements Serializable {
    private String avatarUrl;
    private String nickname;
    private String birthdayTime;
}
