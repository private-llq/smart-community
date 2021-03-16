package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.OrganizationEntity;
import com.jsy.community.vo.TreeCommunityVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lihao
 * @since 2021-03-15
 */
public interface IOrganizationService extends IService<OrganizationEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.vo.OrganizationVO>
	 * @Author lihao
	 * @Description 树形查询所有组织
	 * @Date 2021/3/15 18:05
	 * @Param [communityId]
	 **/
	TreeCommunityVO listOrganization(Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 新增组织机构
	 * @Date 2021/3/15 18:58
	 * @Param [organizationEntity]
	 **/
	void addOrganization(OrganizationEntity organizationEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 删除组织机构
	 * @Date 2021/3/16 9:02
	 * @Param [id, communityId]
	 **/
	void deleteOrganization(Long id, Long communityId);
	
	/**
	 * @return com.jsy.community.entity.OrganizationEntity
	 * @Author lihao
	 * @Description 根据id查询组织机构
	 * @Date 2021/3/16 9:09
	 * @Param [id, communityId]
	 **/
	OrganizationEntity getOrganizationById(Long id, Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改组织机构
	 * @Date 2021/3/16 9:23
	 * @Param [organization]
	 **/
	void updateOrganization(OrganizationEntity organization);
	
}
