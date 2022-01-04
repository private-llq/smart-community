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
    Long addAccount(UserLivingExpensesAccountEntity accountEntity);

    /**
     * @param accountEntity:
     * @author: Pipi
     * @description: 根据account和uid查询账户信息
     * @return: {@link UserLivingExpensesAccountEntity}
     * @date: 2021/12/7 10:30
     **/
    UserLivingExpensesAccountEntity queryAccount(UserLivingExpensesAccountEntity accountEntity);

    /**
     * @author: Pipi
     * @description: 根据id和uid查询账户信息
     * @param accountEntity:
     * @return: {@link UserLivingExpensesAccountEntity}
     * @date: 2021/12/10 18:49
     **/
    UserLivingExpensesAccountEntity queryAccountById(UserLivingExpensesAccountEntity accountEntity);

    /**
     * @author: Pipi
     * @description: 修改账户信息
     * @param accountEntity: 账户信息实体
     * @return: {@link Boolean}
     * @date: 2021/12/28 17:41
     **/
    Boolean modifyAccount(UserLivingExpensesAccountEntity accountEntity);

    /**
     * @author: Pipi
     * @description: 删除户号
     * @param accountEntity:
     * @return: {@link Boolean}
     * @date: 2022/1/4 18:13
     **/
    Boolean deleteAccount(UserLivingExpensesAccountEntity accountEntity);

}
