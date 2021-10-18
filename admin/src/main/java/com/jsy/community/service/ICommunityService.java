package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.utils.PageInfo;

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
	 * @Author: DKS
	 * @Date: 2021/10/18
	**/
	boolean addCommunity(CommunityEntity communityEntity);
	
	/**
	 * @description: 物业端更新社区信息
	 * @param communityEntity:
	 * @return: java.lang.Integer
	 * @Author: DKS
	 * @Date: 2021/10/18
	 **/
	Integer updateCommunity(CommunityEntity communityEntity);
	
	/**
	* @Description: 社区查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CommunityEntity>
	 * @Author: DKS
	 * @Date: 2021/10/18
	**/
	PageInfo<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO);
	
	/**
	 * @Description: 删除角色
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/18
	 **/
	boolean delCommunity(Long id);
}
