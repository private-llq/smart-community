package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.mapper.CommonConstMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.ICommonConstService;
import com.jsy.community.utils.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * <p>
 * 公共常量表 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
@Service
public class CommonConstServiceImpl extends ServiceImpl<CommonConstMapper, CommonConst> implements ICommonConstService {
	
	@Resource
	private CommonConstMapper commonConstMapper;
	
	@Override
	public PageInfo<CommonConst> getConst(Integer constId, @RequestBody BaseQO<CommonConst> baseQO) {
		QueryWrapper<CommonConst> wrapper = new QueryWrapper<>();
		wrapper.eq("type_id", constId);
		if (!StringUtils.isEmpty(baseQO.getQuery().getConstName())) {
			wrapper.like("const_name", baseQO.getQuery().getConstName());
		}
		
		Page<CommonConst> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		commonConstMapper.selectPage(page, wrapper);
		
		PageInfo<CommonConst> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(page, pageInfo);
		return pageInfo;
	}
	
	@Override
	public void addConst(CommonConst commonConst) {
		commonConstMapper.insert(commonConst);
	}
}
