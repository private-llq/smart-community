package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserLivingExpensesOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.mapper.UserLivingExpensesAccountMapper;
import com.jsy.community.mapper.UserLivingExpensesOrderMapper;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.basecommon.enums.ErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
	
	@Autowired
	private UserLivingExpensesAccountMapper accountMapper;
	
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
	public Map<String, List<UserLivingExpensesOrderEntity>> getListOfUserLivingExpensesOrder(UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
		
		QueryWrapper<UserLivingExpensesOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("*,DATE_FORMAT(create_time,'%Y-%m') as monthTime");
		queryWrapper.eq("uid", userLivingExpensesOrderEntity.getUid());
		queryWrapper.eq("deleted", 0);
		// 是否查分类
		if (StringUtils.isNotBlank(userLivingExpensesOrderEntity.getCategoryId())) {
			List<UserLivingExpensesAccountEntity> userLivingExpensesAccountEntities = accountMapper.selectList(new QueryWrapper<UserLivingExpensesAccountEntity>().eq("category_id", userLivingExpensesOrderEntity.getCategoryId()));
			Set<String> accounts = userLivingExpensesAccountEntities.stream().map(UserLivingExpensesAccountEntity::getAccount).collect(Collectors.toSet());
			queryWrapper.in("bill_key", accounts);
		}
		// 是否查月份
		if (userLivingExpensesOrderEntity.getQueryTime() != null) {
			LocalDate endTime = userLivingExpensesOrderEntity.getQueryTime().with(TemporalAdjusters.lastDayOfMonth());
			queryWrapper.ge("create_time", userLivingExpensesOrderEntity.getQueryTime());
			queryWrapper.le("create_time", endTime);
		}
		List<UserLivingExpensesOrderEntity> userLivingExpensesOrderEntities = userLivingExpensesOrderMapper.selectList(queryWrapper);
		if (CollectionUtils.isEmpty(userLivingExpensesOrderEntities)) {
			return new HashMap<>();
		}
		
		Set<String> accounts = userLivingExpensesOrderEntities.stream().map(UserLivingExpensesOrderEntity::getBillKey).collect(Collectors.toSet());
		List<UserLivingExpensesAccountEntity> userLivingExpensesAccountEntityByAccount = accountMapper.selectList(new QueryWrapper<UserLivingExpensesAccountEntity>().in("account", accounts));
		// 查询生活缴费户号列表
		Map<String, UserLivingExpensesAccountEntity> userLivingExpensesAccountEntityMap = userLivingExpensesAccountEntityByAccount.stream()
			.collect(Collectors.toMap(UserLivingExpensesAccountEntity::getAccount, Function.identity()));
		
		// 补充数据
		for (UserLivingExpensesOrderEntity entity : userLivingExpensesOrderEntities) {
			UserLivingExpensesAccountEntity userLivingExpensesAccountEntity = userLivingExpensesAccountEntityMap.get(entity.getBillKey());
			// 补充户号
			entity.setAccount(userLivingExpensesAccountEntity.getAccount());
			// 补充户主
			entity.setHouseholder(userLivingExpensesAccountEntity.getHouseholder());
			// 补充分类名称
			entity.setCategory(userLivingExpensesAccountEntity.getCategory());
		}
		
		// 根据月份封装返回数据
		Map<String, List<UserLivingExpensesOrderEntity>> resultMaps = userLivingExpensesOrderEntities.stream()
			.collect(Collectors.groupingBy(UserLivingExpensesOrderEntity::getMonthTime,
				Collectors.mapping(Function.identity(), Collectors.toList())));
		
		if (CollectionUtils.isEmpty(resultMaps)) {
			return new HashMap<>();
		}
		
		return resultMaps;
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
		// 补充返回字段
		UserLivingExpensesAccountEntity account = accountMapper.selectOne(new QueryWrapper<UserLivingExpensesAccountEntity>().eq("account", userLivingExpensesOrderEntity.getBillKey()));
		// 缴费状态
		userLivingExpensesOrderEntity.setOrderStatusName(userLivingExpensesOrderEntity.getOrderStatus() == 0 ? "订单创建成功" : userLivingExpensesOrderEntity.getOrderStatus() == 1 ? "支付成功"
			: userLivingExpensesOrderEntity.getOrderStatus() == 2 ? "支付失败" : userLivingExpensesOrderEntity.getOrderStatus() == 3 ? "销账成功" : userLivingExpensesOrderEntity.getOrderStatus() == 4 ? "销账失败" :
			userLivingExpensesOrderEntity.getOrderStatus() == 8 ? "实时退款" : "未知状态");
		// 户主
		userLivingExpensesOrderEntity.setHouseholder(account.getHouseholder());
		// 缴费单位
		userLivingExpensesOrderEntity.setCompany(account.getCompany());
		return userLivingExpensesOrderEntity;
	}
}
