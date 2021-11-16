package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 城市VO
 * @Date: 2021/11/12 17:07
 * @Version: 1.0
 **/
@Data
public class CebCityVO implements Serializable {
    // 导航首字母
    private String section;

    // 城市列表
    private List<CebCityModelVO> cityModelList;

}
