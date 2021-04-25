package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.AdminInfoVo;

import java.util.Map;

import java.util.List;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:28
 **/
public interface IPropertyFinanceOrderService extends IService<PropertyFinanceOrderEntity> {
    /**
     * @Description: 每天查询数据库更新账单
     * @author: Hu
     * @since: 2021/4/22 10:05
     * @Param:
     * @return:
     */
    void updateDays();

    /**
    * @Description: 根据收款单号批量查询列表
     * @Param: [receiptNums,query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    List<PropertyFinanceOrderEntity> queryByReceiptNums(Collection<String> receiptNums,PropertyFinanceOrderEntity query);

    /**
    * @Description: 账单号模糊查询收款单号列表
     * @Param: [orderNum]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    List<String> queryReceiptNumsListByOrderNumLike(String orderNum);
    /**
     * @Description: 每天更新账单违约金和总金额
     * @author: Hu
     * @since: 2021/4/22 10:05
     * @Param:
     * @return:
     */
    void updatePenalSum();

    /**
     * @Description: 查询房间所有未缴账单
     * @author: Hu
     * @since: 2021/4/22 10:23
     * @Param:
     * @return:
     */
    Map<String, Object> houseCost(AdminInfoVo userInfo, Long houseId);

    /**
    * @Description: 批量查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    PageInfo<PropertyFinanceOrderEntity> queryPage(BaseQO<PropertyFinanceOrderEntity> baseQO);

    /**
     *@Author: Pipi
     *@Description: 分页查询结算单的账单列表
     *@Param: baseQO:
     *@Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/24 11:44
     **/
    Page<PropertyFinanceOrderEntity> queryPageByStatemenNum(BaseQO<StatementNumQO> baseQO);
}
