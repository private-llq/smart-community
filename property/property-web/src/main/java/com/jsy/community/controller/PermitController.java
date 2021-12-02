package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.qo.PermitQO;
import com.jsy.community.vo.CommonResult;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBasePermissionRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.baseweb.annotation.LoginIgnore;
import lombok.Data;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

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

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER)
    private IBasePermissionRpcService permissionRpcService;

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
}
