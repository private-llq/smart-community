package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserDataService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description: 用户个人信息
 * @author: Hu
 * @create: 2021-03-11 13:37
 **/
@Api(tags = "生活缴费前端控制器--查询")
@RestController
@RequestMapping("/userdata")
@ApiJSYController
public class UserDataController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserDataService userDataService;

    @PostMapping("/selectUserDataOne")
    @ApiOperation("查询个人信息")
    @Login
    public CommonResult selectUserDataOne(){
        String userId = UserUtils.getUserId();
        UserDataVO userDataVO = userDataService.selectUserDataOne(userId);
        return CommonResult.ok(userDataVO);
    }
    @PostMapping("/updateUserData")
    @ApiOperation("修改个人信息")
    @Login
    public CommonResult updateUserData(@RequestBody UserDataQO userDataQO){
        String userId = UserUtils.getUserId();
        userDataService.updateUserData(userDataQO,userId);
        return CommonResult.ok();
    }

}

