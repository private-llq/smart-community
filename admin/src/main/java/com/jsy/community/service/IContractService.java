package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ContractQO;
import com.jsy.community.utils.PageInfo;

/**
 * 合同管理Service
 * @author DKS
 * @since 2021-10-29
 */
public interface IContractService extends IService<AssetLeaseRecordEntity> {
	/**
	 * @Description: 合同管理分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.AssetLeaseRecordEntity>
	 * @Author: DKS
	 * @since 2021/10/29  11:01
	 **/
	PageInfo<AssetLeaseRecordEntity> queryContractPage(BaseQO<ContractQO> baseQO);
}
