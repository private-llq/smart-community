package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserDataQO implements Serializable {
    @ApiModelProperty(hidden = true)
    private String avatarUrl;
    private String nickname;
    private LocalDate birthdayTime;

}