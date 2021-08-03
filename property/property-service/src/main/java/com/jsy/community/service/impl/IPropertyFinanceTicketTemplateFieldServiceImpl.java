package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketTemplateFieldService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;
import com.jsy.community.mapper.FinanceTicketTemplateFieldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 票据模板与字段关联服务实现
 * @Date: 2021/8/2 15:14
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceTicketTemplateFieldServiceImpl extends ServiceImpl<FinanceTicketTemplateFieldMapper, FinanceTicketTemplateFieldEntity> implements IPropertyFinanceTicketTemplateFieldService {
    @Autowired
    private FinanceTicketTemplateFieldMapper ticketTemplateFieldMapper;
    /**
     * @param ticketTemplateFieldEntities : 票据字段列表
     * @author: Pipi
     * @description: 设置票据字段
     * @return: java.lang.Integer
     * @date: 2021/8/3 14:23
     **/
    @Override
    public Integer insertTicketTemplateField(List<FinanceTicketTemplateFieldEntity> ticketTemplateFieldEntities) {
        return ticketTemplateFieldMapper.batchInsert(ticketTemplateFieldEntities);
    }
}
