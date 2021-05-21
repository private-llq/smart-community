package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Desensitization;
import com.jsy.community.api.ITenementService;
import com.jsy.community.aspectj.DesensitizationType;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业租户查询
 * @author: Hu
 * @create: 2021-03-10 14:40
 **/
@Api(tags = "物业租户查询")
@RestController
@RequestMapping("/tenement")
@ApiJSYController
public class TenementController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ITenementService tenementService;

    @ApiOperation("查询租户列表")
    @PostMapping("/list")
    @Desensitization(type = {DesensitizationType.PHONE,DesensitizationType.ID_CARD,DesensitizationType.PHONE,DesensitizationType.ID_CARD}, field = {"mobile","idCard","ownerMobile","ownerIdCard"})
    public CommonResult list(@RequestBody BaseQO<PropertyRelationQO> baseQO){
        System.out.println(baseQO);
        Map map=tenementService.list(baseQO);
        return CommonResult.ok(map);
    }
}
