package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/list")
    @Login
    public CommonResult feeRule(@RequestParam("communityId") String communityId){
        List<PropertyFeeRuleEntity> list=propertyFeeRuleService.findList(communityId);
        return CommonResult.ok(list);
    }


}
