package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SmsMenuEntity;
import com.jsy.community.mapper.SmsMenuMapper;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.ISmsMenuService;
import com.jsy.community.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: 短信套餐
 * @author: DKS
 * @since: 2021/12/9 10:39
 */
@Service
public class SmsMenuServiceImpl extends ServiceImpl<SmsMenuMapper, SmsMenuEntity> implements ISmsMenuService {
    
    @Resource
    private SmsMenuMapper smsMenuMapper;
    
    /**
     * @Description: 新增短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:09
     * @Param: [smsMenuEntity]
     * @return: boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSmsMenu(SmsMenuEntity smsMenuEntity) {
        SmsMenuEntity entity = smsMenuMapper.selectOne(new QueryWrapper<SmsMenuEntity>().eq("sort", smsMenuEntity.getSort()).eq("deleted", 0));
        // sort排序已经存在
        SmsMenuEntity entity1;
        if (entity != null) {
            // 自动增加已有sort排序
            List<SmsMenuEntity> smsMenuEntities = smsMenuMapper.selectList(new QueryWrapper<SmsMenuEntity>().eq("deleted", 0));
            for (int i = 0; i < smsMenuEntities.size(); i++) {
                entity = smsMenuMapper.selectOne(new QueryWrapper<SmsMenuEntity>().eq("sort", smsMenuEntity.getSort() + i).eq("deleted", 0).orderByAsc("update_time").last("limit 1"));
                entity.setSort(entity.getSort() + 1);
                entity1 = smsMenuMapper.selectOne(new QueryWrapper<SmsMenuEntity>().eq("sort", entity.getSort()).eq("deleted", 0));
                smsMenuMapper.updateById(entity);
                if (entity1 == null) {
                    break;
                }
            }
        }
        smsMenuEntity.setId(SnowFlake.nextId());
        return smsMenuMapper.insert(smsMenuEntity) == 1;
    }
    
    /**
     * @Description: 修改短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:09
     * @Param: [smsMenuEntity]
     * @return: boolean
     */
    @Override
    public boolean updateSmsMenu(SmsMenuEntity smsMenuEntity) {
        int row = 0;
        SmsMenuEntity entity = smsMenuMapper.selectOne(new QueryWrapper<SmsMenuEntity>().eq("sort", smsMenuEntity.getSort()).eq("deleted", 0));
        // sort排序已经存在
        SmsMenuEntity entity1;
        if (entity != null) {
            row = smsMenuMapper.updateById(smsMenuEntity);
            // 自动增加已有sort排序
            List<SmsMenuEntity> smsMenuEntities = smsMenuMapper.selectList(new QueryWrapper<SmsMenuEntity>().eq("deleted", 0));
            for (int i = 0; i < smsMenuEntities.size(); i++) {
                entity = smsMenuMapper.selectOne(new QueryWrapper<SmsMenuEntity>().eq("sort", smsMenuEntity.getSort() + i).eq("deleted", 0).orderByAsc("update_time").last("limit 1"));
                entity.setSort(entity.getSort() + 1);
                entity1 = smsMenuMapper.selectOne(new QueryWrapper<SmsMenuEntity>().eq("sort", entity.getSort()).eq("deleted", 0));
                smsMenuMapper.updateById(entity);
                if (entity1 == null) {
                    break;
                }
            }
        }
        return row == 1;
    }
    
    /**
     * @Description: 删除短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:09
     * @Param: [id]
     * @return: boolean
     */
    @Override
    public boolean deleteSmsMenu(Long id) {
        SmsMenuEntity smsMenuEntity = smsMenuMapper.selectById(id);
        if (smsMenuEntity != null) {
            return smsMenuMapper.deleteById(id) == 1;
        } else {
            throw new AdminException("不存在该短信套餐");
        }
    }
    
    /**
     * @Description: 查询短信套餐列表
     * @author: DKS
     * @since: 2021/12/9 11:09
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.SmsMenuEntity>
     */
    @Override
    public List<SmsMenuEntity> selectSmsMenu() {
        List<SmsMenuEntity> smsMenuEntities = smsMenuMapper.selectList(new QueryWrapper<SmsMenuEntity>().eq("deleted", 0));
        if (CollectionUtils.isEmpty(smsMenuEntities)) {
            return new ArrayList<>();
        }
        // list排序
        smsMenuEntities.sort(Comparator.comparing(SmsMenuEntity::getSort));
        return smsMenuEntities;
    }
}
