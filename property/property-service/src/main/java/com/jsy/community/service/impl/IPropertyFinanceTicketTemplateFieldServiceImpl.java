package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketTemplateFieldService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;
import com.jsy.community.mapper.FinanceTicketTemplateFieldMapper;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
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

    /**
     * @param ticketTemplateFieldEntities : 票据字段列表
     * @author: Pipi
     * @description: 更新票据字段
     * @return: java.lang.Integer
     * @date: 2021/8/4 14:07
     **/
    @Override
    public Integer updateTicketTemplateField(List<FinanceTicketTemplateFieldEntity> ticketTemplateFieldEntities) {
        // 清空原有的字段列表
        HashSet<String> templateIdSet = new HashSet<>();
        for (FinanceTicketTemplateFieldEntity ticketTemplateFieldEntity : ticketTemplateFieldEntities) {
            templateIdSet.add(ticketTemplateFieldEntity.getTemplateId());
            ticketTemplateFieldEntity.setId(String.valueOf(SnowFlake.nextId()));
        }
        if (templateIdSet.size() > 1) {
            // 表示这次更新了多个打印模板(包含了其他人的模板)
            throw new PropertyException("票据模板ID不唯一");
        }
        QueryWrapper<FinanceTicketTemplateFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("template_id", templateIdSet);
        ticketTemplateFieldMapper.delete(queryWrapper);
        // 然后新增新的字段列表
        return ticketTemplateFieldMapper.batchInsert(ticketTemplateFieldEntities);
    }

    /**
     * @param templateId : 打印模板ID
     * @author: Pipi
     * @description: 获取打印模板字段列表
     * @return: java.util.List<com.jsy.community.entity.FinanceTicketTemplateFieldEntity>
     * @date: 2021/8/4 15:14
     **/
    @Override
    public List<FinanceTicketTemplateFieldEntity> getTicketTemplateFieldList(String templateId) {
        QueryWrapper<FinanceTicketTemplateFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_id", templateId);
        return ticketTemplateFieldMapper.selectList(queryWrapper);
    }
}
