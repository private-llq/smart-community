package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业成员查询接口
 * @author: Hu
 * @create: 2021-03-05 11:18
 **/
@Api(tags = "物业端房屋租赁常量接口")
@RestController
@RequestMapping("/members")
@ApiJSYController
public class PropertyRelationController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IPropertyRelationService propertyRelationService;

    @ApiOperation("查询业主下所有成员")
    @PostMapping("/list")
    public CommonResult list(BaseQO<PropertyRelationQO> baseQO){
        List list=propertyRelationService.list(baseQO);
        return CommonResult.ok();
    }
}
