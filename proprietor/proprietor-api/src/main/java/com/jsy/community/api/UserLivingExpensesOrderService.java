package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.vo.CebCallbackVO;
import com.jsy.community.vo.CebCashierDeskVO;
import com.jsy.community.vo.LivingExpensesOrderListVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费订单表服务
 * @Date: 2021/12/2 17:51
 * @Version: 1.0
 **/
public interface UserLivingExpensesOrderService extends IService<UserLivingExpensesOrderEntity> {
	
	/**
	 * @Description: 新增生活缴费订单记录
	 * @author: DKS
	 * @since: 2021/12/29 10:40
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.lang.String
	 */
	CebCashierDeskVO addUserLivingExpensesOrder(UserLivingExpensesBillEntity billEntity, String mobile);
	
	/**
	 * @Description: 查询当前用户生活缴费记录列表
	 * @author: DKS
	 * @since: 2021/12/29 11:52
	 * @Param: [userLivingExpensesOrderEntity]
	 * @return: java.util.List<com.jsy.community.entity.UserLivingExpensesOrderEntity>
	 */
	List<LivingExpensesOrderListVO> getListOfUserLivingExpensesOrder(UserLivingExpensesOrderEntity userLivingExpensesOrderEntity);
	
	/**
	 * @Description: 查询生活缴费记录详情
	 * @author: DKS
	 * @since: 2021/12/29 14:03
	 * @Param: [id]
	 * @return: com.jsy.community.entity.UserLivingExpensesOrderEntity
	 */
	UserLivingExpensesOrderEntity getById(Long id);

	/**
	 * @author: Pipi
	 * @description: 完成云缴费订单
	 * @param cebCallbackVO: 云缴费返回参数
	 * @return: {@link Boolean}
	 * @date: 2021/12/31 16:39
	 **/
	Boolean completeCebOrder(CebCallbackVO cebCallbackVO);
}
