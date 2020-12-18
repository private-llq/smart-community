package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.entity.sys.SysRoleEntity;
import com.jsy.community.mapper.SysMenuMapper;
import com.jsy.community.mapper.SysRoleMapper;
import com.jsy.community.qo.sys.SysMenuQO;
import com.jsy.community.qo.sys.SysRoleQO;
import com.jsy.community.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chq459799974
 * @description 系统配置，菜单，角色，权限等
 * @since 2020-12-14 10:29
 **/
@Slf4j
@Service
public class SysConfigServiceImpl implements ISysConfigService {
	
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
		stringRedisTemplate.opsForValue().set("Sys:Menu", JSON.toJSONString(queryMenu()));
	}
	
	/**
	* @Description: 查询大后台菜单
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	public List<SysMenuEntity> queryMenu(){
		List<SysMenuEntity> menuList = sysMenuMapper.selectList(new QueryWrapper<SysMenuEntity>().select("*").eq("pid", 0));
		setChildren(menuList,new LinkedList<SysMenuEntity>());
		return menuList;
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
		if(sysMenuEntity.getPid() != null && sysMenuEntity.getPid() != 0){ //①非顶级节点，查找父节点，确保数据严密性
			SysMenuEntity parent = sysMenuMapper.findParent(sysMenuEntity.getPid());
			if(parent == null){
				return false;
			}
			if(0 == parent.getBelongTo()){//父级是顶级节点
				sysMenuEntity.setBelongTo(parent.getId());
			}else{ //父级也是子级
				sysMenuEntity.setBelongTo(parent.getBelongTo());//同步父节点的顶级节点
			}
		}else { //②顶级节点
			sysMenuEntity.setPid(0L);
			sysMenuEntity.setBelongTo(0L);
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
	
	//寻找顶级菜单ID
//	private void setBelongTo(AppMenuEntity sysMenuEntity,AppMenuEntity parent){
//		if(0L != parent.getPid()){ //要新增的菜单非顶级
//			parent = sysMenuMapper.findParent(parent.getPid());//寻找父节点
//			if(parent.getPid() == 0){ //顶级节点
//				sysMenuEntity.setBelongTo(parent.getId());
//			}else{
//				setBelongTo(sysMenuEntity,parent);
//			}
//		}
//	}
	
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
//		List<Long> subIdList = sysMenuMapper.getSubIdList(Arrays.asList(id));
//		setDeleteIds(idList, subIdList);
//		int result = sysMenuMapper.deleteBatchIds(idList);
		int result = sysMenuMapper.deleteById(id);
		sysMenuMapper.delete(new QueryWrapper<SysMenuEntity>().eq("belong_to",id));
		if(result == 1){
			cacheMenuToRedis(); //刷新redis
			return true;
		}
		return false;
	}
	
	//组装全部需要删除的id
//	private void setDeleteIds(List<Long> idList, List<Long> subIdList) {
//		if(!CollectionUtils.isEmpty(subIdList)){
//			subIdList.removeAll(idList);
//			idList.addAll(subIdList);
//			setDeleteIds(idList,sysMenuMapper.getSubIdList(subIdList));
//		}
//	}
	
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
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public List<SysMenuEntity> listOfMenu() {
		List<SysMenuEntity> list = null;
		try{
			list = JSONArray.parseObject(stringRedisTemplate.opsForValue().get("Sys:Menu"),List.class);
		}catch (Exception e){
			log.error("redis获取菜单失败");
			return queryMenu();//从mysql获取
		}
		return list;
	}
	
	//==================================================== Role角色 ===============================================================
	/**
	 * @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
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
	@Override
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
	@Override
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
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public List<SysRoleEntity> listOfRole(){
		return sysRoleMapper.selectList(new QueryWrapper<SysRoleEntity>().select("*"));
	}
	
	//==================================================== 角色-菜单 ===============================================================
	/**
	 * @Description: 为角色设置菜单
	 * @Param: [menuIds, roleId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Override
	public boolean setRoleMenus(List<Long> menuIds,Long roleId){
		//设置子菜单
		if(!CollectionUtils.isEmpty(menuIds)){
//			List<Long> subIdList = sysMenuMapper.getSubIdList(menuIds);
			List<Long> idBelongList = sysMenuMapper.getIdBelongList(menuIds);
			menuIds.addAll(idBelongList);
		}
		//备份
		List<Long> userRoles = sysRoleMapper.getRoleMenu(roleId);
		//清空
		sysRoleMapper.clearRoleMenu(roleId);
		//新增
		int rows = sysRoleMapper.addRoleMenuBatch(menuIds, roleId);
		//还原
		if(rows != menuIds.size()){
			log.error("设置角色菜单出错：" + roleId,"成功条数：" + rows);
			sysRoleMapper.clearRoleMenu(roleId);
			sysRoleMapper.addRoleMenuBatch(userRoles, roleId);
			return false;
		}
		return true;
	}
	
	//==================================================== 用户-菜单 ===============================================================
	/**
	* @Description: 查询用户菜单权限
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	@Override
	public List<SysMenuEntity> queryUserMenu(Long uid){
		return sysMenuMapper.queryUserMenu(uid);
	}
	
}
