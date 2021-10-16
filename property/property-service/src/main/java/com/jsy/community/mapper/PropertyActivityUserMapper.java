package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.ActivityUserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * @program: com.jsy.community
 * @description: 报名人员
 * @author: Hu
 * @create: 2021-09-23 10:37
 **/
public interface PropertyActivityUserMapper extends BaseMapper<ActivityUserEntity> {
    /**
     * @Description: 查询所有报名人员
     * @author: Hu
     * @since: 2021/9/28 11:06
     * @Param:
     * @return:
     */
    @Select("SELECT uid FROM t_activity_user WHERE deleted=0 AND (activity_id = #{dataId})")
    Set<String> selectUid(@Param("dataId") String dataId);
}
