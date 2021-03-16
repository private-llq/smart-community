package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.DepartmentEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
public interface DepartmentMapper extends BaseMapper<DepartmentEntity> {
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 根据部门id，社区id 删除部门信息
	 * @Date 2021/3/8 16:01
	 * @Param [departmentId, communityId]
	 **/
	void deleteDepartmentById(@Param("departmentId") Long departmentId, @Param("communityId") Long communityId);
}
