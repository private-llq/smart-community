package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyCarService;
import com.jsy.community.api.IPropertyComplaintsService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CommunityFunQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:46
 **/
@Api(tags = "车辆信息")
@RestController
@RequestMapping("/car")
@ApiJSYController
public class PropertyCarController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyCarService propertyCarService;

    @ApiOperation("分页查询所有车辆信息")
    @PostMapping("/list")
//    @Login
    public CommonResult list(@RequestBody BaseQO<CommunityFunQO> baseQO) {
        PageInfo pageInfo = propertyCarService.findList(baseQO);
        return CommonResult.ok(pageInfo);
    }
}
