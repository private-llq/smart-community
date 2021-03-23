package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.OrganizationEntity;
import com.jsy.community.vo.TreeCommunityVO;

import java.util.List;
import java.util.Map;

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
	* @Description: 根据idList批量获取对应组织机构名称
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Long,java.util.Map<java.lang.Long,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/3/16
	**/
	Map<Long, Map<Long, Object>> queryOrganizationNameByIdBatch(List<Long> ids);
	
	/**
	* @Description: 社区组织机构是否存在
	 * @Param: [orgId, communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	boolean isExists(Long orgId, Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改组织机构
	 * @Date 2021/3/16 9:23
	 * @Param [organization]
	 **/
	void updateOrganization(OrganizationEntity organization);
	
}
