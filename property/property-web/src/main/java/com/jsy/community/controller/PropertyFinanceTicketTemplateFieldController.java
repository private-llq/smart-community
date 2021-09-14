package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyFinanceTicketTemplateFieldService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * @author: Pipi
     * @description: 设置票据字段
     * @param ticketTemplateFieldEntities: 票据字段列表
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/3 13:59
     **/
    @Login
    @PostMapping("/addTicketTemplateField")
    @businessLog(operation = "新增",content = "新增了【票据字段】")
    public CommonResult addTicketTemplateField(@RequestBody List<FinanceTicketTemplateFieldEntity> ticketTemplateFieldEntities) {
        if (CollectionUtil.isEmpty(ticketTemplateFieldEntities)) {
            throw new JSYException(400, "请选择票据字段");
        }
        for (FinanceTicketTemplateFieldEntity ticketTemplateFieldEntity : ticketTemplateFieldEntities) {
            ValidatorUtils.validateEntity(ticketTemplateFieldEntity);
            ticketTemplateFieldEntity.setId(SnowFlake.nextId());
        }
        return ticketTemplateFieldService.insertTicketTemplateField(ticketTemplateFieldEntities) > 0 ? CommonResult.ok("保存成功!") : CommonResult.error("保存失败!");
    }

    /**
     * @author: Pipi
     * @description: 更新票据字段
     * @param ticketTemplateFieldEntities: 票据字段列表
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/4 14:05
     **/
    @Login
    @PutMapping("/updateTicketTemplateField")
    @businessLog(operation = "编辑",content = "更新了【票据字段】")
    public CommonResult updateTicketTemplateField(@RequestBody List<FinanceTicketTemplateFieldEntity> ticketTemplateFieldEntities) {
        if (CollectionUtil.isEmpty(ticketTemplateFieldEntities)) {
            throw new JSYException(400, "请选择票据字段");
        }
        for (FinanceTicketTemplateFieldEntity ticketTemplateFieldEntity : ticketTemplateFieldEntities) {
            ticketTemplateFieldEntity.setFieldId(String.valueOf(ticketTemplateFieldEntity.getId()));
            ValidatorUtils.validateEntity(ticketTemplateFieldEntity);
        }
        ticketTemplateFieldService.updateTicketTemplateField(ticketTemplateFieldEntities);
        return CommonResult.ok("修改成功!");
    }

    /**
     * @author: Pipi
     * @description: 获取打印模板字段列表
     * @param templateId: 打印模板ID
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/4 15:13
     **/
    @Login
    @GetMapping("/ticketTemplateFieldList")
    public CommonResult ticketTemplateFieldList(@RequestParam("templateId") String templateId) {
        return CommonResult.ok(ticketTemplateFieldService.getTicketTemplateFieldList(templateId));
    }
}
