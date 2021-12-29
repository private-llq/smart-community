package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserLivingExpensesOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.mapper.UserLivingExpensesOrderMapper;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.basecommon.enums.ErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费订单表服务实现
 * @Date: 2021/12/2 18:01
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesOrderServiceImpl extends ServiceImpl<UserLivingExpensesOrderMapper, UserLivingExpensesOrderEntity> implements UserLivingExpensesOrderService {
	
	@Autowired
	private UserLivingExpensesOrderMapper userLivingExpensesOrderMapper;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService userInfoRpcService;
	
	/**
	 * @Description: 新增生活缴费订单记录
	 * @author: DKS
	 * @since: 2021/12/29 10:40
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.lang.String
	 */
	@Override
	public String addUserLivingExpensesOrder(UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
		userLivingExpensesOrderEntity.setId(SnowFlake.nextId());
		int insert = userLivingExpensesOrderMapper.insert(userLivingExpensesOrderEntity);
		if (insert == 0) {
			throw new ProprietorException(ErrorEnum.ADD_FAIL);
		}
		return String.valueOf(userLivingExpensesOrderEntity.getId());
	}
	
	/**
	 * @Description: 查询当前用户生活缴费记录列表
	 * @author: DKS
	 * @since: 2021/12/29 11:52
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.util.List<com.jsy.community.entity.UserLivingExpensesOrderEntity>
	 */
	@Override
	public List<UserLivingExpensesOrderEntity> getListOfUserLivingExpensesOrder(UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
		QueryWrapper<UserLivingExpensesOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("uid", userLivingExpensesOrderEntity.getUid());
		queryWrapper.eq("deleted", 0);
		if (StringUtils.isNotBlank(userLivingExpensesOrderEntity.getItemCode())) {
			queryWrapper.eq("item_code", userLivingExpensesOrderEntity.getItemCode());
		}
		if (userLivingExpensesOrderEntity.getQueryTime() != null) {
			LocalDate endTime = userLivingExpensesOrderEntity.getQueryTime().with(TemporalAdjusters.lastDayOfMonth());
			queryWrapper.ge("create_time", userLivingExpensesOrderEntity.getQueryTime());
			queryWrapper.le("create_time", endTime);
		}
		List<UserLivingExpensesOrderEntity> userLivingExpensesOrderEntities = userLivingExpensesOrderMapper.selectList(queryWrapper);
		if (CollectionUtils.isEmpty(userLivingExpensesOrderEntities)) {
			return new ArrayList<>();
		}
		Set<String> uidSet = userLivingExpensesOrderEntities.stream().map(UserLivingExpensesOrderEntity::getUid).collect(Collectors.toSet());
		List<RealUserDetail> userDetails = userInfoRpcService.getRealUserDetails(uidSet);
		Map<String, RealUserDetail> userDetailMap = userDetails.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
		for (UserLivingExpensesOrderEntity entity : userLivingExpensesOrderEntities) {
			RealUserDetail realUserDetail = userDetailMap.get(entity.getUid());
			entity.setUserName(realUserDetail.getNickName());
			entity.setMobile(realUserDetail.getPhone());
		}
		return userLivingExpensesOrderEntities;
	}
	
	/**
	 * @Description: 查询生活缴费记录详情
	 * @author: DKS
	 * @since: 2021/12/29 14:03
	 * @Param: [id]
	 * @return: com.jsy.community.entity.UserLivingExpensesOrderEntity
	 */
	@Override
	public UserLivingExpensesOrderEntity getById(Long id) {
		UserLivingExpensesOrderEntity userLivingExpensesOrderEntity = userLivingExpensesOrderMapper.selectById(id);
		if (userLivingExpensesOrderEntity == null) {
			throw new ProprietorException("未找到生活缴费记录");
		}
		return userLivingExpensesOrderEntity;
	}
}
