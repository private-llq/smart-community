package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.mapper.DepartmentMapper;
import com.jsy.community.mapper.DepartmentStaffMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@DubboService(version = Const.version, group = Const.group_property)
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, DepartmentEntity> implements IDepartmentService {
	
	@Autowired
	private DepartmentMapper departmentMapper;
	
	@Autowired
	private DepartmentStaffMapper departmentStaffMapper;
	
	@Override
	public List<DepartmentEntity> listDepartment(Long communityId) {
		QueryWrapper<DepartmentEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id",communityId);
		return departmentMapper.selectList(queryWrapper);
	}
	
	@Override
	public void addDepartment(DepartmentEntity departmentEntity) {
		departmentEntity.setId(SnowFlake.nextId());
		departmentMapper.insert(departmentEntity);
	}
	
	@Override
	public void updateDepartment(DepartmentEntity departmentEntity) {
		departmentMapper.updateById(departmentEntity);
	}
	
	@Override
	public void deleteDepartment(Long id) {
		QueryWrapper<DepartmentStaffEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("department_id",id);
		List<DepartmentStaffEntity> staffEntities = departmentStaffMapper.selectList(queryWrapper);
		if (!CollectionUtils.isEmpty(staffEntities)) {
			throw new PropertyException("请先删除部门下的员工");
		}
		departmentMapper.deleteById(id);
	}
}
