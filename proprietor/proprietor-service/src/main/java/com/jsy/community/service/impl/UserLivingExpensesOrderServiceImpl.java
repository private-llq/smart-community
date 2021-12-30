package com.jsy.community.service.impl;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CebBankService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserLivingExpensesOrderService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.mapper.UserLivingExpensesAccountMapper;
import com.jsy.community.mapper.UserLivingExpensesOrderMapper;
import com.jsy.community.qo.cebbank.CebBillQueryResultDataModelQO;
import com.jsy.community.qo.cebbank.CebCreateCashierDeskQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.cebbank.CebCashierDeskVO;
import com.zhsj.basecommon.enums.ErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private CebBankService cebBankService;
	
	/**
	 * @Description: 新增生活缴费订单记录
	 * @author: DKS
	 * @since: 2021/12/29 10:40
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.lang.String
	 */
	@Override
	public String addUserLivingExpensesOrder(UserLivingExpensesBillEntity billEntity) {
		UserLivingExpensesOrderEntity userLivingExpensesOrderEntity = new UserLivingExpensesOrderEntity();
		userLivingExpensesOrderEntity.setId(SnowFlake.nextId());
		// 添加本地订单数据
		userLivingExpensesOrderEntity.setUid(billEntity.getUid());
		userLivingExpensesOrderEntity.setItemId(billEntity.getItemId());
		userLivingExpensesOrderEntity.setItemCode(billEntity.getItemCode());
		userLivingExpensesOrderEntity.setBillKey(billEntity.getBillKey());
		userLivingExpensesOrderEntity.setBillId(billEntity.getId().toString());
		userLivingExpensesOrderEntity.setBillAmount(new BigDecimal(billEntity.getBillAmount()));
		userLivingExpensesOrderEntity.setPayAmount(billEntity.getPayAmount());
		userLivingExpensesOrderEntity.setCustomerName(billEntity.getCustomerName());
		userLivingExpensesOrderEntity.setContactNo(billEntity.getContactNo());
		userLivingExpensesOrderEntity.setOrderStatus(BusinessEnum.CebbankOrderStatusEnum.INIT.getCode());
		int insert = userLivingExpensesOrderMapper.insert(userLivingExpensesOrderEntity);
		// 组装支付服务需要的参数
		CebCreateCashierDeskQO deskQO = new CebCreateCashierDeskQO();
		deskQO.setMerOrderNo(userLivingExpensesOrderEntity.getId().toString());
		deskQO.setMerOrderDate(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
		deskQO.setPayAmount(billEntity.getPayAmount());
		deskQO.setPaymentItemCode(billEntity.getItemCode());
		deskQO.setPaymentItemId(billEntity.getItemId());
		deskQO.setBillKey(billEntity.getBillKey());
		deskQO.setSessionId("");
		if (billEntity.getBillAmount() != null) {
			deskQO.setBillAmount(new BigDecimal(billEntity.getBillAmount()));
		}
		deskQO.setQueryAcqSsn(billEntity.getQueryAcqSsn());
		deskQO.setCustomerName(billEntity.getCustomerName());
		deskQO.setContractNo(billEntity.getContactNo());
		deskQO.setFiled1(billEntity.getFieldA());
		deskQO.setFiled2(billEntity.getFieldB());
		deskQO.setFiled3(billEntity.getFieldC());
		deskQO.setFiled4(billEntity.getFieldD());
		deskQO.setFiled5(billEntity.getFieldE());
		deskQO.setAppName("E到家");
		deskQO.setAppVersion("1.0.0");

		CebBillQueryResultDataModelQO resultDataModelQO = new CebBillQueryResultDataModelQO();
		resultDataModelQO.setContractNo(billEntity.getContactNo());
//		resultDataModelQO.setCustomerName("");
//		resultDataModelQO.setOriginalCustomerName("");
		resultDataModelQO.setBalance(billEntity.getBalance());
		resultDataModelQO.setPayAmount(billEntity.getPayAmount().toString());
		resultDataModelQO.setBeginDate(billEntity.getBeginDate());
		resultDataModelQO.setEndDate(billEntity.getEndDate());
		resultDataModelQO.setFiled1(billEntity.getFieldA());
		resultDataModelQO.setFiled2(billEntity.getFieldB());
		resultDataModelQO.setFiled3(billEntity.getFieldC());
		resultDataModelQO.setFiled4(billEntity.getFieldD());
		resultDataModelQO.setFiled5(billEntity.getFieldE());
		deskQO.setBillQueryResultDataModel(JSON.toJSONString(resultDataModelQO));
		deskQO.setType(billEntity.getType());
		deskQO.setDeviceType(billEntity.getDeviceType());
		// 调用支付服务下单
		CebCashierDeskVO cashierDesk = cebBankService.createCashierDesk(deskQO);
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
