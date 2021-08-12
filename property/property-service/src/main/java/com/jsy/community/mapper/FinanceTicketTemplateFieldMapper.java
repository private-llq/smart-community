package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 票据模板与字段关联表Mapper
 * @Date: 2021/8/2 14:27
 * @Version: 1.0
 **/
public interface FinanceTicketTemplateFieldMapper extends BaseMapper<FinanceTicketTemplateFieldEntity> {

    /**
     * @author: Pipi
     * @description: 批量新增票据字段数据
     * @param ticketTemplateFieldEntities: 票据字段列表
     * @return: java.lang.Integer
     * @date: 2021/8/3 14:25
     **/
    Integer batchInsert(List<FinanceTicketTemplateFieldEntity> ticketTemplateFieldEntities);
}
