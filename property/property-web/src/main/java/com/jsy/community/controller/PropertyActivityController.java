package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyActivityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 社区活动
 * @author: Hu
 * @create: 2021-09-23 10:00
 **/
@RestController
@RequestMapping("/activity")
@ApiJSYController
@Login
public class PropertyActivityController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyActivityService propertyActivityService;

    @ApiOperation("查询列表")
    @PostMapping("/list")
    public CommonResult list(@RequestBody BaseQO<ActivityEntity> baseQO){
        Map<String, Object> map = propertyActivityService.list(baseQO, UserUtils.getAdminCommunityId());
        return CommonResult.ok(map);
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public CommonResult save(@RequestBody ActivityEntity activityEntity){
        activityEntity.setCommunityId(UserUtils.getAdminCommunityId());
        propertyActivityService.saveBy(activityEntity);
        return CommonResult.ok();
    }

    @ApiOperation("查报名详情详情")
    @GetMapping("/getOne")
    public CommonResult getOne(@RequestParam Long id){
        ActivityEntity entity = propertyActivityService.getOne(id);
        return CommonResult.ok(entity);
    }

    @ApiOperation("编辑")
    @PutMapping("/update")
    public CommonResult update(@RequestBody ActivityEntity activityEntity){
        propertyActivityService.update(activityEntity);
        return CommonResult.ok();
    }

    @ApiOperation("报名详情")
    @PostMapping("/detail/page")
    public CommonResult detailPage(@RequestBody BaseQO<ActivityUserEntity> baseQO){
        Map<String,Object> map = propertyActivityService.detailPage(baseQO,UserUtils.getAdminCommunityId());
        return CommonResult.ok(map);
    }

    @ApiOperation("上传图片")
    @PostMapping("/file")
    public CommonResult file(@RequestParam MultipartFile[] file){
        String[] votes = MinioUtils.uploadForBatch(file, "activity");
        return CommonResult.ok(votes);
    }
}
