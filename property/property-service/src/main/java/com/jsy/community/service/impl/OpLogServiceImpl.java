package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IOpLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.OpLogEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.OpLogMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author DKS
 * @description 用户操作日志
 * @since 2021/8/21  14:44
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class OpLogServiceImpl extends ServiceImpl<OpLogMapper, OpLogEntity> implements IOpLogService {
	
	@Autowired
	private OpLogMapper opLogMapper;
	
	@Autowired
	private AdminUserMapper adminUserMapper;
	
	/**
	 * @author DKS
	 * @description 用户操作日志AOP
	 * @since 2021/8/21  14:45
	 **/
	@Override
	public void saveOpLog(OpLogEntity opLogEntity) {
		opLogEntity.setId(SnowFlake.nextId());
		opLogMapper.insert(opLogEntity);
	}
	
	/**
	 * @Description: 操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.OpLogEntity>
	 * @Author: DKS
	 * @Date: 2021/08/23 11:56
	 **/
	public PageInfo<OpLogEntity> queryOpLogPage(BaseQO<OpLogQO> baseQO) {
		OpLogQO query = baseQO.getQuery();
		Page<OpLogEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<OpLogEntity> queryWrapper = new QueryWrapper<>();
		// 查小区
		if (query.getCommunityId() != null) {
			queryWrapper.eq("community_id", query.getCommunityId());
		}
		// 查操作
		if (query.getOperation() != null) {
			queryWrapper.like("operation", query.getOperation());
		}
		// 模糊查询用户名
		if (query.getUserName() != null) {
			List<String> uidList = adminUserMapper.queryUidListByRealName(query.getUserName());
			queryWrapper.in("user_id", uidList);
		}
		queryWrapper.orderByDesc("create_time");
		Page<OpLogEntity> pageData = opLogMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		// 补充用户名
		for (OpLogEntity entity : pageData.getRecords()) {
			AdminUserEntity adminUserEntity = adminUserMapper.queryByUid(entity.getUserId());
			entity.setUserName(adminUserEntity.getRealName());
		}
		
		PageInfo<OpLogEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
}
