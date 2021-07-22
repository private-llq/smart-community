package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminCommunityEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description 管理员社区权限Mapper
 * @since 2021-07-22 09:40
 **/
public interface AdminCommunityMapper extends BaseMapper<AdminCommunityEntity> {
	
	/**
	* @Description: 管理员社区权限id列表查询
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	**/
	@Select("select community_id from t_admin_community where uid = #{uid}")
	List<Long> queryAdminCommunityIdListByUid(String uid);
	
	/**
	* @Description: 清空用户小区权限
	 * @Param: [uid]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	**/
	@Delete("delete from t_admin_community where uid = #{uid}")
	void clearAdminCommunityByUid(String uid);
	
	/**
	* @Description: 批量添加用户社区权限
	 * @Param: [menuIdsSet, uid]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	**/
	void addAdminCommunityBatch(@Param("ids") Set<Long> menuIdsSet, @Param("uid") String uid);
}
