package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/12 16:56
 * @Version: 1.0
 **/
@Data
public class CebQueryCityVO implements Serializable {
    // 数据模型
    private String cityPagingModel;

    // 所有缴费城市
    private List<CebCityVO> cityCategoryModelList;

    // 热门城市
    private List<CebCityVO> cityHotCategoryModelList;

    // 最近常用城市
    private List<CebCityVO> cityRenCategoryModelList;
}
