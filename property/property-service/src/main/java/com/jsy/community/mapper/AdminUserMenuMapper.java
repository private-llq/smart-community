package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminUserMenuEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chq459799974
 * @description 用户-菜单 Mapper
 * @since 2021-03-25 16:59
 **/
public interface AdminUserMenuMapper extends BaseMapper<AdminUserMenuEntity> {
	
	@Select("select menu_id from t_admin_user_menu where uid = #{uid} and deleted = 0")
	List<Long> queryUserMenu(String uid);
	
	@Select("select menu_id from t_admin_user_menu where uid = #{uid} and deleted = 0")
	List<String> queryUserMenuIdList(String uid);
	
}
