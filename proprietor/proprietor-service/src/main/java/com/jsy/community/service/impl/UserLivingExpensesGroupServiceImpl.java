package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.UserLivingExpensesGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesGroupEntity;
import com.jsy.community.mapper.UserLivingExpensesGroupMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费分组表服务实现
 * @Date: 2021/12/2 16:30
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesGroupServiceImpl extends ServiceImpl<UserLivingExpensesGroupMapper, UserLivingExpensesGroupEntity> implements UserLivingExpensesGroupService {
    @Autowired
    private UserLivingExpensesGroupMapper groupMapper;

    /**
     * @param groupEntity :
     * @author: Pipi
     * @description: 添加生活缴费组
     * @return: {@link Integer}
     * @date: 2021/12/3 11:16
     **/
    @Override
    public String addGroup(UserLivingExpensesGroupEntity groupEntity) {
        groupEntity.setId(SnowFlake.nextId());
        int insert = groupMapper.insert(groupEntity);
        if (insert == 1) {
            return String.valueOf(groupEntity.getId());
        } else {
            return null;
        }
    }

    /**
     * @param groupEntity :
     * @author: Pipi
     * @description: 修改分组
     * @return: {@link Integer}
     * @date: 2021/12/3 11:42
     **/
    @Override
    public Integer updateGroup(UserLivingExpensesGroupEntity groupEntity) {
        return null;
    }
}
