package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SmsTypeEntity;
import com.jsy.community.mapper.SmsTemplateMapper;
import com.jsy.community.mapper.SmsTypeMapper;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.ISmsTypeService;
import com.jsy.community.utils.SnowFlake;
import org.springframework.stereotype.Service;

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
    public boolean deleteSmsType(Long id) {
        SmsTypeEntity smsTypeEntity = smsTypeMapper.selectById(id);
        // 根据短信分类id查询是否存在短信模板
        if (smsTypeEntity != null) {
            Integer integer = smsTemplateMapper.selectSmsTemplateBySmsTypeId(smsTypeEntity.getId());
            if (integer > 0) {
                throw new AdminException("存在短信模板的分类无法被删除");
            } else {
                return smsTypeMapper.deleteById(id) == 1;
            }
        } else {
            throw new AdminException("不存在该短信分类");
        }
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
