package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IOpLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.OpLogEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.OpLogMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.OpLogQO;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;
	
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
		if (StringUtils.isNotBlank(query.getOperation())) {
			queryWrapper.like("operation", query.getOperation());
		}
		// 模糊查询用户名
		if (StringUtils.isNotBlank(query.getUserName())) {
			PageVO<UserDetail> pageVO = baseUserInfoRpcService.queryUser("", query.getUserName(), 0, 99999999);
			Set<String> userIds = pageVO.getData().stream().map(UserDetail::getAccount).collect(Collectors.toSet());
			if (!CollectionUtils.isEmpty(userIds)) {
				queryWrapper.in("user_id", userIds);
			} else {
				return new PageInfo<>();
			}
		}
		queryWrapper.orderByDesc("create_time");
		Page<OpLogEntity> pageData = opLogMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		// 补充用户名
		Set<String> uidSet = pageData.getRecords().stream().map(OpLogEntity::getUserId).collect(Collectors.toSet());
//		Set<Long> uidsSet = uidSet.stream().map(Long::parseLong).collect(Collectors.toSet());
		List<RealUserDetail> realUserDetails = baseUserInfoRpcService.getRealUserDetails(uidSet);
		Map<String, RealUserDetail> userDetailMap = realUserDetails.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
		for (OpLogEntity entity : pageData.getRecords()) {
			if (entity.getUserId() != null) {
				RealUserDetail realUserDetail = userDetailMap.get(entity.getUserId());
				if (realUserDetail != null) {
					entity.setUserName(realUserDetail.getNickName());
				}
			}
		}
		
		PageInfo<OpLogEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
}
