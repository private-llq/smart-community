package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketTemplateFieldService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;
import com.jsy.community.mapper.FinanceTicketTemplateFieldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 票据模板与字段关联服务实现
 * @Date: 2021/8/2 15:14
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceTicketTemplateFieldServiceImpl extends ServiceImpl<FinanceTicketTemplateFieldMapper, FinanceTicketTemplateFieldEntity> implements IPropertyFinanceTicketTemplateFieldService {

}
