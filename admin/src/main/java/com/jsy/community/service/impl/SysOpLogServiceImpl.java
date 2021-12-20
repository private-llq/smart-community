package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SysOpLogEntity;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.mapper.SysOpLogMapper;
import com.jsy.community.mapper.SysUserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
import com.jsy.community.service.ISysOpLogService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author DKS
 * @description 用户操作日志
 * @since 2021/10/20  10:57
 **/
@Service
public class SysOpLogServiceImpl extends ServiceImpl<SysOpLogMapper, SysOpLogEntity> implements ISysOpLogService {
	
	@Resource
	private SysOpLogMapper sysOpLogMapper;
	
	@Resource
	private SysUserMapper sysUserMapper;
	
	/**
	 * @author DKS
	 * @description 用户操作日志AOP
	 * @since 2021/10/20  10:57
	 **/
	@Override
	public void saveOpLog(SysOpLogEntity sysOpLogEntity) {
		sysOpLogEntity.setId(SnowFlake.nextId());
		sysOpLogMapper.insert(sysOpLogEntity);
	}
	
	/**
	 * @Description: 操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.SysOpLogEntity>
	 * @Author: DKS
	 * @since 2021/10/20  10:57
	 **/
	public PageInfo<SysOpLogEntity> queryOpLogPage(BaseQO<OpLogQO> baseQO) {
		OpLogQO query = baseQO.getQuery();
		Page<SysOpLogEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<SysOpLogEntity> queryWrapper = new QueryWrapper<>();
		// 查操作
		if (StringUtils.isNotBlank(query.getOperation())) {
			queryWrapper.like("operation", query.getOperation());
		}
		// 模糊查询用户名
		if (StringUtils.isNotBlank(query.getUserName())) {
			List<String> idList = sysUserMapper.queryUidListByRealName(query.getUserName());
			if (idList == null || idList.size() == 0) {
				return new PageInfo<>();
			}
			queryWrapper.in("user_id", idList);
		}
		queryWrapper.orderByDesc("create_time");
		Page<SysOpLogEntity> pageData = sysOpLogMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		// 补充用户名
		for (SysOpLogEntity entity : pageData.getRecords()) {
			if (entity.getUserId() != null) {
				SysUserEntity sysUserEntity = sysUserMapper.queryById(entity.getUserId());
				entity.setUserName(sysUserEntity.getRealName());
			}
		}
		
		PageInfo<SysOpLogEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
}
