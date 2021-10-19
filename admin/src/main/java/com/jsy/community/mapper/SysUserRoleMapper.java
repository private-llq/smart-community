package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.sys.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: DKS
 * @Description: 用户角色关联表Mapper
 * @Date: 2021/10/12 17:01
 * @Version: 1.0
 **/
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRoleEntity> {
	/**
	 * @Description: 编辑操作员角色
	 * @Param: [entity]
	 * @Return: int
	 * @Author: DKS
	 * @Date: 2021/10/19
	 **/
	int updateOperatorRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
