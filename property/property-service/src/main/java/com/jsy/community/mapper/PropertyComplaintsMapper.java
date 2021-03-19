package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.PropertyComplaintsEntity;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-19 13:36
 **/
@Mapper
public interface PropertyComplaintsMapper extends BaseMapper<PropertyComplaintsEntity> {
    /**
     * @Description: 分页查询物业投诉接口
     * @author: Hu
     * @since: 2021/3/19 14:05
     * @Param:
     * @return:
     */
    Page findList(@Param("propertyComplaintsEntityPage")Page<PropertyComplaintsEntity> propertyComplaintsEntityPage, @Param("query") PropertyComplaintsQO query);
}
