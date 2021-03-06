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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: Pipi
 * @Description: ???????????????????????????????????????
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

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * @Description: ??????????????????????????????
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
		userLivingExpensesOrderEntity.setUid(billEntity.getUid());
		if (billEntity.getId() != null) {
			billEntity = billMapper.selectById(billEntity.getId());
			if (billEntity.getBillStatus().equals(BusinessEnum.PaymentStatusEnum.PAID.getCode())) {
				throw new ProprietorException(JSYError.ALREADY_PAID_PLEASE_DO_NOT_PAY_AGAIN);
			}
		}
		userLivingExpensesOrderEntity.setId(SnowFlake.nextId());
		// ????????????????????????
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
		// ?????????????????????????????????

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
		deskQO.setAppName("E??????");
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
		// ????????????????????????
		CebCashierDeskVO cashierDesk = cebBankService.createCashierDesk(deskQO);
		cashierDesk.setOrderNo(userLivingExpensesOrderEntity.getIdStr());
		return cashierDesk;
	}
	
	/**
	 * @Description: ??????????????????????????????????????????
	 * @author: DKS
	 * @since: 2021/12/29 11:52
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.util.List<com.jsy.community.entity.UserLivingExpensesOrderEntity>
	 */
	@Override
	public List<LivingExpensesOrderListVO> getListOfUserLivingExpensesOrder(UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
		
		QueryWrapper<UserLivingExpensesOrderEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("*,DATE_FORMAT(create_time,'%Y???%m???') as monthTime");
		queryWrapper.eq("uid", userLivingExpensesOrderEntity.getUid());
		queryWrapper.in("order_status", BusinessEnum.CebbankOrderStatusEnum.SUCCESSFUL_PAYMENT.getCode(),
				BusinessEnum.CebbankOrderStatusEnum.SUCCESSFUL_CANCELLATION.getCode());
		queryWrapper.eq("deleted", 0);
		// ???????????????
		if (StringUtils.isNotBlank(userLivingExpensesOrderEntity.getTypeId())) {
			List<UserLivingExpensesAccountEntity> userLivingExpensesAccountEntities = accountMapper.selectList(new QueryWrapper<UserLivingExpensesAccountEntity>().eq("type_id", userLivingExpensesOrderEntity.getTypeId()));
			Set<String> accounts = userLivingExpensesAccountEntities.stream().map(UserLivingExpensesAccountEntity::getAccount).collect(Collectors.toSet());
			queryWrapper.in("bill_key", accounts);
		}
		// ???????????????
		if (userLivingExpensesOrderEntity.getQueryTime() != null) {
			LocalDate endTime = userLivingExpensesOrderEntity.getQueryTime().with(TemporalAdjusters.lastDayOfMonth());
			queryWrapper.ge("create_time", userLivingExpensesOrderEntity.getQueryTime());
			queryWrapper.le("create_time", endTime);
		}
		// ??????????????????
		if (StringUtils.isNotBlank(userLivingExpensesOrderEntity.getBillKey())) {
			queryWrapper.eq("bill_key", userLivingExpensesOrderEntity.getBillKey());
		}
		List<UserLivingExpensesOrderEntity> userLivingExpensesOrderEntities = userLivingExpensesOrderMapper.selectList(queryWrapper);
		if (CollectionUtils.isEmpty(userLivingExpensesOrderEntities)) {
			return new ArrayList<>();
		}
		
		Set<String> accounts = userLivingExpensesOrderEntities.stream().map(UserLivingExpensesOrderEntity::getBillKey).collect(Collectors.toSet());
		List<UserLivingExpensesAccountEntity> userLivingExpensesAccountEntityByAccount = accountMapper.selectList(
				new QueryWrapper<UserLivingExpensesAccountEntity>().eq("uid", userLivingExpensesOrderEntity.getUid())
						.in("account", accounts));
		// ??????????????????????????????
		Map<String, UserLivingExpensesAccountEntity> userLivingExpensesAccountEntityMap = userLivingExpensesAccountEntityByAccount.stream()
			.collect(Collectors.toMap(UserLivingExpensesAccountEntity::getAccount, Function.identity()));
		String costIcon = redisTemplate.opsForValue().get("costIcon");
		Map<Integer, String> map = JSON.parseObject(costIcon, Map.class);
		// ????????????
		for (UserLivingExpensesOrderEntity entity : userLivingExpensesOrderEntities) {
			UserLivingExpensesAccountEntity userLivingExpensesAccountEntity = userLivingExpensesAccountEntityMap.get(entity.getBillKey());
			if (userLivingExpensesAccountEntity != null) {
				// ????????????
				entity.setAccount(userLivingExpensesAccountEntity.getAccount());
				// ????????????
				entity.setHouseholder(userLivingExpensesAccountEntity.getHouseholder());
				// ??????????????????
				entity.setTypeName(userLivingExpensesAccountEntity.getTypeName());
				entity.setTypeId(userLivingExpensesAccountEntity.getTypeId());
			}
			entity.setTypePicUrl((map.get(Integer.valueOf(entity.getTypeId()))));
		}
		
		// ??????????????????????????????
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
	 * @Description: ??????????????????????????????
	 * @author: DKS
	 * @since: 2021/12/29 14:03
	 * @Param: [id]
	 * @return: com.jsy.community.entity.UserLivingExpensesOrderEntity
	 */
	@Override
	public UserLivingExpensesOrderEntity getById(Long id) {
		UserLivingExpensesOrderEntity userLivingExpensesOrderEntity = userLivingExpensesOrderMapper.selectById(id);
		if (userLivingExpensesOrderEntity == null) {
			throw new JSYException("???????????????????????????");
		}
		// ??????????????????
		UserLivingExpensesAccountEntity account = accountMapper.selectOne(new QueryWrapper<UserLivingExpensesAccountEntity>()
				.eq("uid", userLivingExpensesOrderEntity.getUid())
				.eq("account", userLivingExpensesOrderEntity.getBillKey()));
		if (account != null) {
			// ??????
			userLivingExpensesOrderEntity.setHouseholder(account.getHouseholder());
			// ????????????
			userLivingExpensesOrderEntity.setCompany(account.getCompany());
		}
		// ????????????
		userLivingExpensesOrderEntity.setOrderStatusName(BusinessEnum.CebbankOrderStatusEnum.cebbankOrderStatusMap.get(userLivingExpensesOrderEntity.getOrderStatus()));
		return userLivingExpensesOrderEntity;
	}

	/**
	 * @param cebCallbackVO : ?????????????????????
	 * @author: Pipi
	 * @description: ?????????????????????
	 * @return: {@link Boolean}
	 * @date: 2021/12/31 16:39
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean completeCebOrder(CebCallbackVO cebCallbackVO) {
		log.info("????????????");
		UserLivingExpensesOrderEntity userLivingExpensesOrderEntity = userLivingExpensesOrderMapper.selectById(cebCallbackVO.getMerOrderNo());
		log.info("????????????");
		if (userLivingExpensesOrderEntity == null) {
			log.info("???????????????????????????:{}", cebCallbackVO.getMerOrderNo());
			return false;
		}
		if (userLivingExpensesOrderEntity.getPayAmount().compareTo(cebCallbackVO.getPayAmount()) != 0) {
			log.info("????????????????????????????????????,?????????:{}", cebCallbackVO.getMerOrderNo());
			return false;
		}
		if (userLivingExpensesOrderEntity.getOrderStatus().equals(BusinessEnum.CebbankOrderStatusEnum.SUCCESSFUL_PAYMENT.getCode())) {
			log.info("????????????????????????,???????????????");
			// ????????????????????????,???????????????
			return true;
		}
		userLivingExpensesOrderEntity.setOrderStatus(cebCallbackVO.getOrder_status());
		userLivingExpensesOrderEntity.setTransacNo(cebCallbackVO.getTransacNo());
		userLivingExpensesOrderEntity.setOrderDate(cebCallbackVO.getOrderDate());
		userLivingExpensesOrderEntity.setRepoPayAmount(cebCallbackVO.getPayAmount());
		userLivingExpensesOrderEntity.setPayType(cebCallbackVO.getPayType());
		int i = userLivingExpensesOrderMapper.updateById(userLivingExpensesOrderEntity);
		log.info("????????????????????????,????????????:{}", i);
		// ??????????????????
		log.info("????????????????????????");
		QueryWrapper<UserLivingExpensesBillEntity> billEntityQueryWrapper = new QueryWrapper<>();
		billEntityQueryWrapper.eq("id", userLivingExpensesOrderEntity.getBillId());
		UserLivingExpensesBillEntity userLivingExpensesBillEntity = new UserLivingExpensesBillEntity();
		userLivingExpensesBillEntity.setBillStatus(BusinessEnum.PaymentStatusEnum.PAID.getCode());
		billMapper.update(userLivingExpensesBillEntity, billEntityQueryWrapper);
		// ?????????????????????????????????(???????????????,???????????????,????????????????????????,??????????????????????????????????????????,??????????????????????????????,??????????????????)
		QueryWrapper<UserLivingExpensesBillEntity> expensesBillEntityQueryWrapper = new QueryWrapper<>();
		expensesBillEntityQueryWrapper.ne("id", userLivingExpensesOrderEntity.getBillId());
		expensesBillEntityQueryWrapper.eq("bill_key", userLivingExpensesOrderEntity.getBillKey());
		expensesBillEntityQueryWrapper.eq("contact_no", userLivingExpensesOrderEntity.getContactNo());
		billMapper.delete(expensesBillEntityQueryWrapper);
		log.info("??????????????????????????????");
		return i == 1;
	}
}
