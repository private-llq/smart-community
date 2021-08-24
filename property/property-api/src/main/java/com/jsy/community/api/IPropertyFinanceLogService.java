package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FinanceLogEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceLogQO;
import com.jsy.community.utils.PageInfo;

/**
 * @author DKS
 * @description 收款管理操作日志
 * @since 2021/8/23  16:34
 **/
public interface IPropertyFinanceLogService extends IService<FinanceLogEntity> {
	/**
	 * @Description: 保存收款管理操作日志
	 * @Param: [FinanceLogEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/23 16:35
	 **/
	void saveFinanceLog(FinanceLogEntity financeLogEntity);
	
	/**
	 * @Description: 收款管理操作日志分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.FinanceLogEntity>
	 * @Author: DKS
	 * @Date: 2021/08/23 16:35
	 **/
	PageInfo<FinanceLogEntity> queryFinanceLogPage(BaseQO<FinanceLogQO> baseQO);
}
