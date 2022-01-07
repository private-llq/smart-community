package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SmsTemplateEntity;
import com.jsy.community.entity.SmsTypeEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.SmsTemplateMapper;
import com.jsy.community.mapper.SmsTypeMapper;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.ISmsTypeService;
import com.jsy.community.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 短信分类
 * @author: DKS
 * @since: 2021/12/8 10:39
 */
@Service
public class SmsTypeServiceImpl extends ServiceImpl<SmsTypeMapper, SmsTypeEntity> implements ISmsTypeService {
    
    @Resource
    private SmsTypeMapper smsTypeMapper;
    
    @Resource
    private SmsTemplateMapper smsTemplateMapper;
    
    /**
     * @Description: 新增短信分类
     * @author: DKS
     * @since: 2021/12/8 10:48
     * @Param: [smsTypeEntity]
     * @return: boolean
     */
    @Override
    public boolean addSmsType(SmsTypeEntity smsTypeEntity) {
        smsTypeEntity.setId(SnowFlake.nextId());
        return smsTypeMapper.insert(smsTypeEntity) == 1;
    }
    
    /**
     * @Description: 修改短信分类
     * @author: DKS
     * @since: 2021/12/8 10:49
     * @Param: [smsTypeEntity]
     * @return: boolean
     */
    @Override
    public boolean updateSmsType(SmsTypeEntity smsTypeEntity) {
        return smsTypeMapper.updateById(smsTypeEntity) == 1;
    }
    
    /**
     * @Description: 删除短信分类
     * @author: DKS
     * @since: 2021/12/8 10:51
     * @Param: [id]
     * @return: boolean
     */
    @Override
    public boolean deleteSmsType(List<Long> id) {
        List<SmsTypeEntity> smsTypeEntities = smsTypeMapper.selectList(new QueryWrapper<SmsTypeEntity>().in("id", id));
        if (CollectionUtils.isEmpty(smsTypeEntities)) {
            throw new AdminException(JSYError.SMS_TYPE_LOST);
        }
        List<SmsTemplateEntity> smsTemplateEntities = smsTemplateMapper.selectList(new QueryWrapper<SmsTemplateEntity>().in("sms_type_id", id));
        // 根据短信分类id查询是否存在短信模板
        if (!CollectionUtils.isEmpty(smsTemplateEntities)) {
            throw new AdminException(JSYError.SMS_TYPE_DUPLICATE);
        }
        return smsTypeMapper.deleteBatchIds(id) >= 1;
    }
    
    /**
     * @Description: 查询短信分类列表
     * @author: DKS
     * @since: 2021/12/8 10:57
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.SmsTypeEntity>
     */
    @Override
    public List<SmsTypeEntity> selectSmsType() {
        return smsTypeMapper.selectList(new QueryWrapper<SmsTypeEntity>().eq("deleted", 0));
    }
}
