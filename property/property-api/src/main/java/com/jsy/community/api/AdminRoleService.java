package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.entity.property.CarOperationLog;

import java.util.List;

public interface AdminRoleService extends IService<AdminRoleEntity> {
    List<AdminRoleEntity> selectAllRole(Long adminCommunityId);
}
