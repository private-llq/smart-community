package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.RelationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "添加家属信息")
@RestController
@RequestMapping("/relation")
//@Login
@ApiJSYController
public class RelationController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IRelationService relationService;

    @ApiOperation("添加家属信息")
    @PutMapping("/add")
    public CommonResult addRelation(@RequestBody RelationVO relationVO){
        System.out.println(relationService);
        System.out.println(relationVO);
//        relationService.addRelation(relationVO);
        return relationService.addRelation(relationVO)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }
}