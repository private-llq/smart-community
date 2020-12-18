package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.mapper.AdminMenuMapper;
import com.jsy.community.mapper.AdminRoleMapper;
import com.jsy.community.qo.admin.AdminMenuQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
@DubboService(version = Const.version, group = Const.group_property)
public class AdminConfigServiceImpl implements IAdminConfigService {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Resource
	private AdminMenuMapper adminMenuMapper;
	
	@Resource
	private AdminRoleMapper adminRoleMapper;
	
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
		stringRedisTemplate.opsForValue().set("Admin:Menu", JSON.toJSONString(queryMenu()));
	}
	
	/**
	* @Description: 查询大后台菜单
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	public List<AdminMenuEntity> queryMenu(){
		List<AdminMenuEntity> menuList = adminMenuMapper.selectList(new QueryWrapper<AdminMenuEntity>().select("*").eq("pid", 0));
		setChildren(menuList,new LinkedList<AdminMenuEntity>());
		return menuList;
	}
	
	//组装子菜单
	private void setChildren(List<AdminMenuEntity> parentList, List<AdminMenuEntity> childrenList){
		if(!CollectionUtils.isEmpty(parentList)){
			for(AdminMenuEntity adminMenuEntity : parentList){
				childrenList = adminMenuMapper.getChildrenList(adminMenuEntity.getId());
				adminMenuEntity.setChildrenList(childrenList);
				setChildren(childrenList,new LinkedList<AdminMenuEntity>());
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
	public boolean addMenu(AdminMenuEntity adminMenuEntity){
		if(adminMenuEntity.getPid() != null && adminMenuEntity.getPid() != 0){ //①非顶级节点，查找父节点，确保数据严密性
			AdminMenuEntity parent = adminMenuMapper.findParent(adminMenuEntity.getPid());
			if(parent == null){
				return false;
			}
			if(0 == parent.getBelongTo()){//父级是顶级节点
				adminMenuEntity.setBelongTo(parent.getId());
			}else{ //父级也是子级
				adminMenuEntity.setBelongTo(parent.getBelongTo());//同步父节点的顶级节点
			}
		}else { //②顶级节点
			adminMenuEntity.setPid(0L);
			adminMenuEntity.setBelongTo(0L);
		}
		int result = 0;
		if(adminMenuEntity.getSort() == null){
			result = adminMenuMapper.addMenu(adminMenuEntity);
		}else{
			result = adminMenuMapper.insert(adminMenuEntity);
		}
		if(result == 1){
			cacheMenuToRedis(); //刷新redis
			return true;
		}
		return false;
	}
	
	//寻找顶级菜单ID
//	private void setBelongTo(AdminMenuEntity sysMenuEntity,AdminMenuEntity parent){
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
		int result = adminMenuMapper.deleteById(id);
		adminMenuMapper.delete(new QueryWrapper<AdminMenuEntity>().eq("belong_to",id));
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
	public boolean updateMenu(AdminMenuQO sysMenuQO){
		AdminMenuEntity entity = new AdminMenuEntity();
		BeanUtils.copyProperties(sysMenuQO,entity);
		int result = adminMenuMapper.updateById(entity);
		if(result == 1){
			cacheMenuToRedis(); //刷新redis
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 菜单列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public List<AdminMenuEntity> listOfMenu() {
		List<AdminMenuEntity> list = null;
		try{
			list = JSONArray.parseObject(stringRedisTemplate.opsForValue().get("Admin:Menu"),List.class);
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
	public boolean addRole(AdminRoleEntity sysRoleEntity){
		int result = adminRoleMapper.insert(sysRoleEntity);
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
		int result = adminRoleMapper.deleteById(id);
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
	public boolean updateRole(AdminRoleQO sysRoleOQ){
		AdminRoleEntity entity = new AdminRoleEntity();
		BeanUtils.copyProperties(sysRoleOQ,entity);
		int result = adminRoleMapper.updateById(entity);
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
	public List<AdminRoleEntity> listOfRole(){
		return adminRoleMapper.selectList(new QueryWrapper<AdminRoleEntity>().select("*"));
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
			List<Long> idBelongList = adminMenuMapper.getIdBelongList(menuIds);
			menuIds.addAll(idBelongList);
		}
		//备份
		List<Long> userRoles = adminRoleMapper.getRoleMenu(roleId);
		//清空
		adminRoleMapper.clearRoleMenu(roleId);
		//新增
		int rows = adminRoleMapper.addRoleMenuBatch(menuIds, roleId);
		//还原
		if(rows != menuIds.size()){
			log.error("设置角色菜单出错：" + roleId,"成功条数：" + rows);
			adminRoleMapper.clearRoleMenu(roleId);
			adminRoleMapper.addRoleMenuBatch(userRoles, roleId);
			return false;
		}
		return true;
	}
	
	//==================================================== 用户-菜单 ===============================================================
	/**
	* @Description: 查询用户菜单权限
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	@Override
	public List<AdminMenuEntity> queryUserMenu(Long uid){
		return adminMenuMapper.queryUserMenu(uid);
	}
	
}
