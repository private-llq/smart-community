package com.jsy.community.service;

import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.zhsj.base.api.vo.PageVO;

import java.util.List;

public interface AdminRoleService {
    /**
     * @Description: 添加角色
     * @Param: [adminRoleQO]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/12/14
     **/
    void addRole(AdminRoleQO adminRoleQO);

    /**
     * @Description: 删除角色
     * @Param: [id]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/12/14
     **/
    void delRole(List<Long> roleIds);

    /**
     * @Description: 修改角色
     * @Param: [sysRoleQO]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/12/14
     **/
    void updateRole(AdminRoleQO adminRoleQO);

    /**
     * @Description: 角色列表 分页查询
     * @Param: []
     * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
     * @Author: chq459799974
     * @Date: 2020/12/14
     **/
    PageVO<AdminRoleEntity> queryPage(BaseQO<AdminRoleEntity> baseQO);

    /**
     * @author: Pipi
     * @description: 查询角色详情
     * @param roleId: 角色ID
     * @return: com.jsy.community.entity.admin.AdminRoleEntity
     * @date: 2021/8/9 10:33
     **/
    AdminRoleEntity queryRoleDetail(Long roleId);

    //==================================================== 用户-角色 ===============================================================
//    /**
//     * @author: Pipi
//     * @description: 根据用户uid查询用户的角色id
//     * @param uid: 用户uid
//     * @return: java.lang.Long
//     * @date: 2021/8/6 10:50
//     **/
//    AdminUserRoleEntity queryRoleIdByUid(String uid);
}
