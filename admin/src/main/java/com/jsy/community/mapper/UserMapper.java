package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ling
 * @since 2020-11-19 16:50
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	/**
	 * @Description: 根据uid查询用户
	 * @author: DKS
	 * @since: 2021/10/29 16:05
	 * @Param: uid
	 * @return: com.jsy.community.entity.UserEntity
	 */
	UserEntity getUserMobileByUid(String uid);
}
