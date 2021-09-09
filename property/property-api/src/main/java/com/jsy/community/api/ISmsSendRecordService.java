package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsSendRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.SmsSendRecordQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信发送记录
 * @author: DKS
 * @create: 2021-09-08 17:17
 **/
public interface ISmsSendRecordService extends IService<SmsSendRecordEntity> {
	/**
	 * @Description: 新增短信发送记录
	 * @Param: [smsSendRecordEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021-09-08
	 **/
	boolean addSmsSendRecord(SmsSendRecordEntity smsSendRecordEntity);
	
	/**
	 *@Author: DKS
	 *@Description: 批量新增短信发送记录
	 *@Param: [smsSendRecordEntity]
	 *@Return: Integer
	 *@Date: 2021/9/8 17:22
	 **/
	Integer saveSmsSendRecord(List<SmsSendRecordEntity> smsSendRecordEntityList);
	
	/**
	 * @Description: 分页查询短信发送记录
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsSendRecordEntity>>
	 * @Author: DKS
	 * @Date: 2021/09/08
	 **/
	PageInfo<SmsSendRecordEntity> querySmsSendRecord(BaseQO<SmsSendRecordQO> baseQO, List<Long> adminCommunityIdList);
}
