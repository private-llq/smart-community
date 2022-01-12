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
import com.jsy.community.constant.ConstError;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.UserLivingExpensesAccountMapper;
import com.jsy.community.mapper.UserLivingExpensesBillMapper;
import com.jsy.community.mapper.UserLivingExpensesOrderMapper;
import com.jsy.community.qo.cebbank.CebBillQueryResultDataModelQO;
import com.jsy.community.qo.cebbank.CebCreateCashierDeskQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CebCallbackVO;
import com.jsy.community.vo.CebCashierDeskVO;
import com.jsy.community.vo.LivingExpensesOrderListVO;
import com.zhsj.baseweb.annotation.LoginIgnore;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesOrderServiceImpl extends ServiceImpl<UserLivingExpensesOrderMapper, UserLivingExpensesOrderEntity> implements UserLivingExpensesOrderService {
	
	@Autowired
	private UserLivingExpensesOrderMapper userLivingExpensesOrderMapper;
	
	@Autowired
	private UserLivingExpensesAccountMapper accountMapper;

	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private CebBankService cebBankService;

	@Autowired
	private UserLivingExpensesBillMapper billMapper;

	/**
	 * @Description: 新增生活缴费订单记录
	 * @author: DKS
	 * @since: 2021/12/29 10:40
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.lang.String
	 */
	@Override
	@LoginIgnore
	@Transactional(rollbackFor = Exception.class)
	public CebCashierDeskVO addUserLivingExpensesOrder(UserLivingExpensesBillEntity billEntity, String mobile) {
		UserLivingExpensesOrderEntity userLivingExpensesOrderEntity = new UserLivingExpensesOrderEntity();
		CebCreateCashierDeskQO deskQO = new CebCreateCashierDeskQO();
		deskQO.setType(billEntity.getType());
		deskQO.setDeviceType(billEntity.getDeviceType());
		userLivingExpensesOrderEntity.setPayAmount(billEntity.getPayAmount());
		if (billEntity.getId() != null) {
			billEntity = billMapper.selectById(billEntity.getId());
		}
		userLivingExpensesOrderEntity.setId(SnowFlake.nextId());
		// 添加本地订单数据
		userLivingExpensesOrderEntity.setUid(billEntity.getUid());
		userLivingExpensesOrderEntity.setTypeId(billEntity.getTypeId());
		userLivingExpensesOrderEntity.setItemId(billEntity.getItemId());
		userLivingExpensesOrderEntity.setItemCode(billEntity.getItemCode());
		userLivingExpensesOrderEntity.setBillKey(billEntity.getBillKey());
		if (billEntity.getId() != null) {
			userLivingExpensesOrderEntity.setBillId(billEntity.getId().toString());
		}
		userLivingExpensesOrderEntity.setBillAmount(billEntity.getBillAmount());
		userLivingExpensesOrderEntity.setCustomerName(billEntity.getCustomerName());
		userLivingExpensesOrderEntity.setContactNo(billEntity.getContactNo());
		userLivingExpensesOrderEntity.setOrderStatus(BusinessEnum.CebbankOrderStatusEnum.INIT.getCode());
		int insert = userLivingExpensesOrderMapper.insert(userLivingExpensesOrderEntity);
		// 组装支付服务需要的参数

		deskQO.setMerOrderNo(userLivingExpensesOrderEntity.getId().toString());
		deskQO.setMerOrderDate(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
		deskQO.setPayAmount(userLivingExpensesOrderEntity.getPayAmount());
		deskQO.setPaymentItemCode(billEntity.getItemCode());
		deskQO.setPaymentItemId(billEntity.getItemId());
		deskQO.setBillKey(billEntity.getBillKey());
		deskQO.setSessionId(cebBankService.getCebBankSessionId(mobile, billEntity.getDeviceType()));
		deskQO.setBillAmount(billEntity.getBillAmount());
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
		resultDataModelQO.setBalance(billEntity.getBalance() == null ? null : billEntity.getBalance().toString());
		resultDataModelQO.setPayAmount(userLivingExpensesOrderEntity.getPayAmount().toString());
		resultDataModelQO.setBeginDate(billEntity.getBeginDate());
		resultDataModelQO.setEndDate(billEntity.getEndDate());
		resultDataModelQO.setFiled1(billEntity.getFieldA());
		resultDataModelQO.setFiled2(billEntity.getFieldB());
		resultDataModelQO.setFiled3(billEntity.getFieldC());
		resultDataModelQO.setFiled4(billEntity.getFieldD());
		resultDataModelQO.setFiled5(billEntity.getFieldE());
		deskQO.setBillQueryResultDataModel(JSON.toJSONString(resultDataModelQO));
		// 调用支付服务下单
		return cebBankService.createCashierDesk(deskQO);
	}
	
	/**
	 * @Description: 查询当前用户生活缴费记录列表
	 * @author: DKS
	 * @since: 2021/12/29 11:52
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.util.List<com.jsy.community.entity.UserLivingExpensesOrderEntity>
	 */
	@Override
	public List<LivingExpensesOrderListVO> getListOfUserLivingExpensesOrder(UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
		
		QueryWrapper<UserLivingExpensesOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("*,DATE_FORMAT(create_time,'%Y-%m') as monthTime");
		queryWrapper.eq("uid", userLivingExpensesOrderEntity.getUid());
		queryWrapper.eq("deleted", 0);
		// 是否查分类
		if (StringUtils.isNotBlank(userLivingExpensesOrderEntity.getTypeId())) {
			List<UserLivingExpensesAccountEntity> userLivingExpensesAccountEntities = accountMapper.selectList(new QueryWrapper<UserLivingExpensesAccountEntity>().eq("type_id", userLivingExpensesOrderEntity.getTypeId()));
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
			return new ArrayList<>();
		}
		
		Set<String> accounts = userLivingExpensesOrderEntities.stream().map(UserLivingExpensesOrderEntity::getBillKey).collect(Collectors.toSet());
		List<UserLivingExpensesAccountEntity> userLivingExpensesAccountEntityByAccount = accountMapper.selectList(
				new QueryWrapper<UserLivingExpensesAccountEntity>().eq("uid", userLivingExpensesOrderEntity.getUid())
						.in("account", accounts));
		// 查询生活缴费户号列表
		Map<String, UserLivingExpensesAccountEntity> userLivingExpensesAccountEntityMap = userLivingExpensesAccountEntityByAccount.stream()
			.collect(Collectors.toMap(UserLivingExpensesAccountEntity::getAccount, Function.identity()));
		
		// 补充数据
		for (UserLivingExpensesOrderEntity entity : userLivingExpensesOrderEntities) {
			UserLivingExpensesAccountEntity userLivingExpensesAccountEntity = userLivingExpensesAccountEntityMap.get(entity.getBillKey());
			if (userLivingExpensesAccountEntity != null) {
				// 补充户号
				entity.setAccount(userLivingExpensesAccountEntity.getAccount());
				// 补充户主
				entity.setHouseholder(userLivingExpensesAccountEntity.getHouseholder());
				// 补充分类名称
				entity.setTypeName(userLivingExpensesAccountEntity.getTypeName());
				entity.setTypeId(userLivingExpensesAccountEntity.getTypeId());
			}
		}
		
		// 根据月份封装返回数据
		Map<String, List<UserLivingExpensesOrderEntity>> resultMaps = userLivingExpensesOrderEntities.stream()
			.collect(Collectors.groupingBy(UserLivingExpensesOrderEntity::getMonthTime,
				Collectors.mapping(Function.identity(), Collectors.toList())));
		
		if (CollectionUtils.isEmpty(resultMaps)) {
			return new ArrayList<>();
		}
		ArrayList<LivingExpensesOrderListVO> livingExpensesOrderListVOS = new ArrayList<>();
		for (String key : resultMaps.keySet()) {
			LivingExpensesOrderListVO vo = new LivingExpensesOrderListVO();
			vo.setDateString(key);
			vo.setOrderEntityList(resultMaps.get(key));
			livingExpensesOrderListVOS.add(vo);
		}
		return livingExpensesOrderListVOS;
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
			throw new JSYException("未找到生活缴费记录");
		}
		// 补充返回字段
		UserLivingExpensesAccountEntity account = accountMapper.selectOne(new QueryWrapper<UserLivingExpensesAccountEntity>().eq("account", userLivingExpensesOrderEntity.getBillKey()));
		if (account != null) {
			// 户主
			userLivingExpensesOrderEntity.setHouseholder(account.getHouseholder());
			// 缴费单位
			userLivingExpensesOrderEntity.setCompany(account.getCompany());
		}
		// 缴费状态
		userLivingExpensesOrderEntity.setOrderStatusName(BusinessEnum.CebbankOrderStatusEnum.cebbankOrderStatusMap.get(userLivingExpensesOrderEntity.getOrderStatus()));
		return userLivingExpensesOrderEntity;
	}

	/**
	 * @param cebCallbackVO : 云缴费返回参数
	 * @author: Pipi
	 * @description: 完成云缴费订单
	 * @return: {@link Boolean}
	 * @date: 2021/12/31 16:39
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean completeCebOrder(CebCallbackVO cebCallbackVO) {
		log.info("进入回调");
		UserLivingExpensesOrderEntity userLivingExpensesOrderEntity = userLivingExpensesOrderMapper.selectById(cebCallbackVO.getMerOrderNo());
		log.info("查询订单");
		if (userLivingExpensesOrderEntity == null) {
			log.info("没有找到云缴费订单:{}", cebCallbackVO.getMerOrderNo());
			return false;
		}
		if (userLivingExpensesOrderEntity.getPayAmount().compareTo(cebCallbackVO.getPayAmount()) != 0) {
			log.info("云缴费订单支付金额不一致,订单号:{}", cebCallbackVO.getMerOrderNo());
			return false;
		}
		if (userLivingExpensesOrderEntity.getOrderStatus().equals(BusinessEnum.CebbankOrderStatusEnum.SUCCESSFUL_PAYMENT.getCode())) {
			log.info("订单已经支付成功,不再做处理");
			// 订单已经支付成功,不再做处理
			return true;
		}
		userLivingExpensesOrderEntity.setOrderStatus(cebCallbackVO.getOrder_status());
		userLivingExpensesOrderEntity.setTransacNo(cebCallbackVO.getTransacNo());
		userLivingExpensesOrderEntity.setOrderDate(cebCallbackVO.getOrderDate());
		userLivingExpensesOrderEntity.setRepoPayAmount(cebCallbackVO.getPayAmount());
		userLivingExpensesOrderEntity.setPayType(cebCallbackVO.getPayType());
		int i = userLivingExpensesOrderMapper.updateById(userLivingExpensesOrderEntity);
		log.info("订单状态修改完成,修改结果:{}", i);
		// 修改账单状态
		log.info("修改账单缴费状态");
		QueryWrapper<UserLivingExpensesBillEntity> billEntityQueryWrapper = new QueryWrapper<>();
		billEntityQueryWrapper.eq("id", userLivingExpensesOrderEntity.getBillId());
		UserLivingExpensesBillEntity userLivingExpensesBillEntity = new UserLivingExpensesBillEntity();
		userLivingExpensesBillEntity.setBillStatus(BusinessEnum.PaymentStatusEnum.PAID.getCode());
		billMapper.update(userLivingExpensesBillEntity, billEntityQueryWrapper);
		log.info("修改账单缴费状态完成");
		return i == 1;
	}
}
