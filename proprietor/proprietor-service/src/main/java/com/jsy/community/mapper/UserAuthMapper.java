package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserAuthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthEntity> {
	UserAuthEntity queryUserByField(@Param("account") String account, @Param("field") String field);
	
	String queryUserIdByMobile(@Param("mobile") String mobile);
	
	Long checkUserExists(@Param("account") String account, @Param("field") String field);
	
	@Update("update t_user_auth set mobile = #{newMobile} where uid = #{uid}")
	int changeMobile(@Param("newMobile")String newMobile, @Param("uid")String uid);
}
