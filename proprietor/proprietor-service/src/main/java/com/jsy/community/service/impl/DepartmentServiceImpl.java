package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.mapper.DepartmentMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, DepartmentEntity> implements IDepartmentService {
	
	@Autowired
	private DepartmentMapper departmentMapper;
	
	@Override
	public List<DepartmentEntity> listDepartment(Long id) {
		QueryWrapper<DepartmentEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id",id).last("limit 10");// TODO 最多展示10个 先写死
		return departmentMapper.selectList(queryWrapper);
	}
	
	@Override
	public List<DepartmentEntity> listDepartmentTel(Long id) {
		//1. 查询所有带有电话的部门
		QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id",id).isNotNull("phone");
		return departmentMapper.selectList(wrapper);
	}
}
