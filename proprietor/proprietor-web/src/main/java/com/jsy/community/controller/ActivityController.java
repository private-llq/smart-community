package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IActivityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.ActivityUserEntity;
import com.jsy.community.entity.proprietor.ActivityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: com.jsy.community
 * @description: app活动接口
 * @author: Hu
 * @create: 2021-08-13 14:48
 **/
@RestController
@RequestMapping("/activity")
// @ApiJSYController
public class ActivityController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IActivityService activityService;


    @ApiOperation("该小区所有活动")
    @PostMapping("/list")
    @Permit("community:proprietor:activity:list")
    public CommonResult list(@RequestBody BaseQO<ActivityEntity> baseQO) {
        return CommonResult.ok(activityService.list(baseQO));
    }

    @ApiOperation("活动报名")
    @PostMapping("/apply")
    @Permit("community:proprietor:activity:apply")
    public CommonResult apply(@RequestBody ActivityUserEntity activityUserEntity) {
        ValidatorUtils.validateEntity(activityUserEntity,ActivityUserEntity.ActivityUserVerification.class);
        activityUserEntity.setUid(UserUtils.getUserId());
        activityService.apply(activityUserEntity);
        return CommonResult.ok();
    }

    @ApiOperation("取消报名报名")
    @DeleteMapping("/cancel")
    @Permit("community:proprietor:activity:cancel")
    public CommonResult cancel(@RequestParam("id") Long id){
        activityService.cancel(id,UserUtils.getUserId());
        return CommonResult.ok();
    }

    @ApiOperation("查询一条活动详情")
    @GetMapping("/selectOne")
    @Permit("community:proprietor:activity:selectOne")
    public CommonResult selectOne(@RequestParam("id") Long id){
        return CommonResult.ok(activityService.selectOne(id,UserUtils.getUserId()));
    }

    @ApiOperation("上传图片")
    @PostMapping("/file")
    @Permit("community:proprietor:activity:file")
    public CommonResult file(@RequestParam MultipartFile file){
        String upload = MinioUtils.upload(file, "activity");
        return CommonResult.ok(upload);
    }
}
