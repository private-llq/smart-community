package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceTicketTemplateService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 票据打印模板信息控制器
 * @Date: 2021/8/2 16:42
 * @Version: 1.0
 **/
@RestController
@Api("票据打印模板信息控制器")
@RequestMapping("/ticketTemplate")
@ApiJSYController
public class PropertyFinanceTicketTemplateController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceTicketTemplateService ticketTemplateService;
}
