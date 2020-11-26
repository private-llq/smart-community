package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.CommonQueryConsts;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.qo.proprietor.HouseMemberQO;
import com.jsy.community.utils.MyPageUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

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
		int result = houseMemberMapper.insert(houseMemberEntity);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 删除房间成员/撤销邀请
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
			if(CommonQueryConsts.QUERY_HOUSE_MEMBER.equals(houseMemberQO.getType())){ //查成员
				queryWrapper.eq("is_confirm",Const.HouseMemberConsts.JOINED);//已加入
				queryWrapper.eq("house_id",houseMemberQO.getHouseId());
				return houseMemberMapper.selectPage(page, queryWrapper);
			}else if(CommonQueryConsts.QUERY_HOUSE_MEMBER_INVITATION.equals(houseMemberQO.getType())){ // 查邀请
				queryWrapper.eq("is_confirm",Const.HouseMemberConsts.UNJOIN);//未加入
				queryWrapper.eq("uid",houseMemberQO.getUid());
				return houseMemberMapper.selectPage(page,queryWrapper);
			}
		}
		return null;
	}
	
}
