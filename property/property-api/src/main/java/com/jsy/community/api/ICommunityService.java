package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.CommunitySurveyEntity;
import com.jsy.community.entity.property.ConsoleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.property.PropertyCommunityListVO;

import java.util.List;
import java.util.Map;

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
	
	/**
	* @Description: 查询社区模式
	 * @Param: [id]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/1/21
	**/
	Integer getCommunityMode(Long id);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 分布式事物——添加社区测试
	 * @Date 2020/12/23 15:25
	 * @Param []
	 **/
	void addCommunityEntity();
	
	/**
	 * @return com.jsy.community.entity.CommunityEntity
	 * @Author lihao
	 * @Description 根据小区id查询小区名称
	 * @Date 2021/1/27 14:39
	 * @Param [communityId]
	 **/
	CommunityEntity getCommunityNameById(Long communityId);
	
	/**
	* @Description: ids批量查小区
	 * @Param: [idList]
	 * @Return: java.util.List<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	List<CommunityEntity> queryCommunityBatch(List<Long> idList);
	
	/**
	* @Description: id单查小区
	 * @Param: [communityId]
	 * @Return: com.jsy.community.entity.CommunityEntity
	 * @Author: chq459799974
	 * @Date: 2021-07-29
	**/
	CommunityEntity queryDetails(Long communityId);
	
	/**
	 * @return java.util.Map<java.lang.String,java.lang.Object>
	 * @Author lihao
	 * @Description 查询电子地图
	 * @Date 2021/4/16 17:07
	 * @Param []
	 **/
	Map<String, Object> getElectronicMap(Long communityId);
	
	/**
	* @Description: 查询所有小区id
	 * @Param: []
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/6/26
	**/
	List<Long> queryAllCommunityIdList();

	/**
	 * @author: Pipi
	 * @description: 物业端新增社区
	 * @param communityEntity:
	 * @return: java.lang.Integer
	 * @date: 2021/7/21 17:57
	 **/
	Long addCommunity(CommunityEntity communityEntity, String uid);

	/**
	 * @author: Pipi
	 * @description: 分页查询小区列表
	 * @param baseQO: 查询条件
     * @param communityIds: 登录用户uid
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.vo.property.PropertyCommunityListVO>
	 * @date: 2021/7/22 11:46
	 **/
	PageInfo<PropertyCommunityListVO> queryPropertyCommunityList(BaseQO<CommunityEntity> baseQO, List<String> communityIds);

	/**
	 * @author: Pipi
	 * @description: 物业端更新社区信息
	 * @param communityEntity:
	 * @return: java.lang.Integer
	 * @date: 2021/7/22 18:04
	 **/
	Integer updateCommunity(CommunityEntity communityEntity);

	/**
	 * @author: DKS
	 * @description: 获取小区概况
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/24 11:52
	 **/
	CommunitySurveyEntity getCommunitySurvey(Integer month, Long adminCommunityId);

	/**
	 * @author: DKS
	 * @description: 获取物业控制台
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/25 13:45
	 **/
	ConsoleEntity getPropertySurvey(Long companyId, List<String> communityIdList);
	
	/**
	 * @author: DKS
	 * @description: 获取物业控制台里的收费统计
	 * @param communityId:
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/31 17:09
	 **/
	ConsoleEntity getPropertySurveyOrderFrom(Integer year, Long communityId);
	
	/**
	 * @author: DKS
	 * @description: 物业端-系统设置-短信群发
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/30 17:22
	 **/
	Boolean groupSendSMS(List<String> communityIdList, String content, boolean isDistinct, String taskTime, int number, Long companyId);
}
