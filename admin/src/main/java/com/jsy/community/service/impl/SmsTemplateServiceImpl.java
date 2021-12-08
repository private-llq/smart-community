package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SmsTemplateEntity;
import com.jsy.community.mapper.SmsTemplateMapper;
import com.jsy.community.mapper.SmsTypeMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SmsTemplateQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.ISmsTemplateService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 短信模板
 * @author: DKS
 * @since: 2021/12/8 11:17
 */
@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplateEntity> implements ISmsTemplateService {
    
    @Resource
    private SmsTemplateMapper smsTemplateMapper;
    
    @Resource
    private SmsTypeMapper smsTypeMapper;
    
    /**
     * @Description: 新增短信模板
     * @author: DKS
     * @since: 2021/12/8 11:18
     * @Param: [smsTemplateEntity]
     * @return: boolean
     */
    @Override
    public boolean addSmsTemplate(SmsTemplateEntity smsTemplateEntity) {
        smsTemplateEntity.setId(SnowFlake.nextId());
        return smsTemplateMapper.insert(smsTemplateEntity) == 1;
    }
    
    /**
     * @Description: 修改短信模板
     * @author: DKS
     * @since: 2021/12/8 11:19
     * @Param: [smsTemplateEntity]
     * @return: boolean
     */
    @Override
    public boolean updateSmsTemplate(SmsTemplateEntity smsTemplateEntity) {
        return smsTemplateMapper.updateById(smsTemplateEntity) == 1;
    }
    
    /**
     * @Description: 删除短信模板
     * @author: DKS
     * @since: 2021/12/8 11:24
     * @Param: [id]
     * @return: boolean
     */
    @Override
    public boolean deleteSmsTemplate(Long id) {
        SmsTemplateEntity smsTemplateEntity = smsTemplateMapper.selectById(id);
        if (smsTemplateEntity != null) {
            return smsTemplateMapper.deleteById(id) == 1;
        } else {
            throw new AdminException("不存在该短信模板");
        }
    }
    
    /**
     * @Description: 查询短信模板列表
     * @author: DKS
     * @since: 2021/12/8 11:20
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.SmsTemplateEntity>
     */
    @Override
    public List<SmsTemplateEntity> selectSmsTemplate() {
        return smsTemplateMapper.selectList(new QueryWrapper<SmsTemplateEntity>().eq("deleted", 0));
    }
    
    /**
     * @Description: 短信模板分页查询
     * @author: DKS
     * @since: 2021/12/8 11:51
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsTemplateEntity>
     */
    @Override
    public PageInfo<SmsTemplateEntity> querySmsTemplatePage(BaseQO<SmsTemplateQO> baseQO) {
        SmsTemplateQO query = baseQO.getQuery();
        Page<SmsTemplateEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<SmsTemplateEntity> queryWrapper = new QueryWrapper<>();
        // 查短信分类id
        if (query.getSmsTypeId() != null) {
            queryWrapper.eq("sms_type_id", query.getSmsTypeId());
        }
        // 查状态
        if (query.getStatus() != null) {
            queryWrapper.eq("status", query.getStatus());
        }
        // 查关键字
        if (StringUtils.isNotBlank(query.getKeyword())) {
            queryWrapper.like("name", query.getKeyword());
            queryWrapper.or().like("content", query.getKeyword());
        }
        queryWrapper.orderByDesc("create_time");
        Page<SmsTemplateEntity> pageData = smsTemplateMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        // 补充返回数据
        for (SmsTemplateEntity entity : pageData.getRecords()) {
            // 补充短信分类名称
            entity.setSmsTypeIdName(smsTypeMapper.selectById(entity.getSmsTypeId()).getName());
            // 补充状态名称
            entity.setStatusName(entity.getStatus() == 1 ? "启用" : "禁用");
        }
        
        PageInfo<SmsTemplateEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
}
