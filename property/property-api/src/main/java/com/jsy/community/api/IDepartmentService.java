package com.jsy.community.api;

import com.jsy.community.entity.DepartmentEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
public interface IDepartmentService extends IService<DepartmentEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.DepartmentEntity>
	 * @Author lihao
	 * @Description 查询所有部门
	 * @Date 2020/11/30 14:19
	 * @Param []
	 **/
	List<DepartmentEntity> listDepartment(Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 新增部门
	 * @Date 2020/11/30 14:19
	 * @Param [departmentEntity]
	 **/
	void addDepartment(DepartmentEntity departmentEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改部门
	 * @Date 2020/11/30 14:21
	 * @Param [departmentEntity]
	 **/
	void updateDepartment(DepartmentEntity departmentEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 删除部门
	 * @Date 2020/11/30 14:24
	 * @Param [id]
	 **/
	void deleteDepartment(Long id);
}
