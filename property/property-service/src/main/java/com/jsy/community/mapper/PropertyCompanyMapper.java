package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PropertyCompanyEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @program: com.jsy.community
 * @description: 物业公司
 * @author: Hu
 * @create: 2021-08-20 15:05
 **/
public interface PropertyCompanyMapper extends BaseMapper<PropertyCompanyEntity> {
	/**
	 *@Author: DKS
	 *@Description: 查询物业公司名称
	 *@Param: companyId
	 *@Return: java.util.String
	 *@Date: 2021/8/25 16:46
	 **/
	String selectCompanyNameByCompanyId(Long companyId);
	
	/**
	 *@Author: DKS
	 *@Description: 更新物业公司短信剩余数量
	 *@Param: number
	 *@Return: java.util.Integer
	 *@Date: 2021/9/1 14:51
	 **/
	Integer updateSMSQuantity(@Param("number") int number, @Param("companyId")Long companyId);
}
