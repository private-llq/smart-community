package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.admin.AdminRoleCompanyEntity;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.mapper.AdminRoleCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.jsy.community.service.AdminRoleService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.MenuPermission;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.domain.RoleMenu;
import com.zhsj.base.api.entity.UpdateRoleDto;
import com.zhsj.base.api.rpc.IBaseMenuPermissionRpcService;
import com.zhsj.base.api.rpc.IBaseMenuRpcService;
import com.zhsj.base.api.rpc.IBasePermissionRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.base.api.vo.PageVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    @Resource
    private AdminRoleCompanyMapper adminRoleCompanyMapper;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseRoleRpcService baseRoleRpcService;
    
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseMenuRpcService baseMenuRpcService;
    
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseMenuPermissionRpcService baseMenuPermissionRpcService;
    
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBasePermissionRpcService permissionRpcService;

    /**
     * @Description: 添加角色
     * @Param: [adminRoleQO]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/12/14
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(AdminRoleQO adminRoleQO){
    
        PermitRole permitRole = baseRoleRpcService.createRole(adminRoleQO.getName(), adminRoleQO.getRemark(), BusinessConst.PROPERTY_ADMIN, adminRoleQO.getId());
        // 菜单分配给角色
        baseMenuRpcService.menuJoinRole(adminRoleQO.getMenuIds(), permitRole.getId(), adminRoleQO.getId());
        // 查询菜单和权限绑定关系
        List<MenuPermission> menuPermissions = baseMenuPermissionRpcService.listByIds(adminRoleQO.getMenuIds());
        List<Long> permisIds = new ArrayList<>();
        for (MenuPermission menuPermission : menuPermissions) {
            permisIds.add(menuPermission.getPermisId());
        }
        permissionRpcService.permitJoinRole(permisIds, permitRole.getId(), adminRoleQO.getId());
        // 新增角色和物业公司关联信息
        AdminRoleCompanyEntity entity = new AdminRoleCompanyEntity();
        entity.setId(SnowFlake.nextId());
        entity.setCompanyId(adminRoleQO.getCompanyId());
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
    public void delRole(List<Long> roleIds){
        baseRoleRpcService.deleteRole(roleIds);
        // 删除角色和物业公司关联信息
        adminRoleCompanyMapper.delete(new QueryWrapper<AdminRoleCompanyEntity>().in("role_id", roleIds).eq("deleted", 0));
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
//        AdminRoleEntity entity = new AdminRoleEntity();
//        BeanUtils.copyProperties(adminRoleOQ,entity);
//        entity.setCompanyId(null);
//        //更新角色菜单
//        if(!CollectionUtils.isEmpty(entity.getMenuIds())){
//            setRoleMenus(entity.getMenuIds(),entity.getId());
//        }
//        return adminRoleMapper.update(entity,new QueryWrapper<AdminRoleEntity>().eq("id",entity.getId()).eq("company_id",adminRoleOQ.getCompanyId())) == 1;
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
//        Page<AdminRoleEntity> page = new Page<>();
//        MyPageUtils.setPageAndSize(page,baseQO);
//        AdminRoleEntity query = baseQO.getQuery();
//        QueryWrapper<AdminRoleEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("id,name,remark,create_time");
//        queryWrapper.eq("company_id",query.getCompanyId());
//        if(!StringUtils.isEmpty(query.getName())){
//            queryWrapper.like("name",query.getName());
//        }
//        if(query.getId() != null){
//            //查详情
//            queryWrapper.eq("id",query.getId());
//        }
//        Page<AdminRoleEntity> pageData = adminRoleMapper.selectPage(page,queryWrapper);
//        if(query.getId() != null && !CollectionUtils.isEmpty(pageData.getRecords())){
//            //查菜单权限
//            AdminRoleEntity entity = pageData.getRecords().get(0);
//            entity.setMenuIds(adminRoleMapper.getRoleMenu(entity.getId()));
//        }
//        PageInfo<AdminRoleEntity> pageInfo = new PageInfo<>();
//        BeanUtils.copyProperties(pageData,pageInfo);
//        return pageInfo;
        AdminRoleEntity query = baseQO.getQuery();
        Page<AdminRoleEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
    
        PageVO<PermitRole> permitRolePageVO = baseRoleRpcService.selectPage(query.getName(), BusinessConst.PROPERTY_ADMIN, baseQO.getPage().intValue(), baseQO.getSize().intValue());
        if (CollectionUtils.isEmpty(permitRolePageVO.getData())) {
            return new PageVO<>();
        }
    
        PageVO<AdminRoleEntity> pageVO = new PageVO<>();
        // 补充数据
        for (PermitRole permitRole : permitRolePageVO.getData()) {
            AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
            adminRoleEntity.setId(permitRole.getId());
            adminRoleEntity.setIdStr(String.valueOf(permitRole.getId()));
            adminRoleEntity.setName(permitRole.getName());
            adminRoleEntity.setRemark(permitRole.getRemark());
            adminRoleEntity.setCreateTime(LocalDateTime.parse(permitRole.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pageVO.getData().add(adminRoleEntity);
        }
        pageVO.setPageNum(permitRolePageVO.getPageNum());
        pageVO.setPageSize(permitRolePageVO.getPageSize());
        pageVO.setPages(permitRolePageVO.getPages());
        pageVO.setTotal(permitRolePageVO.getTotal());
        return pageVO;
    }

    /**
     * @param roleId : 角色ID
     * @author: Pipi
     * @description: 查询角色详情
     * @return: com.jsy.community.entity.admin.AdminRoleEntity
     * @date: 2021/8/9 10:33
     **/
    @Override
    public AdminRoleEntity queryRoleDetail(Long roleId) {
//        // 查询角色信息
//        QueryWrapper<AdminRoleEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id", roleId);
//        queryWrapper.eq("company_id", companyId);
//        AdminRoleEntity adminRoleEntity = adminRoleMapper.selectOne(queryWrapper);
//        if (adminRoleEntity == null) {
//            return adminRoleEntity;
//        }
//        // 查询分配的菜单列表
//        List<Long> roleMenuIds = adminRoleMenuMapper.queryRoleMuneIdsByRoleId(roleId);
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
//        adminRoleEntity.setMenuIds(roleMenuIds);
//        return adminRoleEntity;
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

//    /**
//     * @param uid : 用户uid
//     * @author: Pipi
//     * @description: 根据用户uid查询用户的角色id
//     * @return: java.lang.Long
//     * @date: 2021/8/6 10:50
//     **/
//    @Override
//    public AdminUserRoleEntity queryRoleIdByUid(String uid) {
//        QueryWrapper<AdminUserRoleEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("uid,role_id");
//        queryWrapper.eq("uid", uid);
//        return adminUserRoleMapper.selectOne(queryWrapper);
//    }
}
