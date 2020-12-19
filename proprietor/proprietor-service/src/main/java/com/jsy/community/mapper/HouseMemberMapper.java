package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseMemberEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 房间成员表 Mapper 接口
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-23
 */
public interface HouseMemberMapper extends BaseMapper<HouseMemberEntity> {

	@Update("update t_house_member set is_confirm = 1 where id = #{id}")
	int confirmJoin(@Param("id")Long id);
}
