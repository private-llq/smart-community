package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;

import java.util.List;

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
}
