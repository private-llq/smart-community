package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.MembersQO;
import com.jsy.community.qo.UserHouseQO;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.utils.PushInfoUtil;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.HouseMembersQO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.MembersVO;
import com.jsy.community.vo.UserHouseVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
import com.zhsj.base.api.entity.RealInfoDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chq459799974
 * @description 业主房屋实现类
 * @since 2020-12-16 11:47
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouseEntity> implements IUserHouseService {

	private final String MEMBERKEY = "pushInFormMember:";
	@Autowired
	private UserHouseMapper userHouseMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserIMMapper userIMMapper;

	@Autowired
	private HouseMapper houseMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	@Value("${H5Url}")
	private String H5Url;

	@Autowired
	private CommunityMapper communityMapper;

	@Autowired
	private HouseMemberMapper houseMemberMapper;

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ProprietorUserService userService;

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IProprietorService proprietorService;

	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IHouseInfoService houseInfoService;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
	private IBaseUserInfoRpcService userInfoRpcService;

	@DubboReference(version = com.zhsj.im.chat.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.im.chat.api.constant.RpcConst.Rpc.Group.GROUP_IM_CHAT, check=false)
	private IImChatPublicPushRpcService iImChatPublicPushRpcService;

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

	@Override
	public HouseMemberEntity selectByUser(Long communityId, Long houseId, String userId) {
		return houseMemberMapper.selectOne(new QueryWrapper<HouseMemberEntity>().eq("community_id",communityId).eq("house_id",houseId).eq("uid",userId));
	}

	/**
	 * @Description: 修改房屋认证表数据
	 * @author: Hu
	 * @since: 2021/10/16 17:28
	 * @Param:
	 * @return:
	 */
	@Override
	public void updateMobile(Set<Long> ids, String uuid) {
		userHouseMapper.updateMobile(ids,uuid);
	}

	/**
	 * @Description: 根据用户手机更新成员表uid
	 * @author: Hu
	 * @since: 2021/10/12 14:52
	 * @Param:
	 * @return:
	 */
	@Override
	@Transactional
	public void updateMobileUser(String uid) {
		// UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", uid));
		UserDetail userDetail = userInfoRpcService.getUserDetail(uid);
		Set<Long> ids = new HashSet<>();
		if (userDetail != null) {
			List<HouseMemberEntity> entityList = houseMemberMapper.selectList(new QueryWrapper<HouseMemberEntity>().eq("mobile", userDetail.getPhone()));
			if (entityList.size()!=0){
				for (HouseMemberEntity houseMemberEntity : entityList) {
					ids.add(houseMemberEntity.getHouseId());
				}

				//给成员表添加uid
				houseMemberMapper.updateByUid(uid,userDetail.getPhone());
				//给房屋认证表添加用户uid
				userHouseMapper.updateByUid(ids,uid);
			}
		}
	}

	/**
	 * @Description: 查询用户社区id(家属租客的)
	 * @Param: [uid]
	 * @Return: java.util.Set<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	 **/
	@Override
	public Set<Long> queryRelationHousesOfCommunityIds(String uid) {
		return userHouseMapper.queryRelationHousesOfCommunityIds(uid);
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
	 * @Description: 业主家属删除接口
	 * @author: Hu
	 * @since: 2021/8/18 9:07
	 * @Param: [ids, userId]
	 * @return: void
	 */
	@Override
	public void membersDelete(String ids, String userId) {
		List<String> list = Arrays.asList(ids.split(","));
		for (String s : list) {
			HouseMemberEntity entity = houseMemberMapper.selectById(s);
			UserImVo userIm = userInfoRpcService.getEHomeUserIm(entity.getUid());
			CommunityEntity communityEntity = communityMapper.selectById(entity.getCommunityId());
			HouseEntity houseEntity = houseMapper.selectById(entity.getHouseId());
			//移除人
			UserDetail userDetail = userInfoRpcService.getUserDetail(userId);

			Map<String, Object> map = new HashMap<>();
			map.put("type",8);
			//推送消息
			PushInfoUtil.PushPublicTextMsg(
					iImChatPublicPushRpcService,
					userIm.getImId(),
					"房屋管理",
					"您有房屋最新消息了！",
					null,
					"尊敬的用户您好，\n" +
							"用户"+userDetail.getNickName()+"已房东的身份移除你在"+communityEntity.getName()+houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor()+BusinessEnum.RelationshipEnum.getCodeName(entity.getRelation())+"身份，如已知晓，请忽略。",
					map,
					BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());

		}
		houseMemberMapper.deleteBatchIds(list);
	}

	/**
	 * @Description: 家属或者租客更新
	 * @author: Hu
	 * @since: 2021/8/17 17:31
	 * @Param: members,userId
	 * @return: void
	 */
	@Override
	@Transactional
	public String membersSave(MembersQO membersQO, String userId) {

		HouseMemberEntity entity = houseMemberMapper.selectOne(new QueryWrapper<HouseMemberEntity>().eq("house_id", membersQO.getHouseId()).eq("mobile", membersQO.getMobile()).eq("relation", 1));
		if (entity!=null){
			throw new ProprietorException("您现在添加的人是当前房间的业主哦！");
		}
		HouseMemberEntity memberEntity = houseMemberMapper.selectOne(new QueryWrapper<HouseMemberEntity>().eq("house_id", membersQO.getHouseId()).eq("mobile", membersQO.getMobile()).eq("relation",membersQO.getRelation()));
		if (memberEntity!=null){
			throw new ProprietorException("当前成员以添加，请勿重复添加！");
		}
		Object obj = redisTemplate.opsForValue().get(MEMBERKEY + membersQO.getMobile()+membersQO.getHouseId());
		if (obj!=null){
			throw new ProprietorException("请勿重复提交，请一个小时后在重新添加！");
		}
		//社区
		CommunityEntity communityEntity = communityMapper.selectById(membersQO.getCommunityId());
		//房间
		HouseEntity houseEntity = houseMapper.selectById(membersQO.getHouseId());
		//被添加的用户
		UserDetail userEntity = userInfoRpcService.getUserDetailByPhone(membersQO.getMobile());
		//业主
		UserDetail user = userInfoRpcService.getUserDetail(userId);

		if (userEntity != null) {

			UserImVo userIm = userInfoRpcService.getEHomeUserIm(userEntity.getAccount());

			String relation = membersQO.getRelation()==6?"亲属":membersQO.getRelation()==7?"租客":"";
			String str = membersQO.getRelation()==6?"点击此处确定关系。":"点击此处确定入驻。";
			String title="房屋绑定"+relation+"!";
			String desc="房屋绑定"+relation  +"\n"+
					"我是"+user.getNickName()+"，邀请您("+membersQO.getName()+")加入"+communityEntity.getName()+houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor()+",成为我的"+relation+"成员，"+ str;

			HouseInfoEntity houseInfoEntity = new HouseInfoEntity();
			houseInfoEntity.setId(String.valueOf(SnowFlake.nextId()));
			houseInfoEntity.setMobile(membersQO.getMobile());
			houseInfoEntity.setTitle(title);
			houseInfoEntity.setContent(desc);
			houseInfoEntity.setCreateTime(LocalDateTime.now());
			houseInfoEntity.setOverdueTime(LocalDateTime.now().plusHours(1));
			houseInfoEntity.setYzUid(userId);
			houseInfoEntity.setYhUid(userEntity.getAccount());
			houseInfoEntity.setHouseId(String.valueOf(membersQO.getHouseId()));
			houseInfoService.saveOne(houseInfoEntity);

			Map<String, Object> map = new HashMap<>();
			map.put("type",9);
			//推送消息
			PushInfoUtil.PushPublicMsg(
					iImChatPublicPushRpcService,
					userIm.getImId(),
					"房屋管理",
					title,
					H5Url+"?id="+houseInfoEntity.getId()+"&mobile="+membersQO.getMobile(),
					desc,
					map,
					BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());
			redisTemplate.opsForValue().set(MEMBERKEY+membersQO.getMobile()+membersQO.getHouseId(), JSON.toJSONString(membersQO),1, TimeUnit.HOURS);
			return H5Url+"?id="+houseInfoEntity.getId()+"&mobile="+membersQO.getMobile();

			//添加成员表数据
//			HouseMemberEntity entity = new HouseMemberEntity();
//			BeanUtils.copyProperties(membersQO,entity);
//			entity.setId(SnowFlake.nextId());
//			entity.setHouseholderId(userId);
//			houseMemberMapper.insert(entity);
		} else {
			String relation = membersQO.getRelation()==6?"亲属":membersQO.getRelation()==7?"租客":"";
			String str = membersQO.getRelation()==6?"点击此处确定关系。":"点击此处确定入驻。";
			String title="房屋绑定"+relation+"!";
			String desc="房屋绑定"+relation  +"\n"+
					"我是"+user.getNickName()+"，邀请您("+membersQO.getName()+")加入"+communityEntity.getName()+houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor()+",成为我的"+relation+"成员，"+ str;

			HouseInfoEntity houseInfoEntity = new HouseInfoEntity();
			houseInfoEntity.setId(String.valueOf(SnowFlake.nextId()));
			houseInfoEntity.setMobile(membersQO.getMobile());
			houseInfoEntity.setTitle(title);
			houseInfoEntity.setContent(desc);
			houseInfoEntity.setCreateTime(LocalDateTime.now());
			houseInfoEntity.setOverdueTime(LocalDateTime.now().plusHours(1));
			houseInfoEntity.setYzUid(userId);
			houseInfoEntity.setHouseId(String.valueOf(membersQO.getHouseId()));
			houseInfoService.saveOne(houseInfoEntity);
			membersQO.setUid(userId);

			redisTemplate.opsForValue().set(MEMBERKEY+membersQO.getMobile()+membersQO.getHouseId(), JSON.toJSONString(membersQO),1, TimeUnit.HOURS);
			return H5Url+"?id="+houseInfoEntity.getId()+"&mobile="+membersQO.getMobile();
		}

		/*
		UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("mobile", membersQO.getMobile()));
		//房主数据
		UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));

		if (userEntity==null||userEntity.equals(null)){
			//注册
			RegisterQO qo = new RegisterQO();
			qo.setAccount(membersQO.getMobile());
			qo.setName(membersQO.getName());
			entity.setUid(userService.registerV2(qo));

			UserIMEntity userIMEntity = userIMMapper.selectOne(new QueryWrapper<UserIMEntity>().eq("uid", entity.getUid()));
			//推送消息
			PushInfoUtil.PushPublicTextMsg(
					userIMEntity.getImId(),
					"房屋管理",
					"您有房屋最新消息了！",
					null,
					"尊敬的用户您好，\n" +
							"用户"+user.getRealName()+"已房东的身份添加你为"+communityEntity.getName()+houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor()+BusinessEnum.RelationshipEnum.getCodeName(membersQO.getRelation())+"身份，如已知晓，请忽略。"
					,null,
					BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());

		}else{
			UserIMEntity imEntity = userIMMapper.selectOne(new QueryWrapper<UserIMEntity>().eq("uid", userEntity.getUid()));
			entity.setUid(userEntity.getUid());
			//推送消息
			PushInfoUtil.PushPublicTextMsg(imEntity.getImId(),
					"房屋管理","您有房屋最新消息了！",
					null,
					"尊敬的用户您好，\n" +
							"用户"+user.getRealName()+"已房东的身份添加你为"+communityEntity.getName()+houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor()+BusinessEnum.RelationshipEnum.getCodeName(membersQO.getRelation())+"身份，如已知晓，请忽略。",
					null,
					BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName()
			);
		}
		 */



	}

	@Override
	public List<UserHouseVO> meHouse(String userId) {
		List<UserHouseVO> list = userHouseMapper.meHouse(userId);
		if (list != null) {
			return list;
		}
		throw new ProprietorException("你还没有认证房屋哦！");
	}

	/**
	 * @Description: 切换房屋
	 * @author: Hu
	 * @since: 2021/8/17 15:57
	 * @Param: [communityId, userId]
	 * @return: java.util.List<com.jsy.community.vo.property.HouseMemberVO>
	 */
	@Override
	public List<UserHouseVO> selectHouse(String userId) {
		List<UserHouseVO> linkedList = new LinkedList<>();
		//存储房间名称  id为key房间名称为value
		Map<Long, String> houseMap = new HashMap<>();
		//存储社区名称
		Map<Long, String> communityMap = new HashMap<>();
		//成员表所有房间id集合
		Set<Long> ids = new HashSet<>();
		//社区id集合
		Set<Long> communityIds = new HashSet<>();
		UserHouseVO userHouseVO=null;
		List<HouseMemberEntity> entityList = houseMemberMapper.selectList(new QueryWrapper<HouseMemberEntity>().eq("uid", userId));
		if (entityList.size()!=0){
			//房间成员
			for (HouseMemberEntity memberEntity : entityList) {
				//房间成员所有房间id
				ids.add(memberEntity.getHouseId());
				communityIds.add(memberEntity.getCommunityId());
			}
			//查询登录人员所有房间
			List<HouseEntity> houseEntities = houseMapper.selectBatchIds(ids);
			for (HouseEntity entity : houseEntities) {
				houseMap.put(entity.getId(),entity.getBuilding()+entity.getUnit()+entity.getDoor());
			}
			//查询当前登录人员所有小区
			List<CommunityEntity> list = communityMapper.selectBatchIds(communityIds);
			for (CommunityEntity communityEntity : list) {
				communityMap.put(communityEntity.getId(),communityEntity.getName());
			}
			for (HouseMemberEntity entity : entityList) {
				userHouseVO = new UserHouseVO();
				userHouseVO.setHouseSite(houseMap.get(entity.getHouseId()));
				userHouseVO.setCommunityText(communityMap.get(entity.getCommunityId()));
				userHouseVO.setRelation(entity.getRelation());
				userHouseVO.setHouseId(entity.getHouseId());
				userHouseVO.setCommunityId(entity.getCommunityId());
				userHouseVO.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(entity.getRelation()));
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
	public void attestation(UserHouseQO userHouseQO, String userId) {
		HouseEntity entity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("community_id",userHouseQO.getCommunityId()).eq("id",userHouseQO.getHouseId()));
		if (entity!=null){
			UserHouseEntity userHouseEntity = userHouseMapper.selectOne(new QueryWrapper<UserHouseEntity>().eq("house_id", userHouseQO.getHouseId()).eq("community_id", userHouseQO.getCommunityId()));
			if (userHouseEntity!=null){
				throw new ProprietorException("当前房屋已被认证，若非本人认证请联系管理员！");
			}else {
				ProprietorEntity proprietorEntity = proprietorService.getByUser(userHouseQO.getName(),userHouseQO.getMobile(),userHouseQO.getHouseId(),userHouseQO.getCommunityId());
				if (Objects.isNull(proprietorEntity)){
					throw new ProprietorException("当前房屋不是你的哦！");
				}

				//房屋认证表里添加数据
				UserHouseEntity houseEntity = new UserHouseEntity();
				houseEntity.setUid(userId);
				houseEntity.setCommunityId(userHouseQO.getCommunityId());
				houseEntity.setHouseId(userHouseQO.getHouseId());
				houseEntity.setCheckStatus(1);
				houseEntity.setId(SnowFlake.nextId());
				userHouseMapper.insert(houseEntity);

				//房间成员表里添加数据
				// UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", userId));
				UserDetail userDetail = userInfoRpcService.getUserDetail(userId);
				RealInfoDto idCardRealInfo = userInfoRpcService.getIdCardRealInfo(userId);
				HouseMemberEntity houseMemberEntity = houseMemberMapper.selectOne(new QueryWrapper<HouseMemberEntity>().eq("community_id", userHouseQO.getCommunityId()).eq("house_id", userHouseQO.getHouseId()).eq("mobile", userHouseQO.getMobile()).eq("name", userHouseQO.getName()));
				if (houseMemberEntity!=null){
					houseMemberEntity.setUid(userId);
					houseMemberMapper.updateById(houseMemberEntity);
				} else {
					HouseMemberEntity memberEntity = new HouseMemberEntity();
					memberEntity.setUid(userId);
					memberEntity.setCommunityId(userHouseQO.getCommunityId());
					memberEntity.setHouseId(userHouseQO.getHouseId());
					if (userDetail != null) {
						memberEntity.setMobile(userDetail.getPhone());
						memberEntity.setSex(userDetail.getSex());
					}
					if (idCardRealInfo != null) {
						memberEntity.setIdCard(idCardRealInfo.getIdCardNumber());
						memberEntity.setName(idCardRealInfo.getIdCardName());
					}
					memberEntity.setRelation(1);
					memberEntity.setId(SnowFlake.nextId());
					houseMemberMapper.insert(memberEntity);
				}
			}
		}else {
			throw new ProprietorException("当前房屋不存在！");
		}
	}

	/**
	 * @Description: 业主权限查询我的房屋
	 * @author: Hu
	 * @since: 2021/8/17 15:26
	 * @Param: [communityId, houseId, userId]
	 * @return: com.jsy.community.vo.UserHouseVO
	 */
	@Override
	public UserHouseVO userHouseDetails(UserHouseQO userHouseQO, String userId) {
		UserHouseEntity entity = userHouseMapper.selectOne(new QueryWrapper<UserHouseEntity>().eq("house_id", userHouseQO.getHouseId()).eq("community_id", userHouseQO.getCommunityId()).eq("uid", userId));
		LinkedList<MembersVO> list = new LinkedList<>();
		if (entity!=null){
			HouseEntity houseEntity = houseMapper.selectById(userHouseQO.getHouseId());
			CommunityEntity communityEntity = communityMapper.selectById(userHouseQO.getCommunityId());


			//当前登录人员为业主
			UserHouseVO userHouseVO = houseMemberMapper.selectLoginUser(userId, userHouseQO.getCommunityId(), userHouseQO.getHouseId(), 1);
			UserDetail userDetail = userInfoRpcService.getUserDetail(userId);
			if (userDetail != null) {
				userHouseVO.setAvatarUrl(userDetail.getAvatarThumbnail());
			}
			userHouseVO.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(userHouseVO.getRelation()));
			userHouseVO.setHouseSite(houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor());
			userHouseVO.setCommunityText(communityEntity.getName());

			//查询房屋下所有成员
			List<MembersVO> voList = houseMemberMapper.selectRelation(userHouseQO.getCommunityId(), userHouseQO.getHouseId(), 0);
			Set<String> uidSet = voList.stream().map(MembersVO::getUid).collect(Collectors.toSet());
			Map<String, RealUserDetail> map = new HashMap<>();
			if (!CollectionUtils.isEmpty(uidSet)) {
				List<RealUserDetail> realUserDetails = userInfoRpcService.getRealUserDetails(uidSet);
				if (!CollectionUtils.isEmpty(realUserDetails)) {
					map = realUserDetails.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
				}
			}
			for (MembersVO membersVO : voList) {
				RealUserDetail realUserDetails = map.get(membersVO.getUid());
				if (realUserDetails != null) {
					membersVO.setAvatarUrl(realUserDetails.getAvatarThumbnail());
				}
				if (!membersVO.getRelation().equals(1)){
					membersVO.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(membersVO.getRelation()));
					list.add(membersVO);
				}

			}
			userHouseVO.setMembers(list);
			return userHouseVO;

		}
		throw new ProprietorException("当前房屋未认证或者不是您的哦！");
	}

	/**
	 * @Description: 家属权限查询我的房屋
	 * @author: Hu
	 * @since: 2021/8/17 15:26
	 * @Param: [communityId, houseId, userId]
	 * @return: com.jsy.community.vo.UserHouseVO
	 */
	@Override
	public UserHouseVO memberHouseDetails(UserHouseQO userHouseQO, String userId) {
		HouseMemberEntity memberEntity = houseMemberMapper.selectOne(new QueryWrapper<HouseMemberEntity>()
				.eq("house_id", userHouseQO.getHouseId())
				.eq("community_id", userHouseQO.getCommunityId())
				.eq("uid", userId)
				.eq("relation", 6));

		if (memberEntity != null) {
			//查询当前房屋是否存在
			UserHouseEntity entity = userHouseMapper.selectOne(new QueryWrapper<UserHouseEntity>().eq("house_id", userHouseQO.getHouseId()).eq("community_id", userHouseQO.getCommunityId()).eq("uid", memberEntity.getHouseholderId()));

			LinkedList<MembersVO> list = new LinkedList<>();
			if (entity!=null){
				HouseEntity houseEntity = houseMapper.selectById(userHouseQO.getHouseId());
				CommunityEntity communityEntity = communityMapper.selectById(userHouseQO.getCommunityId());


				//查询当前登录人员信息
				UserHouseVO userHouseVO = houseMemberMapper.selectLoginUser(userId, userHouseQO.getCommunityId(), userHouseQO.getHouseId(),6);
				userHouseVO.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(userHouseVO.getRelation()));
				userHouseVO.setHouseSite(houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor());
				userHouseVO.setCommunityText(communityEntity.getName());


				//当前房屋下业主的信息  一个房间只能有一个业主
				List<MembersVO> user = houseMemberMapper.selectRelation(userHouseQO.getCommunityId(), userHouseQO.getHouseId(),1);
				MembersVO membersVO = user.get(0);
				membersVO.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(membersVO.getRelation()));
				list.add(membersVO);


				//当前房屋下所有关系为租户的成员
				List<MembersVO> members = houseMemberMapper.selectRelation(userHouseQO.getCommunityId(), userHouseQO.getHouseId(), 7);
				for (MembersVO member : members) {
					member.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(member.getRelation()));
					list.add(member);
				}
				userHouseVO.setMembers(list);
				return userHouseVO;
			}
			throw new ProprietorException("当前房屋不存在！");
		}
		return new UserHouseVO();
	}

	/**
	 * @Description: 租户权限查询我的房屋
	 * @author: Hu
	 * @since: 2021/8/17 15:26
	 * @Param: [communityId, houseId, userId]
	 * @return: com.jsy.community.vo.UserHouseVO
	 */
	@Override
	public UserHouseVO lesseeHouseDetails(UserHouseQO userHouseQO, String userId) {
		HouseMemberEntity memberEntity = houseMemberMapper.selectOne(new QueryWrapper<HouseMemberEntity>()
				.eq("house_id", userHouseQO.getHouseId())
				.eq("community_id", userHouseQO.getCommunityId())
				.eq("uid", userId)
				.eq("relation", 7));

		if (memberEntity != null) {
			UserHouseEntity entity = userHouseMapper.selectOne(new QueryWrapper<UserHouseEntity>().eq("house_id", userHouseQO.getHouseId()).eq("community_id", userHouseQO.getCommunityId()).eq("uid", memberEntity.getHouseholderId()));
			LinkedList<MembersVO> list = new LinkedList<>();
			if (entity!=null){
				HouseEntity houseEntity = houseMapper.selectById(userHouseQO.getHouseId());
				CommunityEntity communityEntity = communityMapper.selectById(userHouseQO.getCommunityId());


				//当前登录人员为租客
				UserHouseVO userHouseVO = houseMemberMapper.selectLoginUser(userId, userHouseQO.getCommunityId(), userHouseQO.getHouseId(), 7);
				userHouseVO.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(userHouseVO.getRelation()));
				userHouseVO.setHouseSite(houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getDoor());
				userHouseVO.setCommunityText(communityEntity.getName());

				//查询当前房屋业主
				List<MembersVO> voList = houseMemberMapper.selectRelation(userHouseQO.getCommunityId(), userHouseQO.getHouseId(), 1);
				MembersVO vo = voList.get(0);
				vo.setRelationText(BusinessEnum.RelationshipEnum.getCodeName(vo.getRelation()));
				list.add(vo);
				userHouseVO.setMembers(list);
				return userHouseVO;
			}
			throw new ProprietorException("当前房屋未认证或者不是您的哦！");
		}
		return new UserHouseVO();
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


	/**
	 * @Description: 养老导入家人列表接口
	 * @author: Hu
	 * @since: 2021/12/2 15:33
	 * @Param: [userId]
	 * @return: java.util.List<com.jsy.community.vo.HouseMembersQO>
	 */
	@Override
	public List<HouseMembersQO> selectMembers(String userId) {
		List<HouseMembersQO> list = houseMemberMapper.selectMembers(userId);
		return list;
	}
}
