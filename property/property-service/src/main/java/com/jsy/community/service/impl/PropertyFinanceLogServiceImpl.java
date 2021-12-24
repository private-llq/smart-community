package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFinanceLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceLogEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyFinanceLogMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceLogQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

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
		if (query.getCommunityId() != null) {
			queryWrapper.eq("community_id", query.getCommunityId());
		}
		
		// 模糊查询用户名
		if (query.getUserName() != null) {
			// TODO: 2021/12/23 等待刘金荣解决
			Set<String> strings = baseUserInfoRpcService.queryRealUserDetail(null, query.getUserName());
			if (strings.size() > 0) {
				queryWrapper.in("user_id", strings);
			} else {
				queryWrapper.eq("user_id", 0);
			}
		}
		
		// 查时间段
		if (query.getStartTime() != null && query.getEndTime() != null) {
			queryWrapper.ge("create_time", query.getStartTime());
			queryWrapper.le("create_time", query.getEndTime());
		}
		
		queryWrapper.orderByDesc("create_time");
		Page<FinanceLogEntity> pageData = propertyFinanceLogMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		// 补充用户名
		for (FinanceLogEntity entity : pageData.getRecords()) {
			AdminUserEntity adminUserEntity = adminUserMapper.queryByUid(entity.getUserId());
			entity.setUserName(adminUserEntity.getRealName());
		}
		
		PageInfo<FinanceLogEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
}
