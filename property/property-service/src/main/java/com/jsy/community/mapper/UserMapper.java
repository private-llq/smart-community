package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;

import java.util.List;

/**
 * <p>
 * 业主 Mapper 接口
 * </p>
 *
 * @author YuLF
 * @since 2020-11-25
 */
public interface UserMapper extends BaseMapper<UserEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.UserEntity>
	 * @Author 91李寻欢
	 * @Description 批量查询业主数据
	 * @Date 2021/5/10 9:49
	 * @Param [ids]
	 **/
	List<UserEntity> listAuthUserInfo(List<String> ids);
}
