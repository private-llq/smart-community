package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.AdminRoleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.mapper.AdminRoleMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService(version = Const.version, group = Const.group_property)
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRoleEntity> implements AdminRoleService {


    @Resource
    private  AdminRoleMapper adminRoleMapper;


    @Override
    public List<AdminRoleEntity> selectAllRole(Long adminCommunityId) {

        QueryWrapper<AdminRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",adminCommunityId);
        List<AdminRoleEntity> list = adminRoleMapper.selectList(queryWrapper);
        return list;
    }
}
