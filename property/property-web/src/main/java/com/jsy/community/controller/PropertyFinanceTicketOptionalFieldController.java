package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceTicketOptionalFieldService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 财务票据可选字段控制器
 * @Date: 2021/8/2 15:48
 * @Version: 1.0
 **/
@RestController
@Api("财务票据可选字段控制器")
@RequestMapping("/ticketOptionalField")
// @ApiJSYController
public class PropertyFinanceTicketOptionalFieldController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceTicketOptionalFieldService ticketOptionalFieldService;

    /**
     * @author: Pipi
     * @description: 获取可选字段列表
     * @param templateType: 模板类型;1:缴费单;2:收据
     * @param chargeType: 收费类型;1:水电气缴费模板;2:租金管理费模板;3:物业费/管理费模板;4:通用模板
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/2 16:46
     **/
    @LoginIgnore
    @GetMapping("/optionalFieldList")
    public CommonResult optionalFieldList(@RequestParam("templateType") Integer templateType, @RequestParam("chargeType") Integer chargeType) {
        return CommonResult.ok(ticketOptionalFieldService.queryOptionalFieldList(templateType, chargeType));
    }
}
