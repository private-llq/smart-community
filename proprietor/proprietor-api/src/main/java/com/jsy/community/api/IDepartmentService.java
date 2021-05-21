package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.DepartmentEntity;

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
	 * @Description 根据社区id查询所有部门信息
	 * @Date 2020/11/24 17:33
	 * @Param [id：社区id]
	 **/
	List<DepartmentEntity> listDepartment(Long id);
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return java.util.List<java.util.Map>
	 * @Author lihao
	 * @Description 查询所有部门信息(APP首页通讯录)
	 * @Date 2021/3/27 16:25
	 * @Param [id]
	 **/
	List<DepartmentEntity> listDepartmentTel(Long id);
}
