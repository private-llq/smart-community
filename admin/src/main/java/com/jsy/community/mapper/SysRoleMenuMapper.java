package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.sys.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: DKS
 * @Description: 角色菜单表Mapper
 * @Date: 2021/10/13 9:10
 * @Version: 1.0
 **/
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenuEntity> {
    /**
     * @author: Pipi
     * @description: 查询角色的菜单ID列表
     * @param roleId: 角色ID
     * @return: java.util.List<java.lang.Long>
     * @date: 2021/8/6 14:09
     **/
    List<Long> queryRoleMuneIdsByRoleIdAndLoginType(@Param("roleId") Long roleId);

    /**
     * @author: Pipi
     * @description: 查询角色的菜单ID列表
     * @param roleId: 角色ID
     * @return: java.util.List<java.lang.Long>
     * @date: 2021/8/9 10:57
     **/
    List<Long> queryRoleMuneIdsByRoleId(Long roleId);
}
