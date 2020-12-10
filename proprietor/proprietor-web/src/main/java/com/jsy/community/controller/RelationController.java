package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.RelationQo;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: 添加家属控制器
 * @author: Hu
 * @since: 2020/12/10 16:35
 * @Param:
 * @return:
 */
@Api(tags = "添加家属信息")
@RestController
@RequestMapping("/relation")
@ApiJSYController
public class RelationController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IRelationService relationService;



    @ApiOperation("添加家属信息")
    @PutMapping("/add")
    @Login
    public CommonResult addRelation(@RequestBody RelationQo relationQo){

        String userId = UserUtils.getUserId();
        System.out.println(userId);
        relationQo.setUserId(userId);
        return relationService.addRelation(relationQo)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }


    @ApiOperation("保存车辆图片")
    @PostMapping("/upload")
    @Login
    public CommonResult upload(@RequestParam("file") MultipartFile file){
        String upload = MinioUtils.upload(file, "aaaa");
        return CommonResult.ok(upload);
    }



}
