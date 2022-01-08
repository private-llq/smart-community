package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/22 15:38
 * @Version: 1.0
 **/
@Data
public class PermitQO implements Serializable {
    private String name;
    private String permit;
    private String scope;
    private String description;
    private Long createUid;
}
