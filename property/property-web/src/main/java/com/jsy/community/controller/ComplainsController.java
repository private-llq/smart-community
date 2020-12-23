package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IComplainsService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.ComplainVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 15:52
 **/
@Api(tags = "投诉建议控制器")
@RestController
@RequestMapping("/complainProperty")
@ApiJSYController
public class ComplainsController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IComplainsService complainsService;

    @ApiOperation("查询所有投诉建议")
    @GetMapping("/list")
    public CommonResult<List<ComplainVO>> list() {
        List<ComplainVO> complainVOS = complainsService.listAll();
        return CommonResult.ok(complainVOS);
    }
    @ApiOperation("反馈内容")
    @PostMapping("/feedback")
    public CommonResult feedback(@RequestBody ComplainFeedbackQO complainFeedbackQO) {
        System.out.println(complainFeedbackQO);
        complainsService.feedback(complainFeedbackQO);
        return CommonResult.ok();
    }
}
