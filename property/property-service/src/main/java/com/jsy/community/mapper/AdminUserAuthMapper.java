package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminUserAuthEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author chq459799974
 * @description 用户登录账户Mapper t_admin_user_auth表
 * @since 2021-03-24 17:42
 **/
public interface AdminUserAuthMapper extends BaseMapper<AdminUserAuthEntity> {
	
	/**
	* @Description: 创建登录用户
	 * @Param: [en]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Insert("insert ignore into t_admin_user_auth (id,mobile,password,salt,create_by,create_time)\n" +
		"values(#{en.id},#{en.mobile},#{en.password},#{en.salt},#{en.createBy},now());")
	void createLoginUser(@Param("en") AdminUserAuthEntity en);
	
}
