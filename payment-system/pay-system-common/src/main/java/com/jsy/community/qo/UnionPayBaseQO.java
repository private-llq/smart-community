package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/5/10 17:49
 * @Version: 1.0
 **/
@Data
public class UnionPayBaseQO implements Serializable {
    private String msgBody;
    private String msgType;
}
