package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.UserLivingExpensesGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesGroupEntity;
import com.jsy.community.mapper.UserLivingExpensesGroupMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费分组表服务实现
 * @Date: 2021/12/2 16:30
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesGroupServiceImpl extends ServiceImpl<UserLivingExpensesGroupMapper, UserLivingExpensesGroupEntity> implements UserLivingExpensesGroupService {
}
