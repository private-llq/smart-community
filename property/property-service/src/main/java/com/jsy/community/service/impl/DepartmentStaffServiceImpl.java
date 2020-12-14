package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.mapper.DepartmentStaffMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@DubboService(version = Const.version, group = Const.group_property)
public class DepartmentStaffServiceImpl extends ServiceImpl<DepartmentStaffMapper, DepartmentStaffEntity> implements IDepartmentStaffService {
	
	@Autowired
	private DepartmentStaffMapper staffMapper;
	
	@Override
	public PageInfo<DepartmentStaffEntity> listDepartmentStaff(Long departmentId, BaseQO<DepartmentStaffEntity> staffEntity) {
		DepartmentStaffEntity entity = staffEntity.getQuery();
		QueryWrapper<DepartmentStaffEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("department_id",departmentId).orderByDesc("update_time");
		if (!StringUtils.isEmpty(entity.getPerson())) {
			queryWrapper.like("person",entity.getPerson());
		}
		if (!StringUtils.isEmpty(entity.getPhone())) {
			queryWrapper.like("phone",entity.getPhone());
		}
		Page<DepartmentStaffEntity> page = new Page<>(staffEntity.getPage(), staffEntity.getSize());
		staffMapper.selectPage(page,queryWrapper);
		
		PageInfo<DepartmentStaffEntity> departmentStaffEntityPageInfo = new PageInfo<>();
		BeanUtils.copyProperties(page,departmentStaffEntityPageInfo);
		return departmentStaffEntityPageInfo;
	}
	
	@Override
	public void addDepartmentStaff(DepartmentStaffEntity staffEntity) {
		staffEntity.setId(SnowFlake.nextId());
		staffMapper.insert(staffEntity);
	}
	
	@Override
	public void updateDepartmentStaff(DepartmentStaffEntity departmentStaffEntity) {
		staffMapper.updateById(departmentStaffEntity);
	}
	
	@Override
	public void deleteStaffByIds(Integer[] ids) {
		staffMapper.deleteBatchIds(Arrays.asList(ids));
	}
}
