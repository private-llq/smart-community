package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PropertyCompanyQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.IPropertyCompanyService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 物业公司
 */
@Slf4j
@Service("propertyCompanyService")
public class PropertyCompanyServiceImpl extends ServiceImpl<PropertyCompanyMapper, PropertyCompanyEntity> implements IPropertyCompanyService {
	
	@Resource
	private PropertyCompanyMapper propertyCompanyMapper;
	
	/**
	 * @Description: 物业公司条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	@Override
	public PageInfo queryCompany(BaseQO<PropertyCompanyQO> baseQO){
		PropertyCompanyQO query = baseQO.getQuery();
		Page<PropertyCompanyEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<PropertyCompanyEntity> queryWrapper = new QueryWrapper<>();
		//是否查详情
		if (query.getId() != null) {
			queryWrapper.eq("id", query.getId());
		}
		//是否查物业公司名称关键字
		if (StringUtils.isNotBlank(query.getName())) {
			queryWrapper.like("name",query.getName());
		}
		//是否查地区
		if (StringUtils.isNotBlank(query.getRegion())) {
			queryWrapper.like("region",query.getRegion());
		}
		queryWrapper.orderByDesc("create_time");
		Page<PropertyCompanyEntity> pageData = propertyCompanyMapper.selectPage(page, queryWrapper);
		PageInfo<PropertyCompanyEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
	
	/**
	 * @Description: 添加物业公司
	 * @Param: [propertyCompanyEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public Boolean addCompany(PropertyCompanyEntity propertyCompanyEntity){
		//生成UUID 和 ID
		propertyCompanyEntity.setId(SnowFlake.nextId());
		return propertyCompanyMapper.insert(propertyCompanyEntity) == 1;
	}
	
	/**
	 * @Description: 编辑物业公司
	 * @Param: [propertyCompanyEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/15
	 **/
	@Override
	public Boolean updateCompany(PropertyCompanyEntity propertyCompanyEntity){
		//查询id
		PropertyCompanyEntity entity = propertyCompanyMapper.selectOne(new QueryWrapper<PropertyCompanyEntity>()
			.eq("id", propertyCompanyEntity.getId()));
		if(entity == null){
			throw new AdminException("物业公司不存在！");
		}
		return propertyCompanyMapper.updateById(propertyCompanyEntity) == 1;
	}
	
	/**
	 * @Description: 删除物业公司
	 * @Author: DKS
	 * @Date: 2021/10/15
	 */
	public void deleteCompany(Long id) {
		//查询id
		PropertyCompanyEntity entity = propertyCompanyMapper.selectById(id);
		if(entity == null){
			throw new AdminException("物业公司不存在！");
		}
		int i = propertyCompanyMapper.deleteById(id);
		if (i != 1) {
			throw new AdminException(JSYError.INTERNAL.getCode(),"删除失败");
		}
	}
}