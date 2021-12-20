package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.admin.AdminUserCompanyEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.AdminUserCompanyMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.service.IAdminUserService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.base.api.rpc.IBaseUpdateUserRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
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
		
		PageVO<UserDetail> userDetailPageVO = userInfoRpcService.queryUser(query.getPhone(), "","property_admin", null, baseQO.getPage().intValue(), baseQO.getSize().intValue());
		
		if (CollectionUtils.isEmpty(userDetailPageVO.getData())) {
			return new PageVO<>();
		}
		
		List<Long> companyIds = new ArrayList<>();
		List<Long> uIds = new ArrayList<>();
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
					uIds.add(Long.valueOf(adminUserCompanyEntity.getUid()));
				}
			}
			if (uIds.size() == 0) {
				return new PageVO<>();
			}
		}
		if (uIds.size() > 0) {
			userDetailPageVO.getData().removeIf(userDetail -> !uIds.contains(userDetail.getId()));
		}
		PageVO<AdminUserEntity> pageVO = new PageVO<>();
		// 补充数据
		for (UserDetail userDetail : userDetailPageVO.getData()) {
			AdminUserEntity adminUserEntity = new AdminUserEntity();
			// 补充物业公司名称
			AdminUserCompanyEntity entity = adminUserCompanyMapper.selectOne(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", userDetail.getId()).eq("deleted", 0));
			if (entity != null) {
				PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(entity.getCompanyId());
				if (companyEntity != null) {
					adminUserEntity.setCompanyName(companyEntity.getName());
				}
			}
			adminUserEntity.setId(userDetail.getId());
			adminUserEntity.setIdStr(String.valueOf(userDetail.getId()));
			adminUserEntity.setNickname(userDetail.getNickName());
			adminUserEntity.setMobile(userDetail.getPhone());
			adminUserEntity.setCreateTime(LocalDateTime.parse(userDetail.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			pageVO.getData().add(adminUserEntity);
		}
		pageVO.setPageNum(userDetailPageVO.getPageNum());
		pageVO.setPageSize(userDetailPageVO.getPageSize());
		pageVO.setPages(userDetailPageVO.getPages());
		pageVO.setTotal(userDetailPageVO.getTotal());
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
	public void addOperator(AdminUserQO adminUserQO){
		// 增加用户
		UserDetail userDetail = baseAuthRpcService.userPhoneRegister(adminUserQO.getNickName(), adminUserQO.getPhone(), adminUserQO.getPassword());
		// 增加登录类型范围为物业中台
		baseAuthRpcService.addLoginTypeScope(userDetail.getId(), "property_admin");
		// 绑定用户和角色
//		List<Long> roleIds = new ArrayList<>();
//		roleIds.add(adminUserQO.getRoleId());
//		baseRoleRpcService.userJoinRole(roleIds, userDetail.getId(), 1460884237115367425L);
		// 绑定用户和物业公司
		AdminUserCompanyEntity entity = new AdminUserCompanyEntity();
		entity.setId(SnowFlake.nextId());
		entity.setCompanyId(adminUserQO.getCompanyId());
		entity.setUid(String.valueOf(userDetail.getId()));
		adminUserCompanyMapper.insert(entity);
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
		//更新资料
		baseUpdateUserRpcService.updateUserInfo(adminUserQO.getId(), adminUserQO.getNickName(),
			adminUserQO.getPhone(), adminUserQO.getPassword(), "property_admin");
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
		baseAuthRpcService.cancellation(id);
		// 删除的同时也要删除用户和物业公司绑定关系
		adminUserCompanyMapper.delete(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", id).eq("deleted", 0));
	}
}