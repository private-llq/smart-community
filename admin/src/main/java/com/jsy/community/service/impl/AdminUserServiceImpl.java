package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.admin.AdminUserCompanyEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.AdminUserCompanyMapper;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyCompanyMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.service.IAdminUserService;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.rpc.IBaseUpdateUserRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author DKS
 * @description 账号管理
 * @since 2021-11-18 16:24
 **/
@Slf4j
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUserEntity> implements IAdminUserService {
	
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
	
	/**
	 * @Description: 操作员条件查询
	 * @author: DKS
	 * @since: 2021/11/19 14:29
	 * @Param: [baseQO]
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.admin.AdminUserEntity>
	 */
	@Override
	public PageVO<UserDetail> queryOperator(BaseQO<AdminUserQO> baseQO){
		AdminUserQO query = baseQO.getQuery();
		Page<AdminUserEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		
		List<Long> companyIds = new ArrayList<>();
		List<Long> uIds = new ArrayList<>();
		// 模糊查物业公司名称
		if (org.apache.commons.lang3.StringUtils.isNotBlank(query.getCompanyName())) {
			List<PropertyCompanyEntity> propertyCompanyEntities = propertyCompanyMapper.selectList(new QueryWrapper<PropertyCompanyEntity>().select("id").like("name", query.getCompanyName()));
			for (PropertyCompanyEntity propertyCompanyEntity : propertyCompanyEntities) {
				companyIds.add(propertyCompanyEntity.getId());
			}
		}
		// 根据物业公司id查出Uid
		if (companyIds.size() > 0) {
			List<AdminUserCompanyEntity> entityList = adminUserCompanyMapper.selectList(new QueryWrapper<AdminUserCompanyEntity>().in("company_id", companyIds).eq("deleted", 0));
			for (AdminUserCompanyEntity adminUserCompanyEntity : entityList) {
				uIds.add(Long.parseLong(adminUserCompanyEntity.getUid()));
			}
		}
		
		PageVO<UserDetail> userDetailPageVO = userInfoRpcService.queryUser(query.getPhone(), "", baseQO.getPage().intValue(), baseQO.getSize().intValue());
		
		if (CollectionUtils.isEmpty(userDetailPageVO.getData())) {
			return new PageVO<>();
		}
		if (uIds.size() > 0) {
			userDetailPageVO.getData().removeIf(userDetail -> !uIds.contains(userDetail.getId()));
		}
		// 补充数据
//		for (AdminUserEntity entity : userDetailPageVO.getData()) {
//			// 补充物业公司名称
//			Long companyId = adminUserMapper.queryCompanyId(entity.getUid());
//			PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(companyId);
//			if (companyEntity != null) {
//				entity.setCompanyName(companyEntity.getName());
//			}
//			// 补充应用
//			Long roleId = adminUserMapper.queryRoleIdByUid(entity.getUid());
//			List<String> menuNameList = adminUserMapper.queryMenuNameByRoleId(roleId);
//			//删除集合中某一元素值
//			StringBuilder sb = new StringBuilder();
//			Iterator<String> iterator = menuNameList.iterator();
//			while (iterator.hasNext()){
//				String next = iterator.next();
//				if (next.contains("2")){
//					iterator.remove();
//				} else {
//					sb.append(next).append(",");
//				}
//			}
//			String menuName = "";
//			if (sb.length() > 0 ) {
//				menuName = sb.substring(0, sb.length() - 1);
//			}
//			entity.setMenuName(menuName);
//		}
		
		return userDetailPageVO;
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