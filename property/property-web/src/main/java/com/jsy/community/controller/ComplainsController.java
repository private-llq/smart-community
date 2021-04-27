package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IComplainsService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
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
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 15:52
 **/
@Api(tags = "投诉建议控制器")
@RestController
@RequestMapping("/complains")
@ApiJSYController
public class ComplainsController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IComplainsService complainsService;

    @ApiOperation("查询所有投诉建议")
    @PostMapping("/list")
    @Login
    public CommonResult list(@RequestBody BaseQO<PropertyComplaintsQO> baseQO) {
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        Map<String, Object> map = complainsService.listAll(baseQO,userInfo);

        return CommonResult.ok(map);
    }
    @ApiOperation("反馈内容")
    @PostMapping("/feedback")
    @Login
    public CommonResult feedback(@RequestBody ComplainFeedbackQO complainFeedbackQO) {
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        complainsService.feedback(complainFeedbackQO,userInfo);
        return CommonResult.ok();
    }
}
