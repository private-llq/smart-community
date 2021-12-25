package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminMenuQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.MenuPermission;
import com.zhsj.base.api.domain.PermitMenu;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.domain.RoleMenu;
import com.zhsj.base.api.entity.UpdateRoleDto;
import com.zhsj.base.api.rpc.IBaseMenuPermissionRpcService;
import com.zhsj.base.api.rpc.IBaseMenuRpcService;
import com.zhsj.base.api.rpc.IBasePermissionRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.base.api.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author chq459799974
 * @description 系统配置，菜单，角色，权限等(物业新版原型代码在最下面，新版无角色)
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

	@Resource
	private AdminUserMenuMapper adminUserMenuMapper;

	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserService adminUserService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseRoleRpcService baseRoleRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseMenuRpcService baseMenuRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseMenuPermissionRpcService baseMenuPermissionRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
	private IBasePermissionRpcService permissionRpcService;
	
	@Resource
	private AdminRoleCompanyMapper adminRoleCompanyMapper;

	@Autowired
	private AdminCommunityMapper adminCommunityMapper;

	@Autowired
	private AdminUserRoleMapper adminUserRoleMapper;

	@Autowired
	private AdminRoleMenuMapper adminRoleMenuMapper;

	//==================================================== Menu菜单 (旧) ===============================================================
	/**
	 * @Description: 新增菜单
	 * @Param: [sysMenuEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Deprecated
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

	//==================================================== Role角色 ===============================================================
	/**
	 * @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRole(AdminRoleEntity adminRoleEntity){
//		adminRoleEntity.setId(SnowFlake.nextId());
//		//设置角色菜单
//		if(!CollectionUtils.isEmpty(adminRoleEntity.getMenuIds())){
//			setRoleMenus(adminRoleEntity.getMenuIds(),adminRoleEntity.getId());
//		}
//		return adminRoleMapper.insert(adminRoleEntity) == 1;
		PermitRole permitRole = baseRoleRpcService.createRole(adminRoleEntity.getName(), adminRoleEntity.getRemark(),
			adminRoleEntity.getRoleType() == 1 ? BusinessConst.PROPERTY_ADMIN : BusinessConst.COMMUNITY_ADMIN, adminRoleEntity.getId());
		// 菜单分配给角色
		baseMenuRpcService.menuJoinRole(adminRoleEntity.getMenuIds(), permitRole.getId(), adminRoleEntity.getId());
		// 查询菜单和权限绑定关系
		List<MenuPermission> menuPermissions = baseMenuPermissionRpcService.listByIds(adminRoleEntity.getMenuIds());
		Set<Long> permisIds = new HashSet<>();
		for (MenuPermission menuPermission : menuPermissions) {
			permisIds.add(menuPermission.getPermisId());
		}
		// 权限绑定到角色
		permissionRpcService.permitJoinRole((List<Long>) permisIds, permitRole.getId(), adminRoleEntity.getId());
		// 新增角色和物业公司关联信息
		AdminRoleCompanyEntity entity = new AdminRoleCompanyEntity();
		entity.setId(SnowFlake.nextId());
		entity.setCompanyId(adminRoleEntity.getCompanyId());
		entity.setRoleId(permitRole.getId());
		adminRoleCompanyMapper.insert(entity);
	}

	/**
	 * @Description: 删除角色
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public void delRole(List<Long> roleIds, Long companyId){
//		return adminRoleMapper.delete(new QueryWrapper<AdminRoleEntity>().eq("id",id).eq("company_id",companyId)) == 1;
		baseRoleRpcService.deleteRole(roleIds);
		// 删除角色和物业公司关联信息
		adminRoleCompanyMapper.delete(new QueryWrapper<AdminRoleCompanyEntity>().eq("company_id", companyId).in("role_id", roleIds).eq("deleted", 0));
	}

	/**
	 * @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRole(AdminRoleQO adminRoleOQ, Long id){
//		AdminRoleEntity entity = new AdminRoleEntity();
//		BeanUtils.copyProperties(adminRoleOQ,entity);
//		entity.setCompanyId(null);
//		//更新角色菜单
//		if(!CollectionUtils.isEmpty(entity.getMenuIds())){
//			setRoleMenus(entity.getMenuIds(),entity.getId());
//		}
//		return adminRoleMapper.update(entity,new QueryWrapper<AdminRoleEntity>().eq("id",entity.getId()).eq("company_id",adminRoleOQ.getCompanyId())) == 1;
		UpdateRoleDto updateRoleDto = new UpdateRoleDto();
		updateRoleDto.setId(adminRoleOQ.getId());
		updateRoleDto.setName(adminRoleOQ.getName());
		if (org.apache.commons.lang3.StringUtils.isNotBlank(adminRoleOQ.getRemark())) {
			updateRoleDto.setRemark(adminRoleOQ.getRemark());
		}
		updateRoleDto.setUpdateUid(id);
		// 修改角色
		baseRoleRpcService.updateRole(updateRoleDto);
		// 需要更改角色的菜单
		if (adminRoleOQ.getMenuIds() != null && adminRoleOQ.getMenuIds().size() > 0) {
			// 先移除绑定角色的菜单，再把新的菜单分配给角色(全删全增)
			// 查询该角色关联的菜单id列表
			List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(adminRoleOQ.getId());
			List<Long> menuIdsList = new ArrayList<>();
			for (RoleMenu roleMenu : roleMenus) {
				menuIdsList.add(roleMenu.getMenuId());
			}
			// 移除角色下的菜单id列表
			baseMenuRpcService.roleRemoveMenu(adminRoleOQ.getId(), menuIdsList);
			// 新菜单分配给角色
			baseMenuRpcService.menuJoinRole(adminRoleOQ.getMenuIds(), adminRoleOQ.getId(), id);
		}
	}

	/**
	 * @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public PageVO<AdminRoleEntity> queryPage(BaseQO<AdminRoleEntity> baseQO){
//		Page<AdminRoleEntity> page = new Page<>();
//		MyPageUtils.setPageAndSize(page,baseQO);
//		AdminRoleEntity query = baseQO.getQuery();
//		QueryWrapper<AdminRoleEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.select("id,name,remark,create_time");
//		queryWrapper.eq("company_id",query.getCompanyId());
//		if(!StringUtils.isEmpty(query.getName())){
//			queryWrapper.like("name",query.getName());
//		}
//		if(query.getId() != null){
//			//查详情
//			queryWrapper.eq("id",query.getId());
//		}
//		Page<AdminRoleEntity> pageData = adminRoleMapper.selectPage(page,queryWrapper);
//		if(query.getId() != null && !CollectionUtils.isEmpty(pageData.getRecords())){
//			//查菜单权限
//			AdminRoleEntity entity = pageData.getRecords().get(0);
//			entity.setMenuIds(adminRoleMapper.getRoleMenu(entity.getId()));
//		}
//		PageInfo<AdminRoleEntity> pageInfo = new PageInfo<>();
//		BeanUtils.copyProperties(pageData,pageInfo);
//		return pageInfo;
		AdminRoleEntity query = baseQO.getQuery();
		Page<AdminRoleEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage(baseQO.getPage().intValue(), baseQO.getSize().intValue(), query.getName(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		if (CollectionUtils.isEmpty(permitRolePageVO.getData())) {
			return new PageVO<>();
		}
		
		PageVO<AdminRoleEntity> pageVO = new PageVO<>();
		// 补充数据
		for (PermitRole permitRole : permitRolePageVO.getData()) {
			// 已排除系统默认角色
			AdminRoleCompanyEntity entity = adminRoleCompanyMapper.selectOne(new QueryWrapper<AdminRoleCompanyEntity>().eq("role_id", permitRole.getId()).eq("deleted", 0));
			// 只返回本物业公司
			if (entity != null) {
				if (entity.getCompanyId().equals(query.getCompanyId())) {
					AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
					adminRoleEntity.setId(permitRole.getId());
					adminRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
					adminRoleEntity.setName(permitRole.getName());
					adminRoleEntity.setRemark(permitRole.getRemark());
					adminRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
					pageVO.getData().add(adminRoleEntity);
				}
			}
		}
		pageVO.setPageNum(baseQO.getPage());
		pageVO.setPageSize(permitRolePageVO.getPageSize());
		pageVO.setPages((permitRolePageVO.getTotal()-2) > 0 ? new Double(Math.ceil((permitRolePageVO.getTotal()-2)/permitRolePageVO.getPageSize())).longValue() : 0);
		pageVO.setTotal(permitRolePageVO.getTotal()-2);
		return pageVO;
	}

	/**
	 * @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public PageVO<AdminRoleEntity> queryPageAll(BaseQO<AdminRoleEntity> baseQO){
//		Page<AdminRoleEntity> page = new Page<>();
//		MyPageUtils.setPageAndSize(page,baseQO);
//		AdminRoleEntity query = baseQO.getQuery();
//		QueryWrapper<AdminRoleEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.select("id,name,remark,create_time");
//		queryWrapper.eq("company_id",query.getCompanyId());
//		if(!StringUtils.isEmpty(query.getName())){
//			queryWrapper.like("name",query.getName());
//		}
//		if(query.getId() != null){
//			//查详情
//			queryWrapper.eq("id",query.getId());
//		}
//		Page<AdminRoleEntity> pageData = adminRoleMapper.selectPage(page,queryWrapper);
//		if(query.getId() != null && !CollectionUtils.isEmpty(pageData.getRecords())){
//			//查菜单权限
//			AdminRoleEntity entity = pageData.getRecords().get(0);
//			entity.setMenuIds(adminRoleMapper.getRoleMenu(entity.getId()));
//		}
//		PageInfo<AdminRoleEntity> pageInfo = new PageInfo<>();
//		BeanUtils.copyProperties(pageData,pageInfo);
//		return pageInfo;
		AdminRoleEntity query = baseQO.getQuery();
		Page<AdminRoleEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);

		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage(baseQO.getPage().intValue(), baseQO.getSize().intValue(), query.getName(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		if (CollectionUtils.isEmpty(permitRolePageVO.getData())) {
			return new PageVO<>();
		}

		PageVO<AdminRoleEntity> pageVO = new PageVO<>();
		// 补充数据
		for (PermitRole permitRole : permitRolePageVO.getData()) {
			if (permitRole.getId() == 1463327674070695937L || permitRole.getId() == 1467739062281084931L) {
				AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
				adminRoleEntity.setId(permitRole.getId());
				adminRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
				adminRoleEntity.setName(permitRole.getName());
				adminRoleEntity.setRemark(permitRole.getRemark());
				adminRoleEntity.setScope(permitRole.getScope());
				adminRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				pageVO.getData().add(adminRoleEntity);
			} else {
				// 已排除系统默认角色
				AdminRoleCompanyEntity entity = adminRoleCompanyMapper.selectOne(new QueryWrapper<AdminRoleCompanyEntity>().eq("role_id", permitRole.getId()).eq("deleted", 0));
				// 只返回本物业公司
				if (entity != null) {
					if (entity.getCompanyId().equals(query.getCompanyId())) {
						AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
						adminRoleEntity.setId(permitRole.getId());
						adminRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
						adminRoleEntity.setName(permitRole.getName());
						adminRoleEntity.setRemark(permitRole.getRemark());
						adminRoleEntity.setScope(permitRole.getScope());
						adminRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
						pageVO.getData().add(adminRoleEntity);
					}
				}
			}
		}
		List<AdminRoleEntity> data = new ArrayList<>();
		if (query.getRoleType() == 1) {
			for (AdminRoleEntity datum : pageVO.getData()) {
				if (datum.getScope() == 8) {
					data.add(datum);
				}
			}
		}
		if (query.getRoleType() == 2) {
			for (AdminRoleEntity datum : pageVO.getData()) {
				if (datum.getScope() == 16) {
					data.add(datum);
				}
			}
		}
		pageVO.setData(data);
		pageVO.setPageNum(baseQO.getPage());
		pageVO.setPageSize(permitRolePageVO.getPageSize());
		pageVO.setPages((permitRolePageVO.getTotal()-2) > 0 ? new Double(Math.ceil((permitRolePageVO.getTotal()-2)/permitRolePageVO.getPageSize())).longValue() : 0);
		pageVO.setTotal(permitRolePageVO.getTotal()-2);
		return pageVO;
	}

	/**
	 * @param roleId : 角色ID
	 * @param companyId : 物业公司ID
	 * @author: Pipi
	 * @description: 查询角色详情
	 * @return: com.jsy.community.entity.admin.AdminRoleEntity
	 * @date: 2021/8/9 10:33
	 **/
	@Override
	public AdminRoleEntity queryRoleDetail(Long roleId, Long companyId) {
//		// 查询角色信息
//		QueryWrapper<AdminRoleEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("id", roleId);
//		queryWrapper.eq("company_id", companyId);
//		AdminRoleEntity adminRoleEntity = adminRoleMapper.selectOne(queryWrapper);
//		if (adminRoleEntity == null) {
//			return adminRoleEntity;
//		}
//		// 查询分配的菜单列表
//		List<Long> roleMenuIds = adminRoleMenuMapper.queryRoleMuneIdsByRoleId(roleId);
//		/*QueryWrapper<AdminMenuEntity> menuEntityQueryWrapper = new QueryWrapper<>();
//		menuEntityQueryWrapper.select("*, name as label");
//		menuEntityQueryWrapper.in("id", roleMuneIds);
//		List<AdminMenuEntity> adminMenuEntities = adminMenuMapper.selectList(menuEntityQueryWrapper);
//		// 查询所有菜单
//		QueryWrapper<AdminMenuEntity> menuEntityQueryWrapper = new QueryWrapper<>();
//		menuEntityQueryWrapper.select("*, name as label");
//		List<AdminMenuEntity> menuEntities = adminMenuMapper.selectList(menuEntityQueryWrapper);
//		for (AdminMenuEntity menuEntity : menuEntities) {
//			if (roleMuneIds.contains(menuEntity.getId())) {
//				menuEntity.setChecked(true);
//			} else {
//				menuEntity.setChecked(false);
//			}
//		}
//		List<AdminMenuEntity> returnMenuEntities = assemblyMenuData(adminMenuEntities);
//		adminRoleEntity.setMenuList(returnMenuEntities);*/
//		adminRoleEntity.setMenuIds(roleMenuIds);
//		return adminRoleEntity;
		AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
		// 查角色详情
		PermitRole permitRole = baseRoleRpcService.getById(roleId);
		// 查角色关联菜单id列表
		List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(permitRole.getId());
		List<Long> menuIds = new ArrayList<>();
		for (RoleMenu roleMenu : roleMenus) {
			menuIds.add(roleMenu.getMenuId());
		}
		adminRoleEntity.setId(permitRole.getId());
		adminRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
		adminRoleEntity.setName(permitRole.getName());
		adminRoleEntity.setRemark(permitRole.getRemark());
		adminRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		adminRoleEntity.setMenuIds(menuIds);
		return adminRoleEntity;
	}

	//==================================================== 角色-菜单 ===============================================================
	/**
	 * @Description: 为角色设置菜单
	 * @Param: [menuIds, roleId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	private void setRoleMenus(List<Long> menuIds,Long roleId){
		//设置子菜单
		/*if(!CollectionUtils.isEmpty(menuIds)){
			List<Long> idBelongList = adminMenuMapper.getIdBelongList(menuIds);
			menuIds.addAll(idBelongList);
		}*/
		//去重
		Set<Long> menuIdsSet = new HashSet<>(menuIds);
		//清空
		adminRoleMapper.clearRoleMenu(roleId);
		//新增
		adminRoleMapper.addRoleMenuBatch(menuIdsSet, roleId);
	}
	//==================================================== 用户-角色 ===============================================================

	/**
	 * @param uid : 用户uid
	 * @author: Pipi
	 * @description: 根据用户uid查询用户的角色id
	 * @return: java.lang.Long
	 * @date: 2021/8/6 10:50
	 **/
	@Override
	public AdminUserRoleEntity queryRoleIdByUid(String uid) {
		QueryWrapper<AdminUserRoleEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("uid,role_id");
		queryWrapper.eq("uid", uid);
		return adminUserRoleMapper.selectOne(queryWrapper);
	}


	//==================================================== 用户-菜单 (旧) ===============================================================
	/**
	 * @Description: 查询用户菜单权限(老接口，暂时弃用)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Deprecated
	@Override
	public List<AdminMenuEntity> queryUserMenu(Long uid){
		return adminMenuMapper.queryUserMenu(uid);
	}

	//================================================== 新版物业端原型 - 用户-菜单start =========================================================================
	/**
	 * @Description: 查询用户菜单权限(新接口)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.admin.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	 **/
	@Override
	public List<AdminMenuEntity> queryMenuByUid(Long roleId, Integer loginType){
		//查ID
		List<Long> menuIdList = adminRoleMenuMapper.queryRoleMuneIdsByRoleIdAndLoginType(roleId, loginType);
		if(CollectionUtils.isEmpty(menuIdList)){
			return null;
		}
		//查实体
		List<AdminMenuEntity> menuEntityList = adminMenuMapper.queryMenuBatch(menuIdList,loginType);
		//组装数据
		List<AdminMenuEntity> returnList = new ArrayList<>();
		for(AdminMenuEntity adminMenuEntity : menuEntityList){
			if(adminMenuEntity.getPid() == 0L){
				returnList.add(adminMenuEntity);
			}
		}
		menuEntityList.removeAll(returnList);
		setChildrenMenu(returnList,menuEntityList);
		return returnList;
	}


	/**
	 * @author: Pipi
	 * @description: 组装菜单数据
	 * @param menuEntityList: 需要组装的数据
	 * @return: java.util.List<com.jsy.community.entity.admin.AdminMenuEntity>
	 * @date: 2021/8/9 11:19
	 **/
	private List<AdminMenuEntity> assemblyMenuData(List<AdminMenuEntity> menuEntityList) {
		List<AdminMenuEntity> returnList = new ArrayList<>();
		for(AdminMenuEntity adminMenuEntity : menuEntityList){
			if(adminMenuEntity.getPid() == 0L){
				returnList.add(adminMenuEntity);
			}
		}
		menuEntityList.removeAll(returnList);
		setChildrenMenu(returnList,menuEntityList);
		return returnList;
	}

	/**
	 * 组装子菜单
	 */
	private void setChildrenMenu(List<AdminMenuEntity> childrenList,List<AdminMenuEntity> childrenCopy){
		List<AdminMenuEntity> selected = new ArrayList<>();
		for(AdminMenuEntity child : childrenList){
			for(AdminMenuEntity entity : childrenCopy){
				if(entity.getPid().equals(child.getId())){
					if(!CollectionUtils.isEmpty(child.getChildren())){
						child.getChildren().add(entity);
					}else{
						List<AdminMenuEntity> childOfChild = new ArrayList<>();
						childOfChild.add(entity);
						child.setChildren(childOfChild);
					}
					selected.add(entity);
				}
			}
			if(!CollectionUtils.isEmpty(child.getChildren())){
				setChildrenMenu(child.getChildren(),childrenCopy);
			}
			childrenCopy.removeAll(selected);
		}
	}

	/**
	 * @Description: 统计用户菜单数
	 * @Param: [uid]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	 **/
	@Override
	public Integer countUserMenu(String uid){
		return adminUserMenuMapper.selectCount(new QueryWrapper<AdminUserMenuEntity>().eq("uid",uid));
	}

	/**
	 * @Description: 查询用户菜单id列表
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	 **/
	@Override
	public List<String> queryUserMenuIdList(String uid){
		//返回UID对应菜单列表
		return adminUserMenuMapper.queryUserMenuIdList(uid);
	}

	/**
	 * @Description: 为用户分配菜单
	 * @Param: [menuIds, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/23
	 **/
	@Override
	public void setUserMenus(List<Long> menuIds,String uid){
		//备份
//		List<Long> menuBackup = adminMenuMapper.getUserMenu(uid);
		//清空
		adminMenuMapper.clearUserMenu(uid);
		if(CollectionUtils.isEmpty(menuIds)){
			return;
		}
		//去重
		Set<Long> menuIdsSet = new HashSet<>(menuIds);
		//新增
		int rows = 0;
		try{
			rows = adminMenuMapper.addUserMenuBatch(menuIdsSet, uid);
		}catch (Exception e){
			//还原
			log.error("设置角色菜单出错：" + uid + "成功条数：" + rows);
//			adminMenuMapper.clearUserMenu(uid);
//			adminMenuMapper.addUserMenuBatch(new HashSet<>(menuBackup), uid);
//			return false;
			throw new PropertyException(JSYError.INTERNAL.getCode(),"用户功能授权失败，操作失败");
		}
		//还原
		if(rows != menuIdsSet.size()){
			log.error("设置角色菜单异常：" + uid + "成功条数：" + rows);
//			adminMenuMapper.clearUserMenu(uid);
//			adminMenuMapper.addUserMenuBatch(new HashSet<>(menuBackup), uid);
//			return false;
			throw new PropertyException(JSYError.INTERNAL.getCode(),"用户功能授权失败，操作失败");
		}
//		return true;
	}

	/**
	 * @Description: (功能授权)菜单列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public List<AdminMenuEntity> listOfMenu() {
		List<AdminMenuEntity> list;
		try{
			// todo 此菜单Redis键名因为与线上环境调用一直,故在本地测试时键名改为Admin:MenuLocal,上传至线上时改为Admin:Menu
			list = JSONArray.parseObject(stringRedisTemplate.opsForValue().get("Admin:MenuLocal"),List.class);
			if (list == null) {
				return queryMenu();//从mysql获取
			}
		}catch (Exception e){
			log.error("redis获取菜单失败");
			return queryMenu();//从mysql获取
		}
		return list;
	}
	
	/**
	 * @Description: 根据角色类型查询所有菜单
	 * @author: DKS
	 * @since: 2021/12/25 9:23
	 * @Param: [roleType, id]
	 * @return: java.util.List<com.zhsj.base.api.domain.PermitMenu>
	 */
	@Override
	public List<PermitMenu> MenuPage(Integer roleType, Long id) {
		List<PermitMenu> permitMenus = baseMenuRpcService.all(id, roleType == 1 ? BusinessConst.PROPERTY_ADMIN : BusinessConst.COMMUNITY_ADMIN);
		// list排序
		permitMenus.sort(Comparator.comparing(PermitMenu::getSort));
		return permitMenus;
	}

	/**
	 * @Description: 缓存菜单到redis
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@PostConstruct
	private void cacheMenuToRedis(){
		stringRedisTemplate.opsForValue().set("Admin:MenuLocal", JSON.toJSONString(queryMenu()));
	}

	/**
	 * @Description: 查询全部菜单
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	private List<AdminMenuEntity> queryMenu(){
		List<AdminMenuEntity> menuList = adminMenuMapper.selectList(new QueryWrapper<AdminMenuEntity>().select("*,name as label").eq("pid", 0));
		setChildren(menuList,new LinkedList<>());
		stringRedisTemplate.opsForValue().set("Admin:MenuLocal", JSON.toJSONString(menuList));
		return menuList;
	}
	/**
	 * 组装子菜单
	 */
	private void setChildren(List<AdminMenuEntity> parentList, List<AdminMenuEntity> childrenList){
		if(!CollectionUtils.isEmpty(parentList)){
			for(AdminMenuEntity adminMenuEntity : parentList){
				childrenList = adminMenuMapper.getChildrenList(adminMenuEntity.getId());
				adminMenuEntity.setChildren(childrenList);
				setChildren(childrenList,new LinkedList<>());
			}
		}
	}

	/**
	 * @Description: 管理员社区权限列表查询
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.admin.AdminCommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	 **/
	@Override
	public List<AdminCommunityEntity> listAdminCommunity(String uid){
		return adminCommunityMapper.selectList(new QueryWrapper<AdminCommunityEntity>().select("community_id").eq("uid",uid));
	}

	/**
	 * @Description: 管理员社区权限id列表查询
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.admin.AdminCommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	 **/
	@Override
	public List<Long> queryAdminCommunityIdListByUid(String uid){
		return adminCommunityMapper.queryAdminCommunityIdListByUid(uid);
	}

	/**
	 * 管理员社区权限批量修改
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAdminCommunityBatch(List<String> communityIds,String uid){
		//清空
		adminCommunityMapper.clearAdminCommunityByUid(uid);
		//去重
		Set<String> communityIdsSet = new HashSet<>(communityIds);
		//添加
		adminCommunityMapper.addAdminCommunityBatch(communityIdsSet,uid);
	}

	/**
	 * @param uid         : 用户id
	 * @param communityId : 社区id
	 * @author: Pipi
	 * @description: 新增用户与小区权限数据
	 * @return: java.lang.Integer
	 * @date: 2021/7/22 10:35
	 **/
	@Override
	public Integer addAdminCommunity(String uid, Long communityId) {
		AdminCommunityEntity adminCommunityEntity = new AdminCommunityEntity();
		adminCommunityEntity.setUid(uid);
		adminCommunityEntity.setCommunityId(String.valueOf(communityId));
		return adminCommunityMapper.insert(adminCommunityEntity);
	}

	//================================================== 新版物业端原型 - 用户-菜单end =========================================================================
}

