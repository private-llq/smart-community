package com.jsy.community.api;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminUserRoleEntity;


public interface AdminUserRoleService  extends IService<AdminUserRoleEntity> {
    /**
     * @author: DKS
     * @description:
     * @param userId: 用户id
         * @param commpanyId: 物业公司id
     * @return: {@link Long}
     * @date: 2021/12/23 17:49
     **/
    Long selectRoleIdByUserId(String userId, Long commpanyId);
}
