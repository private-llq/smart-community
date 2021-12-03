package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserLivingExpensesGroupEntity;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费分组表服务
 * @Date: 2021/12/2 16:28
 * @Version: 1.0
 **/
public interface UserLivingExpensesGroupService extends IService<UserLivingExpensesGroupEntity> {
    /**
     * @author: Pipi
     * @description: 添加生活缴费组
     * @param groupEntity:
     * @return: {@link Integer}
     * @date: 2021/12/3 11:16
     **/
    String addGroup(UserLivingExpensesGroupEntity groupEntity);

    /**
     * @author: Pipi
     * @description: 修改分组
     * @param groupEntity:
     * @return: {@link Integer}
     * @date: 2021/12/3 11:42
     **/
    Integer updateGroup(UserLivingExpensesGroupEntity groupEntity);
}
