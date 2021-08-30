package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	
	/**
	* @Description: uids批量查询 uid-姓名映射
	 * @Param: [uids]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	Map<String, Map<String,String>> queryNameByUidBatch(Collection<String> uids);
	
	/**
	 * @Description: 在固定的uid范围内筛选姓名满足模糊匹配条件的uid
	 * @Param: [uids, nameLike]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	 **/
	List<String> queryUidOfNameLike(List<String> uids, String nameLike);

	UserEntity queryUserDetailByUid(String uid);

	/**
	 * @author: Pipi
	 * @description: 查询社区未同步的人脸信息
	 * @param communityId: 社区ID
	 * @param facilityId: 设备序列号
	 * @return: java.util.List<com.jsy.community.entity.UserEntity>
	 * @date: 2021/8/19 15:41
	 **/
	List<UserEntity> queryUnsyncFaceUrlList(Long communityId, String facilityId);
}
