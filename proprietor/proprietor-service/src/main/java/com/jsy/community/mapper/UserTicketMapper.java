package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserTicketEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author chq459799974
 * @description 用户全平台券(现金券等) Mapper
 * @since 2021-01-28 13:59
 **/
public interface UserTicketMapper extends BaseMapper<UserTicketEntity> {
	
	/**
	* @Description: 统计用户可用券张数
	 * @Param: [id]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Select("select count(1) from t_user_ticket where uid = #{uid}" +
		" and UNIX_TIMESTAMP(expire_time) >= UNIX_TIMESTAMP(now())")
	Integer countAvailableTickets(@Param("uid")String uid);
	
	/**
	* @Description: 检查券是否过期
	 * @Param: [id]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Select("select * from t_user_ticket where id = #{id}" +
		" and UNIX_TIMESTAMP(expire_time) >= UNIX_TIMESTAMP(now())")
	UserTicketEntity checkExpired(@Param("id")Long id);
	
	/**
	* @Description: 使用
	 * @Param: [id, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Update("update t_user_ticket set status = 1,update_time = now() where id = #{id} and uid = #{uid} and status != 1")
	int useTicket(@Param("id")Long id, @Param("uid")String uid);
	
	/**
	* @Description: 退回
	 * @Param: [id, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Update("update t_user_ticket set status = 0,update_time = now() where id = #{id} and uid = #{uid}")
	int rollbackTicket(@Param("id")Long id, @Param("uid")String uid);
	
}
