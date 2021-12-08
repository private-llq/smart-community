package com.jsy.community.controller;

import com.jsy.community.api.UserLivingExpensesGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesGroupEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/12/2 18:04
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/livingExpensesGroup")
public class UserLivingExpensesGroupController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private UserLivingExpensesGroupService groupService;

    /***
     * @author: Pipi
     * @description: 添加生活缴费组
     * @param groupEntity:
     * @return: {@link CommonResult}
     * @date: 2021/12/2 18:09
     **/
    @PostMapping("/v2/addGroup")
    public CommonResult addGroup(@RequestBody UserLivingExpensesGroupEntity groupEntity) {
        ValidatorUtils.validateEntity(groupEntity);
        groupEntity.setUid(UserUtils.getUserId());
        String id = groupService.addGroup(groupEntity);
        return id == null ? CommonResult.error("添加失败") : CommonResult.ok(id, "添加成功");
    }

    /**
     * @param groupEntity:
     * @author: Pipi
     * @description: 修改分组
     * @return: {@link CommonResult}
     * @date: 2021/12/3 11:49
     **/
    @PostMapping("/v2/updateGroup")
    public CommonResult<?> updateGroup(@RequestBody UserLivingExpensesGroupEntity groupEntity) {
        ValidatorUtils.validateEntity(groupEntity);
        if (groupEntity.getId() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "分组ID不能为空");
        }
        Integer integer = groupService.updateGroup(groupEntity);
        return integer == 1 ? CommonResult.ok("修改成功") : CommonResult.error("修改失败");
    }

    /**
     * @author: Pipi
     * @description: 删除分组
     * @param groupEntity:
     * @return: {@link CommonResult}
     * @date: 2021/12/3 14:37
     **/
    @PostMapping("/v2/deleteGroup")
    public CommonResult<?> deleteGroup(@RequestBody UserLivingExpensesGroupEntity groupEntity) {
        if (groupEntity.getId() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "分组ID不能为空");
        }
        groupEntity.setUid(UserUtils.getUserId());
        return groupService.deleteGroup(groupEntity) == 1 ? CommonResult.ok("删除成功") : CommonResult.error("删除失败");
    }

    /**
     * @author: Pipi
     * @description: 查询分组列表
     * @param :
     * @return: {@link CommonResult}
     * @date: 2021/12/3 15:10
     **/
    @GetMapping("/v2/groupList")
    public CommonResult<?> groupList() {
        return CommonResult.ok(groupService.groupList(UserUtils.getUserId()));
    }

    /**
     * @author: Pipi
     * @description: 查询户号列表
     * @param :
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/7 18:14
     **/
    @PostMapping("/v2/accountList")
    public CommonResult<?> accountList() {
        return CommonResult.ok(groupService.accountList(UserUtils.getUserId()));
    }
}
