package com.jsy.community.api;

import com.jsy.community.qo.cebbank.CebBaseQO;
import com.jsy.community.qo.cebbank.CebLoginQO;
import com.jsy.community.qo.cebbank.CebQueryCityContributionCategoryQO;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费服务
 * @Date: 2021/11/15 17:20
 * @Version: 1.0
 **/
public interface CebBankService {

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @param categoryQO:
     * @return: java.lang.String
     * @date: 2021/11/17 17:50
     **/
    String queryCityContributionCategory(CebQueryCityContributionCategoryQO categoryQO);
}
