package com.jsy.community.api;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminUserRoleEntity;


public interface AdminUserRoleService  extends IService<AdminUserRoleEntity> {
    Long selectRoleIdByUserId(String userId);
}
