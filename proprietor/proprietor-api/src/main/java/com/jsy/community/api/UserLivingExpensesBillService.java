package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserLivingExpensesBillEntity;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费账单表服务
 * @Date: 2021/12/2 17:40
 * @Version: 1.0
 **/
public interface UserLivingExpensesBillService extends IService<UserLivingExpensesBillEntity> {

    /**
     * @author: Pipi
     * @description: 查询缴费账单
     * @param billEntity: 账单查询条件
     * @return: {@link UserLivingExpensesBillEntity}
     * @date: 2021/12/30 9:15
     **/
    UserLivingExpensesBillEntity queryBill(UserLivingExpensesBillEntity billEntity);
}
