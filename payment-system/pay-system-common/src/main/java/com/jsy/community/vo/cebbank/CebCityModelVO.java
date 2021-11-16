package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/12 17:09
 * @Version: 1.0
 **/
@Data
public class CebCityModelVO implements Serializable {
    // 城市首字母
    private String cityFlag;

    // 城市
    private String cityName;
}
