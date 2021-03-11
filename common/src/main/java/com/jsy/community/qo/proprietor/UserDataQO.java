package com.jsy.community.qo.proprietor;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserDataQO implements Serializable {
    private String avatarUrl;
    private String nickname;
    private LocalDate birthdayTime;

}