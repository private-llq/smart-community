package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;

import java.util.List;

/**
 * <p>
 * 社区 服务类
 * </p>
 *
 * @author YuLF
 * @since 2020-11-25
 */
public interface ICommunityService extends IService<CommunityEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.CommunityEntity>
	 * @Author lihao
	 * @Description 根据社区名称或详细地址查询社区
	 * @Date 2020/12/21 16:28
	 * @Param [communityName]
	 **/
	List<CommunityEntity> listCommunityByName(String query,Integer areaId);
	
	/**  
	 * @return java.util.List<com.jsy.community.entity.CommunityEntity>
	 * @Author lihao
	 * @Description 根据区域id查询
	 * @Date 2020/12/21 17:22
	 * @Param [areaId] 
	 **/
	List<CommunityEntity> listCommunityByAreaId(Long areaId);
	
}
