package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;

import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 社区接口
 * @since 2020-11-20 09:06
 **/
public interface ICommunityService extends IService<CommunityEntity> {
	/**
	* @Description: 社区查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	Page<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO);

	/**
	 * 通过社区名称和城市id查询相关的社区数据 服务提供者
	 * @author YuLF
	 * @since  2020/11/23 11:21
	 * @Param  communityEntity 	必要参数实体
	 * @return 返回通过社区名称和城市id查询结果
	 */
    List<CommunityEntity> getCommunityByName(CommunityEntity communityEntity);
	
    /**
    * @Description: 小区定位
     * @Param: [uid, location]
     * @Return: com.jsy.community.entity.CommunityEntity
     * @Author: chq459799974
     * @Date: 2020/11/25
    **/
    CommunityEntity locateCommunity(Long uid,Map<String,Double> location);
}
