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
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.UserAccountMapper;
import com.jsy.community.mapper.UserTicketMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.UserTicketQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.UserAccountVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
		boolean b = userAccountRecordService.addAccountRecord(ucoinRecordEntity);
		if(!b){
			throw new ProprietorException("因账户流水记录失败，交易取消");
		}
	}
	
	/**
	* @Description: 统计用户可用券张数
	 * @Param: [uid]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Override
	public Integer countTicketByUid(String uid){
		return userTicketMapper.countAvailableTickets(uid);
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
		Page<UserTicketEntity> pageResult = userTicketMapper.queryUserTicketPage(page, query);
		//若是查询未过期的
		if(UserTicketQO.TICKET_UNEXPIRED.equals(query.getExpired())){
			for(UserTicketEntity ticketEntity : pageResult.getRecords()){
				ticketEntity.setMoneyStr(ticketEntity.getMoney().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
				ticketEntity.setLeastConsumeStr(ticketEntity.getLeastConsume().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
				//减少循环判断，直接设为未过期
				ticketEntity.setExpired(UserTicketQO.TICKET_UNEXPIRED);
			}
		}else if(UserTicketQO.TICKET_EXPIRED.equals(query.getExpired())){
			//若是查询已过期的
			for(UserTicketEntity ticketEntity : pageResult.getRecords()){
				ticketEntity.setMoneyStr(ticketEntity.getMoney().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
				ticketEntity.setLeastConsumeStr(ticketEntity.getLeastConsume().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
				//减少循环判断，直接设为已过期
				ticketEntity.setExpired(UserTicketQO.TICKET_EXPIRED);
			}
		}else{
			//若是查询全部
			for(UserTicketEntity ticketEntity : pageResult.getRecords()){
				ticketEntity.setMoneyStr(ticketEntity.getMoney().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
				ticketEntity.setLeastConsumeStr(ticketEntity.getLeastConsume().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
				if(LocalDateTime.now().isBefore(ticketEntity.getExpireTime())){
					//未过期
					ticketEntity.setExpired(UserTicketQO.TICKET_UNEXPIRED);
				}else{
					//已过期
					ticketEntity.setExpired(UserTicketQO.TICKET_EXPIRED);
				}
			}
		}
		PageInfo<UserTicketEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageResult,pageInfo);
		return pageInfo;
	}
	
	//TODO 券相关操作改为支付(账户操作)时抵用 结果由本平台计算 展示项目结束后修改
	/**
	* @Description: id单查
	 * @Param: [id, uid]
	 * @Return: com.jsy.community.entity.UserTicketEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	@Override
	public UserTicketEntity queryTicketById(Long id, String uid){
		//查用户券t_user_ticket
		UserTicketEntity userTicketEntity = userTicketMapper.selectOne(new QueryWrapper<UserTicketEntity>().select("ticket_id as id,status,expire_time").eq("id",id).eq("uid",uid));
		if(userTicketEntity == null){
			throw new ProprietorException(JSYError.BAD_REQUEST.getCode(),"查无此券");
		}
		//查券信息t_ticket
		UserTicketEntity ticketEntity = userTicketMapper.queryTicketById(userTicketEntity.getId());
		if(ticketEntity == null){
			throw new ProprietorException(JSYError.BAD_REQUEST.getCode(),"平台已无该券，请联系管理员");
		}
		ticketEntity.setStatus(userTicketEntity.getStatus());
		ticketEntity.setExpireTime(userTicketEntity.getExpireTime());
		return ticketEntity;
	}
	
	//TODO 券相关操作改为支付(账户操作)时抵用 结果由本平台计算 展示项目结束后修改
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
	
	//TODO 券相关操作改为支付(账户操作)时抵用 结果由本平台计算 展示项目结束后修改
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
	
	/**
	* @Description: 清理过期券(超期30天)
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	@Scheduled(cron = "0 0 3 * * ?")
	public void deleteExpiredTicket(){
		Long id = SnowFlake.nextId();
		userTicketMapper.deleteExpiredTicket(id);
	}
	
}
