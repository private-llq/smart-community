package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.DepartmentStaffEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-24
 */
public interface IDepartmentStaffService extends IService<DepartmentStaffEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.DepartmentStaffEntity>
	 * @Author lihao
	 * @Description 根据部门id查询联系方式
	 * @Date 2020/11/24 17:33
	 * @Param [departmentId]
	 **/
	List<DepartmentStaffEntity> listStaffPhone(Long departmentId);
}
