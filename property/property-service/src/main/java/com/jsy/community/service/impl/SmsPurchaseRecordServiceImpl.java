package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ISmsPurchaseRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.SmsPurchaseRecordEntity;
import com.jsy.community.mapper.SmsPurchaseRecordMapper;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信购买记录
 * @author: DKS
 * @create: 2021-09-02 09:17
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class SmsPurchaseRecordServiceImpl extends ServiceImpl<SmsPurchaseRecordMapper, SmsPurchaseRecordEntity> implements ISmsPurchaseRecordService {
    
    @Autowired
    private SmsPurchaseRecordMapper smsPurchaseRecordMapper;
	
	/**
	 * @Description: 新增短信购买记录
	 * @Param: [smsPurchaseRecordEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021-09-02
	 **/
	@Override
	public boolean addSmsPurchaseRecord(SmsPurchaseRecordEntity smsPurchaseRecordEntity) {
		int row;
		smsPurchaseRecordEntity.setId(SnowFlake.nextId());
		row = smsPurchaseRecordMapper.insert(smsPurchaseRecordEntity);
		return row == 1;
	}
	
	/**
	 * @Description: 查询短信购买记录
	 * @Param: [adminCommunityIdList]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
	 * @Author: DKS
	 * @Date: 2021/09/14
	 **/
	@Override
	public List<SmsPurchaseRecordEntity> querySmsPurchaseRecord(Long companyId) {
		QueryWrapper<SmsPurchaseRecordEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("company_id", companyId);
		queryWrapper.orderByDesc("create_time");
		List<SmsPurchaseRecordEntity> smsPurchaseRecordEntities = smsPurchaseRecordMapper.selectList(queryWrapper);
		if (CollectionUtils.isEmpty(smsPurchaseRecordEntities)) {
			return new ArrayList<>();
		}
		// 补充状态
		for (SmsPurchaseRecordEntity smsPurchaseRecordEntity : smsPurchaseRecordEntities) {
			if (smsPurchaseRecordEntity.getStatus() == 1) {
				smsPurchaseRecordEntity.setStatusName("已支付 已收货");
			}
		}
		return smsPurchaseRecordEntities;
	}
}