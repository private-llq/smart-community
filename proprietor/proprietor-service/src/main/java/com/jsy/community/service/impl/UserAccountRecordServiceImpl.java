package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IUserAccountRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.UserAccountRecordEntity;
import com.jsy.community.mapper.UserAccountRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.UserAccountRecordQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chq459799974
 * @description 用户账户流水实现类
 * @since 2021-01-08 11:16
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserAccountRecordServiceImpl implements IUserAccountRecordService {
	
	@Autowired
	private UserAccountRecordMapper userAccountRecordMapper;
	
	/**
	* @Description: 新增账户流水
	 * @Param: [userAccountRecordEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	@Override
	public boolean addAccountRecord(UserAccountRecordEntity userAccountRecordEntity){
		return userAccountRecordMapper.insert(userAccountRecordEntity) == 1;
	}
	
	/**
	* @Description: 查询账户流水
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: chq459799974
	 * @Date: 2021/2/7
	**/
	@Override
	public PageInfo queryAccountRecord(BaseQO<UserAccountRecordQO> baseQO){
		Page<UserAccountRecordEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		UserAccountRecordQO query = baseQO.getQuery();
		QueryWrapper<UserAccountRecordEntity> queryWrapper = new QueryWrapper<UserAccountRecordEntity>()
			.select("*")
			.eq("uid", query.getUid())
			.orderByDesc("create_time");
		if(query.getTradeType() != null){
			queryWrapper.eq("trade_type",query.getTradeType());
		}
		Page<UserAccountRecordEntity> resultPage = userAccountRecordMapper.selectPage(page, queryWrapper);
		for(UserAccountRecordEntity userAccountRecordEntity : resultPage.getRecords()){
			userAccountRecordEntity.setTradeTypeStr(PaymentEnum.TradeTypeEnum.tradeTypeMap.get(userAccountRecordEntity.getTradeType()));
			userAccountRecordEntity.setTradeFromStr(PaymentEnum.TradeFromEnum.tradeFromMap.get(userAccountRecordEntity.getTradeFrom()));
		}
		PageInfo<UserAccountRecordEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(resultPage,pageInfo);
		return pageInfo;
	}
	
}
