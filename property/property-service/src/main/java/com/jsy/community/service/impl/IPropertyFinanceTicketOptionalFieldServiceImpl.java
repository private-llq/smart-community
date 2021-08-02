package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketOptionalFieldService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketOptionalFieldEntity;
import com.jsy.community.mapper.FinanceTicketOptionalFieldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 财务票据可选字段服务实现
 * @Date: 2021/8/2 15:34
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceTicketOptionalFieldServiceImpl extends ServiceImpl<FinanceTicketOptionalFieldMapper, FinanceTicketOptionalFieldEntity> implements IPropertyFinanceTicketOptionalFieldService {
}
