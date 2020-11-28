package com.jsy.community.api;

import com.jsy.community.entity.DepartmentStaffEntity;
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
public interface IDepartmentStaffService extends IService<DepartmentStaffEntity> {
	
	List<DepartmentStaffEntity> listStaff();
}
