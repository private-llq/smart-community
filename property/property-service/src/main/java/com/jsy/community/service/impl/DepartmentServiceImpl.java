package com.jsy.community.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.DepartmentMapper;
import com.jsy.community.mapper.DepartmentStaffMapper;
import com.jsy.community.qo.DepartmentQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.DepartmentVO;
import com.jsy.community.vo.TreeCommunityVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	@Autowired
	private CommunityMapper communityMapper;
	
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
		// 不可选择自己或自己的子集成为自己的父级
		QueryWrapper<DepartmentEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("pid", departmentEntity.getId());
		List<DepartmentEntity> childList = departmentMapper.selectList(queryWrapper);
		
		if (!CollectionUtils.isEmpty(childList)) {
			for (DepartmentEntity child : childList) {
				if (child.getId().equals(departmentEntity.getPid())||departmentEntity.getPid().equals(departmentEntity.getId())) {
					throw new PropertyException("不可选择自己或自己的子集成为自己的父级");
				}
			}
		}
		
		// 判断是否有同名
		QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", departmentEntity.getCommunityId());
		List<DepartmentEntity> ones = departmentMapper.selectList(wrapper);
		if (ones == null) {
			throw new PropertyException("该小区没有此部门");
		}
		
		for (DepartmentEntity one : ones) {
			// 跳过当前正在修改的这条数据
			if (one.getId().equals(departmentEntity.getId())) {
				continue;
			}
			if (departmentEntity.getDepartment().equals(one.getDepartment())) {
				throw new PropertyException("您小区已存在同名部门，请重新修改");
			}
		}
		
		// 判断父节点是否存在
		Long pid = departmentEntity.getPid();
		DepartmentEntity dept = departmentMapper.selectById(pid);
		if (departmentEntity.getPid() != 0) {
			if (dept == null) {
				throw new PropertyException("您选择的父节点不存在,请重新选择");
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
			throw new PropertyException("\"" + entity.getDepartment() + "\""+"已有下级节点，不可删除");
		}
		
		QueryWrapper<DepartmentStaffEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("department_id", departmentId).eq("community_id",communityId);
		List<DepartmentStaffEntity> staffEntities = departmentStaffMapper.selectList(queryWrapper);
		if (!CollectionUtils.isEmpty(staffEntities)) {
			throw new PropertyException("\"" + entity.getDepartment() + "\""+"请先删除部门下的员工");
		}
		departmentMapper.deleteById(departmentId);
	}
	
	@Override
	public TreeCommunityVO listDepartment(Long communityId) {
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
		
		// 根据sort字段进行排序
		Collections.sort(parents,order());
		
		// 为根节点设置子节点，getClild是递归调用的
		for (DepartmentVO departmentVO : parents) {
			//获取根节点下的所有子节点 使用getChild方法
			List<DepartmentVO> childList = getChild(departmentVO.getId(), allDepartmentVO);
			//给根节点设置子节点
			departmentVO.setChildren(childList);
		}
		
		CommunityEntity entity = communityMapper.selectById(communityId);
		return new TreeCommunityVO().setCommunityId(communityId).setCommunityName(entity.getName()).setDepartmentVOList(parents);
	}
	
	@Override
	public DepartmentEntity getDepartmentById(Long departmentId, Long communityId) {
		QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id",departmentId).eq("community_id",communityId);
		DepartmentEntity departmentEntity = departmentMapper.selectOne(wrapper);
		if (departmentEntity==null) {
			return new DepartmentEntity();
		}
		
		// 根据父id获取节点名称
		Long pid = departmentEntity.getPid();
		QueryWrapper<DepartmentEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id", pid).eq("community_id", communityId);
		DepartmentEntity parent = departmentMapper.selectOne(queryWrapper);
		
		if (parent != null) {
			departmentEntity.setParentName(parent.getDepartment());
		} else {
			// 如果他没有父节点，那么父节点就展示他的社区名
			CommunityEntity communityEntity = communityMapper.selectById(communityId);
			departmentEntity.setParentName(communityEntity.getName());
		}
		return departmentEntity;
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
		
		//排序
		Collections.sort(childList,order());
		
		//如果节点下没有子节点，返回一个空List（递归退出）
		if (childList.size() == 0) {
			return new ArrayList<DepartmentVO>();
		}
		return childList;
	}
	
	
	/*
	 * 排序,根据sort排序
	 */
	public Comparator<DepartmentVO> order(){
		Comparator<DepartmentVO> comparator = new Comparator<DepartmentVO>() {
			@Override
			public int compare(DepartmentVO o1, DepartmentVO o2) {
				if(o1.getSort() != o2.getSort()){
					return o1.getSort() - o2.getSort();
				}
				return 0;
			}
		};
		return comparator;
	}
	
	
}
