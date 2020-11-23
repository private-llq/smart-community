package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseMemberQO;

import java.util.List;

/**
 * <p>
 * 房间成员表 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-23
 */
public interface IHouseMemberService extends IService<HouseMemberEntity> {
	
	/**
	* @Description: 邀请房间成员
	 * @Param: [houseMemberEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	**/
	boolean addHouseMember(HouseMemberEntity houseMemberEntity);
	
	/**
	* @Description: 删除房间成员/撤销邀请
	 * @Param: [ids]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	**/
	boolean deleteHouseMember(List<Long> ids);
	
	/**
	* @Description: 成员确认加入房间
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	**/
	boolean confirmJoin(Long id);
	
	/**
	* @Description: 分页查询成员/查询邀请
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	**/
	Page<HouseMemberEntity> queryHouseMemberPage(BaseQO<HouseMemberQO> baseQO);

}
