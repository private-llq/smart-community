package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceTicketTemplateService;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.FinanceTicketTemplateEntity;
import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;
import com.jsy.community.mapper.FinanceTicketTemplateFieldMapper;
import com.jsy.community.mapper.FinanceTicketTemplateMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Autowired
    private FinanceTicketTemplateFieldMapper ticketTemplateFieldMapper;
    /**
     * @param templateEntity : 打印模板实体
     * @author: Pipi
     * @description: 添加打印模板
     * @return: java.lang.Integer
     * @date: 2021/8/3 9:28
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertTicketTemplate(FinanceTicketTemplateEntity templateEntity) {
        templateEntity.setId(SnowFlake.nextId());
        for (FinanceTicketTemplateFieldEntity templateFieldEntity : templateEntity.getTemplateFieldEntities()) {
            templateFieldEntity.setFieldId(String.valueOf(templateFieldEntity.getId()));
            templateFieldEntity.setId(SnowFlake.nextId());
            templateFieldEntity.setTemplateId(String.valueOf(templateEntity.getId()));
        }
        // 向模板字段关联表插入数据
        ticketTemplateFieldMapper.batchInsert(templateEntity.getTemplateFieldEntities());
        // 向模板表插入数据
        return ticketTemplateMapper.insert(templateEntity) == 1 ? String.valueOf(templateEntity.getId()) : null;
    }

    /**
     * @param baseQO :
     * @author: Pipi
     * @description: 查询打印模板分页列表
     * @return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.FinanceTicketTemplateEntity>
     * @date: 2021/8/3 17:16
     **/
    @Override
    public PageInfo<FinanceTicketTemplateEntity> ticketTemplatePage(BaseQO<FinanceTicketTemplateEntity> baseQO) {
        Page<FinanceTicketTemplateEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<FinanceTicketTemplateEntity> queryWrapper = new QueryWrapper<>();
        FinanceTicketTemplateEntity query = baseQO.getQuery();
        queryWrapper.eq("community_id", query.getCommunityId());
        if (!StringUtils.isEmpty(query.getName())) {
            queryWrapper.like("name", query.getName());
        }
        if (query.getTemplateType() != null) {
            queryWrapper.eq("template_type", query.getTemplateType());
        }
        queryWrapper.orderByDesc("create_time");
        PageInfo<FinanceTicketTemplateEntity> pageInfo = new PageInfo<>();
        Page<FinanceTicketTemplateEntity> templateEntityPage = ticketTemplateMapper.selectPage(page, queryWrapper);
        BeanUtils.copyProperties(page, pageInfo);
        for (FinanceTicketTemplateEntity templateEntity : pageInfo.getRecords()) {
            templateEntity.setChargeTypeStr(PropertyConstsEnum.ChargeTypeEnum.getName(templateEntity.getChargeType()));
            templateEntity.setTemplateTypeStr(PropertyConstsEnum.TemplateTypeEnum.getName(templateEntity.getTemplateType()));
        }
        return pageInfo;
    }

    /**
     * @param templateEntity : 打印模板实体
     * @author: Pipi
     * @description: 修改打印模板名称
     * @return: java.lang.Integer
     * @date: 2021/8/4 11:33
     **/
    @Override
    public Integer updateTicketTemplate(FinanceTicketTemplateEntity templateEntity) {
        QueryWrapper<FinanceTicketTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", templateEntity.getId());
        queryWrapper.eq("community_id", templateEntity.getCommunityId());
        return ticketTemplateMapper.update(templateEntity, queryWrapper);
    }

    /**
     * @param templateId : 打印模板ID
     * @author: Pipi
     * @description: 删除打印模板
     * @return: java.lang.Integer
     * @date: 2021/8/4 16:46
     **/
    @Override
    public Integer deleteTicketTemplate(String templateId, Long communityId) {
        QueryWrapper<FinanceTicketTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", templateId);
        queryWrapper.eq("community_id", communityId);
        return ticketTemplateMapper.delete(queryWrapper);
    }
}
