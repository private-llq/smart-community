package com.jsy.community.service.impl;

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
}