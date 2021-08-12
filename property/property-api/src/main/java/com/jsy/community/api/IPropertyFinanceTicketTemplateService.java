package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FinanceTicketTemplateEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * @Author: Pipi
 * @Description: 票据打印模板信息服务
 * @Date: 2021/8/2 15:38
 * @Version: 1.0
 **/
public interface IPropertyFinanceTicketTemplateService extends IService<FinanceTicketTemplateEntity> {
    /**
     * @author: Pipi
     * @description: 添加打印模板
     * @param templateEntity: 打印模板实体
     * @return: java.lang.Integer
     * @date: 2021/8/3 9:28
     **/
    String insertTicketTemplate(FinanceTicketTemplateEntity templateEntity);

    /**
     * @author: Pipi
     * @description: 查询打印模板分页列表 
     * @param baseQO: 
     * @return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.FinanceTicketTemplateEntity>
     * @date: 2021/8/4 10:00
     **/
    PageInfo<FinanceTicketTemplateEntity> ticketTemplatePage(BaseQO<FinanceTicketTemplateEntity> baseQO);

    /**
     * @author: Pipi
     * @description: 修改打印模板名称
     * @param templateEntity: 打印模板实体
     * @return: java.lang.Integer
     * @date: 2021/8/4 11:33
     **/
    Integer updateTicketTemplate(FinanceTicketTemplateEntity templateEntity);

    /**
     * @author: Pipi
     * @description:  删除打印模板
     * @param templateId: 打印模板ID
     * @return: java.lang.Integer
     * @date: 2021/8/4 16:46
     **/
    Integer deleteTicketTemplate(String templateId, Long communityId);
}
