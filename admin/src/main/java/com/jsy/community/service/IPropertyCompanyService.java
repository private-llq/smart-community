package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PropertyCompanyQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;


/**
 * 物业公司
 */
public interface IPropertyCompanyService extends IService<PropertyCompanyEntity> {
	
	/**
	 * @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	PageInfo queryCompany(BaseQO<PropertyCompanyQO> baseQO);
	
	/**
	 * @Description: 添加操作员
	 * @Param: [propertyCompanyEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	Boolean addCompany(PropertyCompanyEntity propertyCompanyEntity);
	
	/**
	 * @Description: 编辑操作员
	 * @Param: [propertyCompanyEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	Boolean updateCompany(PropertyCompanyEntity propertyCompanyEntity);
	
	/**
	 * @Description: 删除操作员
	 * @Author: DKS
	 * @Date: 2021/10/15
	 */
	void deleteCompany(Long id);
	
	/**
	 * @Description: 物业公司列表查询
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.PropertyCompanyEntity>
	 * @Author: DKS
	 * @Date: 2021/10/19
	 **/
	List<PropertyCompanyEntity> queryCompanyList();
}
