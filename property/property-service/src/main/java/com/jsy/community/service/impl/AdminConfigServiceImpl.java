package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
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
import com.zhsj.basecommon.constant.BaseUserConstant;
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
import java.util.stream.Collectors;

/**
 * @author chq459799974
 * @description ??????????????????????????????????????????(??????????????????????????????????????????????????????)
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

	//==================================================== Menu?????? (???) ===============================================================
	/**
	 * @Description: ????????????
	 * @Param: [sysMenuEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Deprecated
	@Override
	public boolean addMenu(AdminMenuEntity adminMenuEntity){
		if(adminMenuEntity.getPid() != null && adminMenuEntity.getPid() != 0){ //????????????????????????????????????????????????????????????
			AdminMenuEntity parent = adminMenuMapper.findParent(adminMenuEntity.getPid());
			if(parent == null){
				return false;
			}
			if(0 == parent.getBelongTo()){//?????????????????????
				adminMenuEntity.setBelongTo(parent.getId());
			}else{ //??????????????????
				adminMenuEntity.setBelongTo(parent.getBelongTo());//??????????????????????????????
			}
		}else { //???????????????
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
			cacheMenuToRedis(); //??????redis
			return true;
		}
		return false;
	}

	//??????????????????ID
//	private void setBelongTo(AdminMenuEntity sysMenuEntity,AdminMenuEntity parent){
//		if(0L != parent.getPid()){ //???????????????????????????
//			parent = sysMenuMapper.findParent(parent.getPid());//???????????????
//			if(parent.getPid() == 0){ //????????????
//				sysMenuEntity.setBelongTo(parent.getUserId());
//			}else{
//				setBelongTo(sysMenuEntity,parent);
//			}
//		}
//	}

	/**
	 * @Description: ????????????
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public boolean delMenu(Long id){
		List<Long> idList = new LinkedList<>(); // ????????????????????????id
		idList.add(id);
//		List<Long> subIdList = sysMenuMapper.getSubIdList(Arrays.asList(id));
//		setDeleteIds(idList, subIdList);
//		int result = sysMenuMapper.deleteBatchIds(idList);
		int result = adminMenuMapper.deleteById(id);
		adminMenuMapper.delete(new QueryWrapper<AdminMenuEntity>().eq("belong_to",id));
		if(result == 1){
			cacheMenuToRedis(); //??????redis
			return true;
		}
		return false;
	}

	//???????????????????????????id
//	private void setDeleteIds(List<Long> idList, List<Long> subIdList) {
//		if(!CollectionUtils.isEmpty(subIdList)){
//			subIdList.removeAll(idList);
//			idList.addAll(subIdList);
//			setDeleteIds(idList,sysMenuMapper.getSubIdList(subIdList));
//		}
//	}

	/**
	 * @Description: ????????????
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
			cacheMenuToRedis(); //??????redis
			return true;
		}
		return false;
	}

	//==================================================== Role?????? ===============================================================
	/**
	 * @Description: ????????????
	 * @Param: [sysRoleEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	@LcnTransaction
	public void addRole(AdminRoleEntity adminRoleEntity){
//		adminRoleEntity.setId(SnowFlake.nextId());
//		//??????????????????
//		if(!CollectionUtils.isEmpty(adminRoleEntity.getMenuIds())){
//			setRoleMenus(adminRoleEntity.getMenuIds(),adminRoleEntity.getUserId());
//		}
//		return adminRoleMapper.insert(adminRoleEntity) == 1;
		// ????????????????????????
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage(0, 999999999, "", BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		List<String> permitRoleNames = permitRolePageVO.getData().stream().map(PermitRole::getName).collect(Collectors.toList());
		for (String permitRoleName : permitRoleNames) {
			if (permitRoleName.equals(adminRoleEntity.getName())) {
				throw new PropertyException(JSYError.DUPLICATE_KEY.getCode(), "?????????????????????????????????????????????!");
			}
		}
		
		PermitRole permitRole = baseRoleRpcService.createRole(adminRoleEntity.getName(), adminRoleEntity.getRemark(),
			adminRoleEntity.getRoleType() == BaseUserConstant.Login.DataBasePermitScope.PROPERTY_ADMIN ? BusinessConst.PROPERTY_ADMIN : BusinessConst.COMMUNITY_ADMIN, adminRoleEntity.getId());
		// ?????????????????????
		baseMenuRpcService.menuJoinRole(adminRoleEntity.getMenuIds(), permitRole.getId(), adminRoleEntity.getId());
		// ?????????????????????????????????
		List<MenuPermission> menuPermissions = baseMenuPermissionRpcService.listByIds(adminRoleEntity.getMenuIds());
		if (!CollectionUtils.isEmpty(menuPermissions)) {
			Set<Long> permisIds = new HashSet<>();
			for (MenuPermission menuPermission : menuPermissions) {
				permisIds.add(menuPermission.getPermisId());
			}
			// ?????????????????????
			if (!CollectionUtils.isEmpty(permisIds)) {
				permissionRpcService.permitJoinRole(permisIds, permitRole.getId(), adminRoleEntity.getId());
			}
		}
		// ???????????????????????????????????????
		AdminRoleCompanyEntity entity = new AdminRoleCompanyEntity();
		entity.setId(SnowFlake.nextId());
		entity.setCompanyId(adminRoleEntity.getCompanyId());
		entity.setRoleId(permitRole.getId());
		adminRoleCompanyMapper.insert(entity);
	}

	/**
	 * @Description: ????????????
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public void delRole(List<Long> roleIds, Long companyId){
//		return adminRoleMapper.delete(new QueryWrapper<AdminRoleEntity>().eq("id",id).eq("company_id",companyId)) == 1;
		baseRoleRpcService.deleteRole(roleIds);
		// ???????????????????????????????????????
		adminRoleCompanyMapper.delete(new QueryWrapper<AdminRoleCompanyEntity>().eq("company_id", companyId).in("role_id", roleIds).eq("deleted", 0));
	}

	/**
	 * @Description: ????????????
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
//		//??????????????????
//		if(!CollectionUtils.isEmpty(entity.getMenuIds())){
//			setRoleMenus(entity.getMenuIds(),entity.getUserId());
//		}
//		return adminRoleMapper.update(entity,new QueryWrapper<AdminRoleEntity>().eq("id",entity.getUserId()).eq("company_id",adminRoleOQ.getCompanyId())) == 1;
		// ????????????????????????
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage(0, 999999999, "", BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		List<String> permitRoleNames = permitRolePageVO.getData().stream().map(PermitRole::getName).collect(Collectors.toList());
		for (String permitRoleName : permitRoleNames) {
			if (permitRoleName.equals(adminRoleOQ.getName())) {
				throw new PropertyException(JSYError.DUPLICATE_KEY.getCode(), "?????????????????????????????????????????????!");
			}
		}
		
		UpdateRoleDto updateRoleDto = new UpdateRoleDto();
		updateRoleDto.setId(adminRoleOQ.getId());
		updateRoleDto.setName(adminRoleOQ.getName());
		if (org.apache.commons.lang3.StringUtils.isNotBlank(adminRoleOQ.getRemark())) {
			updateRoleDto.setRemark(adminRoleOQ.getRemark());
		}
		updateRoleDto.setUpdateUid(id);
		// ????????????
		baseRoleRpcService.updateRole(updateRoleDto);
		// ???????????????????????????
		if (adminRoleOQ.getMenuIds() != null && adminRoleOQ.getMenuIds().size() > 0) {
			// ??????????????????????????????????????????????????????????????????(????????????)
			// ??????????????????????????????id??????
			List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(adminRoleOQ.getId());
			List<Long> menuIdsList = new ArrayList<>();
			for (RoleMenu roleMenu : roleMenus) {
				menuIdsList.add(roleMenu.getMenuId());
			}
			// ????????????????????????id??????
			baseMenuRpcService.roleRemoveMenu(adminRoleOQ.getId(), menuIdsList);
			// ????????????????????????
			baseMenuRpcService.menuJoinRole(adminRoleOQ.getMenuIds(), adminRoleOQ.getId(), id);
		}
	}

	/**
	 * @Description: ???????????? ????????????
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
//		if(query.getUserId() != null){
//			//?????????
//			queryWrapper.eq("id",query.getUserId());
//		}
//		Page<AdminRoleEntity> pageData = adminRoleMapper.selectPage(page,queryWrapper);
//		if(query.getUserId() != null && !CollectionUtils.isEmpty(pageData.getRecords())){
//			//???????????????
//			AdminRoleEntity entity = pageData.getRecords().get(0);
//			entity.setMenuIds(adminRoleMapper.getRoleMenu(entity.getUserId()));
//		}
//		PageInfo<AdminRoleEntity> pageInfo = new PageInfo<>();
//		BeanUtils.copyProperties(pageData,pageInfo);
//		return pageInfo;
		AdminRoleEntity query = baseQO.getQuery();
		Page<AdminRoleEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);

		// ????????????
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage(1, 9999, query.getName(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		if (CollectionUtils.isEmpty(permitRolePageVO.getData())) {
			return new PageVO<>();
		}
		Set<Long> roleIdSet = permitRolePageVO.getData().stream().filter(p -> p.getType() != 1).map(PermitRole::getId).collect(Collectors.toSet());
		if (CollectionUtils.isEmpty(roleIdSet)) {
			return new PageVO<>();
		}
		List<AdminRoleCompanyEntity> roleCompanyEntityList = adminRoleCompanyMapper.selectList(new QueryWrapper<AdminRoleCompanyEntity>().eq("company_id", query.getCompanyId()).in("role_id", roleIdSet));
		if (!CollectionUtils.isEmpty(roleCompanyEntityList)) {
			List<Long> companyRoleIdSet = roleCompanyEntityList.stream().map(AdminRoleCompanyEntity::getRoleId).collect(Collectors.toList());
			List<PermitRole> permitRoleList = new ArrayList<>();
			for (PermitRole permitRole : permitRolePageVO.getData()) {
				if (companyRoleIdSet.contains(permitRole.getId())) {
					permitRoleList.add(permitRole);
				}
			}
			List<AdminRoleEntity> result = new ArrayList<>();
			int end = baseQO.getSize() * baseQO.getPage() < permitRoleList.size() ? (int) (baseQO.getSize() * baseQO.getPage()) : permitRoleList.size();
			int start = (int) (baseQO.getSize() * (baseQO.getPage() - 1));
			for (int i = start; i < end; i++) {
				AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
				adminRoleEntity.setId(permitRoleList.get(i).getId());
				adminRoleEntity.setIdStr(String.valueOf(permitRoleList.get(i).getId()));
				adminRoleEntity.setName(permitRoleList.get(i).getName());
				adminRoleEntity.setRemark(permitRoleList.get(i).getRemark());
				adminRoleEntity.setCreateTime(LocalDateTime.parse(permitRoleList.get(i).getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				result.add(adminRoleEntity);
			}
			PageVO<AdminRoleEntity> pageVO = new PageVO<>();
			pageVO.setPageNum(baseQO.getPage());
			pageVO.setPageSize(baseQO.getSize());
			Long pages = permitRoleList.size() > 0 ? new Double(Math.ceil(permitRoleList.size() / baseQO.getSize())).longValue() : 0;
			pageVO.setPages(pages);
			pageVO.setTotal((long) permitRoleList.size());
			pageVO.setData(result);
			return pageVO;
		} else {
			return new PageVO<>();
		}

		/*// ????????????
		for (PermitRole permitRole : permitRolePageVO.getData()) {
			// ???????????????????????????
			AdminRoleCompanyEntity entity = adminRoleCompanyMapper.selectOne(new QueryWrapper<AdminRoleCompanyEntity>().eq("role_id", permitRole.getUserId()).eq("deleted", 0));
			// ????????????????????????
			if (entity != null) {
				if (entity.getCompanyId().equals(query.getCompanyId())) {
					AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
					adminRoleEntity.setId(permitRole.getUserId());
					adminRoleEntity.setIdStr(String.valueOf(permitRole.getUserId()));
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
		return pageVO;*/
	}

	/**
	 * @Description: ???????????? ????????????
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
//		if(query.getUserId() != null){
//			//?????????
//			queryWrapper.eq("id",query.getUserId());
//		}
//		Page<AdminRoleEntity> pageData = adminRoleMapper.selectPage(page,queryWrapper);
//		if(query.getUserId() != null && !CollectionUtils.isEmpty(pageData.getRecords())){
//			//???????????????
//			AdminRoleEntity entity = pageData.getRecords().get(0);
//			entity.setMenuIds(adminRoleMapper.getRoleMenu(entity.getUserId()));
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
		// ????????????
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
				// ???????????????????????????
				AdminRoleCompanyEntity entity = adminRoleCompanyMapper.selectOne(new QueryWrapper<AdminRoleCompanyEntity>().eq("role_id", permitRole.getId()).eq("deleted", 0));
				// ????????????????????????
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
				if (datum.getScope() == BaseUserConstant.Login.DataBasePermitScope.PROPERTY_ADMIN) {
					data.add(datum);
				}
			}
		}
		if (query.getRoleType() == 2) {
			for (AdminRoleEntity datum : pageVO.getData()) {
				if (datum.getScope() == BaseUserConstant.Login.DataBasePermitScope.COMMUNITY_ADMIN) {
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
	 * @param roleId : ??????ID
	 * @param companyId : ????????????ID
	 * @author: Pipi
	 * @description: ??????????????????
	 * @return: com.jsy.community.entity.admin.AdminRoleEntity
	 * @date: 2021/8/9 10:33
	 **/
	@Override
	public AdminRoleEntity queryRoleDetail(Long roleId, Long companyId) {
//		// ??????????????????
//		QueryWrapper<AdminRoleEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("id", roleId);
//		queryWrapper.eq("company_id", companyId);
//		AdminRoleEntity adminRoleEntity = adminRoleMapper.selectOne(queryWrapper);
//		if (adminRoleEntity == null) {
//			return adminRoleEntity;
//		}
//		// ???????????????????????????
//		List<Long> roleMenuIds = adminRoleMenuMapper.queryRoleMuneIdsByRoleId(roleId);
//		/*QueryWrapper<AdminMenuEntity> menuEntityQueryWrapper = new QueryWrapper<>();
//		menuEntityQueryWrapper.select("*, name as label");
//		menuEntityQueryWrapper.in("id", roleMuneIds);
//		List<AdminMenuEntity> adminMenuEntities = adminMenuMapper.selectList(menuEntityQueryWrapper);
//		// ??????????????????
//		QueryWrapper<AdminMenuEntity> menuEntityQueryWrapper = new QueryWrapper<>();
//		menuEntityQueryWrapper.select("*, name as label");
//		List<AdminMenuEntity> menuEntities = adminMenuMapper.selectList(menuEntityQueryWrapper);
//		for (AdminMenuEntity menuEntity : menuEntities) {
//			if (roleMuneIds.contains(menuEntity.getUserId())) {
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
		// ???????????????
		PermitRole permitRole = baseRoleRpcService.getById(roleId);
		// ?????????????????????id??????
		List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(permitRole.getId());
		List<Long> menuIds = new ArrayList<>();
		List<String> menuIdsStr = new ArrayList<>();
		for (RoleMenu roleMenu : roleMenus) {
			menuIds.add(roleMenu.getMenuId());
			menuIdsStr.add(String.valueOf(roleMenu.getMenuId()));
		}
		adminRoleEntity.setId(permitRole.getId());
		adminRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
		adminRoleEntity.setName(permitRole.getName());
		adminRoleEntity.setRemark(permitRole.getRemark());
		adminRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		adminRoleEntity.setMenuIds(menuIds);
		adminRoleEntity.setMenuIdsStr(menuIdsStr);
		adminRoleEntity.setScope(permitRole.getScope());
		adminRoleEntity.setRoleType(permitRole.getScope());
		return adminRoleEntity;
	}

	//==================================================== ??????-?????? ===============================================================
	/**
	 * @Description: ?????????????????????
	 * @Param: [menuIds, roleId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	private void setRoleMenus(List<Long> menuIds,Long roleId){
		//???????????????
		/*if(!CollectionUtils.isEmpty(menuIds)){
			List<Long> idBelongList = adminMenuMapper.getIdBelongList(menuIds);
			menuIds.addAll(idBelongList);
		}*/
		//??????
		Set<Long> menuIdsSet = new HashSet<>(menuIds);
		//??????
		adminRoleMapper.clearRoleMenu(roleId);
		//??????
		adminRoleMapper.addRoleMenuBatch(menuIdsSet, roleId);
	}
	//==================================================== ??????-?????? ===============================================================

	/**
	 * @param uid : ??????uid
	 * @author: Pipi
	 * @description: ????????????uid?????????????????????id
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


	//==================================================== ??????-?????? (???) ===============================================================
	/**
	 * @Description: ????????????????????????(????????????????????????)
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

	//================================================== ????????????????????? - ??????-??????start =========================================================================
	/**
	 * @Description: ????????????????????????(?????????)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.admin.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	 **/
	@Override
	public List<AdminMenuEntity> queryMenuByUid(Long roleId, Integer loginType){
		//???ID
		List<Long> menuIdList = adminRoleMenuMapper.queryRoleMuneIdsByRoleIdAndLoginType(roleId, loginType);
		if(CollectionUtils.isEmpty(menuIdList)){
			return null;
		}
		//?????????
		List<AdminMenuEntity> menuEntityList = adminMenuMapper.queryMenuBatch(menuIdList,loginType);
		//????????????
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
	 * @description: ??????????????????
	 * @param menuEntityList: ?????????????????????
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
	 * ???????????????
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
	 * @Description: ?????????????????????
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
	 * @Description: ??????????????????id??????
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	 **/
	@Override
	public List<String> queryUserMenuIdList(String uid){
		//??????UID??????????????????
		return adminUserMenuMapper.queryUserMenuIdList(uid);
	}

	/**
	 * @Description: ?????????????????????
	 * @Param: [menuIds, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/23
	 **/
	@Override
	public void setUserMenus(List<Long> menuIds,String uid){
		//??????
//		List<Long> menuBackup = adminMenuMapper.getUserMenu(uid);
		//??????
		adminMenuMapper.clearUserMenu(uid);
		if(CollectionUtils.isEmpty(menuIds)){
			return;
		}
		//??????
		Set<Long> menuIdsSet = new HashSet<>(menuIds);
		//??????
		int rows = 0;
		try{
			rows = adminMenuMapper.addUserMenuBatch(menuIdsSet, uid);
		}catch (Exception e){
			//??????
			log.error("???????????????????????????" + uid + "???????????????" + rows);
//			adminMenuMapper.clearUserMenu(uid);
//			adminMenuMapper.addUserMenuBatch(new HashSet<>(menuBackup), uid);
//			return false;
			throw new PropertyException(JSYError.INTERNAL.getCode(),"???????????????????????????????????????");
		}
		//??????
		if(rows != menuIdsSet.size()){
			log.error("???????????????????????????" + uid + "???????????????" + rows);
//			adminMenuMapper.clearUserMenu(uid);
//			adminMenuMapper.addUserMenuBatch(new HashSet<>(menuBackup), uid);
//			return false;
			throw new PropertyException(JSYError.INTERNAL.getCode(),"???????????????????????????????????????");
		}
//		return true;
	}

	/**
	 * @Description: (????????????)????????????
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public List<AdminMenuEntity> listOfMenu() {
		List<AdminMenuEntity> list;
		try{
			// todo ?????????Redis???????????????????????????????????????,?????????????????????????????????Admin:MenuLocal,????????????????????????Admin:Menu
			list = JSONArray.parseObject(stringRedisTemplate.opsForValue().get("Admin:MenuLocal"),List.class);
			if (list == null) {
				return queryMenu();//???mysql??????
			}
		}catch (Exception e){
			log.error("redis??????????????????");
			return queryMenu();//???mysql??????
		}
		return list;
	}
	
	/**
	 * @Description: ????????????????????????????????????
	 * @author: DKS
	 * @since: 2021/12/25 9:23
	 * @Param: [roleType, id]
	 * @return: java.util.List<com.zhsj.base.api.domain.PermitMenu>
	 */
	@Override
	public List<PermitMenu> MenuPage(Integer roleType, Long id) {
		List<PermitMenu> permitMenus = baseMenuRpcService.all(id, roleType == 8 ? BusinessConst.PROPERTY_ADMIN : BusinessConst.COMMUNITY_ADMIN);
		// list??????
		permitMenus.sort(Comparator.comparing(PermitMenu::getSort));
		return permitMenus;
	}

	/**
	 * @Description: ???????????????redis
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
	 * @Description: ??????????????????
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
	 * ???????????????
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
	 * @Description: ?????????????????????????????????
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
	 * @Description: ?????????????????????id????????????
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
	 * ?????????????????????????????????
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAdminCommunityBatch(List<String> communityIds,String uid){
		//??????
		adminCommunityMapper.clearAdminCommunityByUid(uid);
		//??????
		Set<String> communityIdsSet = new HashSet<>(communityIds);
		//??????
		adminCommunityMapper.addAdminCommunityBatch(communityIdsSet,uid);
	}

	/**
	 * @param uid         : ??????id
	 * @param communityId : ??????id
	 * @author: Pipi
	 * @description: ?????????????????????????????????
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

	//================================================== ????????????????????? - ??????-??????end =========================================================================
}

