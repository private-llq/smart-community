package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsPurchaseRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SmsPurchaseRecordQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信购买记录
 * @author: DKS
 * @create: 2021-12-09 15:30
 **/
public interface ISmsPurchaseRecordService extends IService<SmsPurchaseRecordEntity> {
	
	/**
	 * @Description: 查询短信购买记录
	 * @Param: [smsPurchaseRecordQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
	 * @Author: DKS
	 * @Date: 2021/12/9
	 *
	 * @return*/
	PageInfo<SmsPurchaseRecordEntity> querySmsPurchaseRecord(BaseQO<SmsPurchaseRecordQO> smsPurchaseRecordQO);
	
	/**
	 * @Description: 批量删除短信购买记录
	 * @author: DKS
	 * @since: 2021/12/9 17:07
	 * @Param: [ids]
	 * @return: boolean
	 */
	boolean deleteIds(List<Long> ids);
}
