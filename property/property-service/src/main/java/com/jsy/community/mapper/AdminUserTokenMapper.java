package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminUserTokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Token
 */
@Mapper
public interface AdminUserTokenMapper extends BaseMapper<AdminUserTokenEntity> {
	
//	AdminUserTokenEntity queryByToken(String token);
	
}
