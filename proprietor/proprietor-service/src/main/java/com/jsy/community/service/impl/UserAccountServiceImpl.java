package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IUserAccountRecordService;
import com.jsy.community.api.IUserAccountService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.UserAccountEntity;
import com.jsy.community.entity.UserAccountRecordEntity;
import com.jsy.community.entity.UserTicketEntity;
import com.jsy.community.mapper.UserAccountMapper;
import com.jsy.community.mapper.UserTicketMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.UserTicketQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.utils.*;
import com.jsy.community.vo.UserAccountVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
	
	@Autowired
	private UserTicketMapper userTicketMapper;
	
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
	public void trade(UserAccountTradeQO tradeQO){
		int updateResult = 0;
		//处理(加或减)
		if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex().equals(tradeQO.getTradeType())){ //收入
			updateResult = userAccountMapper.updateBalance(tradeQO.getTradeAmount(), tradeQO.getUid());
		}else if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex().equals(tradeQO.getTradeType())){ //支出
			UserAccountVO userAccountVO = queryBalance(tradeQO.getUid());
			BigDecimal balance = userAccountVO.getBalance();
			if(balance.compareTo(tradeQO.getTradeAmount()) == -1){
				throw new ProprietorException("余额不足");
			}
			updateResult = userAccountMapper.updateBalance(tradeQO.getTradeAmount().negate(), tradeQO.getUid());
		} else{
			throw new ProprietorException("非法交易类型");
		}
		if(updateResult != 1){
			throw new ProprietorException("系统异常，交易失败");
		}
		//写流水
		UserAccountRecordEntity ucoinRecordEntity = new UserAccountRecordEntity();
		BeanUtils.copyProperties(tradeQO, ucoinRecordEntity);
		ucoinRecordEntity.setId(SnowFlake.nextId());
		ucoinRecordEntity.setBalance(queryBalance(tradeQO.getUid()).getBalance());//交易后余额
		boolean b = userAccountRecordService.addUcoinRecord(ucoinRecordEntity);
		if(!b){
			throw new ProprietorException("因账户流水记录失败，交易取消");
		}
	}
	
	/**
	* @Description: 查用户拥有的所有券
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.UserTicketEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Override
	public PageInfo<UserTicketEntity> queryTickets(BaseQO<UserTicketQO> baseQO){
		Page<UserTicketEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		UserTicketQO query = baseQO.getQuery();
		QueryWrapper<UserTicketEntity> queryWrapper = new QueryWrapper<UserTicketEntity>().select("*").eq("uid", query.getUid());
		//条件查询
		if(query.getType() != null){
			queryWrapper.eq("type",query.getType());
		}
		if(query.getStatus() != null){
			queryWrapper.eq("status",query.getStatus());
		}
		if(UserTicketQO.TICKET_UNEXPIRED.equals(query.getExpired())){
			queryWrapper.ge("expire_time",LocalDateTime.now());
		}else if(UserTicketQO.TICKET_EXPIRED.equals(query.getExpired())){
			queryWrapper.le("expire_time",LocalDateTime.now());
		}
		Page<UserTicketEntity> pageResult = userTicketMapper.selectPage(page, queryWrapper);
		PageInfo<UserTicketEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageResult,pageInfo);
		return pageInfo;
	}
	
	/**
	* @Description: id单查
	 * @Param: [id, uid]
	 * @Return: com.jsy.community.entity.UserTicketEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Override
	public UserTicketEntity queryTicketById(Long id, String uid){
		return userTicketMapper.selectOne(new QueryWrapper<UserTicketEntity>().select("*").eq("id",id).eq("uid",uid));
	}
	
	/**
	* @Description: 使用
	 * @Param: [id, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Override
	public boolean useTicket(Long id, String uid){
		//检查
		UserTicketEntity userTicketEntity = userTicketMapper.checkExpired(id);
		if(userTicketEntity == null){
			throw new ProprietorException("券已过期");
		}
		if(userTicketEntity.getStatus() == 1){
			throw new ProprietorException("券已使用");
		}
		return userTicketMapper.useTicket(id,uid) == 1;
	}
	
	/**
	* @Description: 退回
	 * @Param: [id, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Override
	public boolean rollbackTicket(Long id, String uid){
		return userTicketMapper.rollbackTicket(id,uid) == 1;
	}
	
}
