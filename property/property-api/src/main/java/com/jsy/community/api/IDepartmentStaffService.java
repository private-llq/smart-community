package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * <p>
 *  服务类
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
	PageInfo<DepartmentStaffEntity> listDepartmentStaff(Long departmentId, BaseQO<DepartmentStaffEntity> staffEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加员工信息
	 * @Date 2020/11/30 16:29
	 * @Param [staffEntity]
	 **/
	void addDepartmentStaff(DepartmentStaffEntity staffEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改员工信息
	 * @Date 2020/11/30 16:31
	 * @Param [departmentStaffEntity]
	 **/
	void updateDepartmentStaff(DepartmentStaffEntity departmentStaffEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 批量删除员工信息
	 * @Date 2020/11/30 16:35
	 * @Param [ids]
	 **/
	void deleteStaffByIds(Integer[] ids);
}
