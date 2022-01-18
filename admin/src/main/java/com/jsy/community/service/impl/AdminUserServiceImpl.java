package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.admin.AdminUserCompanyEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.AdminUserCompanyMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.IAdminUserService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.base.api.rpc.IBaseUpdateUserRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
import com.zhsj.basecommon.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author DKS
 * @description 账号管理
 * @since 2021-11-18 16:24
 **/
@Slf4j
@Service
public class AdminUserServiceImpl implements IAdminUserService {
	
	@Resource
	private PropertyCompanyMapper propertyCompanyMapper;
	
	@Resource
	private AdminUserCompanyMapper adminUserCompanyMapper;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService userInfoRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseAuthRpcService baseAuthRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUpdateUserRpcService baseUpdateUserRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseRoleRpcService baseRoleRpcService;
	
	/**
	 * @Description: 操作员条件查询
	 * @author: DKS
	 * @since: 2021/11/19 14:29
	 * @Param: [baseQO]
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.admin.AdminUserEntity>
	 */
	@Override
	public PageVO<AdminUserEntity> queryOperator(BaseQO<AdminUserQO> baseQO){
		AdminUserQO query = baseQO.getQuery();
		Page<AdminUserEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		
		// 查出所有
		PageVO<UserDetail> userDetailPageVO = userInfoRpcService.queryUser(query.getMobile(), "", BusinessConst.PROPERTY_ADMIN, null, 0, 999999999);
		
		if (CollectionUtils.isEmpty(userDetailPageVO.getData())) {
			return new PageVO<>();
		}
		userDetailPageVO.setData(userDetailPageVO.getData().stream().filter(userDetail -> !userDetail.getAccount().equals(query.getUid())).collect(Collectors.toList()));
		List<Long> companyIds = new ArrayList<>();
		List<String> uIds = new ArrayList<>();
		// 模糊查物业公司名称
		if (org.apache.commons.lang3.StringUtils.isNotBlank(query.getCompanyName())) {
			List<PropertyCompanyEntity> propertyCompanyEntities = propertyCompanyMapper.selectList(new QueryWrapper<PropertyCompanyEntity>().select("id").like("name", query.getCompanyName()));
			for (PropertyCompanyEntity propertyCompanyEntity : propertyCompanyEntities) {
				companyIds.add(propertyCompanyEntity.getId());
			}
			// 根据物业公司id查出Uid
			if (companyIds.size() > 0) {
				List<AdminUserCompanyEntity> entityList = adminUserCompanyMapper.selectList(new QueryWrapper<AdminUserCompanyEntity>().in("company_id", companyIds).eq("deleted", 0));
				for (AdminUserCompanyEntity adminUserCompanyEntity : entityList) {
					uIds.add(adminUserCompanyEntity.getUid());
				}
			}
			if (uIds.size() == 0) {
				return new PageVO<>();
			}
		}
		if (uIds.size() > 0) {
			userDetailPageVO.getData().removeIf(userDetail -> !uIds.contains(userDetail.getAccount()));
		}
		
		if (CollectionUtils.isEmpty(userDetailPageVO.getData())) {
			return new PageVO<>();
		}
		PageVO<AdminUserEntity> pageVO = new PageVO<>();
		
//		// 补充数据
//		for (UserDetail userDetail : userDetailPageVO.getData()) {
//			AdminUserEntity adminUserEntity = new AdminUserEntity();
//			// 补充物业公司名称
//			AdminUserCompanyEntity entity = adminUserCompanyMapper.selectOne(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", userDetail.getAccount()));
//			if (entity != null) {
//				PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(entity.getCompanyId());
//				if (companyEntity != null) {
//					adminUserEntity.setCompanyName(companyEntity.getName());
//					adminUserEntity.setCommunityId(companyEntity.getId());
//					adminUserEntity.setCompanyIdStr(String.valueOf(companyEntity.getId()));
//				}
//			}
//			adminUserEntity.setId(Long.parseLong(userDetail.getAccount()));
//			adminUserEntity.setIdStr(userDetail.getAccount());
//			adminUserEntity.setNickName(userDetail.getNickName());
//			adminUserEntity.setMobile(userDetail.getPhone());
//			adminUserEntity.setCreateTime(LocalDateTime.parse(userDetail.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//			pageVO.getData().add(adminUserEntity);
//		}
		
		// 获取物业公司信息
		Set<String> accountSet = userDetailPageVO.getData().stream().map(UserDetail::getAccount).collect(Collectors.toSet());
		List<AdminUserCompanyEntity> entityList = adminUserCompanyMapper.selectList(new QueryWrapper<AdminUserCompanyEntity>().in("uid", accountSet));
		Map<String, Long> companyMap = entityList.stream().collect(Collectors.toMap(AdminUserCompanyEntity::getUid, AdminUserCompanyEntity::getCompanyId));
		List<PropertyCompanyEntity> propertyCompanyEntities = propertyCompanyMapper.selectList(new QueryWrapper<>());
		Map<Long, PropertyCompanyEntity> propertyCompanyEntityMap = propertyCompanyEntities.stream().collect(Collectors.toMap(PropertyCompanyEntity::getId, Function.identity()));
		
		int end = baseQO.getSize() * baseQO.getPage() < userDetailPageVO.getData().size() ? (int) (baseQO.getSize() * baseQO.getPage()) : userDetailPageVO.getData().size();
		int start = (int) (baseQO.getSize() * (baseQO.getPage() - 1));
		for (int i = start; i < end; i++) {
			AdminUserEntity adminUserEntity = new AdminUserEntity();
			// 补充物业公司名称
			if (!CollectionUtils.isEmpty(companyMap)) {
				Long companyId = companyMap.get(userDetailPageVO.getData().get(i).getAccount());
				if (!CollectionUtils.isEmpty(propertyCompanyEntityMap)) {
					PropertyCompanyEntity companyEntity = propertyCompanyEntityMap.get(companyId);
					if (companyEntity != null) {
						adminUserEntity.setCompanyName(companyEntity.getName());
						adminUserEntity.setCommunityId(companyEntity.getId());
						adminUserEntity.setCompanyIdStr(String.valueOf(companyEntity.getId()));
					}
				}
			}
			adminUserEntity.setId(Long.valueOf(userDetailPageVO.getData().get(i).getAccount()));
			adminUserEntity.setIdStr(userDetailPageVO.getData().get(i).getAccount());
			adminUserEntity.setNickName(userDetailPageVO.getData().get(i).getNickName());
			adminUserEntity.setMobile(userDetailPageVO.getData().get(i).getPhone());
			adminUserEntity.setCreateTime(LocalDateTime.parse(userDetailPageVO.getData().get(i).getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			pageVO.getData().add(adminUserEntity);
		}
		
		
		pageVO.setPageNum(baseQO.getPage());
		pageVO.setPageSize(baseQO.getSize());
		Long pages = userDetailPageVO.getData().size() > 0 ? new Double(Math.ceil(userDetailPageVO.getData().size() / baseQO.getSize())).longValue() : 0;
		pageVO.setPages(pages);
		pageVO.setTotal((long) userDetailPageVO.getData().size());
		return pageVO;
	}
	
	/**
	 * @Description: 添加操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserQO]
	 * @return: void
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer addOperator(AdminUserQO adminUserQO){
		// 判断是新增(1)的还是原有(2)的
		int result = 1;
		// 增加用户
		UserDetail userDetail = null;
		
		try {
			userDetail = baseAuthRpcService.userPhoneRegister(adminUserQO.getNickName(), adminUserQO.getMobile(), adminUserQO.getPassword());
		} catch (BaseException e) {
			// 手机号是否已经注册
			if (e.getErrorEnum().getCode() == 103) {
				userDetail = userInfoRpcService.getUserDetailByPhone(adminUserQO.getMobile());
				result = 2;
			}
		}
		if (userDetail == null) {
			throw new AdminException("用户添加失败");
		}
		// 增加登录类型范围为物业中台
		baseAuthRpcService.addLoginTypeScope(userDetail.getId(), BusinessConst.PROPERTY_ADMIN);
		baseAuthRpcService.addLoginTypeScope(userDetail.getId(), BusinessConst.COMMUNITY_ADMIN);
		// 绑定用户和角色
//		List<Long> roleIds = new ArrayList<>();
//		roleIds.add(adminUserQO.getRoleId());
//		baseRoleRpcService.userJoinRole(roleIds, userDetail.getUserId(), 1460884237115367425L);
		// 先查询是否已经绑定用户和物业公司
		AdminUserCompanyEntity entity = adminUserCompanyMapper.selectOne(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", userDetail.getAccount()).eq("company_id", adminUserQO.getCompanyId()));
		// 没有绑定
		if (entity == null) {
			// 绑定用户和物业公司
			entity = new AdminUserCompanyEntity();
			entity.setId(SnowFlake.nextId());
			entity.setCompanyId(adminUserQO.getCompanyId());
			entity.setUid(userDetail.getAccount());
			adminUserCompanyMapper.insert(entity);
		}
		return result;
	}
	
	/**
	 * @Description: 编辑操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserQO]
	 * @return: void
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOperator(AdminUserQO adminUserQO){
		UserDetail userDetail = userInfoRpcService.getUserDetail(String.valueOf(adminUserQO.getId()));
		//更新资料
		baseUpdateUserRpcService.updateUserInfo(userDetail.getId(), adminUserQO.getNickName(),
			adminUserQO.getMobile(), adminUserQO.getPassword(), BusinessConst.PROPERTY_ADMIN);
	}
	
	/**
	 * @Description: 删除操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [id]
	 * @return: void
	 */
	@Override
	public void deleteOperator(Long id) {
		UserDetail userDetail = userInfoRpcService.getUserDetail(String.valueOf(id));
		List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		// 移除用户角色绑定关系
		Set<Long> roleIds = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
		baseRoleRpcService.roleRemoveToUser(roleIds, userDetail.getId());
		// 移除登录类型范围
		baseAuthRpcService.removeLoginTypeScope(userDetail.getId(), BusinessConst.PROPERTY_ADMIN, false);
		baseAuthRpcService.removeLoginTypeScope(userDetail.getId(), BusinessConst.COMMUNITY_ADMIN, false);
//		baseAuthRpcService.cancellation(id);
		// 删除的同时也要删除用户和物业公司绑定关系
		adminUserCompanyMapper.delete(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", id));
	}
}