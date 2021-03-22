package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyComplaintsService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description: 物业投诉受理接口
 * @author: Hu
 * @create: 2021-03-19 11:17
 **/
@Api(tags = "物业投诉受理")
@RestController
@RequestMapping("/propertyComplaints")
@ApiJSYController
public class PropertyComplaintsController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyComplaintsService propertyComplaintsService;

    @ApiOperation("查询所有物业投诉信息")
    @PostMapping("/list")
    public CommonResult list(@RequestBody BaseQO<PropertyComplaintsQO> baseQO){
        System.out.println(baseQO);
        PageInfo list=propertyComplaintsService.findList(baseQO);
        return CommonResult.ok(list);
    }
    @ApiOperation("投诉回复")
    @PostMapping("/complainFeedback")
    public CommonResult complainFeedback(@RequestBody ComplainFeedbackQO complainFeedbackQO){
        complainFeedbackQO.setUid(UserUtils.getUserId());
        propertyComplaintsService.complainFeedback(complainFeedbackQO);
        return CommonResult.ok();
    }


}
