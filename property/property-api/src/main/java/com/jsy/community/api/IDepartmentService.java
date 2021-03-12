package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.qo.DepartmentQO;
import com.jsy.community.vo.DepartmentVO;

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
	 * @return void
	 * @Author lihao
	 * @Description 新增部门
	 * @Date 2020/11/30 14:19
	 * @Param [departmentEntity]
	 **/
	void addDepartment(DepartmentQO departmentEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改部门
	 * @Date 2020/11/30 14:21
	 * @Param [departmentEntity]
	 **/
	void updateDepartment(DepartmentQO departmentEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 删除部门
	 * @Date 2020/11/30 14:24
	 * @Param [id]
	 **/
	void deleteDepartment(Long departmentId,Long communityId);
	
	/**
	 * @return java.util.List<com.jsy.community.vo.DepartmentVO>
	 * @Author lihao
	 * @Description 树形结构查询部门信息
	 * @Date 2021/3/11 17:49
	 * @Param [communityId]
	 **/
	List<DepartmentVO> listDepartment(Long communityId);
}
