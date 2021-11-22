package com.jsy.community.controller;

import com.github.xiaoymin.knife4j.annotations.Ignore;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.InformListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
        userInformEntity.setUid(userId);
        userInformEntity.setId(SnowFlake.nextId());
        return userInformService.save(userInformEntity)?CommonResult.ok():CommonResult.error(JSYError.INTERNAL);
    }

    /**
     * @author YuLF
     * @since  2020/12/21 14:52
     * 用户社区总消息列表、
     * 拉取用户总消息列表
     */
    @Login
    @GetMapping("/totalList")
    @ApiOperation("用户社区总未读消息列表查看")
    public CommonResult<List<InformListVO>> totalCommunityInformList(){
        return CommonResult.ok(userInformService.totalCommunityInformList(UserUtils.getUserId()));
    }
    /**
     * @Description: 查看当前登录人员是否有未读消息
     * @author: Hu
     * @since: 2021/5/10 15:06
     * @Param:
     * @return:
     */
    @Login
    @GetMapping("/totalInForm")
    @ApiOperation("查看当前登录人员是否有未读消息")
    public CommonResult<Integer> totalInForm(){
        return CommonResult.ok(userInformService.totalInForm(UserUtils.getUserId()));
    }


}
