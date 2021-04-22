package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:  小区物业收费规则
 * @author: Hu
 * @create: 2021-04-20 16:36
 **/
@Api(tags = "小区物业收费规则")
@RestController
@RequestMapping("/feeRule")
@ApiJSYController
public class PropertyFeeRuleController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFeeRuleService propertyFeeRuleService;

    @ApiOperation("查询当前小区物业收费规则")
    @GetMapping("/list")
    @Login
    public CommonResult feeRule(){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        List<PropertyFeeRuleEntity> list=propertyFeeRuleService.findList(userInfo.getCommunityId());
        return CommonResult.ok(list);
    }

    @ApiOperation("查询当前小区物业收费规则")
    @GetMapping("/selectOne")
    @Login
    public CommonResult selectOne(@RequestParam("type")Integer type){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        PropertyFeeRuleEntity propertyFeeRuleEntity=propertyFeeRuleService.selectByOne(userInfo.getCommunityId(),type);
        return CommonResult.ok(propertyFeeRuleEntity);
    }

    @ApiOperation("启用或者停用")
    @GetMapping("/startOrOut")
    @Login
    public CommonResult startOrOut(@RequestParam("status")Integer status,Long id){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFeeRuleService.startOrOut(userInfo,status,id);
        return CommonResult.ok();
    }

    @ApiOperation("修改")
    @PutMapping("/updateById")
    @Login
    public CommonResult updateById(@RequestBody PropertyFeeRuleEntity propertyFeeRuleEntity){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        propertyFeeRuleService.updateOneRule(userInfo,propertyFeeRuleEntity);
        return CommonResult.ok();
    }

}
