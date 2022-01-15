package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceLogEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyFinanceLogMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceLogQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author DKS
 * @description 收款管理操作日志
 * @since 2021/8/23  16:38
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyFinanceLogServiceImpl extends ServiceImpl<PropertyFinanceLogMapper, FinanceLogEntity> implements IPropertyFinanceLogService {
	
	@Autowired
	private PropertyFinanceLogMapper propertyFinanceLogMapper;
	
	@Autowired
	private AdminUserMapper adminUserMapper;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;
	
	/**
	 * @author DKS
	 * @description 收款管理操作日志AOP
	 * @since 2021/8/23  16:38
	 **/
	@Override
	public void saveFinanceLog(FinanceLogEntity financeLogEntity) {
		financeLogEntity.setId(SnowFlake.nextId());
		propertyFinanceLogMapper.insert(financeLogEntity);
	}
	
	/**
	 * @Description: 操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.FinanceLogEntity>
	 * @Author: DKS
	 * @Date: 2021/08/23 16:38
	 **/
	public PageInfo<FinanceLogEntity> queryFinanceLogPage(BaseQO<FinanceLogQO> baseQO) {
		FinanceLogQO query = baseQO.getQuery();
		Page<FinanceLogEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<FinanceLogEntity> queryWrapper = new QueryWrapper<>();
		// 查小区
		queryWrapper.eq("community_id", query.getCommunityId());
		
		// 模糊查询用户名
		if (StringUtils.isNotBlank(query.getUserName())) {
			PageVO<UserDetail> pageVO = baseUserInfoRpcService.queryUser("", query.getUserName(), 0, 9999);
			Set<Long> uid = pageVO.getData().stream().map(UserDetail::getId).collect(Collectors.toSet());
			if (uid.size() > 0) {
				queryWrapper.in("user_id", uid);
			} else {
				queryWrapper.eq("user_id", 0);
			}
		}
		
		// 查时间段
		if (query.getStartTime() != null && query.getEndTime() != null) {
			queryWrapper.ge("create_time", query.getStartTime());
			queryWrapper.lt("create_time", query.getEndTime().plusDays(1));
		}
		
		queryWrapper.orderByDesc("create_time");
		Page<FinanceLogEntity> pageData = propertyFinanceLogMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		Set<String> userIds = pageData.getRecords().stream().map(FinanceLogEntity::getUserId).collect(Collectors.toSet());
//		Set<Long> userId = userIds.stream().map(Long::parseLong).collect(Collectors.toSet());
		List<RealUserDetail> realUserDetailsByUid = baseUserInfoRpcService.getRealUserDetails(userIds);
		Map<Long, String> uIdMaps = realUserDetailsByUid.stream().collect(Collectors.toMap(RealUserDetail::getId, RealUserDetail::getNickName));
		
		// 补充用户名
		for (FinanceLogEntity entity : pageData.getRecords()) {
			entity.setUserName(uIdMaps.get(Long.parseLong(entity.getUserId())));
		}
		
		PageInfo<FinanceLogEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
}
