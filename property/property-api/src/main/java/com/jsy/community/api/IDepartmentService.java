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
	
	List<DepartmentEntity> listDepartment();
}
