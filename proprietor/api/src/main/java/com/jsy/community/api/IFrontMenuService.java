package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FrontMenuEntity;
import com.jsy.community.qo.BaseQO;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-14
 */
public interface IFrontMenuService extends IService<FrontMenuEntity> {
	
	
	/**
	 * @return java.lang.Long
	 * @Author lihao
	 * @Description 添加菜单信息
	 * @Date 2020/11/14 17:37
	 * @Param [menuEntity]
	 **/
	Integer saveMenu(FrontMenuEntity menuEntity);
	
	/**
	 * @return java.lang.Integer
	 * @Author lihao
	 * @Description 根据id修改菜单信息
	 * @Date 2020/11/14 17:52
	 * @Param [menuEntity]
	 **/
	Integer updateMenu(FrontMenuEntity menuEntity);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.FrontMenuEntity>
	 * @Author lihao
	 * @Description 分页查询所有菜单信息
	 * @Date 2020/11/14 17:59
	 * @Param [baseEntity]
	 **/
	List<FrontMenuEntity> listFrontMenu(BaseQO<FrontMenuEntity> baseQO);
}
