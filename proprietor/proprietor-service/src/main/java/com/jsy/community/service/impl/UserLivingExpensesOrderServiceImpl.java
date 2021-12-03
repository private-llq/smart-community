package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.UserLivingExpensesOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.mapper.UserLivingExpensesOrderMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费订单表服务实现
 * @Date: 2021/12/2 18:01
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesOrderServiceImpl extends ServiceImpl<UserLivingExpensesOrderMapper, UserLivingExpensesOrderEntity> implements UserLivingExpensesOrderService {
}
