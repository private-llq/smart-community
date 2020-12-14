package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.SysMenuEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统菜单Mapper
 * @since 2020-12-14 10:27
 **/
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuEntity> {
	
	@Insert("insert into t_sys_menu(icon,name,url,pid,sort)" +
		"select #{entity.icon},#{entity.name},#{entity.url},#{entity.pid},max(sort)+1 from t_sys_menu where pid = #{entity.pid}")
	int addMenu(@Param("entity") SysMenuEntity sysMenuEntity);
	
	@Select("select * from t_sys_menu where pid = #{id}")
	List<SysMenuEntity> getChildrenList(Long id);
	
	@Select("select id from t_sys_menu where pid = #{id}")
	List<Long> getSubIdList(List<Long> ids);
	
}
