package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.entity.admin.SysMenuEntity;
import com.jsy.community.entity.admin.SysRoleEntity;
import com.jsy.community.mapper.SysMenuMapper;
import com.jsy.community.mapper.SysRoleMapper;
import com.jsy.community.qo.admin.SysMenuQO;
import com.jsy.community.qo.admin.SysRoleQO;
import com.jsy.community.service.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chq459799974
 * @description 系统配置，菜单，角色，权限等
 * @since 2020-12-14 10:29
 **/
@Service
public class SysConfigServiceImpl implements SysConfigService {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Resource
	private SysMenuMapper sysMenuMapper;
	
	@Resource
	private SysRoleMapper sysRoleMapper;
	
	//==================================================== Menu菜单 ===============================================================
	/**
	* @Description: 缓存菜单
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostConstruct
	private void cacheMenuToRedis(){
		List<SysMenuEntity> menuList = sysMenuMapper.selectList(new QueryWrapper<SysMenuEntity>().select("*").eq("pid", 0));
		setChildren(menuList,new LinkedList<SysMenuEntity>());
		stringRedisTemplate.opsForValue().set("Admin:Menu", JSON.toJSONString(menuList));
	}
	
	//组装子菜单
	private void setChildren(List<SysMenuEntity> parentList,List<SysMenuEntity> childrenList){
		if(!CollectionUtils.isEmpty(parentList)){
			for(SysMenuEntity sysMenuEntity : parentList){
				childrenList = sysMenuMapper.getChildrenList(sysMenuEntity.getId());
				sysMenuEntity.setChildrenList(childrenList);
				setChildren(childrenList,new LinkedList<SysMenuEntity>());
			}
		}
	}
	
	/**
	* @Description: 新增菜单
	 * @Param: [sysMenuEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public boolean addMenu(SysMenuEntity sysMenuEntity){
		if(sysMenuEntity.getPid() == null){
			sysMenuEntity.setPid(0L);
		}
		int result = 0;
		if(sysMenuEntity.getSort() == null){
			result = sysMenuMapper.addMenu(sysMenuEntity);
		}else{
			result = sysMenuMapper.insert(sysMenuEntity);
		}
		if(result == 1){
			cacheMenuToRedis(); //刷新redis
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 级联删除
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public boolean delMenu(Long id){
		List<Long> idList = new LinkedList<>(); // 级联出的要删除的id
		idList.add(id);
		List<Long> subIdList = sysMenuMapper.getSubIdList(Arrays.asList(id));
		setDeleteIds(idList, subIdList);
		int result = sysMenuMapper.deleteBatchIds(idList);
		if(result > 0){
			cacheMenuToRedis(); //刷新redis
			return true;
		}
		return false;
	}
	
	//组装全部需要删除的id
	private void setDeleteIds(List<Long> idList, List<Long> subIdList) {
		if(!CollectionUtils.isEmpty(subIdList)){
			subIdList.removeAll(idList);
			idList.addAll(subIdList);
			setDeleteIds(idList,sysMenuMapper.getSubIdList(subIdList));
		}
	}
	
	/**
	* @Description: 修改菜单
	 * @Param: [sysMenuQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public boolean updateMenu(SysMenuQO sysMenuQO){
		SysMenuEntity entity = new SysMenuEntity();
		BeanUtils.copyProperties(sysMenuQO,entity);
		int result = sysMenuMapper.updateById(entity);
		if(result == 1){
			cacheMenuToRedis(); //刷新redis
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 菜单列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.admin.SysMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public List<SysMenuEntity> listOfMenu() {
		return JSONArray.parseObject(stringRedisTemplate.opsForValue().get("Admin:Menu"),List.class);
	}
	
	//==================================================== Role角色 ===============================================================
	/**
	 * @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	public boolean addRole(SysRoleEntity sysRoleEntity){
		int result = sysRoleMapper.insert(sysRoleEntity);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 删除角色
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	public boolean delRole(Long id){
		int result = sysRoleMapper.deleteById(id);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	public boolean updateRole(SysRoleQO sysRoleOQ){
		SysRoleEntity entity = new SysRoleEntity();
		BeanUtils.copyProperties(sysRoleOQ,entity);
		int result = sysRoleMapper.updateById(entity);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 角色列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.admin.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	public List<SysRoleEntity> listOfRole(){
		return sysRoleMapper.selectList(new QueryWrapper<SysRoleEntity>().select("*"));
	}
	
}
