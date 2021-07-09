package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.RelationCarEntity;
import com.jsy.community.qo.proprietor.RelationCarsQO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-23 14:52
 **/
@Mapper
public interface RelationCarMapper extends BaseMapper<RelationCarEntity> {
    /**
     * @Description: 批量新增
     * @author: Hu
     * @since: 2021/7/9 12:51
     * @Param:
     * @return:
     */
    void insertList(List<RelationCarsQO> insert);

    /**
     * @Description: 批量修改
     * @author: Hu
     * @since: 2021/7/9 12:51
     * @Param:
     * @return:
     */
    void updateList(HashMap<Long, RelationCarsQO> map);
}
