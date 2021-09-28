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
import org.springframework.util.CollectionUtils;

import java.util.*;

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
            ticketTemplateFieldEntity.setId(SnowFlake.nextId());
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
    public Map<Integer, List<FinanceTicketTemplateFieldEntity>> getTicketTemplateFieldList(String templateId) {
        Map<Integer, List<FinanceTicketTemplateFieldEntity>> fieldMap = new HashMap<>();
        fieldMap.put(1, new ArrayList<>());
        fieldMap.put(2, new ArrayList<>());
        fieldMap.put(3, new ArrayList<>());
        QueryWrapper<FinanceTicketTemplateFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("field_id AS id,template_id,location_type,`name`,name_en,sort");
        queryWrapper.eq("template_id", templateId);
        queryWrapper.orderByAsc("location_type");
        queryWrapper.orderByAsc("sort");
        List<FinanceTicketTemplateFieldEntity> fieldEntities = ticketTemplateFieldMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(fieldEntities)) {
            for (FinanceTicketTemplateFieldEntity fieldEntity : fieldEntities) {
                fieldEntity.setTemplateId(null);
                fieldEntity.setIdStr(String.valueOf(fieldEntity.getId()));
                fieldMap.get(fieldEntity.getLocationType()).add(fieldEntity);
            }
        }
        return fieldMap;
    }
}
