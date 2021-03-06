package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyFinanceTicketTemplateService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Pipi
 * @Description: 票据打印模板信息控制器
 * @Date: 2021/8/2 16:42
 * @Version: 1.0
 **/
@RestController
@Api("票据打印模板信息控制器")
@RequestMapping("/ticketTemplate")
// @ApiJSYController
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
    @PostMapping("/addTicketTemplate")
    @businessLog(operation = "新增",content = "新增了【票据打印模板】")
    @Permit("community:property:ticketTemplate:addTicketTemplate")
    public CommonResult addTicketTemplate(@RequestBody FinanceTicketTemplateEntity templateEntity) {
        ValidatorUtils.validateEntity(templateEntity, FinanceTicketTemplateEntity.AddTicketTemplateValidate.class);
        templateEntity.setDeleted(0L);
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
    @PostMapping("/ticketTemplatePage")
    @Permit("community:property:ticketTemplate:ticketTemplatePage")
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
    @PostMapping("/updateTicketTemplate")
    @businessLog(operation = "编辑",content = "更新了【票据打印模板】")
    @Permit("community:property:ticketTemplate:updateTicketTemplate")
    public CommonResult updateTicketTemplate(@RequestBody FinanceTicketTemplateEntity templateEntity) {
        ValidatorUtils.validateEntity(templateEntity, FinanceTicketTemplateEntity.UpdateTicketTemplateValidate.class);
        if (templateEntity.getId() == null) {
            throw new JSYException(400, "模板ID不能为空!");
        }
        templateEntity.setCommunityId(String.valueOf(UserUtils.getAdminCommunityId()));
        ticketTemplateService.updateTicketTemplate(templateEntity);
        return CommonResult.ok("修改成功!");
    }

    /**
     * @author: Pipi
     * @description: 删除打印模板
     * @param templateId: 打印模板ID
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/4 16:46
     **/
    @DeleteMapping("/deleteTicketTemplate")
    @businessLog(operation = "删除",content = "删除了【票据打印模板】")
    @Permit("community:property:ticketTemplate:deleteTicketTemplate")
    public CommonResult deleteTicketTemplate(@RequestParam("templateId") String templateId) {
        return ticketTemplateService.deleteTicketTemplate(templateId, UserUtils.getAdminCommunityId()) > 0 ? CommonResult.ok("删除成功!") : CommonResult.error("删除失败!");
    }
}
