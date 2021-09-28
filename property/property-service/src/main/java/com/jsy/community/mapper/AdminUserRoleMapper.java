package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Author: Pipi
 * @Description: 用户角色关联表Mapper
 * @Date: 2021/8/6 11:04
 * @Version: 1.0
 **/
public interface AdminUserRoleMapper extends BaseMapper<AdminUserRoleEntity> {

    /**
     * @author: Pipi
     * @description: 查询用户角色列表
     * @param uidSet: 用户uid列表
     * @param companyId: 公司ID
     * @return: java.util.List<com.jsy.community.entity.admin.AdminUserRoleEntity>
     * @date: 2021/9/27 17:04
     **/
    List<AdminUserRoleEntity> queryByUids(@Param("uidSet") Set<String> uidSet, @Param("companyId") Long companyId);
}
