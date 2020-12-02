package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.ProprietorServiceApp;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseMemberQO;
import com.jsy.community.utils.MyPageUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

/**
 * <p>
 * 房间成员表 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-23
 */
@DubboService(version = Const.version, group = Const.group)
public class HouseMemberServiceImpl extends ServiceImpl<HouseMemberMapper, HouseMemberEntity> implements IHouseMemberService {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserHouseService iUserHouseService;
	
	@Autowired
	private HouseMemberMapper houseMemberMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * @Description: 邀请房间成员
	 * @Param: [houseMemberEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	 **/
	@Override
	public boolean addHouseMember(HouseMemberEntity houseMemberEntity){
		int result = 0;
		try{
			result = houseMemberMapper.insert(houseMemberEntity);
		} catch (DuplicateKeyException e) {
			throw new ProprietorException(JSYError.DUPLICATE_KEY.getCode(),"请勿重复邀请");
		}
		if(result == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 删除房间成员/撤销邀请 by批量id
	 * @Param: [ids]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	 **/
	@Override
	public boolean deleteHouseMember(List<Long> ids){
		int result = houseMemberMapper.deleteBatchIds(ids);
		if(result > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 删除房间成员/撤销邀请 by房主id
	 * @Param: [houseHolderId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	 **/
	public boolean deleteHouseMember(Long houseHolderId){
		int result = houseMemberMapper.delete(new QueryWrapper<HouseMemberEntity>().eq("householder_id",houseHolderId));
		if(result > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 成员确认加入房间
	 * @Param: [houseMemberEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/26
	 **/
	@Override
	public boolean confirmJoin(HouseMemberEntity houseMemberEntity){
		int result = houseMemberMapper.confirmJoin(houseMemberEntity.getId());
		int setBelongTo = userMapper.setUserBelongTo(houseMemberEntity.getHouseholderId(),houseMemberEntity.getUid());
		if(result == 1 && setBelongTo == 1){
			return true;
		}
		throw new ProprietorException(JSYError.INTERNAL.getCode(),"加入失败");
	}
	
	/**
	 * @Description: 分页查询成员/查询邀请
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/23
	 **/
	//TODO 物业端 houseid
	//TODO 后台 code(t_house) - id(t_house) - houseid(t_house_member)
	@Override
	public Page<HouseMemberEntity> queryHouseMemberPage(BaseQO<HouseMemberQO> baseQO){
		HouseMemberQO houseMemberQO = baseQO.getQuery();
		if(houseMemberQO != null){
			Page<HouseMemberEntity> page = new Page<>();
			MyPageUtils.setPageAndSize(page, baseQO); //设置分页参数
			QueryWrapper<HouseMemberEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.select("*");// 暂时
			if(BusinessConst.QUERY_HOUSE_MEMBER.equals(houseMemberQO.getType())){ //查成员
				queryWrapper.eq("is_confirm",Const.HouseMemberConsts.JOINED);//已加入
				queryWrapper.eq("house_id",houseMemberQO.getHouseId());
				return houseMemberMapper.selectPage(page, queryWrapper);
			}else if(BusinessConst.QUERY_HOUSE_MEMBER_INVITATION.equals(houseMemberQO.getType())){ // 查邀请
				queryWrapper.eq("is_confirm",Const.HouseMemberConsts.UNJOIN);//未加入
				queryWrapper.eq("uid",houseMemberQO.getUid());
				return houseMemberMapper.selectPage(page,queryWrapper);
			}
		}
		return null;
	}
	
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
	
}
