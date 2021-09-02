package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsPurchaseRecordEntity;

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
}
