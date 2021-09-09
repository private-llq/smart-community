package com.jsy.community.api;

/**
 * @program: com.jsy.community
 * @description:  财务账单每天更新实现类
 * @author: Hu
 * @create: 2021-04-24 14:19
 **/
public interface IFinanceBillService {
    /**
     * @Description: 更新所有按月生成的周期账单
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

    /**
     * @Description: 更新所有按年生成的周期账单
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    void updateAnnual();
    /**
     * @Description: 更新所有临时的账单   临时账单只更新一次  更新完成过后就把收费项目的状态改为未启动或者删除临时项目
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    void updateTemporary();


}
