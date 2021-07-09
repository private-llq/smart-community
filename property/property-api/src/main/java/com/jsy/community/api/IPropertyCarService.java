package com.jsy.community.api;

import com.jsy.community.entity.PropertyCarEntity;
import com.jsy.community.qo.property.ElasticsearchCarQO;

import java.util.List;

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

    /**
     * @Description: 批量修改
     * @author: Hu
     * @since: 2021/7/9 12:52
     * @Param:
     * @return:
     */
    void updateList(List<ElasticsearchCarQO> cars);
    /**
     * @Description: 批量新增
     * @author: Hu
     * @since: 2021/7/9 12:52
     * @Param:
     * @return:
     */
    void insertList(List<ElasticsearchCarQO> cars);
}
