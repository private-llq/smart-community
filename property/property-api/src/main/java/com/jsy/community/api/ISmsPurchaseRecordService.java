package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsPurchaseRecordEntity;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信购买记录
 * @author: DKS
 * @create: 2021-09-02 09:17
 **/
public interface ISmsPurchaseRecordService extends IService<SmsPurchaseRecordEntity> {
	/**
	 * @Description: 新增短信购买记录
	 * @Param: [smsPurchaseRecordEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021-09-02
	 **/
	boolean addSmsPurchaseRecord(SmsPurchaseRecordEntity smsPurchaseRecordEntity);
	
	/**
	 * @Description: 查询短信购买记录
	 * @Param: [adminCommunityIdList]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
	 * @Author: DKS
	 * @Date: 2021/09/14
	 **/
	List<SmsPurchaseRecordEntity> querySmsPurchaseRecord(Long companyId);
	
	/**
	 * @Description: 根据订单编号查询短信购买记录详情
	 * @author: DKS
	 * @since: 2021/12/13 14:35
	 * @Param: [orderNum]
	 * @return: com.jsy.community.entity.SmsPurchaseRecordEntity
	 */
	SmsPurchaseRecordEntity querySmsPurchaseByOrderNum(String orderNum);
	
	/**
	 * @Description: 修改短信购买记录
	 * @author: DKS
	 * @since: 2021/12/13 16:52
	 * @Param: [smsPurchaseRecordEntity]
	 * @return: void
	 */
	void updateSmsPurchase(SmsPurchaseRecordEntity smsPurchaseRecordEntity);
}
