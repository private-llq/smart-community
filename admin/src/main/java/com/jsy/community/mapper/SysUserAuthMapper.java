package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.sys.SysUserAuthEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author DKS
 * @description 用户登录账户Mapper t_sys_user_auth表
 * @since 2021-10-12 16:24
 **/
public interface SysUserAuthMapper extends BaseMapper<SysUserAuthEntity> {
	
	/**
	* @Description: 创建登录用户
	 * @Param: [en]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Insert("insert into t_sys_user_auth (id,mobile,password,salt,create_by,create_time)\n" +
		"values(#{en.id},#{en.mobile},#{en.password},#{en.salt},#{en.createBy},now());")
	void createLoginUser(@Param("en") SysUserAuthEntity en);
	
	/**
	 * @Description: 更换手机号
	 * @Param: [newMobile, oldMobile]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	 **/
	@Update("update t_sys_user_auth set mobile = #{newMobile} where mobile = #{oldMobile}")
	int changeMobile(@Param("newMobile") String newMobile, @Param("oldMobile") String oldMobile);
	
}
