package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserAuthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthEntity> {
	UserAuthEntity queryUserByField(@Param("account") String account, @Param("field") String field);
	
	String queryUserIdByMobile(@Param("mobile") String mobile);
	
	Long checkUserExists(@Param("account") String account, @Param("field") String field);
}
