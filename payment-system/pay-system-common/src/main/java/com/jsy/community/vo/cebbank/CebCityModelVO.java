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
    private String cityId;
    private String provinceId;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String description;
    private String categoryId;
    private String categoryType;
    private String cityFlag;
}
