package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityHardWareEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author chq459799974
 * @description 社区硬件Mapper
 * @since 2021-04-25 13:43
 **/
public interface CommunityHardWareMapper extends BaseMapper<CommunityHardWareEntity> {
	
	/**
	* @Description: 查询硬件所属社区
	 * @Param: [id, type]
	 * @Return: java.lang.Long
	 * @Author: chq459799974
	 * @Date: 2021/4/25
	**/
	@Select("select community_id from t_community_hardware where hardware_id = #{id} and hardware_type = #{type}")
	Long queryCommunityIdByHardWareIdAndType(@Param("id")String id,@Param("type")Integer type);
}
