package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketTemplateService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceTicketTemplateEntity;
import com.jsy.community.mapper.FinanceTicketTemplateMapper;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Pipi
 * @Description: 票据打印模板信息服务实现
 * @Date: 2021/8/2 15:39
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class IPropertyFinanceTicketTemplateServiceImpl extends ServiceImpl<FinanceTicketTemplateMapper, FinanceTicketTemplateEntity> implements IPropertyFinanceTicketTemplateService {
    @Autowired
    private FinanceTicketTemplateMapper ticketTemplateMapper;
    /**
     * @param templateEntity : 打印模板实体
     * @author: Pipi
     * @description: 添加打印模板
     * @return: java.lang.Integer
     * @date: 2021/8/3 9:28
     **/
    @Override
    public String insertTicketTemplate(FinanceTicketTemplateEntity templateEntity) {
        templateEntity.setId(SnowFlake.nextId());
        return ticketTemplateMapper.insert(templateEntity) == 1 ? String.valueOf(templateEntity.getId()) : null;
    }
}
