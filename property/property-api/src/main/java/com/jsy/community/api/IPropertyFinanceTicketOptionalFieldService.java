package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FinanceTicketOptionalFieldEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 财务票据可选字段服务
 * @Date: 2021/8/2 15:30
 * @Version: 1.0
 **/
public interface IPropertyFinanceTicketOptionalFieldService extends IService<FinanceTicketOptionalFieldEntity> {
    /**
     * @author: Pipi
     * @description:  获取可选字段列表
     * @param :
     * @return: java.util.Map<java.lang.Integer,java.util.List<com.jsy.community.entity.FinanceTicketOptionalFieldEntity>>
     * @date: 2021/8/2 16:53
     **/
    Map<Integer, List<FinanceTicketOptionalFieldEntity>> queryOptionalFieldList();
}
