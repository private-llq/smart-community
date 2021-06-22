package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserHouseEntity;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 业主房屋认证 Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2020-11-25
 */
public interface UserHouseMapper extends BaseMapper<UserHouseEntity> {
	
	/**
	 * @return java.util.List<java.lang.String>
	 * @Author 91李寻欢
	 * @Description 根据id查询已认证用户id
	 * @Date 2021/5/10 9:45
	 * @Param [communityId]
	 **/
	Set<String> listAuthUserId(Long communityId);
}
