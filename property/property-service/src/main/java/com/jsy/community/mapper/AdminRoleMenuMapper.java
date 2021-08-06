package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminRoleMenuEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 角色菜单表Mapper
 * @Date: 2021/8/6 14:04
 * @Version: 1.0
 **/
public interface AdminRoleMenuMapper extends BaseMapper<AdminRoleMenuEntity> {
    /**
     * @author: Pipi
     * @description: 查询角色的菜单ID列表
     * @param roleId: 角色ID
     * @param loginType: 登录类型
     * @return: java.util.List<java.lang.Long>
     * @date: 2021/8/6 14:09
     **/
    List<Long> queryRoleMuneIds(@Param("roleId") Long roleId, @Param("loginType") Integer loginType);
}
