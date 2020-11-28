package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;


/**
 * YuLF
 * 2020-11-28
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	UserEntity queryUserInfoByUid(Long uid);
	
	@Update("update t_user set householder_id = #{householderId} where id = #{uid}")
	int setUserBelongTo(Long householderId,Long uid);

	/**
	 * 【用户】业主信息更新接口、
	 * @param userEntity 参数实体
	 * @return			 返回更新行数
	 */
    int proprietorUpdate(UserEntity userEntity);
}
