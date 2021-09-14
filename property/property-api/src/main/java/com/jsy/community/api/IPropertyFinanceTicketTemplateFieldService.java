package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 票据模板与字段关联服务
 * @Date: 2021/8/2 15:13
 * @Version: 1.0
 **/
public interface IPropertyFinanceTicketTemplateFieldService extends IService<FinanceTicketTemplateFieldEntity> {
    /**
     * @author: Pipi
     * @description: 设置票据字段
     * @param ticketTemplateFieldEntities: 票据字段列表
     * @return: java.lang.Integer
     * @date: 2021/8/3 14:23
     **/
    Integer insertTicketTemplateField(List<FinanceTicketTemplateFieldEntity> ticketTemplateFieldEntities);

    /**
     * @author: Pipi
     * @description: 更新票据字段
     * @param ticketTemplateFieldEntities: 票据字段列表
     * @return: java.lang.Integer
     * @date: 2021/8/4 14:07
     **/
    Integer updateTicketTemplateField(List<FinanceTicketTemplateFieldEntity> ticketTemplateFieldEntities);

    /**
     * @param templateId: 打印模板ID
     * @author: Pipi
     * @description: 获取打印模板字段列表
     * @return: java.util.List<com.jsy.community.entity.FinanceTicketTemplateFieldEntity>
     * @date: 2021/8/4 15:14
     **/
    Map<Integer, List<FinanceTicketTemplateFieldEntity>> getTicketTemplateFieldList(String templateId);
}
