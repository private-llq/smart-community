package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserAuthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthEntity> {
	UserAuthEntity queryUserByField(@Param("account") String account, @Param("field") String field);
	
	String queryUserIdByMobile(@Param("mobile") String mobile);
	
	Long checkUserExists(@Param("account") String account, @Param("field") String field);
	
	/**
	* @Description: 更换手机号
	 * @Param: [newMobile, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/29
	**/
	@Update("update t_user_auth set mobile = #{newMobile} where uid = #{uid}")
	int changeMobile(@Param("newMobile")String newMobile, @Param("uid")String uid);
	
	/**
	* @Description: 账号安全状态查询
	 * @Param: [uid]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/3/29
	**/
	@Select("select count(pay_password) as hasPayPassword,count(password) as hasPassword,mobile from t_user_auth where uid = #{uid}")
	Map<String,String> querySafeStatus(String uid);
}
