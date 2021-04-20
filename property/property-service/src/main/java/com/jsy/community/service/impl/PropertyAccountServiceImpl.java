package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IPropertyAccountService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyAccountBankEntity;
import com.jsy.community.mapper.PropertyAccountBankMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chq459799974
 * @description 社区账户实现类
 * @since 2021-04-20 17:38
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyAccountServiceImpl implements IPropertyAccountService {
	
	@Autowired
	private PropertyAccountBankMapper propertyAccountBankMapper;
	
	/**
	* @Description: 社区id查对公账号 - 银行卡
	 * @Param: [communityId]
	 * @Return: com.jsy.community.entity.property.PropertyAccountBankEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/20
	**/
	@Override
	public PropertyAccountBankEntity queryBankAccount(Long communityId){
		return propertyAccountBankMapper.selectOne(new QueryWrapper<PropertyAccountBankEntity>().eq("community_id",communityId));
	}
}
