package com.jsy.community.service.impl;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseMemberQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * 房间成员表 服务实现类(此套房主邀请加入房间(添加房间成员)的方案暂时搁置)
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-23
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class HouseMemberServiceImpl extends ServiceImpl<HouseMemberMapper, HouseMemberEntity> implements IHouseMemberService {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IUserHouseService iUserHouseService;
	
	@Autowired
	private HouseMemberMapper houseMemberMapper;

	@Autowired
	private UserMapper userMapper;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;
//	@Autowired
//	private UserMapper userMapper;
	
	/**
	 * @Description: 邀请房间成员
	 * @Param: [houseMemberEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	 **/
//	@Override
//	public boolean addHouseMember(HouseMemberEntity houseMemberEntity){
//		int result = 0;
//		try{
//			result = houseMemberMapper.insert(houseMemberEntity);
//		} catch (DuplicateKeyException e) {
//			throw new ProprietorException(JSYError.DUPLICATE_KEY.getCode(),"请勿重复邀请");
//		}
//		if(result == 1){
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * @Description: 删除房间成员/撤销邀请 by批量id
	 * @Param: [ids]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	 **/
//	@Override
//	public boolean deleteHouseMember(List<Long> ids){
//		int result = houseMemberMapper.deleteBatchIds(ids);
//		if(result > 0){
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * @Description: 删除房间成员/撤销邀请 by房主id
	 * @Param: [houseHolderId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	 **/
//	public boolean deleteHouseMember(Long houseHolderId){
//		int result = houseMemberMapper.delete(new QueryWrapper<HouseMemberEntity>().eq("householder_id",houseHolderId));
//		if(result > 0){
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * @Description: 成员确认加入房间
	 * @Param: [houseMemberEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/26
	 **/
//	@Override
//	public boolean confirmJoin(HouseMemberEntity houseMemberEntity){
//		int result = houseMemberMapper.confirmJoin(houseMemberEntity.getId());
//		int setBelongTo = userMapper.setUserBelongTo(houseMemberEntity.getHouseholderId(),houseMemberEntity.getUid());
//		if(result == 1 && setBelongTo == 1){
//			return true;
//		}
//		throw new ProprietorException(JSYError.INTERNAL.getCode(),"加入失败");
//	}
	
	/**
	 * @Description: 分页查询成员/查询邀请
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	 **/
	//TODO 物业端 houseid
	//TODO 后台 code(t_house) - id(t_house) - houseid(t_house_member)
//	@Override
//	public Page<HouseMemberEntity> queryHouseMemberPage(BaseQO<HouseMemberQO> baseQO){
//		HouseMemberQO houseMemberQO = baseQO.getQuery();
//		if(houseMemberQO != null){
//			Page<HouseMemberEntity> page = new Page<>();
//			MyPageUtils.setPageAndSize(page, baseQO); //设置分页参数
//			QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
//			queryWrapper.select("*");// 暂时
//			if(BusinessConst.QUERY_HOUSE_MEMBER.equals(houseMemberQO.getType())){ //查成员
//				queryWrapper.eq("is_confirm",Const.HouseMemberConsts.JOINED);//已加入
//				queryWrapper.eq("house_id",houseMemberQO.getHouseId());
//				return houseMemberMapper.selectPage(page, queryWrapper);
//			}else if(BusinessConst.QUERY_HOUSE_MEMBER_INVITATION.equals(houseMemberQO.getType())){ // 查邀请
//				queryWrapper.eq("is_confirm",Const.HouseMemberConsts.UNJOIN);//未加入
//				queryWrapper.eq("uid",houseMemberQO.getUid());
//				return houseMemberMapper.selectPage(page,queryWrapper);
//			}
//		}
//		return null;
//	}
	
	/**
	 * @Description: 检查是否是房主
	 * @Param: [uid, houseId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	 **/
	@Override
	public boolean checkHouseHolder(Long uid, Long houseId){
		return iUserHouseService.checkHouseHolder(uid,houseId);
	}
	
	/**
	* @Description: 房主亲属 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@Override
	public PageInfo<HouseMemberEntity> getHouseMembers(BaseQO<HouseMemberQO> baseQO){
		HouseMemberQO query = baseQO.getQuery();
		Page<HouseMemberEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		Page<HouseMemberEntity> pageResult = houseMemberMapper.selectPage(page, new QueryWrapper<HouseMemberEntity>()
			.select("name", "sex", "mobile", "id_card")
			.eq("householder_id", query.getHouseholderId())
			.eq("person_type", BusinessConst.PERSON_TYPE_RELATIVE)
		);
		PageInfo pageInfo = new PageInfo();
		BeanUtils.copyProperties(pageResult,pageInfo);
		return pageInfo;
	}
	
	/**
	 * @Description: 房间ID 查 成员List
	 * @Param: [houseId]
	 * @Return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	 **/
	@Override
	public List<HouseMemberEntity> queryByHouseId(Long houseId){
		return houseMemberMapper.queryByHouseId(houseId);
	}
	
	/**
	 * @Description: id单查
	 * @Param: [id]
	 * @Return: com.jsy.community.entity.HouseMemberEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	 **/
	@Override
	public HouseMemberEntity queryById(Long id){
		return houseMemberMapper.selectById(id);
	}
	
	/**
	 * @Description: ids批量查
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Long,com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	 **/
	@Override
	public Map<Long,HouseMemberEntity> queryByIdBatch(Set<Long> ids){
		return houseMemberMapper.queryByIdsBatch(ids);
	}

	/**
	 * @author: Pipi
	 * @description: 查询社区里所有的成员
	 * @param communityId: 社区ID
	 * @return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @date: 2021/8/19 17:22
	 **/
	@Override
	public List<HouseMemberEntity> queryByCommunityId(Long communityId) {
		QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id", communityId);
		return houseMemberMapper.selectList(queryWrapper);
	}

	/**
	 * @param communityId : 社区ID
	 * @param uidSet      : 用户uid列表
	 * @author: Pipi
	 * @description: 查询社区所有成员信息
	 * @return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @date: 2021/9/9 15:07
	 **/
	@Override
	public List<HouseMemberEntity> queryByCommunityIdAndUids(Long communityId, Set<String> uidSet) {
		QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("uid, relation");
		queryWrapper.eq("community_id", communityId);
		queryWrapper.in("uid", uidSet);
		return houseMemberMapper.selectList(queryWrapper);
	}

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
	@Override
	public Integer addMember(String uid, String homeOwnerUid, Long communityId, Long houseId, LocalDateTime validTime) {
		UserDetail userDetail = baseUserInfoRpcService.getUserDetail(uid);
		RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(uid);
		// UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", uid));
		HouseMemberEntity houseMemberEntity = new HouseMemberEntity();
		houseMemberEntity.setId(SnowFlake.nextId());
		houseMemberEntity.setStatus(1);
		houseMemberEntity.setUid(uid);
		houseMemberEntity.setHouseholderId(homeOwnerUid);
		houseMemberEntity.setCommunityId(communityId);
		houseMemberEntity.setHouseId(houseId);
		if (userDetail != null) {
			houseMemberEntity.setSex(userDetail.getSex());
			houseMemberEntity.setMobile(userDetail.getPhone());
		}
		houseMemberEntity.setRelation(7);
		houseMemberEntity.setEnterTime(LocalDate.now());
		houseMemberEntity.setIdentificationType(1);
		if (idCardRealInfo != null) {
			houseMemberEntity.setName(idCardRealInfo.getIdCardName());
			houseMemberEntity.setIdCard(idCardRealInfo.getIdCardNumber());
		}
		houseMemberEntity.setRemark("");
		houseMemberEntity.setValidTime(validTime);
		houseMemberEntity.setDeleted(0L);
		houseMemberEntity.setCreateTime(LocalDateTime.now());
		return houseMemberMapper.insert(houseMemberEntity);
	}
}
