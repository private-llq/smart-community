package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

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
	
	/**
	 * @return java.util.List<com.jsy.community.entity.UserEntity>
	 * @Author DKS
	 * @Description 批量查询业主数据
	 * @Date 2021/11/08 10:42
	 * @Param [ids]
	 **/
	List<UserEntity> listAuthUserInfo(Collection<String> ids);
}
