package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.DepartmentEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-24
 */
public interface IDepartmentService extends IService<DepartmentEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.DepartmentEntity>
	 * @Author lihao
	 * @Description 查询所有部门信息
	 * @Date 2020/11/24 17:33
	 * @Param [id]
	 **/
	List<DepartmentEntity> listDepartment(Long id);
}
