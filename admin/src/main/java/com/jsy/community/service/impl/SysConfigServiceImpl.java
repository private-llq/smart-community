package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.entity.sys.SysRoleEntity;
import com.jsy.community.entity.sys.SysUserRoleEntity;
import com.jsy.community.mapper.SysMenuMapper;
import com.jsy.community.mapper.SysRoleMapper;
import com.jsy.community.mapper.SysRoleMenuMapper;
import com.jsy.community.mapper.SysUserRoleMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysMenuQO;
import com.jsy.community.qo.sys.SysRoleQO;
import com.jsy.community.service.ISysConfigService;
import com.jsy.community.utils.MyPageUtils;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.MenuPermission;
import com.zhsj.base.api.domain.PermitMenu;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.domain.RoleMenu;
import com.zhsj.base.api.entity.AddMenuDto;
import com.zhsj.base.api.entity.UpdateMenuDto;
import com.zhsj.base.api.entity.UpdateRoleDto;
import com.zhsj.base.api.rpc.IBaseMenuPermissionRpcService;
import com.zhsj.base.api.rpc.IBaseMenuRpcService;
import com.zhsj.base.api.rpc.IBasePermissionRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.base.api.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
	
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	
	@Resource
	private SysRoleMenuMapper sysRoleMenuMapper;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseRoleRpcService baseRoleRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseMenuRpcService baseMenuRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseMenuPermissionRpcService baseMenuPermissionRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
	private IBasePermissionRpcService permissionRpcService;
	
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
		setChildren(menuList,new LinkedList<>());
		menuList.sort(Comparator.comparing(SysMenuEntity::getSort));
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
	public void addMenu(SysMenuEntity sysMenuEntity){
//		if(sysMenuEntity.getPid() != null && sysMenuEntity.getPid() != 0){ //①非顶级节点，查找父节点，确保数据严密性
//			SysMenuEntity parent = sysMenuMapper.findParent(sysMenuEntity.getPid());
//			if(parent == null){
//				return false;
//			}
//			if(0 == parent.getBelongTo()){//父级是顶级节点
//				sysMenuEntity.setBelongTo(parent.getId());
//			}else{ //父级也是子级
//				sysMenuEntity.setBelongTo(parent.getBelongTo());//同步父节点的顶级节点
//			}
//		}else { //②顶级节点
//			sysMenuEntity.setPid(0L);
//			sysMenuEntity.setBelongTo(0L);
//		}
//		int result = 0;
//		if(sysMenuEntity.getSort() == null){
//			result = sysMenuMapper.addMenu(sysMenuEntity);
//		}else{
//			result = sysMenuMapper.insert(sysMenuEntity);
//		}
//		if(result == 1){
//			cacheMenuToRedis(); //刷新redis
//			return true;
//		}
//		return false;
		AddMenuDto addMenuDto = new AddMenuDto();
		addMenuDto.setLoginType(sysMenuEntity.getLoginType() == 1 ? BusinessConst.ULTIMATE_ADMIN : sysMenuEntity.getLoginType() == 2 ? BusinessConst.PROPERTY_ADMIN : BusinessConst.COMMUNITY_ADMIN);
		addMenuDto.setName(sysMenuEntity.getName());
		addMenuDto.setIcon(sysMenuEntity.getIcon());
		addMenuDto.setPath(sysMenuEntity.getPath());
		addMenuDto.setSort(sysMenuEntity.getSort());
		addMenuDto.setPid(sysMenuEntity.getPid());
		addMenuDto.setType(sysMenuEntity.getType());
		addMenuDto.setUid(sysMenuEntity.getId());
		// 新增菜单
		PermitMenu permitMenu = baseMenuRpcService.addMenu(addMenuDto);
		// 绑定菜单到默认角色
		List<Long> menuIds = new ArrayList<>();
		menuIds.add(permitMenu.getId());
		baseMenuRpcService.menuJoinRole(menuIds, sysMenuEntity.getLoginType() == 1 ? 1463327674104250369L : sysMenuEntity.getLoginType() == 2 ? 1463327674070695937L : 1467739062281084931L , 1460884237115367425L);
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
	public void delMenu(Long id){
//		List<Long> idList = new LinkedList<>(); // 级联出的要删除的id
//		idList.add(id);
////		List<Long> subIdList = sysMenuMapper.getSubIdList(Arrays.asList(id));
////		setDeleteIds(idList, subIdList);
////		int result = sysMenuMapper.deleteBatchIds(idList);
//		int result = sysMenuMapper.deleteById(id);
//		sysMenuMapper.delete(new QueryWrapper<SysMenuEntity>().eq("belong_to",id));
//		if(result == 1){
//			cacheMenuToRedis(); //刷新redis
//			return true;
//		}
//		return false;
		List<Long> menuIds = new ArrayList<>();
		menuIds.add(id);
		baseMenuRpcService.deleteMenu(menuIds);
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
	public void updateMenu(SysMenuQO sysMenuQO){
//		SysMenuEntity entity = new SysMenuEntity();
//		BeanUtils.copyProperties(sysMenuQO,entity);
//		int result = sysMenuMapper.updateById(entity);
//		if(result == 1){
//			cacheMenuToRedis(); //刷新redis
//			return true;
//		}
//		return false;
		UpdateMenuDto updateMenuDto = new UpdateMenuDto();
		updateMenuDto.setId(sysMenuQO.getId());
		updateMenuDto.setLoginType(sysMenuQO.getLoginType() == 1 ? BusinessConst.ULTIMATE_ADMIN : sysMenuQO.getLoginType() == 2 ? BusinessConst.PROPERTY_ADMIN : BusinessConst.COMMUNITY_ADMIN);
		updateMenuDto.setName(sysMenuQO.getName());
		updateMenuDto.setIcon(sysMenuQO.getIcon());
		updateMenuDto.setPath(sysMenuQO.getPath());
		updateMenuDto.setSort(sysMenuQO.getSort());
		updateMenuDto.setPid(sysMenuQO.getPid());
		updateMenuDto.setUid(sysMenuQO.getUpdateId());
		
		baseMenuRpcService.updateMenu(updateMenuDto);
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
			// list排序
			if (CollectionUtils.isEmpty(list)) {
				return new ArrayList<>();
			}
			list.sort(Comparator.comparing(SysMenuEntity::getSort));
		}catch (Exception e){
			log.error("redis获取菜单失败");
			return queryMenu();//从mysql获取
		}
		return list;
	}
	
	/**
	 * @Description: 查询用户菜单权限(新接口)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysMenuEntity>
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public List<SysMenuEntity> queryMenuByUid(Long roleId){
		//查ID
		List<Long> menuIdList = sysRoleMenuMapper.queryRoleMuneIdsByRoleIdAndLoginType(roleId);
		if(CollectionUtils.isEmpty(menuIdList)){
			return null;
		}
		//查实体
		List<SysMenuEntity> menuEntityList = sysMenuMapper.queryMenuBatch(menuIdList);
		//组装数据
		List<SysMenuEntity> returnList = new ArrayList<>();
		for(SysMenuEntity sysMenuEntity : menuEntityList){
			if(sysMenuEntity.getPid() == 0L){
				returnList.add(sysMenuEntity);
			}
		}
		// returnList排序
		returnList.sort(Comparator.comparing(SysMenuEntity::getSort));
		menuEntityList.removeAll(returnList);
		setChildrenMenu(returnList,menuEntityList);
		return returnList;
	}
	
	/**
	 * 组装子菜单
	 */
	private void setChildrenMenu(List<SysMenuEntity> childrenList, List<SysMenuEntity> childrenCopy){
		List<SysMenuEntity> selected = new ArrayList<>();
		for(SysMenuEntity child : childrenList){
			for(SysMenuEntity entity : childrenCopy){
				if(entity.getPid().equals(child.getId())){
					if(!CollectionUtils.isEmpty(child.getChildrenList())){
						child.getChildrenList().add(entity);
					}else{
						List<SysMenuEntity> childOfChild = new ArrayList<>();
						childOfChild.add(entity);
						child.setChildrenList(childOfChild);
					}
					selected.add(entity);
				}
			}
			if(!CollectionUtils.isEmpty(child.getChildrenList())){
				setChildrenMenu(child.getChildrenList(),childrenCopy);
			}
			childrenCopy.removeAll(selected);
		}
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
	public void addRole(SysRoleEntity sysRoleEntity){
//		sysRoleEntity.setId(SnowFlake.nextId());
//		//设置角色菜单
//		if(!CollectionUtils.isEmpty(sysRoleEntity.getMenuIds())){
//			setRoleMenus(sysRoleEntity.getMenuIds(),sysRoleEntity.getId());
//		}
//		int result = sysRoleMapper.insert(sysRoleEntity);
//        return result == 1;
		PermitRole permitRole = baseRoleRpcService.createRole(sysRoleEntity.getName(), sysRoleEntity.getRemark(), BusinessConst.ULTIMATE_ADMIN, sysRoleEntity.getId());
		// 菜单分配给角色
		baseMenuRpcService.menuJoinRole(sysRoleEntity.getMenuIds(), permitRole.getId(), sysRoleEntity.getId());
		// 查询菜单和权限绑定关系
		List<MenuPermission> menuPermissions = baseMenuPermissionRpcService.listByIds(sysRoleEntity.getMenuIds());
		Set<Long> permisIds = new HashSet<>();
		for (MenuPermission menuPermission : menuPermissions) {
			permisIds.add(menuPermission.getPermisId());
		}
		// 将权限添加到角色
		permissionRpcService.permitJoinRole(permisIds, permitRole.getId(), sysRoleEntity.getId());
	}
	
	/**
	 * @Description: 删除角色
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public void delRole(Long id){
//		int result = sysRoleMapper.deleteById(id);
//        return result == 1;
		List<Long> roleIds = new ArrayList<>();;
		roleIds.add(id);
		baseRoleRpcService.deleteRole(roleIds);
    }
	
	/**
	 * @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public void updateRole(SysRoleQO sysRoleOQ, Long id){
//		SysRoleEntity entity = new SysRoleEntity();
//		BeanUtils.copyProperties(sysRoleOQ,entity);
//		//更新角色菜单
//		if(!CollectionUtils.isEmpty(entity.getMenuIds())){
//			setRoleMenus(entity.getMenuIds(),entity.getId());
//		}
//		int result = sysRoleMapper.updateById(entity);
//        return result == 1;
		UpdateRoleDto updateRoleDto = new UpdateRoleDto();
		updateRoleDto.setId(sysRoleOQ.getId());
		updateRoleDto.setName(sysRoleOQ.getName());
		if (org.apache.commons.lang3.StringUtils.isNotBlank(sysRoleOQ.getRemark())) {
			updateRoleDto.setRemark(sysRoleOQ.getRemark());
		}
		updateRoleDto.setUpdateUid(id);
		// 修改角色
		baseRoleRpcService.updateRole(updateRoleDto);
		// 需要更改角色的菜单
		if (sysRoleOQ.getMenuIds() != null && sysRoleOQ.getMenuIds().size() > 0) {
			// 先移除绑定角色的菜单，再把新的菜单分配给角色(全删全增)
			// 查询该角色关联的菜单id列表
			List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(sysRoleOQ.getId());
			List<Long> menuIdsList = new ArrayList<>();
			for (RoleMenu roleMenu : roleMenus) {
				menuIdsList.add(roleMenu.getMenuId());
			}
			// 移除角色下的菜单id列表
			baseMenuRpcService.roleRemoveMenu(sysRoleOQ.getId(), menuIdsList);
			// 新菜单分配给角色
			baseMenuRpcService.menuJoinRole(sysRoleOQ.getMenuIds(), sysRoleOQ.getId(), id);
		}
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
//		return sysRoleMapper.selectList(new QueryWrapper<SysRoleEntity>().select("*
		List<SysRoleEntity> sysRoleEntities = new ArrayList<>();
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage("", BusinessConst.ULTIMATE_ADMIN, 0, 999999999);
		for (PermitRole permitRole : permitRolePageVO.getData()) {
			SysRoleEntity sysRoleEntity = new SysRoleEntity();
			sysRoleEntity.setId(permitRole.getId());
			sysRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
			sysRoleEntity.setName(permitRole.getName());
			sysRoleEntity.setRemark(permitRole.getRemark());
			sysRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			sysRoleEntities.add(sysRoleEntity);
		}
		return sysRoleEntities;
	}
	
	/**
	 * @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public PageVO<SysRoleEntity> queryPage(BaseQO<SysRoleEntity> baseQO){
//		Page<SysRoleEntity> page = new Page<>();
//		MyPageUtils.setPageAndSize(page,baseQO);
//		SysRoleEntity query = baseQO.getQuery();
//		QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.select("id,name,remark,create_time");
//		if(!StringUtils.isEmpty(query.getName())){
//			queryWrapper.like("name",query.getName());
//		}
//		if(query.getId() != null){
//			//查详情
//			queryWrapper.eq("id",query.getId());
//		}
//		Page<SysRoleEntity> pageData = sysRoleMapper.selectPage(page,queryWrapper);
//		if(query.getId() != null && !CollectionUtils.isEmpty(pageData.getRecords())){
//			//查菜单权限
//			SysRoleEntity entity = pageData.getRecords().get(0);
//			entity.setMenuIds(sysRoleMapper.getRoleMenu(entity.getId()));
//		}
//		PageInfo<SysRoleEntity> pageInfo = new PageInfo<>();
//		BeanUtils.copyProperties(pageData,pageInfo);
//		return pageInfo;
		SysRoleEntity query = baseQO.getQuery();
		Page<SysRoleEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage(query.getName(), BusinessConst.ULTIMATE_ADMIN, baseQO.getPage().intValue(), baseQO.getSize().intValue());
		if (CollectionUtils.isEmpty(permitRolePageVO.getData())) {
			return new PageVO<>();
		}
		
		PageVO<SysRoleEntity> pageVO = new PageVO<>();
		// 补充数据
		for (PermitRole permitRole : permitRolePageVO.getData()) {
			SysRoleEntity sysRoleEntity = new SysRoleEntity();
			sysRoleEntity.setId(permitRole.getId());
			sysRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
			sysRoleEntity.setName(permitRole.getName());
			sysRoleEntity.setRemark(permitRole.getRemark());
			sysRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			pageVO.getData().add(sysRoleEntity);
		}
		pageVO.setPageNum(permitRolePageVO.getPageNum());
		pageVO.setPageSize(permitRolePageVO.getPageSize());
		pageVO.setPages(permitRolePageVO.getPages());
		pageVO.setTotal(permitRolePageVO.getTotal());
		return pageVO;
	}
	
	/**
	 * @param uid : 用户uid
	 * @author: Pipi
	 * @description: 根据用户uid查询用户的角色id
	 * @return: java.lang.Long
	 * @date: 2021/8/6 10:50
	 **/
	@Override
	public SysUserRoleEntity queryRoleIdByUid(String uid) {
		QueryWrapper<SysUserRoleEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("user_id,role_id");
		queryWrapper.eq("user_id", uid);
		return sysUserRoleMapper.selectOne(queryWrapper);
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
	
	/**
	 * @param roleId : 角色ID
	 * @author: DKS
	 * @description: 查询角色详情
	 * @return: com.jsy.community.entity.admin.AdminRoleEntity
	 * @date: 2021/10/18 16:10
	 **/
	@Override
	public SysRoleEntity queryRoleDetail(Long roleId) {
//		// 查询角色信息
//		QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("id", roleId);
//		SysRoleEntity sysRoleEntity = sysRoleMapper.selectOne(queryWrapper);
//		if (sysRoleEntity == null) {
//			return new SysRoleEntity();
//		}
//		// 查询分配的菜单列表
//		List<Long> roleMenuIds = sysRoleMapper.queryRoleMuneIdsByRoleId(roleId);
//		// 去重
//		List<Long> collect = roleMenuIds.stream().distinct().collect(Collectors.toList());
//		sysRoleEntity.setMenuIds(collect);
//		return sysRoleEntity;
		SysRoleEntity sysRoleEntity = new SysRoleEntity();
		// 查角色详情
		PermitRole permitRole = baseRoleRpcService.getById(roleId);
		// 查角色关联菜单id列表
		List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(permitRole.getId());
		List<Long> menuIds = new ArrayList<>();
		for (RoleMenu roleMenu : roleMenus) {
			menuIds.add(roleMenu.getMenuId());
		}
		sysRoleEntity.setId(permitRole.getId());
		sysRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
		sysRoleEntity.setName(permitRole.getName());
		sysRoleEntity.setRemark(permitRole.getRemark());
		sysRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		sysRoleEntity.setMenuIds(menuIds);
		return sysRoleEntity;
	}
}
