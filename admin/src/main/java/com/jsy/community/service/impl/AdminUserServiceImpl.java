package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.admin.AdminUserAuthEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.IAdminUserService;
import com.jsy.community.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author DKS
 * @description 账号管理
 * @since 2021-11-18 16:24
 **/
@Slf4j
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUserEntity> implements IAdminUserService {
	
	@Resource
	private RedisTemplate redisTemplate;
	
	@Resource
	private AdminUserMapper adminUserMapper;
	
	@Resource
	private AdminUserAuthMapper adminUserAuthMapper;
	
	@Resource
	private AdminCommunityMapper adminCommunityMapper;
	
	@Resource
	private PropertyCompanyMapper propertyCompanyMapper;
	
	@Resource
	private CommunityMapper communityMapper;
	
	@Value("${propertyLoginExpireHour}")
	private long loginExpireHour = 12;
	
	/**
	 * @Description: 操作员条件查询
	 * @author: DKS
	 * @since: 2021/11/19 14:29
	 * @Param: [baseQO]
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.admin.AdminUserEntity>
	 */
	@Override
	public PageInfo<AdminUserEntity> queryOperator(BaseQO<AdminUserQO> baseQO){
		AdminUserQO query = baseQO.getQuery();
		Page<AdminUserEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		QueryWrapper<AdminUserEntity> queryWrapper = new QueryWrapper<>();
		// 模糊查物业公司名称
		if (org.apache.commons.lang3.StringUtils.isNotBlank(query.getCompanyNameOrMobile())) {
			List<PropertyCompanyEntity> propertyCompanyEntities = propertyCompanyMapper.selectList(new QueryWrapper<PropertyCompanyEntity>().select("id").like("name", query.getCompanyNameOrMobile()));
			List<Long> companyIds = new ArrayList<>();
			for (PropertyCompanyEntity propertyCompanyEntity : propertyCompanyEntities) {
				companyIds.add(propertyCompanyEntity.getId());
			}
			// 根据物业公司id查出Uid
			if (companyIds.size() > 0) {
				List<String> uidList = adminUserMapper.queryUidByCompanyIds(companyIds);
				queryWrapper.in("uid", uidList);
			}
			queryWrapper.or().like("mobile", query.getCompanyNameOrMobile());
		}
		queryWrapper.orderByDesc("create_time");
		Page<AdminUserEntity> pageData = adminUserMapper.selectPage(page, queryWrapper);
		if (CollectionUtils.isEmpty(pageData.getRecords())) {
			return new PageInfo<>();
		}
		// 补充数据
		for (AdminUserEntity entity : pageData.getRecords()) {
			// 补充物业公司名称
			Long companyId = adminUserMapper.queryCompanyId(entity.getUid());
			PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(companyId);
			if (companyEntity != null) {
				entity.setCompanyName(companyEntity.getName());
			}
			// 补充应用
			Long roleId = adminUserMapper.queryRoleIdByUid(entity.getUid());
			List<String> menuNameList = adminUserMapper.queryMenuNameByRoleId(roleId);
			//删除集合中某一元素值
			StringBuilder sb = new StringBuilder();
			Iterator<String> iterator = menuNameList.iterator();
			while (iterator.hasNext()){
				String next = iterator.next();
				if (next.contains("2")){
					iterator.remove();
				} else {
					sb.append(next).append(",");
				}
			}
			String menuName = "";
			if (sb.length() > 0 ) {
				menuName = sb.substring(0, sb.length() - 1);
			}
			entity.setMenuName(menuName);
		}
		
		PageInfo<AdminUserEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
	
	/**
	 * @Description: 添加操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserEntity]
	 * @return: void
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addOperator(AdminUserEntity adminUserEntity){
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		//生成UUID 和 ID
		String uid = UserUtils.randomUUID();
		adminUserEntity.setId(SnowFlake.nextId());
		adminUserEntity.setUid(uid);
		//t_admin_user用户资料表插入数据
		adminUserEntity.setPassword(new Sha256Hash(RSAUtil.privateDecrypt(adminUserEntity.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), salt).toHex());
		adminUserEntity.setSalt(salt);
		adminUserMapper.addOperator(adminUserEntity);
		// TODO 变为添加角色
//		AdminUserRoleEntity adminUserRoleEntity = new AdminUserRoleEntity();
//		adminUserRoleEntity.setUid(uid);
//		adminUserRoleEntity.setRoleId(adminUserEntity.getRoleId());
//		adminUserRoleEntity.setCreateTime(LocalDateTime.now());
//		adminUserRoleMapper.insert(adminUserRoleEntity);
//		//t_admin_user_menu添加菜单权限
//		adminConfigService.setUserMenus(adminUserEntity.getMenuIdList(), uid);
		//t_admin_user_auth用户登录表插入数据
		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
		BeanUtils.copyProperties(adminUserEntity,adminUserAuthEntity);
		adminUserAuthMapper.createLoginUser(adminUserAuthEntity);
//		//发短信通知，并发送初始密码
//		SmsUtil.sendSmsPassword(adminUserEntity.getMobile(), randomPass);
		//添加社区权限(添加物业下面所有社区)
		if(adminUserEntity.getCompanyId() != null){
			List<String> communityIdList = new ArrayList<>();
			List<CommunityEntity> communityEntities = communityMapper.selectList(new QueryWrapper<CommunityEntity>().eq("property_id", adminUserEntity.getCompanyId()));
			for (CommunityEntity communityEntity : communityEntities) {
				communityIdList.add(communityEntity.getId().toString());
			}
			if (communityIdList.size() > 0) {
				updateAdminCommunityBatch(communityIdList,uid);
			}
		}
	}
	
	/**
	 * @Description: 编辑操作员
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [adminUserEntity]
	 * @return: void
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOperator(AdminUserEntity adminUserEntity){
		//查询uid
		UserEntity user = adminUserMapper.queryUidById(adminUserEntity.getId());
		if(user == null){
			throw new AdminException("用户不存在！");
		}
		//更新密码
		if(!StringUtils.isEmpty(adminUserEntity.getPassword())){
			//生成盐值并对密码加密
			String salt = RandomStringUtils.randomAlphanumeric(20);
			String password = new Sha256Hash(RSAUtil.privateDecrypt(adminUserEntity.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), salt).toHex();
			//更新
			AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
			adminUserAuthEntity.setPassword(password);
			adminUserAuthEntity.setSalt(salt);
			adminUserAuthMapper.update(adminUserAuthEntity, new UpdateWrapper<AdminUserAuthEntity>().eq("mobile",user.getMobile()));
		}
		//修改手机号
		if(!StringUtils.isEmpty(adminUserEntity.getMobile()) && !adminUserEntity.getMobile().equals(user.getMobile())){
			//用户是否已注册
			boolean exists = checkUserExists(adminUserEntity.getMobile());
			if(exists){
				throw new AdminException(JSYError.DUPLICATE_KEY.getCode(),"该手机号已被注册");
			}
			//更换手机号操作
			boolean b = changeMobile(adminUserEntity.getMobile(), user.getMobile());
			if(b){
				//旧手机账号退出登录
				UserUtils.destroyToken("Admin:Login",String.valueOf(redisTemplate.opsForValue().get("Admin:LoginAccount:" + user.getMobile())));
				UserUtils.destroyToken("Admin:LoginAccount",user.getMobile());
			}
		}
		//更新资料
		adminUserMapper.updateOperator(adminUserEntity);
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
		AdminUserEntity adminUserEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("id", id));
		if (adminUserEntity == null) {
			throw new AdminException(JSYError.OPERATOR_INFORMATION_NOT_OBTAINED.getCode(),"未获取到操作员信息");
		}
		int i = adminUserMapper.deleteById(id);
		if (i != 1) {
			throw new AdminException(JSYError.INTERNAL.getCode(),"删除失败");
		}
	}
	
	/**
	 * 管理员社区权限批量修改
	 */
	private void updateAdminCommunityBatch(List<String> communityIds, String uid){
		//清空
		adminCommunityMapper.clearAdminCommunityByUid(uid);
		//去重
		Set<String> communityIdsSet = new HashSet<>(communityIds);
		//添加
		adminCommunityMapper.addAdminCommunityBatch(communityIdsSet,uid);
	}
	
	/**
	 * @Description: 根据手机号检查小区用户是否已存在(t_admin_user)
	 * @author: DKS
	 * @since: 2021/11/19 16:55
	 * @Param: [mobile]
	 * @return: boolean
	 */
	@Override
	public boolean checkUserExists(String mobile){
		return adminUserMapper.countUser(mobile) != null;
	}
	
	/**
	 * @Description: 修改手机号
	 * @author: DKS
	 * @since: 2021/11/19 16:56
	 * @Param: [newMobile, oldMobile]
	 * @return: boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean changeMobile(String newMobile,String oldMobile){
		int result1 = adminUserAuthMapper.changeMobile(newMobile, oldMobile);
		int result2 = adminUserMapper.changeMobile(newMobile, oldMobile);
		return result1 == 1 && result2 > 0;
	}
}