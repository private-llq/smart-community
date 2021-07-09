package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PropertyCarEntity;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 15:49
 **/
@Mapper
public interface PropertyCarMapper extends BaseMapper<PropertyCarEntity> {

    void updateOne(ElasticsearchCarQO elasticsearchCarQO);


    /**
     * @Description: 批量修改
     * @author: Hu
     * @since: 2021/7/9 12:57
     * @Param:
     * @return:
     */
    void updateMap(@Param("map")Map<Long, ElasticsearchCarQO> map);

    /**
     * @Description: 批量新增
     * @author: Hu
     * @since: 2021/7/9 12:57
     * @Param:
     * @return:
     */
    void insertList(List<ElasticsearchCarQO> cars);
}
