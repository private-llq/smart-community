package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitorPersonEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author chq459799974
 * @description 随行人员Mapper接口
 * @since 2020-11-12
 */
public interface VisitorPersonMapper extends BaseMapper<VisitorPersonEntity> {
	/**
	 * @Description: 批量新增随行人员
	 * @Param: [list]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	int addPersonBatch(List<VisitorPersonEntity> list);
	
	//添加随行人员 如果已删除则恢复 索引不冲突字段(name)以新入参数据为准
	@Insert("replace into t_visitor_person(id,uid,name,mobile,deleted,create_time) values(#{entity.id},#{entity.uid},#{entity.name},#{entity.mobile},0,now())")
	int addPerson(@Param("entity")VisitorPersonEntity entity);
}
