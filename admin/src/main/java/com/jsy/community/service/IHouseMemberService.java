package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * 大后台住户信息Service
 * @author DKS
 * @since 2021-10-22
 */
public interface IHouseMemberService extends IService<HouseMemberEntity> {
	
	/**
	* @Description: 【住户】条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: DKS
	 * @Date: 2021/10/22
	**/
	PageInfo<HouseMemberEntity> queryHouseMember(BaseQO<HouseMemberQO> baseQO);
	
	/**
	 * @Description: 查询住户
	 * @Param: HouseEntity
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: DKS
	 * @Date: 2021/10/22 16:38
	 **/
	List<HouseMemberEntity> queryExportHouseMemberExcel(HouseMemberQO houseMemberQO);
}
