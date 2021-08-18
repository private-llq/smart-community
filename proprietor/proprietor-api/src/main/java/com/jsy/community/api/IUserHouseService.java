package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.MembersVO;
import com.jsy.community.vo.UserHouseVO;

import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description 业主房屋
 * @since 2020-12-16 11:48
 **/
public interface IUserHouseService extends IService<UserHouseEntity> {
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description
	 * @Date 2020/12/15 15:07
	 * @Param [uid, houseEntityList]
	 **/
	Boolean saveUserHouse(String uid, List<UserHouseEntity> houseEntityList);
	
	/**
	* @Description: 查询用户社区id(房屋已认证的)
	 * @Param: [uid]
	 * @Return: java.util.Set<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	**/
	Set<Long> queryUserHousesOfCommunityIds(String uid);
	
	/**
	* @Description: 查询业主所有拥有房屋id和相应社区id(房屋已认证的)
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	List<UserHouseEntity> queryUserHouseIdsAndCommunityIds(String uid);
	
	/**
	* @Description: 查询业主房屋及所属社区(房屋已认证的)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.UserHouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	List<UserHouseEntity> queryUserHouses(String uid);
	
	/**
	 * @Description: 检查用户是否是房主
	 * @Param: [uid, houseId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	 **/
	boolean checkHouseHolder(Long uid, Long houseId);



	/**
	 * 通过用户id查出用户房屋信息
	 * @author YuLF
	 * @since  2020/12/18 14:18
	 * @param userId 		用户id
	 * @return				返回房屋信息列表
	 */
	List<HouseVo> queryUserHouseList(String userId);
	
	/**
	 * @Description: 查询指定小区内是否有房(是否是业主)
	 * @Param: [uid, communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/23
	 **/
	boolean hasHouse(String uid,Long communityId);

	/**
	 * 批量新增房屋信息
	 * @param any		房屋信息
	 * @param uid 		用户id
	 * @author YuLF
	 * @since  2020/12/24 14:07
	 */
    void addHouseBatch(List<UserHouseQo> any, String uid);


	/**
	 * 更新房屋信息
	 * @param h 	房屋信息
	 * @param uid	用户id
	 */
	void update(UserHouseQo h, String uid);

	/**
	 * @author: Pipi
	 * @description: 业主解绑房屋
	 * @param: userHouseEntity:
	 * @return: boolean
	 * @date: 2021/6/21 10:33
	 **/
	boolean untieHouse(UserHouseEntity userHouseEntity);

	/**
	 * @Description: 查询当前登录人员所认证的所有房屋
	 * @author: Hu
	 * @since: 2021/8/16 15:29
	 * @Param:
	 * @return:
	 */
	List<UserHouseEntity> selectUserHouse(Long communityId, String uid);

	/**
	 * @Description: 查询房间成员信息
	 * @author: Hu
	 * @since: 2021/8/17 15:10
	 * @Param:
	 * @return:
	 */
    UserHouseVO userHouseDetails(Long communityId, Long houseId, String userId);

    /**
     * @Description: 房屋认证
     * @author: Hu
     * @since: 2021/8/17 15:40
     * @Param:
     * @return:
     */
	void attestation(Long communityId, Long houseId, String userId);

	/**
	 * @Description: 查询业主当前小区下所有认证的房屋
	 * @author: Hu
	 * @since: 2021/8/17 15:57
	 * @Param:
	 * @return:
	 */
	List<UserHouseVO> selectHouse(Long communityId, String userId);

	/**
	 * @Description: 家属或者租客更新
	 * @author: Hu
	 * @since: 2021/8/17 17:31
	 * @Param:
	 * @return:
	 */
	void membersUpdate(List<MembersVO> members, String userId);
}
