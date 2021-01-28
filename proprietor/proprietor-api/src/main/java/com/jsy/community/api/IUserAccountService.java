package com.jsy.community.api;

import com.jsy.community.entity.UserTicketEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.RedbagQO;
import com.jsy.community.qo.UserTicketQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.UserAccountVO;

/**
 * @author chq459799974
 * @description 用户账户Service
 * @since 2021-01-08 11:13
 **/
public interface IUserAccountService {
	
	/**
	* @Description: 创建用户账户
	 * @Param: [uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	boolean createUserAccount(String uid);
	
	/**
	* @Description: 查询余额
	 * @Param: [uid]
	 * @Return: com.jsy.community.vo.UserAccountVO
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	UserAccountVO queryBalance(String uid);
	
	/**
	* @Description: 账户交易
	 * @Param: [userid, uAccountRecordQO]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	void trade(UserAccountTradeQO uAccountRecordQO);
	
	/**
	* @Description: 统计用户可用券张数
	 * @Param: [uid]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	Integer countTicketByUid(String uid);
	
	/**
	* @Description: 查用户拥有的所有券
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.UserTicketEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	PageInfo<UserTicketEntity> queryTickets(BaseQO<UserTicketQO> baseQO);
	
	/**
	* @Description: 单查
	 * @Param: [id, uid]
	 * @Return: com.jsy.community.entity.UserTicketEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	UserTicketEntity queryTicketById(Long id, String uid);
	
	/**
	* @Description: 使用
	 * @Param: [id, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	boolean useTicket(Long id, String uid);
	
	/**
	* @Description: 退回
	 * @Param: [id, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/28
	**/
	boolean rollbackTicket(Long id, String uid);
	
}
