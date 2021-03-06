package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Pipi
 * @Description: 用户角色关联表Mapper
 * @Date: 2021/8/6 11:04
 * @Version: 1.0
 **/
@Mapper
public interface AdminUserRoleMapper extends BaseMapper<AdminUserRoleEntity> {
}
