package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;

import java.util.Map;

/**
 * @author chq459799974
 * @description 社区接口
 * @since 2020-11-20 09:06
 **/
public interface ICommunityService extends IService<CommunityEntity> {
	/**
	* @Description: 社区新增
	 * @Param: [communityEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	boolean addCommunity(CommunityEntity communityEntity);
	/**
	* @Description: 社区删除
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	boolean deleteCommunity(Long id);
	/**
	* @Description: 社区修改
	 * @Param: [communityEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	Map<String,Object> updateCommunity(CommunityEntity communityEntity);
	/**
	* @Description: 社区查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	Page<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO);
}
