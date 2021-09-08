package com.jsy.community.api;

/**
 * @program: com.jsy.community
 * @description:  财务账单每天更新实现类
 * @author: Hu
 * @create: 2021-04-24 14:19
 **/
public interface IFinanceBillService {
    /**
     * @Description: 每天查询数据库更新账单
     * @author: Hu
     * @since: 2021/4/22 10:05
     * @Param:
     * @return:
     */
    void updateMonth();

    /**
     * @Description: 每天更新账单违约金和总金额
     * @author: Hu
     * @since: 2021/4/22 10:05
     * @Param:
     * @return:
     */
    void updatePenalSum();
}
