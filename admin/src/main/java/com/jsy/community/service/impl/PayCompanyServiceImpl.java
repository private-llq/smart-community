package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.mapper.PayCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.IPayCompanyService;
import com.jsy.community.utils.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 * 缴费单位 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-12-11
 */
@Service
public class PayCompanyServiceImpl extends ServiceImpl<PayCompanyMapper, PayCompanyEntity> implements IPayCompanyService {
	
	@Resource
	private PayCompanyMapper payCompanyMapper;
	
	@Override
	public PageInfo<PayCompanyEntity> getPayCompany(BaseQO<PayCompanyEntity> baseQO) {
		QueryWrapper<PayCompanyEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(baseQO.getQuery().getName())) {
			wrapper.like("name", baseQO.getQuery().getName());// 根据公司名模糊查询
		}
		Page<PayCompanyEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		payCompanyMapper.selectPage(page,wrapper);
		PageInfo<PayCompanyEntity> info = new PageInfo<>();
		BeanUtils.copyProperties(page,info);
		return info;
	}
	
	@Override
	public void addPayCompany(PayCompanyEntity companyEntity) {
		payCompanyMapper.insert(companyEntity);
	}
}
