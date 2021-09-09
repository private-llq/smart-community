package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.SmsSendRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信发送记录
 * @author: DKS
 * @create: 2021-09-08 17:17
 **/
public interface SmsSendRecordMapper extends BaseMapper<SmsSendRecordEntity> {
	/**
	 *@Author: DKS
	 *@Description: 批量新增短信发送记录
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/9/8 17:19
	 **/
	Integer saveSmsSendRecord(@Param("list") List<SmsSendRecordEntity> smsSendRecordEntityList);
}
