package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;

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
}
