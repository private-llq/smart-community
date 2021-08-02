package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceTicketOptionalFieldService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/8/2 15:48
 * @Version: 1.0
 **/
@RestController
@Api("财务票据可选字段控制器")
@RequestMapping("/ticketOptionalField")
@ApiJSYController
public class PropertyFinanceTicketOptionalFieldController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceTicketOptionalFieldService ticketOptionalFieldService;
}
