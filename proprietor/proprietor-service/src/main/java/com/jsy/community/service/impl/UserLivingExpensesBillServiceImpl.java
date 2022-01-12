package com.jsy.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.UserLivingExpensesBillService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.mapper.UserLivingExpensesAccountMapper;
import com.jsy.community.mapper.UserLivingExpensesBillMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserLivingExpensesAccountMapper accountMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

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
        queryWrapper.eq("id", billEntity.getId());
        UserLivingExpensesBillEntity userLivingExpensesBillEntity = billMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotNull(userLivingExpensesBillEntity)) {
            QueryWrapper<UserLivingExpensesAccountEntity> accountEntityQueryWrapper = new QueryWrapper<>();
            accountEntityQueryWrapper.eq("uid", billEntity.getUid());
            accountEntityQueryWrapper.eq("account", userLivingExpensesBillEntity.getBillKey());
            UserLivingExpensesAccountEntity userLivingExpensesAccountEntity = accountMapper.selectOne(accountEntityQueryWrapper);
            if (userLivingExpensesAccountEntity != null) {
                userLivingExpensesBillEntity.setCompanyName(userLivingExpensesAccountEntity.getCompany());
                userLivingExpensesBillEntity.setAddress(userLivingExpensesAccountEntity.getAddress());
            }
            String costIcon = redisTemplate.opsForValue().get("costIcon");
            Map<Integer, String> map = JSON.parseObject(costIcon, Map.class);
            userLivingExpensesBillEntity.setTypePicUrl(map.get(Integer.valueOf(userLivingExpensesBillEntity.getTypeId())));
        }
        return userLivingExpensesBillEntity;
    }

    /**
     * @param billEntity :
     * @author: Pipi
     * @description: 查询账单列表
     * @return: {@link List < Map< String,  BigDecimal >>}
     * @date: 2022/1/8 17:59
     **/
    @Override
    public List<UserLivingExpensesBillEntity> queryBillList(UserLivingExpensesBillEntity billEntity) {
        QueryWrapper<UserLivingExpensesBillEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,begin_date,bill_amount,create_time");
        queryWrapper.eq("uid", billEntity.getUid());
        queryWrapper.eq("bill_key", billEntity.getBillKey());
        queryWrapper.eq("bill_status", BusinessEnum.PaymentStatusEnum.UNPAID.getCode());
        return billMapper.selectList(queryWrapper);
    }
}
