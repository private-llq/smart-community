package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.qo.DepartmentStaffQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
public interface IDepartmentStaffService extends IService<DepartmentStaffEntity> {
	
	/**
	 * @return com.jsy.community.utils.PageInfo<com.jsy.community.entity.DepartmentStaffEntity>
	 * @Author lihao
	 * @Description 查询所有员工信息
	 * @Date 2020/11/30 14:57
	 * @Param [staffEntity]
	 **/
	PageInfo<DepartmentStaffEntity> listDepartmentStaff(Long departmentId,Long page,Long size);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加员工信息
	 * @Date 2020/11/30 16:29
	 * @Param [staffEntity]
	 **/
	void addDepartmentStaff(DepartmentStaffQO staffEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改员工信息
	 * @Date 2020/11/30 16:31
	 * @Param [departmentStaffEntity]
	 **/
	void updateDepartmentStaff(DepartmentStaffQO departmentStaffEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 删除员工信息
	 * @Date 2021/3/10 14:33
	 * @Param [id, communityId]
	 **/
	void deleteStaffByIds(Long id, Long communityId);
	
	/**
	 * @return java.util.Map<java.lang.String,java.lang.Object>
	 * @Author lihao
	 * @Description 通过Excel添加通讯录
	 * @Date 2021/3/11 16:23
	 * @Param [strings]
	 **/
	Map<String, Object> addLinkByExcel(List<String[]> strings);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 根据id查询员工信息
	 * @Date 2021/3/16 16:13
	 * @Param [id, communityId]
	 **/
	DepartmentStaffEntity getDepartmentStaffById(Long id);
}
