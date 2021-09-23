package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyActivityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 社区活动
 * @author: Hu
 * @create: 2021-09-23 10:00
 **/
@RestController
@RequestMapping("/property/activity")
@ApiJSYController
@Login
public class PropertyActivityController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyActivityService propertyActivityService;

    @ApiOperation("查询列表")
    @PostMapping("/list")
    public CommonResult list(@RequestBody BaseQO<ActivityEntity> baseQO){
        List<ActivityEntity> list = propertyActivityService.list(baseQO, UserUtils.getAdminCommunityId());
        return CommonResult.ok(list);
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public CommonResult save(@RequestBody ActivityEntity activityEntity){
        propertyActivityService.saveBy(activityEntity);
        return CommonResult.ok();
    }

    @ApiOperation("查报名详情详情")
    @GetMapping("/getOne")
    public CommonResult getOne(@RequestParam Long id){
        propertyActivityService.getOne(id);
        return CommonResult.ok();
    }

    @ApiOperation("编辑")
    @GetMapping("/update")
    public CommonResult update(@RequestBody ActivityEntity activityEntity){
        propertyActivityService.update(activityEntity);
        return CommonResult.ok();
    }
}
