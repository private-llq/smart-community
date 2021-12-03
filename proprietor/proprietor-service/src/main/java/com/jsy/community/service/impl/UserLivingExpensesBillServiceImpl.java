package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.UserLivingExpensesBillService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.mapper.UserLivingExpensesBillMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费账单表服务实现
 * @Date: 2021/12/2 17:41
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesBillServiceImpl extends ServiceImpl<UserLivingExpensesBillMapper, UserLivingExpensesBillEntity> implements UserLivingExpensesBillService {
}
