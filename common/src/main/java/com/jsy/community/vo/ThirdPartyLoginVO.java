package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-10 15:40
 **/
@Data
public class ThirdPartyLoginVO implements Serializable {
    private Integer isBindWeChat;
    private Integer isBindMobile;
    private Object data;

}
