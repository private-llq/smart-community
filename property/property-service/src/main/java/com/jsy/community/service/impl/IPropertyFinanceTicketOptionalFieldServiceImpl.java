package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketOptionalFieldService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketOptionalFieldEntity;
import com.jsy.community.entity.FinanceTicketOptionalTypeFieldEntity;
import com.jsy.community.mapper.FinanceTicketOptionalFieldMapper;
import com.jsy.community.mapper.FinanceTicketOptionalTypeFieldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 财务票据可选字段服务实现
 * @Date: 2021/8/2 15:34
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceTicketOptionalFieldServiceImpl extends ServiceImpl<FinanceTicketOptionalFieldMapper, FinanceTicketOptionalFieldEntity> implements IPropertyFinanceTicketOptionalFieldService {

    @Autowired
    private FinanceTicketOptionalFieldMapper ticketOptionalFieldMapper;

    @Autowired
    private FinanceTicketOptionalTypeFieldMapper ticketOptionalTypeFieldMapper;

    /**
     * @author: Pipi
     * @description:  获取可选字段列表
     * @param templateType: 模板类型;1:缴费单;2:收据
     * @param chargeType: 收费类型;1:水电气缴费模板;2:租金管理费模板;3:物业费/管理费模板;4:通用模板
     * @return: java.util.Map<java.lang.Integer,java.util.List<com.jsy.community.entity.FinanceTicketOptionalFieldEntity>>
     * @date: 2021/8/2 16:53
     **/
    @Override
    public Map<Integer, List<FinanceTicketOptionalFieldEntity>> queryOptionalFieldList(Integer templateType, Integer chargeType) {
        Map<Integer, List<FinanceTicketOptionalFieldEntity>> optionalFieldMap = new HashMap<>();
        // 根据类型查询可选字段列表
        QueryWrapper<FinanceTicketOptionalTypeFieldEntity> typeFieldEntityQueryWrapper = new QueryWrapper<>();
        typeFieldEntityQueryWrapper.eq("template_type", templateType);
        typeFieldEntityQueryWrapper.eq("charge_type", chargeType);
        List<FinanceTicketOptionalTypeFieldEntity> typeFieldEntities = ticketOptionalTypeFieldMapper.selectList(typeFieldEntityQueryWrapper);
        if (CollectionUtil.isEmpty(typeFieldEntities)) {
            return optionalFieldMap;
        }
        List<Long> fieldIds = new ArrayList<>();
        for (FinanceTicketOptionalTypeFieldEntity typeFieldEntity : typeFieldEntities) {
            fieldIds.add(typeFieldEntity.getFieldId());
        }
        // 根据字段列表查询字段信息
        QueryWrapper<FinanceTicketOptionalFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", fieldIds);
        List<FinanceTicketOptionalFieldEntity> ticketOptionalFieldEntities = ticketOptionalFieldMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(ticketOptionalFieldEntities)) {
            for (FinanceTicketOptionalFieldEntity ticketOptionalFieldEntity : ticketOptionalFieldEntities) {
                if (optionalFieldMap.containsKey(ticketOptionalFieldEntity.getLocationType())) {
                    optionalFieldMap.get(ticketOptionalFieldEntity.getLocationType()).add(ticketOptionalFieldEntity);
                } else {
                    List<FinanceTicketOptionalFieldEntity> ticketOptionalFieldEntityList = new ArrayList<>();
                    ticketOptionalFieldEntityList.add(ticketOptionalFieldEntity);
                    optionalFieldMap.put(ticketOptionalFieldEntity.getLocationType(), ticketOptionalFieldEntityList);
                }
            }
        }
        return optionalFieldMap;
    }
}
