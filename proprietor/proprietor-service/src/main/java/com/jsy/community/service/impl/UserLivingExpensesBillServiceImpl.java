package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.UserLivingExpensesBillService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.mapper.UserLivingExpensesBillMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费账单表服务实现
 * @Date: 2021/12/2 17:41
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesBillServiceImpl extends ServiceImpl<UserLivingExpensesBillMapper, UserLivingExpensesBillEntity> implements UserLivingExpensesBillService {

    @Autowired
    private UserLivingExpensesBillMapper billMapper;

    /**
     * @param billEntity : 账单查询条件
     * @author: Pipi
     * @description: 查询缴费账单
     * @return: {@link UserLivingExpensesBillEntity}
     * @date: 2021/12/30 9:15
     **/
    @Override
    public UserLivingExpensesBillEntity queryBill(UserLivingExpensesBillEntity billEntity) {
        QueryWrapper<UserLivingExpensesBillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", billEntity.getUid());
        queryWrapper.eq("bill_key", billEntity.getBillKey());
        queryWrapper.eq("bill_status", BusinessEnum.PaymentStatusEnum.UNPAID.getCode());
        // 只展示一条未缴纳
        queryWrapper.last("limit 1");
        return billMapper.selectOne(queryWrapper);
    }
}
