package com.jsy.community.service.impl;
import com.google.common.collect.Lists;

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
import com.jsy.community.utils.DateUtils;
import com.jsy.community.vo.property.ContentVO;
import com.jsy.community.vo.property.FinanceOrderEntityVO;
import com.jsy.community.vo.property.StatisticsVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	
	/**
	 * 缴费统计
	 */
	@Override
	public StatisticsVO orderPaidCount(PropertyFinanceCountEntity query){
		StatisticsVO statisticsVO = new StatisticsVO();
		List<FinanceOrderEntityVO> orderEntities = orderMapper.statisticsReceipt(query);
		// 将结果list转换为map
		HashMap<String, FinanceOrderEntityVO> orderEntityVOMap = new HashMap<>();
		for (FinanceOrderEntityVO orderEntity : orderEntities) {
			orderEntityVOMap.put(orderEntity.getCreateTime(), orderEntity);
		}

		List<String> name = new ArrayList<>();
		name.add("总金额");
		name.add("物业费");
		name.add("车位费");
		name.add("水费");
		statisticsVO.setName(name);

		List<String> createTime = DateUtils.getDayListOfMonth(query.getStartDate().toString(), query.getEndDate().toString());
		// 物业费
		List<BigDecimal> propertyCosts = new ArrayList<>();
		// 停车费
		List<BigDecimal> parkingFee = new ArrayList<>();
		// 水费
		List<BigDecimal> waterFee = new ArrayList<>();
		// 总金额
		List<BigDecimal> totalFee = new ArrayList<>();
		for (String s : createTime) {
			if (orderEntityVOMap.containsKey(s)) {
				propertyCosts.add(orderEntityVOMap.get(s).getTotalMoney());
				parkingFee.add(BigDecimal.ZERO);
				waterFee.add(BigDecimal.ZERO);
				totalFee.add(BigDecimal.ZERO.add(BigDecimal.ZERO).add(orderEntityVOMap.get(s).getTotalMoney()));
				break;
			} else {
				propertyCosts.add(BigDecimal.ZERO);
				parkingFee.add(BigDecimal.ZERO);
				waterFee.add(BigDecimal.ZERO);
				totalFee.add(BigDecimal.ZERO);
			}
		}
		statisticsVO.setCreateTime(createTime);

		ContentVO contentVO = new ContentVO();
		contentVO.setPropertyCosts(propertyCosts);
		contentVO.setParkingFee(parkingFee);
		contentVO.setWaterFee(waterFee);
		contentVO.setTotalFee(totalFee);
		statisticsVO.setContent(contentVO);
		return statisticsVO;
		/*PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
		receiptEntity.setStartDate(query.getStartDate());
		receiptEntity.setEndDate(query.getEndDate());
		//查询缴费时间满足筛选条件的缴费单号
		List<String> receiptNums = receiptMapper.queryReceiptNumsByCondition(receiptEntity);
		if(CollectionUtils.isEmpty(receiptNums)){
			return;
		}
		List<Map<String, String>> monthAndReceiptNum = receiptMapper.queryReceiptNumsAndMonthByMonth(receiptEntity);
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
		System.out.println("缴费统计:" + maps.get(0));*/


		
	}
	
	/**
	 * 应收统计
	 */
	@Override
	public StatisticsVO orderReceivableCount(PropertyFinanceCountEntity query){
		StatisticsVO statisticsVO = new StatisticsVO();
		List<FinanceOrderEntityVO> financeOrderEntityVOS = orderMapper.statisticsReceivable(query);
		// 将结果list转换为map
		HashMap<String, FinanceOrderEntityVO> orderEntityVOMap = new HashMap<>();
		for (FinanceOrderEntityVO orderEntity : financeOrderEntityVOS) {
			orderEntityVOMap.put(orderEntity.getCreateTime(), orderEntity);
		}
		List<String> name = new ArrayList<>();
		name.add("总金额");
		name.add("已收金额");
		name.add("欠费总金额");
		name.add("物业费欠收");
		name.add("车位费欠收");
		statisticsVO.setName(name);
		List<String> createTime = DateUtils.getDayListOfMonth(query.getStartDate().toString(), query.getEndDate().toString());
		// 已收金额
		List<BigDecimal> receivedAmount = new ArrayList<>();
		// 欠费总金额
		List<BigDecimal> arrearsAmount = new ArrayList<>();
		// 物业费欠收
		List<BigDecimal> arrearsPropertyAmount = new ArrayList<>();
		// 车位费欠收
		List<BigDecimal> arrearsParkingFee = new ArrayList<>();
		// 总金额
		List<BigDecimal> totalFee = new ArrayList<>();
		// 总金额map
		Map<String, BigDecimal> totalFeeMap = new HashMap<>();
		// 欠费总金额map
		Map<String, BigDecimal> arrearsAmountMap = new HashMap<>();
		// 已收金额map
		Map<String, BigDecimal> receivedAmountMap = new HashMap<>();
		// 物业费欠收map
		Map<String, BigDecimal> arrearsPropertyAmountMap = new HashMap<>();
		// 车位费欠收map
		Map<String, BigDecimal> arrearsParkingFeeMap = new HashMap<>();
		for (String s : createTime) {
			if (orderEntityVOMap.containsKey(s)) {
				if (!createTime.contains(orderEntityVOMap.get(s).getCreateTime())) {
					createTime.add(orderEntityVOMap.get(s).getCreateTime());
				}
				// 因为一个月有多条数据,所以一个月的总金额由多条数据累加
				if (totalFeeMap != null && totalFeeMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
					BigDecimal totalMoney = totalFeeMap.get(orderEntityVOMap.get(s).getCreateTime()).add(orderEntityVOMap.get(s).getTotalMoney());
					totalFeeMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
				} else {
					totalFeeMap.put(orderEntityVOMap.get(s).getCreateTime(), orderEntityVOMap.get(s).getTotalMoney());
				}
				// 欠费总金额
				if (orderEntityVOMap.get(s).getOrderStatus() == 0) {
					if (arrearsAmountMap != null && arrearsAmountMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = arrearsAmountMap.get(orderEntityVOMap.get(s).getCreateTime()).add(orderEntityVOMap.get(s).getTotalMoney());
						arrearsAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						arrearsAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), orderEntityVOMap.get(s).getTotalMoney());
					}
				} else {
					if (arrearsAmountMap != null && arrearsAmountMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = arrearsAmountMap.get(orderEntityVOMap.get(s).getCreateTime()).add(BigDecimal.ZERO);
						arrearsAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						arrearsAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), BigDecimal.ZERO);
					}
				}
				// 已收金额
				if (orderEntityVOMap.get(s).getOrderStatus() == 1) {
					if (receivedAmountMap != null && receivedAmountMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = receivedAmountMap.get(orderEntityVOMap.get(s).getCreateTime()).add(orderEntityVOMap.get(s).getTotalMoney());
						receivedAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						receivedAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), orderEntityVOMap.get(s).getTotalMoney());
					}
				} else {
					if (receivedAmountMap != null && receivedAmountMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = receivedAmountMap.get(orderEntityVOMap.get(s).getCreateTime()).add(BigDecimal.ZERO);
						receivedAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						receivedAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), BigDecimal.ZERO);
					}
				}
				// 物业费欠收
				if ("物业费".equals(orderEntityVOMap.get(s).getOrderType()) && orderEntityVOMap.get(s).getOrderStatus() == 0) {
					if (arrearsPropertyAmountMap != null && arrearsPropertyAmountMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = arrearsPropertyAmountMap.get(orderEntityVOMap.get(s).getCreateTime()).add(orderEntityVOMap.get(s).getTotalMoney());
						arrearsPropertyAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						arrearsPropertyAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), orderEntityVOMap.get(s).getTotalMoney());
					}
				} else {
					if (arrearsPropertyAmountMap != null && arrearsPropertyAmountMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = arrearsPropertyAmountMap.get(orderEntityVOMap.get(s).getCreateTime()).add(BigDecimal.ZERO);
						arrearsPropertyAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						arrearsPropertyAmountMap.put(orderEntityVOMap.get(s).getCreateTime(), BigDecimal.ZERO);
					}
				}
				// 车位费欠收
				if ("车位费".equals(orderEntityVOMap.get(s).getOrderType()) && orderEntityVOMap.get(s).getOrderStatus() == 0) {
					if (arrearsParkingFeeMap != null && arrearsParkingFeeMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = arrearsParkingFeeMap.get(orderEntityVOMap.get(s).getCreateTime()).add(orderEntityVOMap.get(s).getTotalMoney());
						arrearsParkingFeeMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						arrearsParkingFeeMap.put(orderEntityVOMap.get(s).getCreateTime(), orderEntityVOMap.get(s).getTotalMoney());
					}
				} else {
					if (arrearsParkingFeeMap != null && arrearsParkingFeeMap.containsKey(orderEntityVOMap.get(s).getCreateTime())) {
						BigDecimal totalMoney = arrearsParkingFeeMap.get(orderEntityVOMap.get(s).getCreateTime()).add(BigDecimal.ZERO);
						arrearsParkingFeeMap.put(orderEntityVOMap.get(s).getCreateTime(), totalMoney);
					} else {
						arrearsParkingFeeMap.put(orderEntityVOMap.get(s).getCreateTime(), BigDecimal.ZERO);
					}
				}
			} else {
				totalFeeMap.put(s, BigDecimal.ZERO);
				arrearsAmountMap.put(s, BigDecimal.ZERO);
				receivedAmountMap.put(s, BigDecimal.ZERO);
				arrearsPropertyAmountMap.put(s, BigDecimal.ZERO);
				arrearsParkingFeeMap.put(s, BigDecimal.ZERO);
			}
		}
		List<String> keyList = totalFeeMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		for (String key : keyList) {
			totalFee.add(totalFeeMap.get(key));
		}
		keyList = arrearsAmountMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		for (String key : keyList) {
			arrearsAmount.add(arrearsAmountMap.get(key));
		}
		keyList = arrearsPropertyAmountMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		for (String key : keyList) {
			arrearsPropertyAmount.add(arrearsPropertyAmountMap.get(key));
		}
		keyList = arrearsParkingFeeMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		for (String key : keyList) {
			arrearsParkingFee.add(arrearsParkingFeeMap.get(key));
		}
		keyList = receivedAmountMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		for (String key : keyList) {
			receivedAmount.add(receivedAmountMap.get(key));
		}
		statisticsVO.setCreateTime(createTime);
		ContentVO contentVO = new ContentVO();
		contentVO.setReceivedAmount(receivedAmount);
		contentVO.setArrearsAmount(arrearsAmount);
		contentVO.setArrearsPropertyAmount(arrearsPropertyAmount);
		contentVO.setArrearsParkingFee(arrearsParkingFee);
		contentVO.setTotalFee(totalFee);
		statisticsVO.setContent(contentVO);
		return statisticsVO;
	}
	
	/**
	 * 结算统计
	 */
	@Override
	public StatisticsVO statementCount(PropertyFinanceCountEntity query) {
		List<String> dayListOfMonth = DateUtils.getDayListOfMonth(query.getStartDate().toString(), query.getEndDate().toString());
		StatisticsVO statisticsVO = new StatisticsVO();
		statisticsVO.setCreateTime(dayListOfMonth);
		List<String> name = new ArrayList<>();
		name.add("已结算");
		name.add("待结算");
		statisticsVO.setName(name);
		// 已结算
		List<FinanceOrderEntityVO> settledStatisticsVO = orderMapper.settledStatistics(query);
		HashMap<String, FinanceOrderEntityVO> settledStatisticsMap = new HashMap<>();
		for (FinanceOrderEntityVO orderEntityVO : settledStatisticsVO) {
			settledStatisticsMap.put(orderEntityVO.getCreateTime(), orderEntityVO);
		}
		// 未结算
		List<FinanceOrderEntityVO> unsettlementStatisticsVO = orderMapper.unsettlementStatistics(query);
		HashMap<String, FinanceOrderEntityVO> unsettlementStatisticsMap = new HashMap<>();
		for (FinanceOrderEntityVO orderEntityVO : unsettlementStatisticsVO) {
			unsettlementStatisticsMap.put(orderEntityVO.getCreateTime(), orderEntityVO);
		}
		List<BigDecimal> settledAmount = new ArrayList<>();
		List<BigDecimal> unsettlementAmount = new ArrayList<>();
		for (String s : dayListOfMonth) {
			if (settledStatisticsMap.containsKey(s)) {
				settledAmount.add(settledStatisticsMap.get(s).getTotalMoney());
			} else {
				settledAmount.add(BigDecimal.ZERO);
			}
			if (unsettlementStatisticsMap.containsKey(s)) {
				unsettlementAmount.add(unsettlementStatisticsMap.get(s).getTotalMoney());
			} else {
				unsettlementAmount.add(BigDecimal.ZERO);
			}
		}
		ContentVO contentVO = new ContentVO();
		contentVO.setSettledAmount(settledAmount);
		contentVO.setUnsettlementAmount(unsettlementAmount);
		statisticsVO.setContent(contentVO);
		return statisticsVO;
	}
}
