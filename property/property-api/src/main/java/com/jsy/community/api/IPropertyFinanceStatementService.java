package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 物业财务-结算单服务
 * @Date: 2021/4/22 16:55
 * @Version: 1.0
 **/
public interface IPropertyFinanceStatementService extends IService<PropertyFinanceStatementEntity> {
    /**
     *@Author: Pipi
     *@Description: 定时产生结算单
     *@Param: :
     *@Return: void
     *@Date: 2021/4/22 16:59
     **/
    void timingStatement();
    
    /**
    * @Description: 结算单号批量查 单号-结算单数据 映射
     * @Param: [nums]
     * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PropertyFinanceStatementEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    Map<String,PropertyFinanceStatementEntity> queryByStatementNumBatch(Collection<String> nums);
}
