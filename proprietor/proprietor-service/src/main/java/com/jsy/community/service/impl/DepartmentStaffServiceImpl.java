package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.mapper.DepartmentStaffMapper;
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
@DubboService(version = Const.version, group = Const.group)
public class DepartmentStaffServiceImpl extends ServiceImpl<DepartmentStaffMapper, DepartmentStaffEntity> implements IDepartmentStaffService {
	
	@Autowired
	private DepartmentStaffMapper departmentStaffMapper;
	
	@Override
	public List<DepartmentStaffEntity> listStaffPhone(Long departmentId) {
		QueryWrapper<DepartmentStaffEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("department_id",departmentId);
		return departmentStaffMapper.selectList(queryWrapper);
	}
}
