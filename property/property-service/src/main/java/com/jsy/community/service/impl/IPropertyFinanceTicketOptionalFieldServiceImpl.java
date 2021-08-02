package com.jsy.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketOptionalFieldService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketOptionalFieldEntity;
import com.jsy.community.mapper.FinanceTicketOptionalFieldMapper;
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

    /**
     * @author: Pipi
     * @description: 获取可选字段列表
     * @return: java.util.Map<java.lang.Integer, java.util.List < com.jsy.community.entity.FinanceTicketOptionalFieldEntity>>
     * @date: 2021/8/2 16:53
     **/
    @Override
    public Map<Integer, List<FinanceTicketOptionalFieldEntity>> queryOptionalFieldList() {
        Map<Integer, List<FinanceTicketOptionalFieldEntity>> optionalFieldMap = new HashMap<>();
        QueryWrapper<FinanceTicketOptionalFieldEntity> queryWrapper = new QueryWrapper<>();
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
