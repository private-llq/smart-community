package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserLivingExpensesGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.entity.UserLivingExpensesGroupEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.UserLivingExpensesAccountMapper;
import com.jsy.community.mapper.UserLivingExpensesGroupMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Autowired
    private UserLivingExpensesAccountMapper accountMapper;

    /**
     * @param groupEntity :
     * @author: Pipi
     * @description: 添加生活缴费组
     * @return: {@link Integer}
     * @date: 2021/12/3 11:16
     **/
    @Override
    public String addGroup(UserLivingExpensesGroupEntity groupEntity) {
        QueryWrapper<UserLivingExpensesGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", groupEntity.getUid());
        queryWrapper.eq("group_name", groupEntity.getGroupName());
        UserLivingExpensesGroupEntity expensesGroupEntity = groupMapper.selectOne(queryWrapper);
        if (expensesGroupEntity != null) {
            throw new ProprietorException(JSYError.DUPLICATE_KEY);
        }
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
        return groupMapper.updateById(groupEntity);
    }

    /**
     * @param groupEntity :
     * @author: Pipi
     * @description: 删除分组
     * @return: {@link Integer}
     * @date: 2021/12/3 14:34
     **/
    @Override
    public Integer deleteGroup(UserLivingExpensesGroupEntity groupEntity) {
        QueryWrapper<UserLivingExpensesGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", groupEntity.getId());
        queryWrapper.eq("uid", groupEntity.getUid());
        return groupMapper.delete(queryWrapper);
    }

    /**
     * @param uid :
     * @author: Pipi
     * @description: 查询分组列表
     * @return: {@link List < UserLivingExpensesGroupEntity>}
     * @date: 2021/12/3 15:03
     **/
    @Override
    public List<UserLivingExpensesGroupEntity> groupList(String uid) {
        QueryWrapper<UserLivingExpensesGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return groupMapper.selectList(queryWrapper);
    }

    /**
     * @param uid : 用户uid
     * @author: Pipi
     * @description: 查询户号列表
     * @return: {@link List< UserLivingExpensesGroupEntity>}
     * @date: 2021/12/7 18:17
     **/
    @Override
    public List<UserLivingExpensesGroupEntity> accountList(String uid) {
        QueryWrapper<UserLivingExpensesGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        List<UserLivingExpensesGroupEntity> userLivingExpensesGroupEntities = groupMapper.selectList(queryWrapper);
        Set<Long> groupIdSet = userLivingExpensesGroupEntities.stream().map(UserLivingExpensesGroupEntity::getId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(groupIdSet)) {
            QueryWrapper<UserLivingExpensesAccountEntity> accountEntityQueryWrapper = new QueryWrapper<>();
            accountEntityQueryWrapper.eq("uid", uid);
            accountEntityQueryWrapper.in("group_id", groupIdSet);
            List<UserLivingExpensesAccountEntity> accountEntities = accountMapper.selectList(accountEntityQueryWrapper);
            Map<String, List<UserLivingExpensesAccountEntity>> accountEntityMap = accountEntities.stream()
                    .collect(Collectors.groupingBy(UserLivingExpensesAccountEntity::getGroupId,
                            Collectors.mapping(Function.identity(), Collectors.toList())));
            for (UserLivingExpensesGroupEntity userLivingExpensesGroupEntity : userLivingExpensesGroupEntities) {
                userLivingExpensesGroupEntity.setAccountEntityList(accountEntityMap.get(String.valueOf(userLivingExpensesGroupEntity.getId())));
            }
        }
        return userLivingExpensesGroupEntities;
    }
}
