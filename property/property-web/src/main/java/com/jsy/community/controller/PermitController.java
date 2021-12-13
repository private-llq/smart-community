package com.jsy.community.controller;

import com.jsy.community.qo.MenuQO;
import com.jsy.community.qo.PermissionQO;
import com.jsy.community.qo.PermitQO;
import com.jsy.community.qo.RoleQO;
import com.jsy.community.vo.CommonResult;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.rpc.IBaseMenuRpcService;
import com.zhsj.base.api.rpc.IBasePermissionRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.basecommon.PermitDto;
import com.zhsj.basecommon.constant.BaseConstant;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.interfaces.IPermitRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 仅用于开发阶段手动添加权限路由
 * @Date: 2021/11/22 14:47
 * @Version: 1.0
 **/
@RestController
// @ApiJSYController
@RequestMapping(value = "/permit")
public class PermitController {

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBasePermissionRpcService permissionRpcService;
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseMenuRpcService baseMenuRpcService;
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseRoleRpcService baseRoleRpcService;
    @DubboReference(version = BaseConstant.Rpc.VERSION, group = BaseConstant.Rpc.Group.GROUP_BASE_USER, check = false)
    private IPermitRpcService permitRpcService;

    /***
     * @author: Pipi
     * @description: 手动添加权限路由
     * @param name: 权限名称
         * @param permit: 权限标识
         * @param scope: 权限 作用域  e_home(E到家用户),shop_admin（商家后台），property_admin（物业后台），ultimate_admin（大后台）
         * @param description: 描述
         * @param createUid: 用户id
     * @return: {@link CommonResult}
     * @date: 2021/11/22 15:05
     **/
    @PostMapping("/addPermit")
    @LoginIgnore
    public CommonResult addPermit(@RequestBody PermitQO permitQO) {
        permissionRpcService.createPermission(permitQO.getName(), permitQO.getPermit(), permitQO.getScope(), permitQO.getDescription(), permitQO.getCreateUid());
        return CommonResult.ok();
    }
    
    /**
     * @Description: 新增角色
     * @author: DKS
     * @since: 2021/12/6 14:08
     * @Param: [roleQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/createRole")
    @LoginIgnore
    public CommonResult createRole(@RequestBody RoleQO roleQO) {
        baseRoleRpcService.createRole(roleQO.getName(), roleQO.getRemark(), "ultimate_admin", roleQO.getCreateUid());
        return CommonResult.ok();
    }
    
    /**
     * @Description: 给用户添加角色
     * @author: DKS
     * @since: 2021/12/6 14:08
     * @Param: [roleQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/userJoinRole")
    @LoginIgnore
    public CommonResult userJoinRole(@RequestBody RoleQO roleQO) {
        baseRoleRpcService.userJoinRole(roleQO.getRoleIds(), roleQO.getUid(), roleQO.getUpdateUid());
        return CommonResult.ok();
    }
    
    /**
     * @Description: 菜单分配给角色
     * @author: DKS
     * @since: 2021/12/6 14:25
     * @Param: [menuQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/menuJoinRole")
    @LoginIgnore
    public CommonResult menuJoinRole(@RequestBody MenuQO menuQO) {
        baseMenuRpcService.menuJoinRole(menuQO.getMenuIds(), menuQO.getRoleId(), menuQO.getUid());
        return CommonResult.ok();
    }
    
    /**
     * @Description: 将权限添加到角色
     * @author: DKS
     * @since: 2021/12/6 14:38
     * @Param: [permissionQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/permitJoinRole")
    @LoginIgnore
    public CommonResult permitJoinRole(@RequestBody PermissionQO permissionQO) {
        permissionRpcService.permitJoinRole(permissionQO.getPermitIds(), permissionQO.getRoleId(), permissionQO.getUid());
        return CommonResult.ok();
    }
    
    /**
     * @Description: 查询权限
     * @author: DKS
     * @since: 2021/12/6 14:53
     * @Param: [permissionQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/getPermit")
    @LoginIgnore
    public CommonResult getPermit(PermissionQO permissionQO) {
        PermitDto permit = permitRpcService.getPermit(String.valueOf(permissionQO.getUid()), permissionQO.getAccount(), "ultimate_admin");
        return CommonResult.ok(permit);
    }
    
    /**
     * @Description: 通过用户uid查询 该用户角色下的所有角色和权限
     * @author: DKS
     * @since: 2021/12/6 15:28
     * @Param: [permissionQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/listAllRolePermission")
    @LoginIgnore
    public CommonResult listAllRolePermission(Long uid) {
        List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(uid, "ultimate_admin");
        return CommonResult.ok(permitRoles);
    }
}
