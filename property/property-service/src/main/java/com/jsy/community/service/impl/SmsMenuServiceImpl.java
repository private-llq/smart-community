package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ISmsMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.SmsMenuEntity;
import com.jsy.community.mapper.SmsMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Propagation;
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
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class SmsMenuServiceImpl extends ServiceImpl<SmsMenuMapper, SmsMenuEntity> implements ISmsMenuService {
    
    @Resource
    private SmsMenuMapper smsMenuMapper;
    
    /**
     * @Description: 查询短信套餐列表
     * @author: DKS
     * @since: 2021/12/10 10:31
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
