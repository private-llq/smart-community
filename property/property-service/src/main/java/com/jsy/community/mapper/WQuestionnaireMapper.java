package com.jsy.community.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jsy.community.entity.property.WQuestionnaire;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Arli
 * @since 2021-08-17
 */
public interface WQuestionnaireMapper extends BaseMapper<WQuestionnaire> {

@Select("select id FROM t_house  h WHERE h.id=(select hou.pid  FROM t_user_house as uhou JOIN t_house as hou on uhou.house_id=hou.id and uhou.uid=#{userId})")
    String selectbBuildingId(@Param("userId") String userId);
}
