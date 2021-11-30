package com.jsy.community.vo.cebbank.test;

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
public class CebCityModelListVO implements Serializable {
    private List<CebCityVO> cityCategoryModelList;
    private List<CebCityVO> cityHotCategoryModelList;
    private List<CebCityVO> cityRenCategoryModelList;

}
