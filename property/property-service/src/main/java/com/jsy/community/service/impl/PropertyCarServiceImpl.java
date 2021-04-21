package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyCarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PropertyCarEntity;
import com.jsy.community.mapper.PropertyCarMapper;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:54
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyCarServiceImpl extends ServiceImpl<PropertyCarMapper, PropertyCarEntity> implements IPropertyCarService {
    @Autowired
    private PropertyCarMapper propertyCarMapper;

    @Override
    public void deleteById(String id) {
        propertyCarMapper.deleteById(id);
    }

    @Override
    public void updateOne(ElasticsearchCarQO elasticsearchCarQO) {
        propertyCarMapper.updateOne(elasticsearchCarQO);
    }

    @Override
    public void insertOne(PropertyCarEntity entity) {
        propertyCarMapper.insert(entity);
    }


}
