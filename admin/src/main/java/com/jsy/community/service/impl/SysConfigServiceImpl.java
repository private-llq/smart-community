package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.entity.sys.SysRoleEntity;
import com.jsy.community.entity.sys.SysUserRoleEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.SysMenuMapper;
import com.jsy.community.mapper.SysRoleMapper;
import com.jsy.community.mapper.SysRoleMenuMapper;
import com.jsy.community.mapper.SysUserRoleMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysMenuQO;
import com.jsy.community.qo.sys.SysRoleQO;
import com.jsy.community.service.AdminException;
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
import java.util.stream.Collectors;

/**
 * @author chq459799974
 * @description ??????????????????????????????????????????
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
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBasePermissionRpcService permissionRpcService;
	
	//==================================================== Menu?????? ===============================================================
	/**
	* @Description: ????????????
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
	* @Description: ?????????????????????
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	public List<PermitMenu> queryMenu(){
		List<PermitMenu> ultimateMenu = baseMenuRpcService.all(BusinessConst.ULTIMATE_ADMIN);
//		List<SysMenuEntity> menuList = sysMenuMapper.selectList(new QueryWrapper<SysMenuEntity>().select("*").eq("pid", 0));
//		setChildren(menuList,new LinkedList<>());
//		menuList.sort(Comparator.comparing(SysMenuEntity::getSort));
		return ultimateMenu;
	}
	
	//???????????????
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
	* @Description: ????????????
	 * @Param: [sysMenuEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public void addMenu(SysMenuEntity sysMenuEntity){
//		if(sysMenuEntity.getPid() != null && sysMenuEntity.getPid() != 0){ //????????????????????????????????????????????????????????????
//			SysMenuEntity parent = sysMenuMapper.findParent(sysMenuEntity.getPid());
//			if(parent == null){
//				return false;
//			}
//			if(0 == parent.getBelongTo()){//?????????????????????
//				sysMenuEntity.setBelongTo(parent.getUserId());
//			}else{ //??????????????????
//				sysMenuEntity.setBelongTo(parent.getBelongTo());//??????????????????????????????
//			}
//		}else { //???????????????
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
//			cacheMenuToRedis(); //??????redis
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
		// ????????????
		PermitMenu permitMenu = baseMenuRpcService.addMenu(addMenuDto);
		// ???????????????????????????
		List<Long> menuIds = new ArrayList<>();
		menuIds.add(permitMenu.getId());
		baseMenuRpcService.menuJoinRole(menuIds, sysMenuEntity.getLoginType() == 1 ? 1463327674104250369L : sysMenuEntity.getLoginType() == 2 ? 1463327674070695937L : 1467739062281084931L , 1460884237115367425L);
	}
	
	//??????????????????ID
//	private void setBelongTo(AppMenuEntity sysMenuEntity,AppMenuEntity parent){
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
	public void delMenu(Long id){
//		List<Long> idList = new LinkedList<>(); // ????????????????????????id
//		idList.add(id);
////		List<Long> subIdList = sysMenuMapper.getSubIdList(Arrays.asList(id));
////		setDeleteIds(idList, subIdList);
////		int result = sysMenuMapper.deleteBatchIds(idList);
//		int result = sysMenuMapper.deleteById(id);
//		sysMenuMapper.delete(new QueryWrapper<SysMenuEntity>().eq("belong_to",id));
//		if(result == 1){
//			cacheMenuToRedis(); //??????redis
//			return true;
//		}
//		return false;
		List<Long> menuIds = new ArrayList<>();
		menuIds.add(id);
		baseMenuRpcService.deleteMenu(menuIds);
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
	public void updateMenu(SysMenuQO sysMenuQO){
//		SysMenuEntity entity = new SysMenuEntity();
//		BeanUtils.copyProperties(sysMenuQO,entity);
//		int result = sysMenuMapper.updateById(entity);
//		if(result == 1){
//			cacheMenuToRedis(); //??????redis
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
	* @Description: ????????????
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public List<PermitMenu> listOfMenu() {
		return queryMenu();//???mysql??????
	}
	
	/**
	 * @Description: ????????????????????????(?????????)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysMenuEntity>
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public List<SysMenuEntity> queryMenuByUid(Long roleId){
		//???ID
		List<Long> menuIdList = sysRoleMenuMapper.queryRoleMuneIdsByRoleIdAndLoginType(roleId);
		if(CollectionUtils.isEmpty(menuIdList)){
			return null;
		}
		//?????????
		List<SysMenuEntity> menuEntityList = sysMenuMapper.queryMenuBatch(menuIdList);
		//????????????
		List<SysMenuEntity> returnList = new ArrayList<>();
		for(SysMenuEntity sysMenuEntity : menuEntityList){
			if(sysMenuEntity.getPid() == 0L){
				returnList.add(sysMenuEntity);
			}
		}
		// returnList??????
		returnList.sort(Comparator.comparing(SysMenuEntity::getSort));
		menuEntityList.removeAll(returnList);
		setChildrenMenu(returnList,menuEntityList);
		return returnList;
	}
	
	/**
	 * ???????????????
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
	
	//==================================================== Role?????? ===============================================================
	/**
	 * @Description: ????????????
	 * @Param: [sysRoleEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public void addRole(SysRoleEntity sysRoleEntity){
//		sysRoleEntity.setId(SnowFlake.nextId());
//		//??????????????????
//		if(!CollectionUtils.isEmpty(sysRoleEntity.getMenuIds())){
//			setRoleMenus(sysRoleEntity.getMenuIds(),sysRoleEntity.getUserId());
//		}
//		int result = sysRoleMapper.insert(sysRoleEntity);
//        return result == 1;
		// ????????????????????????
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage("", BusinessConst.ULTIMATE_ADMIN, 0, 999999999);
		List<String> permitRoleNames = permitRolePageVO.getData().stream().map(PermitRole::getName).collect(Collectors.toList());
		for (String permitRoleName : permitRoleNames) {
			if (permitRoleName.equals(sysRoleEntity.getName())) {
				throw new AdminException(JSYError.DUPLICATE_KEY.getCode(), "?????????????????????????????????????????????!");
			}
		}
		
		PermitRole permitRole = baseRoleRpcService.createRole(sysRoleEntity.getName(), sysRoleEntity.getRemark(), BusinessConst.ULTIMATE_ADMIN, sysRoleEntity.getId());
		// ?????????????????????
		baseMenuRpcService.menuJoinRole(sysRoleEntity.getMenuIds(), permitRole.getId(), sysRoleEntity.getId());
		// ?????????????????????????????????
		List<MenuPermission> menuPermissions = baseMenuPermissionRpcService.listByIds(sysRoleEntity.getMenuIds());
		Set<Long> permisIds = new HashSet<>();
		for (MenuPermission menuPermission : menuPermissions) {
			permisIds.add(menuPermission.getPermisId());
		}
		// ????????????????????????
		if (!CollectionUtils.isEmpty(permisIds)) {
			permissionRpcService.permitJoinRole(permisIds, permitRole.getId(), sysRoleEntity.getId());
		}
	}
	
	/**
	 * @Description: ????????????
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public void delRole(Long id){
//		int result = sysRoleMapper.deleteById(id);
//        return result == 1;
		List<Long> roleIds = new ArrayList<>();
		roleIds.add(id);
		baseRoleRpcService.deleteRole(roleIds);
    }
	
	/**
	 * @Description: ????????????
	 * @Param: [sysRoleQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Override
	public void updateRole(SysRoleQO sysRoleOQ, Long id){
//		SysRoleEntity entity = new SysRoleEntity();
//		BeanUtils.copyProperties(sysRoleOQ,entity);
//		//??????????????????
//		if(!CollectionUtils.isEmpty(entity.getMenuIds())){
//			setRoleMenus(entity.getMenuIds(),entity.getUserId());
//		}
//		int result = sysRoleMapper.updateById(entity);
//        return result == 1;
		// ????????????????????????
		PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage("", BusinessConst.ULTIMATE_ADMIN, 0, 999999999);
		List<String> permitRoleNames = permitRolePageVO.getData().stream().map(PermitRole::getName).collect(Collectors.toList());
		for (String permitRoleName : permitRoleNames) {
			if (permitRoleName.equals(sysRoleOQ.getName())) {
				throw new AdminException(JSYError.DUPLICATE_KEY.getCode(), "?????????????????????????????????????????????!");
			}
		}
		
		UpdateRoleDto updateRoleDto = new UpdateRoleDto();
		updateRoleDto.setId(sysRoleOQ.getId());
		updateRoleDto.setName(sysRoleOQ.getName());
		if (org.apache.commons.lang3.StringUtils.isNotBlank(sysRoleOQ.getRemark())) {
			updateRoleDto.setRemark(sysRoleOQ.getRemark());
		}
		updateRoleDto.setUpdateUid(id);
		// ????????????
		baseRoleRpcService.updateRole(updateRoleDto);
		// ???????????????????????????
		if (sysRoleOQ.getMenuIds() != null && sysRoleOQ.getMenuIds().size() > 0) {
			// ??????????????????????????????????????????????????????????????????(????????????)
			// ??????????????????????????????id??????
			List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(sysRoleOQ.getId());
			List<Long> menuIdsList = new ArrayList<>();
			for (RoleMenu roleMenu : roleMenus) {
				menuIdsList.add(roleMenu.getMenuId());
			}
			// ????????????????????????id??????
			baseMenuRpcService.roleRemoveMenu(sysRoleOQ.getId(), menuIdsList);
			// ????????????????????????
			baseMenuRpcService.menuJoinRole(sysRoleOQ.getMenuIds(), sysRoleOQ.getId(), id);
		}
    }
	
	/**
	 * @Description: ????????????
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
	 * @Description: ???????????? ????????????
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
//		if(query.getUserId() != null){
//			//?????????
//			queryWrapper.eq("id",query.getUserId());
//		}
//		Page<SysRoleEntity> pageData = sysRoleMapper.selectPage(page,queryWrapper);
//		if(query.getUserId() != null && !CollectionUtils.isEmpty(pageData.getRecords())){
//			//???????????????
//			SysRoleEntity entity = pageData.getRecords().get(0);
//			entity.setMenuIds(sysRoleMapper.getRoleMenu(entity.getUserId()));
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
		// ????????????
		for (PermitRole permitRole : permitRolePageVO.getData()) {
			SysRoleEntity sysRoleEntity = new SysRoleEntity();
			sysRoleEntity.setId(permitRole.getId());
			sysRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
			sysRoleEntity.setName(permitRole.getName());
			sysRoleEntity.setRemark(permitRole.getRemark());
			sysRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			// ????????????????????????id
			List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(permitRole.getId());
			List<Long> menuIdList = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
			List<String> menuIdsList = menuIdList.stream().map(String::valueOf).collect(Collectors.toList());
			sysRoleEntity.setMenuIds(menuIdList);
			sysRoleEntity.setMenuIdsStr(menuIdsList);
			pageVO.getData().add(sysRoleEntity);
		}
		pageVO.setPageNum(permitRolePageVO.getPageNum());
		pageVO.setPageSize(permitRolePageVO.getPageSize());
		pageVO.setPages(permitRolePageVO.getPages());
		pageVO.setTotal(permitRolePageVO.getTotal());
		return pageVO;
	}
	
	/**
	 * @param uid : ??????uid
	 * @author: Pipi
	 * @description: ????????????uid?????????????????????id
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
	
	//==================================================== ??????-?????? ===============================================================
	/**
	 * @Description: ?????????????????????
	 * @Param: [menuIds, roleId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Override
	public boolean setRoleMenus(List<Long> menuIds,Long roleId){
		//???????????????
		if(!CollectionUtils.isEmpty(menuIds)){
//			List<Long> subIdList = sysMenuMapper.getSubIdList(menuIds);
			List<Long> idBelongList = sysMenuMapper.getIdBelongList(menuIds);
			menuIds.addAll(idBelongList);
		}
		//??????
		List<Long> userRoles = sysRoleMapper.getRoleMenu(roleId);
		//??????
		sysRoleMapper.clearRoleMenu(roleId);
		//??????
		int rows = sysRoleMapper.addRoleMenuBatch(menuIds, roleId);
		//??????
		if(rows != menuIds.size()){
			log.error("???????????????????????????" + roleId,"???????????????" + rows);
			sysRoleMapper.clearRoleMenu(roleId);
			sysRoleMapper.addRoleMenuBatch(userRoles, roleId);
			return false;
		}
		return true;
	}
	
	//==================================================== ??????-?????? ===============================================================
	/**
	* @Description: ????????????????????????
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
	 * @param roleId : ??????ID
	 * @author: DKS
	 * @description: ??????????????????
	 * @return: com.jsy.community.entity.admin.AdminRoleEntity
	 * @date: 2021/10/18 16:10
	 **/
	@Override
	public SysRoleEntity queryRoleDetail(Long roleId) {
//		// ??????????????????
//		QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("id", roleId);
//		SysRoleEntity sysRoleEntity = sysRoleMapper.selectOne(queryWrapper);
//		if (sysRoleEntity == null) {
//			return new SysRoleEntity();
//		}
//		// ???????????????????????????
//		List<Long> roleMenuIds = sysRoleMapper.queryRoleMuneIdsByRoleId(roleId);
//		// ??????
//		List<Long> collect = roleMenuIds.stream().distinct().collect(Collectors.toList());
//		sysRoleEntity.setMenuIds(collect);
//		return sysRoleEntity;
		SysRoleEntity sysRoleEntity = new SysRoleEntity();
		// ???????????????
		PermitRole permitRole = baseRoleRpcService.getById(roleId);
		// ????????????????????????id
		List<RoleMenu> roleMenus = baseRoleRpcService.listAllRoleMenu(permitRole.getId());
		List<Long> menuIdList = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
		List<String> menuIdsList = menuIdList.stream().map(String::valueOf).collect(Collectors.toList());
		
		sysRoleEntity.setId(permitRole.getId());
		sysRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
		sysRoleEntity.setName(permitRole.getName());
		sysRoleEntity.setRemark(permitRole.getRemark());
		sysRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		sysRoleEntity.setMenuIds(menuIdList);
		sysRoleEntity.setMenuIdsStr(menuIdsList);
		return sysRoleEntity;
	}
}
