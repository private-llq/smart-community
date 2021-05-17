package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 响应接参
 * @Date: 2021/4/10 14:37
 * @Version: 1.0
 **/
@Data
public class PlugRandomMsgBodyVO extends UnionPayBaseVO implements Serializable {
    // 随机因子list
    private List<PlugRandomKeyVO> list;
}
