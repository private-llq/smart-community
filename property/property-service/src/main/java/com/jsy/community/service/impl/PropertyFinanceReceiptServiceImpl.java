package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.api.IPropertyFinanceReceiptService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.mapper.PropertyFinanceReceiptMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author chq459799974
 * @description 物业财务-收款单 服务实现类
 * @since 2021-04-21 17:00
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyFinanceReceiptServiceImpl implements IPropertyFinanceReceiptService {
    
    @Autowired
    private PropertyFinanceReceiptMapper propertyFinanceReceiptMapper;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;
    
    /**
    * @Description: 分页查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo
     * @Author: chq459799974
     * @Date: 2021/4/21
    **/
    @Override
    public PageInfo queryPage(BaseQO<PropertyFinanceReceiptEntity> baseQO){
        PropertyFinanceReceiptEntity query = baseQO.getQuery();
        Page<PropertyFinanceReceiptEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page,baseQO);
        QueryWrapper<PropertyFinanceReceiptEntity> queryWrapper = new QueryWrapper<>();
	    queryWrapper.select("*");
        queryWrapper.eq("community_id",query.getCommunityId());
	    queryWrapper.orderByDesc("create_time");
        if(!StringUtils.isEmpty(query.getReceiptNum())){
        	queryWrapper.like("receipt_num",query.getReceiptNum());
        }
	    PropertyFinanceOrderEntity orderQuery = null;
//        if(!StringUtils.isEmpty(query.getOrderNum())){
//	        orderQuery = new PropertyFinanceOrderEntity();
//	        orderQuery.setOrderNum(query.getOrderNum());
//        }
	    //带账单号模糊查询
	    if(!StringUtils.isEmpty(query.getOrderNum())){
		    List<String> receiptNumsList = propertyFinanceOrderService.queryReceiptNumsListByOrderNumLike(query.getOrderNum());
		    if(CollectionUtils.isEmpty(receiptNumsList)){
			    return new PageInfo();
		    }
		        queryWrapper.in("receipt_num",receiptNumsList);
	    }
        if(query.getStartDate() != null){
        	queryWrapper.ge("create_time",query.getStartDate());
        }
	    if(query.getEndDate() != null){
		    queryWrapper.le("create_time",query.getEndDate());
	    }
	    if(query.getTransactionType() != null){
		    queryWrapper.eq("transaction_type",query.getTransactionType());
	    }
	    if(query.getTransactionNo() != null){
		    queryWrapper.like("transaction_no",query.getTransactionNo());
	    }
        //分页查询
        Page<PropertyFinanceReceiptEntity> pageData = propertyFinanceReceiptMapper.selectPage(page,queryWrapper);
        if(CollectionUtils.isEmpty(pageData.getRecords())){
            return new PageInfo();
        }
        //收款合计
	    BigDecimal totalReceipt = new BigDecimal(0);
        HashSet<String> receiptNums = new HashSet<>();
        for(PropertyFinanceReceiptEntity entity : pageData.getRecords()){
            receiptNums.add(entity.getReceiptNum());
	        totalReceipt = totalReceipt.add(entity.getReceiptMoney());
        }
        //关联查询账单
        List<PropertyFinanceOrderEntity> orderTotalList = propertyFinanceOrderService.queryByReceiptNums(receiptNums,orderQuery);
        for(PropertyFinanceReceiptEntity receiptEntity : pageData.getRecords()){
        	List<PropertyFinanceOrderEntity> orderList = new ArrayList();
        	for(PropertyFinanceOrderEntity orderEntity : orderTotalList){
	            if(receiptEntity.getReceiptNum().equals(orderEntity.getReceiptNum())){
		            orderList.add(orderEntity);
	            }
	        }
	        receiptEntity.setOrderList(orderList);
        }
        PageInfo<PropertyFinanceReceiptEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData,pageInfo);
        Map<String,Object> extra = new HashMap<>();
	    extra.put("totalReceipt",totalReceipt);
	    pageInfo.setExtra(extra);
        return pageInfo;
    }
	
    /**
    * @Description: 收款单号批量查 单号-收款单数据 映射
     * @Param: [nums]
     * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PropertyFinanceReceiptEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    @Override
	public Map<String,PropertyFinanceReceiptEntity> queryByReceiptNumBatch(Collection<String> nums){
    	if(CollectionUtils.isEmpty(nums) || (nums.size() == 1 && nums.contains(null))){
    		return new HashMap<>();
	    }
    	return propertyFinanceReceiptMapper.queryByReceiptNumBatch(nums);
	}
	
	/**
	* @Description: 条件查询批量收款单号
	 * @Param: [query]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	@Override
	public List<String> queryReceiptNumsByCondition(PropertyFinanceReceiptEntity query){
        return propertyFinanceReceiptMapper.queryReceiptNumsByCondition(query);
	}

	/**
	 *@Author: Pipi
	 *@Description: 查询导出的收款单列表
	 *@Param: receiptEntity:
	 *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceReceiptEntity>
	 *@Date: 2021/4/26 9:34
	 **/
	@Override
	public List<PropertyFinanceReceiptEntity> queryExportReceiptList(PropertyFinanceReceiptEntity receiptEntity) {
		QueryWrapper<PropertyFinanceReceiptEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("*");
		queryWrapper.eq("community_id",receiptEntity.getCommunityId());
		queryWrapper.orderByDesc("create_time");
		if(!StringUtils.isEmpty(receiptEntity.getReceiptNum())){
			queryWrapper.like("receipt_num",receiptEntity.getReceiptNum());
		}
		//带账单号模糊查询
		if(!StringUtils.isEmpty(receiptEntity.getOrderNum())){
			List<String> receiptNumsList = propertyFinanceOrderService.queryReceiptNumsListByOrderNumLike(receiptEntity.getOrderNum());
			if (CollectionUtils.isEmpty(receiptNumsList)) {
				receiptNumsList.add("0");
			}
			queryWrapper.in("receipt_num", receiptNumsList);
		}
		if(receiptEntity.getStartDate() != null){
			queryWrapper.ge("create_time",receiptEntity.getStartDate());
		}
		if(receiptEntity.getEndDate() != null){
			queryWrapper.le("create_time",receiptEntity.getEndDate());
		}
		if(receiptEntity.getTransactionType() != null){
			queryWrapper.eq("transaction_type",receiptEntity.getTransactionType());
		}
		if(receiptEntity.getTransactionNo() != null){
			queryWrapper.like("transaction_no",receiptEntity.getTransactionNo());
		}
		List<PropertyFinanceReceiptEntity> receiptEntities = propertyFinanceReceiptMapper.selectList(queryWrapper);
		if (receiptEntity.getExportType() == 2 && !CollectionUtils.isEmpty(receiptEntities)) {
			// 查询管理账单列表
			HashSet<String> receiptNums = new HashSet<>();
			for (PropertyFinanceReceiptEntity entity : receiptEntities) {
				receiptNums.add(entity.getReceiptNum());
			}
			PropertyFinanceOrderEntity orderQuery = null;
			//关联查询账单
			List<PropertyFinanceOrderEntity> orderTotalList = propertyFinanceOrderService.queryByReceiptNums(receiptNums,orderQuery);
			for (PropertyFinanceReceiptEntity entity : receiptEntities) {
				if (!CollectionUtils.isEmpty(orderTotalList)) {
					List<PropertyFinanceOrderEntity> orderList = new ArrayList();
					for (PropertyFinanceOrderEntity orderEntity : orderTotalList) {
						if (orderEntity.getReceiptNum().equals("8888")) {
							System.out.println(1);
						}
						if(orderEntity.getReceiptNum().equals(entity.getReceiptNum())){
							orderList.add(orderEntity);
						}
					}
					entity.setOrderList(orderList);
				}
			}
		}
		return receiptEntities;
	}
}
