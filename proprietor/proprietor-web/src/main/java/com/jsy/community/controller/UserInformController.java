package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/inform")
@Api(tags = "社区通知消息")
@RestController
@ApiJSYController
public class UserInformController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserInformService userInformService;



    /**
     * @Description: 保存已读人员到数据库
     * @author: Hu
     * @since: 2020/12/4 14:46
     * @Param:
     * @return:
     */
    @ApiOperation("添加已读通知人员")
    @PostMapping("/add")
    @Login
    public CommonResult save(@RequestBody UserInformEntity userInformEntity) {
        String userId = UserUtils.getUserId();
        return userInformService.save(userInformEntity)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }




}
