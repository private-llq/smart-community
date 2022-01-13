package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SysOpLogEntity;
import com.jsy.community.mapper.SysOpLogMapper;
import com.jsy.community.mapper.SysUserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
import com.jsy.community.service.ISysOpLogService;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService userInfoRpcService;
	
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
			PageVO<UserDetail> pageVO = userInfoRpcService.queryUser("", query.getUserName(), 0, 99999999);
			Set<Long> userIds = pageVO.getData().stream().map(UserDetail::getId).collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(userIds)) {
				queryWrapper.in("user_id", userIds);
			} else {
				return new PageInfo<>();
			}
		}
		queryWrapper.orderByDesc("create_time");
		Page<SysOpLogEntity> pageData = sysOpLogMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		Set<String> userIds = pageData.getRecords().stream().map(SysOpLogEntity::getUserId).collect(Collectors.toSet());
		Set<Long> userIdList = userIds.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toSet());
		List<RealUserDetail> realUserDetails = userInfoRpcService.getRealUserDetailsByUid(userIdList);
		Map<Long, RealUserDetail> realUserDetailMap = realUserDetails.stream().collect(Collectors.toMap(RealUserDetail::getId, Function.identity()));
		// 补充用户名
		for (SysOpLogEntity entity : pageData.getRecords()) {
			if (entity.getUserId() != null) {
				entity.setUserName(realUserDetailMap.get(Long.parseLong(entity.getUserId())).getNickName());
			}
		}
		
		PageInfo<SysOpLogEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
}
