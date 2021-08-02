package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketTemplateService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateEntity;
import com.jsy.community.mapper.FinanceTicketTemplateMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Pipi
 * @Description: 票据打印模板信息服务实现
 * @Date: 2021/8/2 15:39
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceTicketTemplateServiceImpl extends ServiceImpl<FinanceTicketTemplateMapper, FinanceTicketTemplateEntity> implements IPropertyFinanceTicketTemplateService {
}
