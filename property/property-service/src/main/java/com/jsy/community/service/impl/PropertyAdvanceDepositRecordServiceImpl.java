package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.IPropertyAdvanceDepositRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.entity.property.PropertyAdvanceDepositRecordEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositRecordQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业预存款余额明细记录表
 * @author: DKS
 * @create: 2021-08-12 14:15
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PropertyAdvanceDepositRecordServiceImpl extends ServiceImpl<PropertyAdvanceDepositRecordMapper, PropertyAdvanceDepositRecordEntity> implements IPropertyAdvanceDepositRecordService {
    
    @Autowired
    private PropertyAdvanceDepositRecordMapper propertyAdvanceDepositRecordMapper;
	
	@Autowired
	private PropertyAdvanceDepositMapper propertyAdvanceDepositMapper;
	
	@Autowired
	private HouseMapper houseMapper;
	
	@Autowired
	private ProprietorMapper proprietorMapper;
	
	@Autowired
	private PropertyFinanceOrderMapper propertyFinanceOrderMapper;
	
	/**
	 * @Description: 新增预存款变更明细记录
	 * @Param: [propertyAdvanceDepositRecordEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/12
	 **/
    @Override
    @LcnTransaction
    public boolean addPropertyAdvanceDepositRecord(PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity){
	    int row;
//	    //根据预存款id查询是否新增
//	    List<PropertyAdvanceDepositRecordEntity> propertyAdvanceDepositRecordEntities = propertyAdvanceDepositRecordMapper.queryAdvanceDepositRecordList(
//	    	propertyAdvanceDepositRecordEntity.getAdvanceDepositId(), propertyAdvanceDepositRecordEntity.getCommunityId());
//	    if (propertyAdvanceDepositRecordEntities.size() == 0) {
	    	// 新增
		    propertyAdvanceDepositRecordEntity.setId(SnowFlake.nextId());
		    row = propertyAdvanceDepositRecordMapper.insert(propertyAdvanceDepositRecordEntity);
//	    } else {
//	    	// 不是新增，查最新一次记录并设置余额明细
//		    PropertyAdvanceDepositRecordEntity entity = propertyAdvanceDepositRecordMapper.queryMaxCreateTimeRecord(
//		    	propertyAdvanceDepositRecordEntity.getAdvanceDepositId(), propertyAdvanceDepositRecordEntity.getCommunityId());
//		    propertyAdvanceDepositRecordEntity.setId(SnowFlake.nextId());
//		    row = propertyAdvanceDepositRecordMapper.insert(propertyAdvanceDepositRecordEntity);
//	    }
	    
        return row == 1;
    }
    
    /**
     * @Description: 预存款分页查询变更明细
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyAdvanceDepositRecordEntity>>
     * @Author: DKS
     * @Date: 2021/08/12
     **/
    @Override
    public PageInfo<PropertyAdvanceDepositRecordEntity> queryPropertyAdvanceDepositRecord(BaseQO<PropertyAdvanceDepositRecordQO> baseQO) {
	    PropertyAdvanceDepositRecordQO query = baseQO.getQuery();
        Page<PropertyAdvanceDepositRecordEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<PropertyAdvanceDepositRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id", query.getCommunityId());
        //是否查详情
        if (query.getAdvanceDepositId() != null) {
            queryWrapper.eq("advance_deposit_id", query.getAdvanceDepositId());
        }
        queryWrapper.orderByDesc("create_time");
        Page<PropertyAdvanceDepositRecordEntity> pageData = propertyAdvanceDepositRecordMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
	    for (PropertyAdvanceDepositRecordEntity entity : pageData.getRecords()) {
		    // 补充类型名称
	        entity.setTypeName(entity.getType() == 1 ? "预存款支付" : "预存款充值：后台充值");
	    }
        PageInfo<PropertyAdvanceDepositRecordEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }
	
	/**
	 * @Description: 查询预存款明细记录导出数据
	 * @Param: PropertyAdvanceDepositRecordEntity
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
	 * @Author: DKS
	 * @Date: 2021/8/17
	 **/
	@Override
	public List<PropertyAdvanceDepositRecordEntity> queryExportHouseExcel(PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity) {
		List<PropertyAdvanceDepositRecordEntity> propertyAdvanceDepositRecordEntities;
		QueryWrapper<PropertyAdvanceDepositRecordEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id", propertyAdvanceDepositRecordEntity.getCommunityId());
		//是否查详情
		if (propertyAdvanceDepositRecordEntity.getAdvanceDepositId() != null) {
			queryWrapper.eq("advance_deposit_id", propertyAdvanceDepositRecordEntity.getAdvanceDepositId());
		}
		queryWrapper.orderByDesc("create_time");
		propertyAdvanceDepositRecordEntities = propertyAdvanceDepositRecordMapper.selectList(queryWrapper);
		if (CollectionUtils.isEmpty(propertyAdvanceDepositRecordEntities)) {
			return propertyAdvanceDepositRecordEntities;
		}
		for (PropertyAdvanceDepositRecordEntity entity : propertyAdvanceDepositRecordEntities) {
			// 补充类型名称
			entity.setTypeName(entity.getType() == 1 ? "预存款支付" : "预存款充值：后台充值");
		}
		return propertyAdvanceDepositRecordEntities;
	}
	
	/**
	 * @Description: 通过id获取预存款明细记录打印信息
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/08/20
	 **/
	@Override
	public PropertyAdvanceDepositRecordEntity getAdvanceDepositRecordById(Long id, Long communityId) {
		PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = propertyAdvanceDepositRecordMapper.selectById(id);
		// 通过预存款id查询房屋id再查询房屋地址
		PropertyAdvanceDepositEntity entity = propertyAdvanceDepositMapper.selectById(propertyAdvanceDepositRecordEntity.getAdvanceDepositId());
		// 补充房屋地址数据
		HouseEntity houseEntity = houseMapper.selectById(entity.getHouseId());
		propertyAdvanceDepositRecordEntity.setHouseAddress(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
		// 补充收款人真实姓名数据
		ProprietorEntity proprietorEntity = proprietorMapper.queryNameAndMobileByHouseId(entity.getHouseId(), communityId);
		if (proprietorEntity.getRealName() != null) {
			propertyAdvanceDepositRecordEntity.setRealName(proprietorEntity.getRealName());
		}
		// 补充收款方式
		if (propertyAdvanceDepositRecordEntity.getType() == 2) {
			if (propertyAdvanceDepositRecordEntity.getDepositAmount() != null || propertyAdvanceDepositRecordEntity.getDepositAmount().compareTo(BigDecimal.ZERO) == 1) {
				propertyAdvanceDepositRecordEntity.setPayTypeName("充值");
			} else if (propertyAdvanceDepositRecordEntity.getPayAmount() != null || propertyAdvanceDepositRecordEntity.getPayAmount().compareTo(BigDecimal.ZERO) == 1) {
				propertyAdvanceDepositRecordEntity.setPayTypeName("提现");
			}
		} else if (propertyAdvanceDepositRecordEntity.getType() == 1) {
			PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderMapper.selectById(propertyAdvanceDepositRecordEntity.getOrderId());
			if (propertyFinanceOrderEntity.getPayType() != null) {
				propertyAdvanceDepositRecordEntity.setPayTypeName(propertyFinanceOrderEntity.getPayType() == 1 ? "app支付" : "物业后台");
			}
		}
		return propertyAdvanceDepositRecordEntity;
	}
}