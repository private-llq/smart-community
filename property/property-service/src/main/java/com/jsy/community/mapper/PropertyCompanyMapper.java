package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PropertyCompanyEntity;

/**
 * @program: com.jsy.community
 * @description: 物业公司
 * @author: Hu
 * @create: 2021-08-20 15:05
 **/
public interface PropertyCompanyMapper extends BaseMapper<PropertyCompanyEntity> {
	/**
	 *@Author: DKS
	 *@Description: 查询communityIds下每月的物业费统计
	 *@Param: companyId
	 *@Return: java.util.String
	 *@Date: 2021/8/25 16:46
	 **/
	String selectCompanyNameByCompanyId(Long companyId);
}
