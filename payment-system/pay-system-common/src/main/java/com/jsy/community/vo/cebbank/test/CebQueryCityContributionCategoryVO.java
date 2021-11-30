package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询城市下缴费类别VO
 * @Date: 2021/11/12 17:20
 * @Version: 1.0
 **/
@Data
public class CebQueryCityContributionCategoryVO implements Serializable {
    // 缴费类别的Model 做外层
    private CebQueryCityContributionCategoryVO paymentCitiesForClientModel;

    //缴费类别的列表
    private List<CebCategoryVO> cebPaymentCategoriesList;
}
