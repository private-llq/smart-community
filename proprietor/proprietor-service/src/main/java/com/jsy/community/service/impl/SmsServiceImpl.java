package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.ISmsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.SmsEntity;
import com.jsy.community.mapper.SmsMapper;
import com.jsy.community.utils.AESOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


/**
 * @Description: 短信业务实现
 * @author: DKS
 * @since: 2021/12/7 10:54
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class SmsServiceImpl implements ISmsService {
	
	@Resource
	private SmsMapper smsMapper;
	
	/**
	 * @Description: 查询短信配置
	 * @author: DKS
	 * @since: 2021/12/7 10:55
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