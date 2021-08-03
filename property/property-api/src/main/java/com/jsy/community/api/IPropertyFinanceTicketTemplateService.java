package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FinanceTicketTemplateEntity;

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
}
