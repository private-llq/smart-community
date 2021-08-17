package com.jsy.community.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.UserHouseMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.MembersVO;
import com.jsy.community.vo.UserHouseVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description 业主房屋实现类
 * @since 2020-12-16 11:47
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouseEntity> implements IUserHouseService {

	@Autowired
	private UserHouseMapper userHouseMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private HouseMapper houseMapper;

	@Autowired
	private HouseMemberMapper houseMemberMapper;
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description
	 * @Date 2020/12/15 15:07
	 * @Param [uid, houseEntityList]
	 **/
	@Override
	public Boolean saveUserHouse(String uid, List<UserHouseEntity> houseEntityList) {
		if (!CollectionUtils.isEmpty(houseEntityList)) {
			for (UserHouseEntity houseEntity : houseEntityList) {
				Long communityId = houseEntity.getCommunityId();

				houseEntity.setUid(uid);
				houseEntity.setCommunityId(communityId);
				houseEntity.setHouseId(houseEntity.getHouseId());
				houseEntity.setCheckStatus(0);//待审核
				houseEntity.setId(SnowFlake.nextId());
				
				userHouseMapper.insert(houseEntity);
			}
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 查询用户社区id(房屋已认证的)
	 * @Param: [uid]
	 * @Return: java.util.Set<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	**/
	@Override
	public Set<Long> queryUserHousesOfCommunityIds(String uid){
		return userHouseMapper.queryUserHousesOfCommunityIds(uid);
	}
	
	/**
	 * @Description: 查询业主所有拥有房屋id和相应社区id
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	 **/
	@Override
	public List<UserHouseEntity> queryUserHouseIdsAndCommunityIds(String uid){
		return userHouseMapper.queryUserHouseIdsAndCommunityIds(uid);
	}
	
	/**
	* @Description: 查询业主房屋及所属社区
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.UserHouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	@Override
	public List<UserHouseEntity> queryUserHouses(String uid){
		return userHouseMapper.queryUserHouses(uid);
	}
	
	/**
	 * @Description: 检查用户是否是房主
	 * @Param: [uid, houseId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	 **/
	@Override
	public boolean checkHouseHolder(Long uid, Long houseId) {
		Integer integer = userHouseMapper.selectCount(new QueryWrapper<UserHouseEntity>().eq("uid", uid).eq("house_id", houseId));
		if (integer == 1) {
			return true;
		}
		return false;
	}



	/**
	 * 通过用户id查出用户房屋信息
	 * @param userId 		用户id
	 * @return				返回房屋信息列表
	 */
	@Override
	@Deprecated
	public List<HouseVo> queryUserHouseList(String userId) {
		return userHouseMapper.queryUserHouseList(userId);
	}


	/**
	 * @Description: 查询指定小区内是否有房(是否是业主)
	 * @Param: [uid, communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/23
	 **/
	@Override
	public boolean hasHouse(String uid,Long communityId){
		Integer count = userHouseMapper.selectCount(new QueryWrapper<UserHouseEntity>().eq("uid", uid).eq("community_id", communityId));
		return count > 0;
	}


	/**
	 * 批量新增房屋信息
	 * @author YuLF
	 * @since  2020/12/24 14:07
	 */
	@Override
	public void addHouseBatch(List<UserHouseQo> any, String uid) {
		userHouseMapper.addHouseBatch(any, uid);
	}


	@Override
	public void update(UserHouseQo h, String uid) {
		userHouseMapper.update(h, uid);
	}

	/**
	 * @author: Pipi
	 * @description: 业主解绑房屋
	 * @param: userHouseEntity:
	 * @return: boolean
	 * @date: 2021/6/21 10:33
	 */
	@Override
	public boolean untieHouse(UserHouseEntity userHouseEntity) {
		int delete = userHouseMapper.delete(new QueryWrapper<UserHouseEntity>()
				.eq("house_id", userHouseEntity.getHouseId())
				.eq("uid", userHouseEntity.getUid())
				.eq("deleted", 0)
		);
		return delete == 1 ? true : false;
	}



	/**
	 * @Description: 查询业主当前小区下所有认证的房屋
	 * @author: Hu
	 * @since: 2021/8/17 15:57
	 * @Param: [communityId, userId]
	 * @return: java.util.List<com.jsy.community.vo.property.HouseMemberVO>
	 */
	@Override
	public List<UserHouseVO> selectHouse(Long communityId, String userId) {
		LinkedList<UserHouseVO> linkedList = new LinkedList<>();
		UserHouseVO userHouseVO=null;
		List<UserHouseEntity> list = userHouseMapper.selectList(new QueryWrapper<UserHouseEntity>().eq("community_id", communityId).eq("uid", userId));
		if (list!=null){
			LinkedList<Long> ids = new LinkedList<>();
			for (UserHouseEntity entity : list) {
				ids.add(entity.getId());
			}
			List<HouseEntity> houseEntities = houseMapper.selectBatchIds(ids);
			for (HouseEntity entity : houseEntities) {
				userHouseVO = new UserHouseVO();
				userHouseVO.setHouseId(entity.getId());
				userHouseVO.setHouseSite(entity.getBuilding()+entity.getUnit()+entity.getDoor());
				linkedList.add(userHouseVO);
			}
			return linkedList;
		}
		return null;
	}

	/**
	 * @Description: 房屋认证
	 * @author: Hu
	 * @since: 2021/8/17 15:41
	 * @Param: [communityId, houseId, userId]
	 * @return: void
	 */
	@Override
	public void attestation(Long communityId, Long houseId, String userId) {
		HouseEntity entity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("community_id",communityId).eq("house_id",houseId));
		if (entity!=null){
			UserHouseEntity userHouseEntity = userHouseMapper.selectOne(new QueryWrapper<UserHouseEntity>().eq("house_id", houseId).eq("community_id", communityId));
			if (userHouseEntity!=null){
				throw new ProprietorException("当前房屋已被认证，若非本人认证请联系管理员！");
			}else {
				//房屋认证表里添加数据
				UserHouseEntity houseEntity = new UserHouseEntity();
				houseEntity.setUid(userId);
				houseEntity.setCommunityId(communityId);
				houseEntity.setHouseId(houseId);
				houseEntity.setCheckStatus(1);
				houseEntity.setId(SnowFlake.nextId());
				userHouseMapper.insert(houseEntity);

				//房间成员表里添加数据
				UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));
				HouseMemberEntity memberEntity = new HouseMemberEntity();
				memberEntity.setUid(userId);
				memberEntity.setHouseholderId(userId);
				memberEntity.setCommunityId(communityId);
				memberEntity.setHouseId(houseId);
				memberEntity.setName(userEntity.getRealName());
				memberEntity.setSex(userEntity.getSex());
				memberEntity.setMobile(userEntity.getMobile());
				memberEntity.setRelation(1);
				memberEntity.setIdCard(userEntity.getIdCard());
				memberEntity.setId(SnowFlake.nextId());
				houseMemberMapper.insert(memberEntity);
			}
		}else {
			throw new ProprietorException("当前房屋不存在！");
		}
	}

	/**
	 * @Description: 查询我的房屋列表
	 * @author: Hu
	 * @since: 2021/8/17 15:26
	 * @Param: [communityId, houseId, userId]
	 * @return: com.jsy.community.vo.UserHouseVO
	 */
	@Override
	public UserHouseVO userHouseDetails(Long communityId, Long houseId, String userId) {
		UserHouseEntity entity = userHouseMapper.selectOne(new QueryWrapper<UserHouseEntity>().eq("house_id", houseId).eq("community_id", communityId).eq("uid", userId));
		UserHouseVO houseVO = new UserHouseVO();
		if (entity!=null){
			MembersVO vo = null;
			List<MembersVO> objects = new LinkedList<>();
			UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));
			HouseEntity houseEntity = houseMapper.selectById(houseId);
			houseVO.setName(userEntity.getRealName());
			houseVO.setHouseId(houseEntity.getHouseId());
			houseVO.setHouseSite(houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor());
			List<HouseMemberEntity> list = houseMemberMapper.selectList(new QueryWrapper<HouseMemberEntity>().eq("householder_id", userId).eq("house_id", houseId).eq("community_id", communityId));
			if (list.size()!=0){
				for (HouseMemberEntity houseMemberEntity : list) {
					vo=new MembersVO();
					vo.setRelationText(BusinessEnum.RelationshipEnum.getCode(houseMemberEntity.getRelation()));
					BeanUtils.copyProperties(houseMemberEntity,vo);
					objects.add(vo);
				}
				houseVO.setMembers(objects);
			}
			return houseVO;

		}
		throw new ProprietorException("当前房屋未认证或者不是您的哦！");
	}

	/**
	 * @Description: 查询当前登录人员所有房屋
	 * @author: Hu
	 * @since: 2021/8/16 15:29
	 * @Param: [communityId, uid]
	 * @return: void
	 */
	@Override
	public List<UserHouseEntity> selectUserHouse(Long communityId, String uid) {
		List<UserHouseEntity> list = userHouseMapper.selectList(new QueryWrapper<UserHouseEntity>().eq("community_id", communityId).eq("uid", uid));
		return list;
	}
}
