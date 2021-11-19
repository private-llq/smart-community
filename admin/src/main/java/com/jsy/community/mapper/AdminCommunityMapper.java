package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminCommunityEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author chq459799974
 * @description 管理员社区权限Mapper
 * @since 2021-07-22 09:40
 **/
@Mapper
public interface AdminCommunityMapper extends BaseMapper<AdminCommunityEntity> {
	
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
	void addAdminCommunityBatch(@Param("ids") Set<String> menuIdsSet, @Param("uid") String uid);
}
