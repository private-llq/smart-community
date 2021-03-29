package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IComplainsService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/list")
    public CommonResult list(BaseQO<PropertyComplaintsQO> baseQO) {
        PageInfo pageInfo = complainsService.listAll(baseQO);
        return CommonResult.ok(pageInfo);
    }
    @ApiOperation("反馈内容")
    @PostMapping("/feedback")
    public CommonResult feedback(@RequestBody ComplainFeedbackQO complainFeedbackQO) {
        System.out.println(complainFeedbackQO);
        complainsService.feedback(complainFeedbackQO);
        return CommonResult.ok();
    }
}
