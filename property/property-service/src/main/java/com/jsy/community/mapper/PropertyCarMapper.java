package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PropertyCarEntity;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:49
 **/
@Mapper
public interface PropertyCarMapper extends BaseMapper<PropertyCarEntity> {

    void updateOne(ElasticsearchCarQO elasticsearchCarQO);
}
