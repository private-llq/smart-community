package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SmsEntity;
import com.jsy.community.mapper.SmsMapper;
import com.jsy.community.service.ISmsService;
import com.jsy.community.utils.AESOperator;
import com.jsy.community.utils.SnowFlake;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 短信配置
 * @author: DKS
 * @since: 2021/12/6 11:27
 */
@Service
public class SmsServiceImpl extends ServiceImpl<SmsMapper, SmsEntity> implements ISmsService {
	
	@Resource
	private SmsMapper smsMapper;
	
	/**
	 * @Description: 新增或修改短信配置
	 * @author: DKS
	 * @since: 2021/12/6 11:21
	 * @Param: [smsEntity]
	 * @return: boolean
	 */
	@Override
	public boolean addSmsSetting(SmsEntity smsEntity) {
		int row;
		// 加密
		smsEntity.setAccessKeyId(AESOperator.encrypt(smsEntity.getAccessKeyId()));
		smsEntity.setAccessKeySecret(AESOperator.encrypt(smsEntity.getAccessKeySecret()));
		// 查询是否有阿里云短信配置
		List<SmsEntity> smsEntities = smsMapper.selectList(new QueryWrapper<SmsEntity>().select("*"));
		if (smsEntities != null && smsEntities.size() > 0) {
			row = smsMapper.update(smsEntity,new QueryWrapper<SmsEntity>().eq("deleted", 0));
		} else {
			smsEntity.setId(SnowFlake.nextId());
			row = smsMapper.insert(smsEntity);
		}
		return row == 1;
	}
	
	/**
	 * @Description: 查询短信配置
	 * @author: DKS
	 * @since: 2021/12/6 11:35
	 * @Param: []
	 * @return: com.jsy.community.entity.SmsEntity
	 */
	@Override
	public SmsEntity querySmsSetting() {
		SmsEntity smsEntity = smsMapper.selectOne(new QueryWrapper<SmsEntity>().select("*"));
		smsEntity.setAccessKeyId(AESOperator.decrypt(smsEntity.getAccessKeyId()));
		smsEntity.setAccessKeySecret(AESOperator.decrypt(smsEntity.getAccessKeySecret()));
		return smsEntity;
	}
}
