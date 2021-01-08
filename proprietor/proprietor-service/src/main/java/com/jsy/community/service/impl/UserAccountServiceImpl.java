package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IUserAccountRecordService;
import com.jsy.community.api.IUserAccountService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.UserAccountEntity;
import com.jsy.community.entity.UserAccountRecordEntity;
import com.jsy.community.mapper.UserAccountMapper;
import com.jsy.community.qo.proprietor.UserAccountRecordQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.UserAccountVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 用户账户实现类
 * @since 2021-01-08 11:14
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserAccountServiceImpl implements IUserAccountService {
	
	@Autowired
	private UserAccountMapper userAccountMapper;
	
	@Autowired
	private IUserAccountRecordService userAccountRecordService;
	
	/**
	* @Description: 创建用户账户
	 * @Param: [uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	@Override
	public boolean createUserAccount(String uid){
		UserAccountEntity entity = new UserAccountEntity();
		entity.setUid(uid);
		entity.setBalance(new BigDecimal(0));
		return userAccountMapper.insert(entity) == 1;
	}
	
	/**
	 * @Description: 查询余额
	 * @Param: [uid]
	 * @Return: com.jsy.community.vo.UserAccountVO
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	 **/
	@Override
	public UserAccountVO queryBalance(String uid){
		UserAccountEntity userAccountEntity = userAccountMapper.selectOne(new QueryWrapper<UserAccountEntity>().select("uid", "balance").eq("uid", uid));
		UserAccountVO userAccountVO = new UserAccountVO();
		BeanUtils.copyProperties(userAccountEntity,userAccountVO);
		return userAccountVO;
	}
	
	/**
	 * @Description: 账户交易
	 * @Param: [userid, uAccountRecordQO]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void trade(UserAccountRecordQO uAccountRecordQO){
		int updateResult = 0;
		//处理(加或减)
		if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex().equals(uAccountRecordQO.getTradeType())){ //收入
			updateResult = userAccountMapper.updateBalance(uAccountRecordQO.getTradeAmount(), uAccountRecordQO.getUid());
		}else if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex().equals(uAccountRecordQO.getTradeType())){ //支出
			UserAccountVO userAccountVO = queryBalance(uAccountRecordQO.getUid());
			BigDecimal balance = userAccountVO.getBalance();
			if(balance.compareTo(uAccountRecordQO.getTradeAmount()) == -1){
				throw new ProprietorException("余额不足");
			}
			updateResult = userAccountMapper.updateBalance(uAccountRecordQO.getTradeAmount().negate(), uAccountRecordQO.getUid());
		} else{
			throw new ProprietorException("非法交易类型");
		}
		if(updateResult != 1){
			throw new ProprietorException("系统异常，交易失败");
		}
		//写流水
		UserAccountRecordEntity ucoinRecordEntity = new UserAccountRecordEntity();
		BeanUtils.copyProperties(uAccountRecordQO, ucoinRecordEntity);
		ucoinRecordEntity.setId(SnowFlake.nextId());
		ucoinRecordEntity.setBalance(queryBalance(uAccountRecordQO.getUid()).getBalance());//交易后余额
		boolean b = userAccountRecordService.addUcoinRecord(ucoinRecordEntity);
		if(!b){
			throw new ProprietorException("因账户流水记录失败，交易取消");
		}
	}
	
}
