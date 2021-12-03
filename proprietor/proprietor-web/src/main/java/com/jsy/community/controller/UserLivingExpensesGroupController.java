package com.jsy.community.controller;

import com.jsy.community.api.UserLivingExpensesGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesGroupEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/12/2 18:04
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/livingExpenses")
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
        return  id == null ? CommonResult.error("添加失败") : CommonResult.ok(id, "添加成功");
    }
}
