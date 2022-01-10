package com.jsy.community.controller;

import com.jsy.community.constant.BusinessConst;
import com.jsy.community.qo.MenuQO;
import com.jsy.community.qo.PermissionQO;
import com.jsy.community.qo.PermitQO;
import com.jsy.community.qo.RoleQO;
import com.jsy.community.vo.CommonResult;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.MenuPermission;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.rpc.*;
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
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseMenuPermissionRpcService baseMenuPermissionRpcService;
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService userInfoRpcService;
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseAuthRpcService baseAuthRpcService;

    /***
     * @author: Pipi
     * @description: 手动添加权限路由
     *  name: 权限名称
     *  permit: 权限标识
     *  scope: 权限 作用域  e_home(E到家用户),shop_admin（商家后台），property_admin（物业后台），ultimate_admin（大后台）
     *  description: 描述
     *  createUid: 用户id
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
        PermitRole permitRole = baseRoleRpcService.createRole(roleQO.getName(), roleQO.getRemark(), BusinessConst.ULTIMATE_ADMIN, roleQO.getCreateUid());
        return CommonResult.ok(permitRole);
    }
    
    /**
     * @Description: 删除菜单
     * @author: DKS
     * @since: 2021/12/22 11:58
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @DeleteMapping("/deleteMenu")
    @LoginIgnore
    public CommonResult deleteMenu(List<Long> menuIds) {
        baseMenuRpcService.deleteMenu(menuIds);
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
        PermitDto permit = permitRpcService.getPermit(String.valueOf(permissionQO.getUid()), permissionQO.getAccount(), BusinessConst.PROPERTY_ADMIN);
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
        List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(uid, BusinessConst.ULTIMATE_ADMIN);
        return CommonResult.ok(permitRoles);
    }
    
    /**
     * @Description: 新增菜单关联权限
     * @author: DKS
     * @since: 2021/12/20 11:11
     * @Param: [entityList]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/saveBatch")
    @LoginIgnore
    public CommonResult saveBatch(@RequestBody MenuPermission menuPermission) {
        return baseMenuPermissionRpcService.save(menuPermission) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
    }
    
    /**
     * @Description: 查询菜单关联权限列表
     * @author: DKS
     * @since: 2021/12/20 14:44
     * @Param: [idList]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/listByIds")
    @LoginIgnore
    public CommonResult listByIds(@RequestParam List<Long> idList) {
        List<MenuPermission> menuPermissions = baseMenuPermissionRpcService.listByIds(idList);
        return CommonResult.ok(menuPermissions);
    }
    
    /**
     * @Description: 通过uid列表查询电话号码和真实姓名的接口
     * @author: DKS
     * @since: 2021/12/23 14:17
     * @Param: [idList]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/getRealUserDetailsByUid")
    @LoginIgnore
    public CommonResult getRealUserDetailsByUid(@RequestParam("idList") List<Long> idList) {
        List<RealUserDetail> realUserDetailsByUid = userInfoRpcService.getRealUserDetailsByUid(idList);
        return CommonResult.ok(realUserDetailsByUid);
    }
    
    /**
     * @Description: 增加登录类型范围
     * @author: DKS
     * @since: 2021/12/20 11:11
     * @Param: [entityList]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/addLoginTypeScope")
    @LoginIgnore
    public CommonResult addLoginTypeScope(@RequestParam("id") Long id, String type) {
        baseAuthRpcService.addLoginTypeScope(id, type);
        return CommonResult.ok();
    }
    
    /**
     * @Description: 根据号码查询uid
     * @author: DKS
     * @since: 2021/12/30 15:21
     * @Param: [phone]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/queryRealUserDetailByUid")
    @LoginIgnore
    public CommonResult queryRealUserDetailByUid(String phone) {
        return CommonResult.ok(userInfoRpcService.queryRealUserDetailByUid(phone, ""));
    }
    
    /**
     * @Description: 手机号、昵称 模糊查询，并可分页
     * @author: DKS
     * @since: 2022/1/10 13:55
     * @Param: [name]
     * @return: com.jsy.community.vo.CommonResult
     */
    @GetMapping("/queryUser")
    @LoginIgnore
    public CommonResult queryUser(String name) {
        return CommonResult.ok(userInfoRpcService.queryUser("", name, null, 0, 99999999));
    }
}
