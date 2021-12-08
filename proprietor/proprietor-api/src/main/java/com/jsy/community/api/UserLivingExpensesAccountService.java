package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费户号表服务
 * @Date: 2021/12/2 16:40
 * @Version: 1.0
 **/
public interface UserLivingExpensesAccountService extends IService<UserLivingExpensesAccountEntity> {
    /**
     * @author: Pipi
     * @description: 绑定户号
     * @param accountEntity:
     * @return: {@link Integer}
     * @date: 2021/12/3 16:57
     **/
    Integer addAccount(UserLivingExpensesAccountEntity accountEntity);

    /**
     * @param accountEntity:
     * @author: Pipi
     * @description: 根据account和uid查询账户信息
     * @return: {@link UserLivingExpensesAccountEntity}
     * @date: 2021/12/7 10:30
     **/
    UserLivingExpensesAccountEntity queryAccount(UserLivingExpensesAccountEntity accountEntity);
}
