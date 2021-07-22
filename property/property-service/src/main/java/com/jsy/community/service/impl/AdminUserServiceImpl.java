package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConsts;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.admin.AdminUserAuthEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.AdminUserAuthMapper;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.util.Constant;
import com.jsy.community.util.SimpleMailSender;
import com.jsy.community.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 系统用户
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUserEntity> implements IAdminUserService {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	
//	@Value("${email.linkExpiretime}")
	public long emailLinkExpiretime = 24;
	
	@Autowired
	private SimpleMailSender simpleMailSender;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Resource
	private AdminUserMapper adminUserMapper;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IOrganizationService organizationService;
	
	@Autowired
	private IAdminConfigService adminConfigService;
	
	@Autowired
	private AdminUserAuthMapper adminUserAuthMapper;
	
	
	/**
	* @Description: 设置用户角色
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public boolean setUserRoles(List<Long> roleIds,Long userId){
		//备份
		List<Long> userRoles = adminUserMapper.getUserRole(userId);
		//清空
		adminUserMapper.clearUserRole(userId);
		//新增
		int rows = adminUserMapper.addUserRoleBatch(roleIds, userId);
		//还原
		if(rows != roleIds.size()){
			log.error("设置用户角色出错：" + userId,"成功条数：" + rows);
			adminUserMapper.clearUserRole(userId);
			adminUserMapper.addUserRoleBatch(userRoles, userId);
			return false;
		}
		return true;
	}
	
//	@Override
//	public IPage<AdminUserEntity> queryPage(BaseQO<NameAndCreatorQO> qo) {
//		String username = qo.getQuery().getName();
//		Long createUserId = qo.getQuery().getCreateUserId();
//
//		return page(
//			new Query<SysUserEntity>().getPage(qo),
//			new LambdaQueryWrapper<SysUserEntity>()
//				.like(StrUtil.isNotBlank(username), SysUserEntity::getUsername, username)
//				.eq(createUserId != null, SysUserEntity::getCreateUserId, createUserId)
//		);
//	}
	
	@Override
	public List<String> queryAllPerms(Long userId) {
		return baseMapper.queryAllPerms(userId);
	}
	
	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return baseMapper.queryAllMenuId(userId);
	}
	
//	@Override
//	public AdminUserEntity queryByUserName(String username) {
//		return baseMapper.queryByUserName(username);
//	}
	
//	@Override
//	public AdminUserEntity queryByEmail(String email) {
//		return baseMapper.queryByEmail(email);
//	}
	
	@Override
	@Transactional
	@Deprecated
	public void saveUser(AdminUserEntity user) {
		user.setCreateTime(LocalDateTime.now());
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
		user.setSalt(salt);
		user.setId(SnowFlake.nextId());
		user.setUid(UserUtils.randomUUID());
		this.save(user);
		
		//检查角色是否越权
//		checkRole(user);
		
		// TODO 保存用户与角色关系
	}
	
	@Override
	@Deprecated
//	@Transactional
	public boolean updateUser(AdminUserEntity user) {
		if (StrUtil.isBlank(user.getPassword())) {
			user.setPassword(null);
		} else {
			user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
		}
		boolean result = this.updateById(user);
		
		//检查角色是否越权
//		checkRole(user);
		
		//TODO 保存用户与角色关系
		return result;
	}
	
	@Deprecated
	@Override
	public void deleteBatch(Long[] userId) {
		this.removeByIds(Arrays.asList(userId));
	}
	
	@Deprecated
	@Override
	public boolean updatePassword(Long userId, String password, String newPassword) {
		AdminUserEntity userEntity = new AdminUserEntity();
		userEntity.setPassword(newPassword);
		return this.update(userEntity,
			new QueryWrapper<AdminUserEntity>().eq("user_id", userId).eq("password", password));
	}
	
	/**
	 * 检查角色是否越权
	 */
	@Deprecated
	private void checkRole(AdminUserEntity user) {
		if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
			return;
		}
		//如果不是超级管理员，则需要判断用户的角色是否自己创建
		if (Constant.SUPER_ADMIN.equals(user.getCreateUserId())) {
			return;
		}
	}
	
	/**
	* @Description: 邮件注册邀请
	 * @Param: [sysUserEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	@Override
	public Map<String,String> invitation(AdminUserEntity adminUserEntity){
		Map<String, String> map = new HashMap<>();
		if(checkEmailExists(adminUserEntity.getEmail())){
			map.put("result","false");
			map.put("reason","用户已注册，无需邀请");
			return map;
		}
		//redis暂存邮件邀请
		redisTemplate.opsForValue().set("AdminInvite:" + adminUserEntity.getEmail(),adminUserEntity.getRealName(),emailLinkExpiretime, TimeUnit.HOURS);
		//发送邀请邮件
		simpleMailSender.sendRegisterEmail("mail/invite.html",adminUserEntity);
		map.put("result","true");
		return map;
	}
	
	/**
	* @Description: 邮件注册激活确认
	 * @Param: [sysUserEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	@Override
	public Map<String,String> activation(AdminUserEntity adminUserEntity){
		Map<String, String> map = new HashMap<>();
		map.put("templateName","mail/activation.html");
		//检测邮箱是否已注册
		if(checkEmailExists(adminUserEntity.getEmail())){
			map.put("reason","您已注册，请直接登录");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis取出暂存的邮件邀请
		String realName = String.valueOf(redisTemplate.opsForValue().get("AdminInvite:" + adminUserEntity.getEmail()));
		if("null".equals(realName)){
			map.put("reason","邀请过期，请联系邀请者重新邀请");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//生成随机初始密码
		String password = UUID.randomUUID().toString().substring(0, 6);
		map.put("password", password);
		adminUserEntity.setPassword(password);
		adminUserEntity.setRealName(realName);
		//user存库
		try{
			this.saveUser(adminUserEntity);
		}catch (Exception e){
			e.printStackTrace();
			map.put("reason","账户激活失败，请联系邀请者重新邀请或联系管理员");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis销毁邀请
		redisTemplate.delete("AdminInvite:" + adminUserEntity.getEmail());
		//发邮件通知
		AdminUserEntity noticeEntity = new AdminUserEntity();
		noticeEntity.setEmail(adminUserEntity.getEmail());
		noticeEntity.setPassword(password);
		simpleMailSender.sendRegisterEmail("mail/activation.html",noticeEntity);
		return map;
	}
	
	/**
	* @Description: 检测邮箱是否已注册
	 * @Param: [email]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	@Deprecated
	private boolean checkEmailExists(String email){
		Integer count = baseMapper.selectCount(new QueryWrapper<AdminUserEntity>().eq("email", email));
		if(count > 0){
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 检测用户名是否已存在
	 * @Param: [username]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	@Override
	@Deprecated
	public boolean checkUsernameExists(String username){
		Integer count = baseMapper.selectCount(new QueryWrapper<AdminUserEntity>().eq("username", username));
		if(count > 0){
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 邮件邀请注册后设置用户名
	 * @Param: [uid, username]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	@Override
	public Map<String,String> setUserName(Long uid,String username){
		Map<String, String> map = new HashMap<>();
		if(username.contains("@")){
			map.put("result","false");
			map.put("code", String.valueOf(JSYError.REQUEST_PARAM.getCode()));
			map.put("reason","用户名不能带有@符号");
			return map;
		}
		if(checkUsernameExists(username)){
			map.put("result","false");
			map.put("code", String.valueOf(JSYError.REQUEST_PARAM.getCode()));
			map.put("reason","用户名已被占用");
			return map;
		}
		AdminUserEntity adminUserEntity = new AdminUserEntity();
		adminUserEntity.setId(uid);
		adminUserEntity.setUsername(username);
		boolean result = this.updateUser(adminUserEntity);
		if(!result){
			map.put("result","false");
			map.put("code", String.valueOf(JSYError.INTERNAL.getCode()));
			map.put("reason","设置用户名失败");
			return map;
		}
		map.put("result","true");
		return map;
	}
	
	//==================================== 物业端（新）begin ====================================
	//================ 用户登录相关begin =================
	/**
	 * @Description: 根据手机号查询用户是否存在
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	 **/
	@Override
	public boolean isExistsByMobile(String mobile){
		return adminUserAuthMapper.selectCount(new QueryWrapper<AdminUserAuthEntity>().eq("mobile",mobile)) == 1;
	}
	
	/**
	* @Description: 根据手机号查询登录用户
	 * @Param: [mobile]
	 * @Return: com.jsy.community.entity.admin.AdminUserAuthEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Override
	public AdminUserAuthEntity queryLoginUserByMobile(String mobile) {
		return adminUserAuthMapper.selectOne(new QueryWrapper<AdminUserAuthEntity>().select("*").eq("mobile",mobile));
	}
	
	/**
	* @Description: 查询用户小区账户资料
	 * @Param: [mobile, communityId]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Override
	public AdminUserEntity queryUserByMobile(String mobile,Long communityId){
		QueryWrapper queryWrapper = new QueryWrapper<AdminUserEntity>();
		queryWrapper.select("*");
		queryWrapper.eq("mobile",mobile);
		if(communityId != null){
			queryWrapper.eq("community_id",communityId);
		}
		return adminUserMapper.selectOne(queryWrapper);
	}
	
	/**
	* @Description: 根据uid查询用户信息
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@Override
	public AdminUserEntity queryByUid(String uid){
		return adminUserMapper.queryByUid(uid);
	}
	
	/**
	* @Description: uid批量查姓名
	 * @Param: [uidList]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@Override
	public Map<String,Map<String,String>> queryNameByUidBatch(Collection<String> uidList){
		if(CollectionUtils.isEmpty(uidList) || (uidList.size() == 1 && uidList.contains(null))){
			return new HashMap<>();
		}
		return adminUserMapper.queryNameByUidBatch(uidList);
	}
	
	/**
	 * @Description: 查询用户菜单id列表
	 * @Param: [id]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	 **/
	@Override
	public List<String> queryUserMenuIdList(Long id){
		//查询操作员UID
		String uid = queryUidById(id);
		//返回UID对应菜单列表
		return adminConfigService.queryUserMenuIdList(uid);
	}
	//================ 用户登录相关end =================
	
	//============== 操作员管理相关begin ===============
	/**
	* @Description: 查询登录用户(操作员)已加入小区idList
	 * @Param: [mobile]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Override
	public List<Long> queryCommunityIdList(String mobile){
		return adminUserMapper.queryCommunityIdListByMobile(mobile);
	}
	
	/**
	* @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: chq459799974
	 * @Date: 2021/3/16
	**/
	@Override
	public PageInfo queryOperator(BaseQO<AdminUserQO> baseQO){
		AdminUserQO query = baseQO.getQuery();
		Page<AdminUserEntity> page = new Page();
		MyPageUtils.setPageAndSize(page,baseQO);
//		QueryWrapper<AdminUserEntity> queryWrapper = new QueryWrapper<AdminUserEntity>().select("id,number,real_name,mobile,id_card,status,role_type,org_id,job,create_by,create_time,update_by,update_time");
		QueryWrapper<AdminUserEntity> queryWrapper = new QueryWrapper<AdminUserEntity>().select("id,real_name,mobile,create_time");
//		queryWrapper.eq("community_id",query.getCommunityId());
		//是否查详情
		Integer menuCount = null;
		if(query.getId() != null){
			queryWrapper.eq("id",query.getId());
			//TODO 换成查角色 回显到详情
//			String uid = adminUserMapper.queryUidById(query.getId());
//			menuCount = adminConfigService.countUserMenu(uid);
		}
		
		if(!StringUtils.isEmpty(query.getName())){
			queryWrapper.and(wrapper -> wrapper
//				.like("number",query.getName())
//				.or().like("real_name",query.getName())
				.like("real_name",query.getName())
				.or().like("mobile",query.getName())
//				.or().like("id_card",query.getName())
			);
		}
//		if(query.getStatus() != null){
//			queryWrapper.eq("status",query.getStatus());
//		}
//		queryWrapper.orderByAsc("role_type","status");
		queryWrapper.orderByDesc("create_time");
		Page<AdminUserEntity> pageData = adminUserMapper.selectPage(page,queryWrapper);
		if(CollectionUtils.isEmpty(pageData.getRecords())){
			return new PageInfo<>();
		}
//		//补创建人和更新人姓名
//		Set<String> createUidSet = new HashSet<>();
//		Set<String> updateUidSet = new HashSet<>();
//		//补组织机构名称
//		Set<Long> orgIdSet = new HashSet<>();
//		for(AdminUserEntity entity : pageData.getRecords()){
//			orgIdSet.add(entity.getOrgId());
//			createUidSet.add(entity.getCreateBy());
//			updateUidSet.add(entity.getUpdateBy());
//		}
//		Map<Long, Map<Long, Object>> orgMap = organizationService.queryOrganizationNameByIdBatch(orgIdSet);
//		Map<String, Map<String,String>> createUserMap = queryNameByUidBatch(createUidSet);
//		Map<String, Map<String,String>> updateUserMap = queryNameByUidBatch(updateUidSet);
//		for(AdminUserEntity entity : pageData.getRecords()){
//			if (entity.getOrgId() != null) {
//				// 允许组织机构为空
//				entity.setOrgName(String.valueOf(orgMap.get(BigInteger.valueOf(entity.getOrgId())).get("name")));
//			}
//			entity.setCreateBy(createUserMap.get(entity.getCreateBy()) == null ? null : createUserMap.get(entity.getCreateBy()).get("name"));
//			entity.setUpdateBy(updateUserMap.get(entity.getUpdateBy()) == null ? null : updateUserMap.get(entity.getUpdateBy()).get("name"));
//		}
//		//查详情 已授权菜单数统计
//		if(query.getId() != null){
//			pageData.getRecords().get(0).setMenuCount(menuCount);
//		}
		PageInfo<AdminUserEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
	/**
	* @Description: 添加操作员
	 * @Param: [adminUserEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean addOperator(AdminUserEntity adminUserEntity){
//		//查询组织机构是否存在
//		if(!organizationService.isExists(adminUserEntity.getOrgId(),adminUserEntity.getCommunityId())){
//			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"组织机构不存在！");
//		}
//		//生成随机密码
//		String randomPass = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
//		//TODO 测试阶段暂时生成固定密码
//		randomPass = "11111111";
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		//生成UUID 和 ID
		String uid = UserUtils.randomUUID();
		adminUserEntity.setId(SnowFlake.nextId());
		adminUserEntity.setUid(uid);
		//t_admin_user用户资料表插入数据
//		adminUserEntity.setStatus(adminUserEntity.getStatus() != null ? adminUserEntity.getStatus() : 0);
//		adminUserEntity.setPassword(new Sha256Hash(randomPass, salt).toHex());
		adminUserEntity.setPassword(new Sha256Hash(adminUserEntity.getPassword(), salt).toHex());
		adminUserEntity.setSalt(salt);
		int result = adminUserMapper.addOperator(adminUserEntity);
		// TODO 变为添加角色
//		//t_admin_user_menu添加菜单权限
//		adminConfigService.setUserMenus(adminUserEntity.getMenuIdList(), uid);
		//t_admin_user_auth用户登录表插入数据
		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
		BeanUtils.copyProperties(adminUserEntity,adminUserAuthEntity);
		adminUserAuthMapper.createLoginUser(adminUserAuthEntity);
//		//发短信通知，并发送初始密码
//		SmsUtil.sendSmsPassword(adminUserEntity.getMobile(), randomPass);
		return result == 1;
	}
	
	/**
	* @Description: 编辑操作员
	 * @Param: [adminUserEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@Override
	public boolean updateOperator(AdminUserEntity adminUserEntity){
		if(adminUserEntity.getOrgId() != null){
			//查询组织机构是否存在
			if(!organizationService.isExists(adminUserEntity.getOrgId(),adminUserEntity.getCommunityId())){
				throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"组织机构不存在！");
			}
		}
		//查询uid
		String uid = adminUserMapper.queryUidById(adminUserEntity.getId());
		if(StringUtils.isEmpty(uid)){
			throw new PropertyException("用户不存在！");
		}
		//更新菜单权限
		adminConfigService.setUserMenus(adminUserEntity.getMenuIdList(), uid);
		return adminUserMapper.updateOperator(adminUserEntity) == 1;
	}
	
	/**
	* @Description: 重置密码(随机)
	 * @Param: [id, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/18
	**/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean resetPassword(Long id,String uid){
		AdminUserEntity adminUser = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().select("mobile").eq("id", id));
		if(adminUser == null){
			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"用户不存在");
		}
		//生成随机密码
		String randomPass = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
		//TODO 测试阶段暂时生成固定密码
		randomPass = "22222222";
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		String password = new Sha256Hash(randomPass, salt).toHex();
		//更新
		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
		adminUserAuthEntity.setPassword(password);
		adminUserAuthEntity.setSalt(salt);
		adminUserAuthEntity.setUpdateBy(uid);
		int result = adminUserAuthMapper.update(adminUserAuthEntity, new UpdateWrapper<AdminUserAuthEntity>().eq("mobile", adminUser.getMobile()));
		//发短信通知新初始密码
		SmsUtil.resetPassword(adminUser.getMobile(), randomPass);
		return result == 1;
	}
	
	/**
	* @Description: 根据id查询uid
	 * @Param: [id]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	**/
	@Override
	public String queryUidById(Long id){
		return adminUserMapper.queryUidById(id);
	}
	
	/**
	* @Description: 根据手机号检查小区用户是否已存在(t_admin_user)
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	@Override
	public boolean checkUserExists(String mobile){
		return adminUserMapper.selectCount(new QueryWrapper<AdminUserEntity>().eq("mobile",mobile)) == 1;
	}
	
	/**
	* @Description: 根据uid查询手机号
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	@Override
	public String queryMobileByUid(String uid){
		return adminUserMapper.queryMobileByUid(uid);
	}
	//============== 操作员管理相关end ===============
	
	//============== 个人中心相关start ===============
	/**
	* @Description: 更新用户头像
	 * @Param: [url, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Override
	public boolean updateAvatar(String url,String uid){
		return adminUserMapper.updateAvatar(url,uid) == 1;
	}
	
	/**
	* @Description: 个人信息查询
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Override
	public AdminUserEntity queryPersonalData(String uid){
		AdminUserEntity user = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().select("mobile,real_name,org_id,job,id_card,role_type,avatar_url").eq("uid",uid));
		user.setRoleTypeName(PropertyConstsEnum.RoleTypeEnum.ROLE_TYPE_MAP.get(user.getRoleType()));
		//查询组织机构名称
		user.setOrgName(organizationService.queryOrganizationNameById(user.getOrgId()));
		//查询是否设置密码(by手机号)
		Integer count = adminUserAuthMapper.selectCount(new QueryWrapper<AdminUserAuthEntity>().eq("mobile",user.getMobile()).isNotNull("password"));
		user.setHasPassword(count > 0 ? PropertyConsts.ACCOUNT_PASS_HAD : PropertyConsts.ACCOUNT_PASS_HAD_NOT);
		user.setOrgId(null);
		user.setRoleType(null);
		return user;
	}
	
	/**
	* @Description: 修改密码
	 * @Param: [qo, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Override
	public boolean updatePassword(ResetPasswordQO qo,String uid){
		//没传账号(手机号)，属于在线修改密码操作，根据uid查出手机号
		if(StringUtils.isEmpty(qo.getAccount())){
			String mobile = adminUserMapper.queryMobileByUid(uid);
			if(StringUtils.isEmpty(mobile)){
				throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"账号不存在");
			}
			qo.setAccount(mobile);
		}
		//目前只有手机号账户，查询用户登录账户固定用手机号
		AdminUserAuthEntity userAuthEntity = adminUserAuthMapper.selectOne(new QueryWrapper<AdminUserAuthEntity>().select("mobile").eq("mobile", qo.getAccount()));
		if(userAuthEntity == null){
			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"账号不存在");
		}
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		String password = new Sha256Hash(qo.getPassword(), salt).toHex();
		//更新
		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
		adminUserAuthEntity.setPassword(password);
		adminUserAuthEntity.setSalt(salt);
		adminUserAuthEntity.setUpdateBy(uid);
		int result = adminUserAuthMapper.update(adminUserAuthEntity, new UpdateWrapper<AdminUserAuthEntity>().eq("mobile", userAuthEntity.getMobile()));
		return result == 1;
	}
	
	/**
	* @Description: 修改手机号
	 * @Param: [newMobile, oldMobile]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean changeMobile(String newMobile,String oldMobile){
		int result1 = adminUserAuthMapper.changeMobile(newMobile, oldMobile);
		int result2 = adminUserMapper.changeMobile(newMobile, oldMobile);
		if(result1 == 1 && result2 > 0){
			return true;
		}
		return false;
	}
	//============== 个人中心相关end ===============
	//==================================== 物业端（新）end ====================================
	
}