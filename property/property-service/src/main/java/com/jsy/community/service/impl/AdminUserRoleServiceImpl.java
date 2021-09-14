package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.AdminUserRoleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import com.jsy.community.mapper.AdminUserRoleMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(version = Const.version, group = Const.group_property)
public class AdminUserRoleServiceImpl extends ServiceImpl<AdminUserRoleMapper, AdminUserRoleEntity> implements AdminUserRoleService {
    @Resource
    private AdminUserRoleMapper adminUserRoleMapper;


    @Override
    public Long selectRoleIdByUserId(String userId) {
        QueryWrapper<AdminUserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",userId);
        AdminUserRoleEntity adminUserRoleEntity = adminUserRoleMapper.selectOne(queryWrapper);
        Long roleId = adminUserRoleEntity.getRoleId();
        return roleId;
    }
}
