package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFinanceTicketTemplateService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * @author: Pipi
     * @description: 添加打印模板
     * @param templateEntity: 打印模板实体
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/3 9:27
     **/
    @Login
    @PostMapping("/addTicketTemplate")
    public CommonResult addTicketTemplate(@RequestBody FinanceTicketTemplateEntity templateEntity) {
        ValidatorUtils.validateEntity(templateEntity);
        templateEntity.setDeleted(0);
        templateEntity.setCommunityId(String.valueOf(UserUtils.getAdminCommunityId()));
        String id = ticketTemplateService.insertTicketTemplate(templateEntity);
        return StringUtils.isNotBlank(id) ? CommonResult.ok(id, "添加成功!") : CommonResult.error("添加失败了!");
    }

    /**
     * @author: Pipi
     * @description: 查询打印模板分页列表
     * @param baseQO: 分页查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/3 17:15
     **/
    @Login
    @PostMapping("/ticketTemplatePage")
    public CommonResult ticketTemplatePage(@RequestBody BaseQO<FinanceTicketTemplateEntity> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new FinanceTicketTemplateEntity());
        }
        baseQO.getQuery().setCommunityId(String.valueOf(UserUtils.getAdminCommunityId()));
        return CommonResult.ok(ticketTemplateService.ticketTemplatePage(baseQO), "查询成功");
    }

    /**
     * @author: Pipi
     * @description: 修改打印模板名称
     * @param templateEntity: 打印模板实体
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/4 11:29
     **/
    @Login
    @PostMapping("/updateTicketTemplate")
    public CommonResult updateTicketTemplate(@RequestBody FinanceTicketTemplateEntity templateEntity) {
        ValidatorUtils.validateEntity(templateEntity, FinanceTicketTemplateEntity.UpdateTicketTemplateValidate.class);
        if (templateEntity.getId() == null) {
            throw new JSYException(400, "模板ID不能为空!");
        }
        templateEntity.setCommunityId(String.valueOf(UserUtils.getAdminCommunityId()));
        ticketTemplateService.updateTicketTemplate(templateEntity);
        return CommonResult.ok("修改成功!");
    }
}
