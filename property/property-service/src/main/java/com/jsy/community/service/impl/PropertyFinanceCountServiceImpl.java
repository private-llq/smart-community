package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IPropertyFinanceCountService;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.api.IPropertyFinanceReceiptService;
import com.jsy.community.api.IPropertyFinanceStatementService;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.property.PropertyFinanceCountEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.entity.property.TestEntity;
import com.jsy.community.mapper.PropertyFinanceOrderMapper;
import com.jsy.community.mapper.PropertyFinanceReceiptMapper;
import com.jsy.community.mapper.PropertyFinanceStatementMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @author chq459799974
 * @description 财务统计
 * @since 2021-04-26 16:10
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyFinanceCountServiceImpl implements IPropertyFinanceCountService {
	
	//统计模块直接引入各个Service和Mapper
	
	@Autowired
	private PropertyFinanceOrderMapper orderMapper;
	
	@Autowired
	private PropertyFinanceReceiptMapper receiptMapper;
	
	@Autowired
	private PropertyFinanceStatementMapper statementMapper;
	
//	@DubboReference(version = Const.version, group = Const.group_property, check = false)
//	private IPropertyFinanceReceiptService receiptService;
	
//	@DubboReference(version = Const.version, group = Const.group_property, check = false)
//	private IPropertyFinanceOrderService orderService;
//
//
//	@DubboReference(version = Const.version, group = Const.group_property, check = false)
//	private IPropertyFinanceStatementService statementService;
	
	//缴费统计
	public void orderPaidCount(PropertyFinanceCountEntity query){
		PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
		receiptEntity.setStartDate(query.getStartDate());
		receiptEntity.setEndDate(query.getEndDate());
		//查询缴费时间满足筛选条件的缴费单号
//		List<String> receiptNums = receiptMapper.queryReceiptNumsByCondition(receiptEntity);
//		if(CollectionUtils.isEmpty(receiptNums)){
//			return;
//		}
		
//		List<Map<String, String>> monthAndReceiptNum = receiptMapper.queryReceiptNumsAndMonthByMonth(receiptEntity);
		//TODO a.月份与收款单号 (每月收款单号)
		List<Map<String, String>> monthAndReceiptNumList = receiptMapper.queryReceiptNumsAndMonthMapByMonth(receiptEntity);
		ArrayList<String> dateList = new ArrayList<>();
		ArrayList<ArrayList<String>> outList = new ArrayList<>();      // ["7777,8888","9999"]          //TODO 需要二维集合？
		for(Map<String, String> map : monthAndReceiptNumList){
			ArrayList<String> receiptNumList = new ArrayList<>();
			dateList.add(map.get("perMonth"));
			receiptNumList.add(map.get("receiptNums"));
			outList.add(receiptNumList);
			
			//测试数据
			outList.add(receiptNumList);
		}
		List<BigDecimal> totalMoneys = orderMapper.test1(outList);
		System.out.println(totalMoneys);
		QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
		//TODO 换成按月统计查询
//		select '2021-04',sum(total_money) from t_property_finance_order where receipt_num in ('7777','8888')
		
//		queryWrapper.select("sum(total_money) as totalMoney");
//		queryWrapper.eq("order_status", PropertyConstsEnum.OrderStatusEnum.ORDER_STATUS_PAID.getCode());
//		queryWrapper.in("receipt_num",receiptNums); //通过前面查出的缴费单号限定时间
		
		//TODO 1.查账单信息 in(receiptNums)
		//TODO 2. b.查出1.这些收款单与账单号 Map  | 不要MAP 有序就行了
		
		List<Map<String, Object>> maps = orderMapper.selectMaps(queryWrapper);
		System.out.println("缴费统计:" + maps.get(0));
		
	}
	
	//应收统计
	public void orderCount(LocalDate startDate, LocalDate endDate){
		QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("sum(total_money) as totalMoney");
//		queryWrapper.ge("");
//		queryWrapper.le("");
//		orderMapper.selectMaps();
	}
	
	//结算统计
	public void statementCount(LocalDate startDate, LocalDate endDate){
	
	}
}
