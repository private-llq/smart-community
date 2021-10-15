package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseMemberQO;
import com.jsy.community.utils.PageInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * 房间成员表 服务类(此套房主邀请加入房间(添加房间成员)的方案暂时搁置)
 * </p>
 *
 * @author qq459799974
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
//	boolean addHouseMember(HouseMemberEntity houseMemberEntity);
	
	/**
	* @Description: 删除房间成员/撤销邀请 by批量id
	 * @Param: [ids]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	**/
//	boolean deleteHouseMember(List<Long> ids);
	
	/**
	* @Description: 删除房间成员/撤销邀请 by房主id
	 * @Param: [houseHolderId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
//	boolean deleteHouseMember(Long houseHolderId);
	
	/**
	* @Description: 成员确认加入房间
	 * @Param: [houseMemberEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/26
	**/
//	boolean confirmJoin(HouseMemberEntity houseMemberEntity);
	
	/**
	* @Description: 分页查询成员/查询邀请
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	**/
//	Page<HouseMemberEntity> queryHouseMemberPage(BaseQO<HouseMemberQO> baseQO);
	
	/**
	* @Description: 检查是否是房主
	 * @Param: [uid, houseId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	boolean checkHouseHolder(Long uid, Long houseId);
	
	/**
	* @Description: 房主亲属 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	PageInfo<HouseMemberEntity> getHouseMembers(BaseQO<HouseMemberQO> baseQO);
	
	/**
	* @Description: 房间ID 查 成员List
	 * @Param: [houseId]
	 * @Return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	List<HouseMemberEntity> queryByHouseId(Long houseId);
	
	/**
	* @Description: id单查
	 * @Param: [id]
	 * @Return: com.jsy.community.entity.HouseMemberEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	HouseMemberEntity queryById(Long id);
	
	/**
	* @Description: ids批量查
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Long,com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	Map<Long,HouseMemberEntity> queryByIdBatch(Set<Long> ids);

	/**
	 * @author: Pipi
	 * @description: 查询社区里所有的成员
	 * @param communityId: 社区ID
	 * @return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @date: 2021/8/19 17:23
	 **/
	List<HouseMemberEntity> queryByCommunityId(Long communityId);

	/**
	 * @author: Pipi
	 * @description: 查询社区所有成员信息 
	 * @param communityId: 社区ID
     * @param uidSet: 用户uid列表
	 * @return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @date: 2021/9/9 15:07
	 **/
	List<HouseMemberEntity> queryByCommunityIdAndUids(Long communityId, Set<String> uidSet);

	/**
	 * @author: Pipi
	 * @description: 新增房屋成员
	 * @param uid: 新增成员uid
     * @param homeOwnerUid: 业主uid
     * @param communityId: 社区ID
     * @param houseId: 房屋ID
     * @param validTime: 租户有效时间,此处传合同截止时间即可
	 * @return: java.lang.Integer
	 * @date: 2021/10/15 17:45
	 **/
	Integer addMember(String uid, String homeOwnerUid, Long communityId, Long houseId, LocalDateTime validTime);
}
