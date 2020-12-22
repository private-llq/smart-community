package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserEntity;

/**
 * <p>
 * 业主 服务类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-25
 */
public interface IUserService extends IService<UserEntity> {
	
	/**
	 * @return com.jsy.community.entity.UserEntity
	 * @Author lihao
	 * @Description 根据用户uid查询用户信息
	 * @Date 2020/12/22 10:26
	 * @Param [uid]
	 **/
	UserEntity selectOne(String uid);
}
