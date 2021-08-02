package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceTicketTemplateFieldService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 票据模板与字段关联控制器
 * @Date: 2021/8/2 16:40
 * @Version: 1.0
 **/
@RestController
@Api("票据模板与字段关联控制器")
@RequestMapping("/ticketTemplateField")
@ApiJSYController
public class PropertyFinanceTicketTemplateFieldController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceTicketTemplateFieldService ticketTemplateFieldService;
}
