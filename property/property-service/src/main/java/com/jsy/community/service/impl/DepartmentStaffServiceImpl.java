package com.jsy.community.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.mapper.DepartmentMapper;
import com.jsy.community.mapper.DepartmentStaffMapper;
import com.jsy.community.qo.DepartmentStaffQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@DubboService(version = Const.version, group = Const.group_property)
public class DepartmentStaffServiceImpl extends ServiceImpl<DepartmentStaffMapper, DepartmentStaffEntity> implements IDepartmentStaffService {
	
	@Autowired
	private DepartmentStaffMapper staffMapper;
	
	@Autowired
	private DepartmentMapper departmentMapper;
	
	@Override
	public PageInfo<DepartmentStaffEntity> listDepartmentStaff(Long departmentId, Long page, Long size) {
		QueryWrapper<DepartmentStaffEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("department_id", departmentId);
		Page<DepartmentStaffEntity> pageInfo = new Page<>(page, size);
		staffMapper.selectPage(pageInfo, queryWrapper);
		
		PageInfo<DepartmentStaffEntity> departmentStaffEntityPageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageInfo, departmentStaffEntityPageInfo);
		return departmentStaffEntityPageInfo;
	}
	
	@Override
	public void addDepartmentStaff(DepartmentStaffQO staffEntity) {
		DepartmentStaffEntity staff = new DepartmentStaffEntity();
		BeanUtils.copyProperties(staffEntity, staff);
		
		List<String> phones = staffEntity.getPhone();
		if (phones != null && phones.size() > 0) {
			String str = Convert.toStr(phones);
			String replace = str.replace("[", "");
			String newStr = replace.replace("]", "");
			staff.setPhone(newStr);
		}
		staff.setId(SnowFlake.nextId());
		staffMapper.insert(staff);
	}
	
	@Override
	public void updateDepartmentStaff(DepartmentStaffQO departmentStaffEntity) {
		QueryWrapper<DepartmentStaffEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", departmentStaffEntity.getId()).eq("community_id", departmentStaffEntity.getCommunityId()).eq("department_id", departmentStaffEntity.getDepartmentId());
		DepartmentStaffEntity one = staffMapper.selectOne(wrapper);
		if (one == null) {
			throw new PropertyException("该部门没有此员工");
		}
		
		DepartmentStaffEntity department = new DepartmentStaffEntity();
		BeanUtils.copyProperties(departmentStaffEntity, department);
		
		// 将电话集合转换成一个字符串(去掉前后的 [ 和 ] )
		List<String> phones = departmentStaffEntity.getPhone();
		if (phones != null && phones.size() > 0) {
			String str = Convert.toStr(phones);
			String replace = str.replace("[", "");
			String newStr = replace.replace("]", "");
			department.setPhone(newStr);
		}
		department.setId(departmentStaffEntity.getId());
		staffMapper.updateById(department);
	}
	
	@Override
	public void deleteStaffByIds(Long id, Long communityId) {
		QueryWrapper<DepartmentStaffEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", id).eq("community_id", communityId);
		DepartmentStaffEntity staff = staffMapper.selectOne(wrapper);
		if (staff == null) {
			throw new PropertyException("您要删除的员工不存在或不属于该部门");
		}
		staffMapper.deleteById(id);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> addLinkByExcel(List<String[]> strings) {
		long communityId = 5;
		
		// 成功数
		int success = 0;
		// 失败数
		int fail = 0;
		// 失败明细数据
		List<Map<String, String>> staffEntityList = new ArrayList<>();
		// 返回结果
		Map<String, Object> resultMap = new HashMap<>();
		
		for (String[] string : strings) {
			DepartmentStaffEntity staff = new DepartmentStaffEntity();
			// 主键id
			staff.setId(SnowFlake.nextId());
			
			// 社区id
			staff.setCommunityId(communityId);
			
			// 姓名
			String name = string[0];
			staff.setPerson(name);
			
			// 部门
			String department = string[1];
			// 对部门进行处理
			QueryWrapper<DepartmentEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("department", department).eq("community_id", communityId);
			DepartmentEntity dep = departmentMapper.selectOne(wrapper);
			// 若该部门不存在
			if (dep == null) {
				DepartmentEntity entity = new DepartmentEntity();
				entity.setId(SnowFlake.nextId());
				entity.setPid(0L);
				entity.setDepartment(department);
				entity.setCommunityId(communityId);
				departmentMapper.insert(entity);
				staff.setDepartmentId(entity.getId());
			} else {
				// 若部门存在
				Long departmentId = dep.getId();
				staff.setDepartmentId(departmentId);
			}
			
			// 职务
			String duty = string[2];
			staff.setDuty(duty);
			
			// 电话
			String phone = string[3];
			// 对电话号码进行处理
			// 如果电话号码不符号要求则不能添加成功
			if (!StringUtils.isEmpty(phone)) {
				String[] strs = phone.split(",");
				
				List<String> phones = Arrays.asList(strs);
				// 将电话集合转换成一个字符串(去掉前后的 [ 和 ] )
				String str = Convert.toStr(phones);
				String replace = str.replace("[", "");
				String newStr = replace.replace("]", "");
				staff.setPhone(newStr);
			}
			
			// 邮箱
			String email = string[4];
			staff.setEmail(email);
			
			// 如果Excel中有与数据库中 有相同部门下的同名员工 且 电话号码完全相同或其中1个以上电话号码相同则不能添加成功
			// 如果电话号码大于3个则不能添加成功
			if (dep != null) {
				QueryWrapper<DepartmentStaffEntity> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("department_id", dep.getId()).eq("community_id", communityId);
				DepartmentStaffEntity departmentStaffEntity = staffMapper.selectOne(queryWrapper);
				
				String person = departmentStaffEntity.getPerson();
				String entityPhone = departmentStaffEntity.getPhone();
				
				// 数据表中那条数据的电话
				String[] mysqlData = entityPhone.split(",");
				List<String> mysqlList = Arrays.asList(mysqlData);
				// Excel中那条数据的电话
				String[] excelData = phone.split(",");
				List<String> excelList = Arrays.asList(excelData);
				
				// 如果两个指定的集合没有共同的元素，则返回true
				boolean flag = Collections.disjoint(mysqlList, excelList);
				
				// 判断电话数是否大于3个
				boolean telFlag = phone.length() > 3;
				
				// 有相同部门下的同名员工 且 电话号码完全相同或其中1个以上电话号码相同则不能添加成功
				if ((!flag && name.equals(person))||telFlag) {
					HashMap<String, String> map = new HashMap<>();
					map.put("name", name);
					map.put("department", department);
					map.put("duty", duty);
					map.put("phone", phone);
					map.put("email", email);
					staffEntityList.add(map);
					fail += 1;
					continue;
				}
			}
			staffMapper.insert(staff);
			success += 1;
			
		}
		resultMap.put("success", "成功" + success + "条");
		resultMap.put("fail", "失败" + fail + "条");
		resultMap.put("failData", staffEntityList);
		return resultMap;
	}
	
	@Override
	public DepartmentStaffEntity getDepartmentStaffById(Long id) {
		return staffMapper.selectById(id);
	}
}
