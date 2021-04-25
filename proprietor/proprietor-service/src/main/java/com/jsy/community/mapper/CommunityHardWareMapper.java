package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CommunityHardWareEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
//	Long queryCommunityIdByHardWareIdAndType(@Param("id")String id,@Param("type")Integer type);
	List<Long> queryCommunityIdByHardWareIdAndType(@Param("id")String id, @Param("type")Integer type);//测试用
	
	/**
	* @Description: 查询小区是否有某种类型的设备
	 * @Param: [communityId, type]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/4/25
	**/
	@Select("select count(0) from t_community_hardware where community_id = #{communityId} and hardware_type = #{type}")
	Integer countCommunityHardWare(@Param("communityId")Long communityId,@Param("type")Integer type);
	
}
