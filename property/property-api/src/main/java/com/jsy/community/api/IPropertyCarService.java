package com.jsy.community.api;

import com.jsy.community.entity.PropertyCarEntity;
import com.jsy.community.qo.property.ElasticsearchCarQO;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:48
 **/
public interface IPropertyCarService{

    void insertOne(PropertyCarEntity entity);

    void updateOne(ElasticsearchCarQO elasticsearchCarQO);

    void deleteById(String id);
}
