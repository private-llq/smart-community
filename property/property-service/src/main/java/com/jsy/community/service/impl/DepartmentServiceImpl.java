package com.jsy.community.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.mapper.DepartmentMapper;
import com.jsy.community.mapper.DepartmentStaffMapper;
import com.jsy.community.qo.DepartmentQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.DepartmentVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
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
	public void addDepartment(DepartmentQO departmentEntity) {
		// 判断是否已经存在同名部门
		QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", departmentEntity.getCommunityId());
		List<DepartmentEntity> departmentList = departmentMapper.selectList(wrapper);
		if (departmentList != null && departmentList.size() > 0) {
			for (DepartmentEntity entity : departmentList) {
				if (departmentEntity.getDepartment().equals(entity.getDepartment())) {
					throw new PropertyException("已存在同名部门，请重新添加");
				}
			}
		}
		
		DepartmentEntity entity = new DepartmentEntity();
		BeanUtils.copyProperties(departmentEntity, entity);
		
		// 将电话集合转换成一个字符串(去掉前后的 [ 和 ] )
		List<String> phones = departmentEntity.getPhone();
		if (phones != null && phones.size() > 0) {
			String str = Convert.toStr(phones);
			String replace = str.replace("[", "");
			String newStr = replace.replace("]", "");
			entity.setPhone(newStr);
		}
		entity.setId(SnowFlake.nextId());
		departmentMapper.insert(entity);
	}
	
	@Override
	public void updateDepartment(DepartmentQO departmentEntity) {
		QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", departmentEntity.getId()).eq("community_id", departmentEntity.getCommunityId());
		DepartmentEntity one = departmentMapper.selectOne(wrapper);
		if (one == null) {
			throw new PropertyException("该小区没有此部门");
		}
		
		if (departmentEntity.getDepartment().equals(one.getDepartment())) {
			throw new PropertyException("您小区已存在同名部门，请重新修改");
		}
		
		DepartmentEntity entity = new DepartmentEntity();
		BeanUtils.copyProperties(departmentEntity, entity);
		
		// 将电话集合转换成一个字符串(去掉前后的 [ 和 ] )
		List<String> phones = departmentEntity.getPhone();
		if (phones != null && phones.size() > 0) {
			String str = Convert.toStr(phones);
			String replace = str.replace("[", "");
			String newStr = replace.replace("]", "");
			entity.setPhone(newStr);
		}
		entity.setId(departmentEntity.getId());
		departmentMapper.updateById(entity);
	}
	
	@Override
	public void deleteDepartment(Long departmentId, Long communityId) {
		QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", departmentId).eq("community_id", communityId);
		DepartmentEntity entity = departmentMapper.selectOne(wrapper);
		if (entity == null) {
			throw new PropertyException("您要删除的部门不存在，请重新选择");
		}
		
		QueryWrapper<DepartmentEntity> departmentQuery = new QueryWrapper<>();
		departmentQuery.eq("pid", departmentId).eq("community_id", communityId);
		List<DepartmentEntity> childList = departmentMapper.selectList(departmentQuery);
		if (childList != null && childList.size() > 0) {
			throw new PropertyException("\"" + entity.getDepartment() + "\"已有下级节点，不可删除");
		}
		
		QueryWrapper<DepartmentStaffEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("department_id", departmentId);
		List<DepartmentStaffEntity> staffEntities = departmentStaffMapper.selectList(queryWrapper);
		if (!CollectionUtils.isEmpty(staffEntities)) {
			throw new PropertyException("请先删除部门下的员工");
		}
		departmentMapper.deleteDepartmentById(departmentId, communityId);
	}
	
	@Override
	public List<DepartmentVO> listDepartment(Long communityId) {
		//1. 查询所有部门
		QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		List<DepartmentEntity> allDepartment = departmentMapper.selectList(wrapper);
		List<DepartmentVO> allDepartmentVO = new ArrayList<>();
		for (DepartmentEntity departmentEntity : allDepartment) {
			DepartmentVO departmentVO = new DepartmentVO();
			BeanUtils.copyProperties(departmentEntity, departmentVO);
			allDepartmentVO.add(departmentVO);
		}
		
		//2. 根节点
		List<DepartmentVO> parents = new ArrayList<DepartmentVO>();
		for (DepartmentVO entity : allDepartmentVO) {
			// pid为0就是根节点
			if (entity.getPid() == 0) {
				
				// 获取并设置该节点(父)的人员数量
				Long departmentId = entity.getId();
				QueryWrapper<DepartmentStaffEntity> staffQuery = new QueryWrapper<>();
				staffQuery.eq("department_id",departmentId);
				Integer count = departmentStaffMapper.selectCount(staffQuery);
				entity.setCount(count);
				parents.add(entity);
			}
		}
		// 为根节点设置子节点，getClild是递归调用的
		for (DepartmentVO departmentVO : parents) {
			//获取根节点下的所有子节点 使用getChild方法
			List<DepartmentVO> childList = getChild(departmentVO.getId(), allDepartmentVO);
			//给根节点设置子节点
			departmentVO.setChildren(childList);
		}
		
		return parents;
	}
	
	
	/**
	 * 获取子节点
	 *
	 * @param id      父节点id
	 * @param allDepartment 所有节点列表
	 * @return 每个根节点下，所有子菜单列表
	 */
	public List<DepartmentVO> getChild(Long id, List<DepartmentVO> allDepartment) {
		//子节点
		List<DepartmentVO> childList = new ArrayList<DepartmentVO>();
		for (DepartmentVO nav : allDepartment) {
			// 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
			//相等说明：为该根节点的子节点。
			if (id.equals(nav.getPid())) {
				
				// 获取并设置该节点(子)的人员数量
				Long departmentId = nav.getId();
				QueryWrapper<DepartmentStaffEntity> staffQuery = new QueryWrapper<>();
				staffQuery.eq("department_id",departmentId);
				Integer count = departmentStaffMapper.selectCount(staffQuery);
				nav.setCount(count);
				childList.add(nav);
			}
		}
		//递归
		for (DepartmentVO nav : childList) {
			nav.setChildren(getChild(nav.getId(), allDepartment));
		}
		//如果节点下没有子节点，返回一个空List（递归退出）
		if (childList.size() == 0) {
			return new ArrayList<DepartmentVO>();
		}
		return childList;
	}
	
	
}
