package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.IOrganizationService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConsts;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.admin.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.util.Constant;
import com.jsy.community.util.SimpleMailSender;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.RSAUtil;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
import com.zhsj.base.api.rpc.IBaseUpdateUserRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.PageVO;
import com.zhsj.basecommon.constant.BaseUserConstant;
import com.zhsj.basecommon.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * ????????????
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUserEntity> implements IAdminUserService {
	
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

	@Autowired
	private AdminCommunityMapper adminCommunityMapper;
	
	@Resource
	private AdminUserCompanyMapper adminUserCompanyMapper;
	
	@Resource
	private PropertyCompanyMapper propertyCompanyMapper;
	
	@Autowired
	private AdminRoleCompanyMapper adminRoleCompanyMapper;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseAuthRpcService baseAuthRpcService;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseRoleRpcService baseRoleRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUpdateUserRpcService baseUpdateUserRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService userInfoRpcService;
	
	@Value("${propertyLoginExpireHour}")
	private long loginExpireHour = 12;
	
	/**
	* @Description: ??????????????????
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Override
	public boolean setUserRoles(List<Long> roleIds,Long userId){
		//??????
		List<Long> userRoles = adminUserMapper.getUserRole(userId);
		//??????
		adminUserMapper.clearUserRole(userId);
		//??????
		int rows = adminUserMapper.addUserRoleBatch(roleIds, userId);
		//??????
		if(rows != roleIds.size()){
			log.error("???????????????????????????" + userId,"???????????????" + rows);
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
		//sha256??????
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
		user.setSalt(salt);
		user.setId(SnowFlake.nextId());
		user.setUid(UserUtils.randomUUID());
		this.save(user);
		
		//????????????????????????
//		checkRole(user);
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
		
		//????????????????????????
//		checkRole(user);
		
		//TODO ???????????????????????????
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
	 * ????????????????????????
	 */
	@Deprecated
	private void checkRole(AdminUserEntity user) {
		if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
			return;
		}
		//??????????????????????????????????????????????????????????????????????????????
		if (Constant.SUPER_ADMIN.equals(user.getCreateUserId())) {
			return;
		}
	}
	
	/**
	* @Description: ??????????????????
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
			map.put("reason","??????????????????????????????");
			return map;
		}
		//redis??????????????????
		redisTemplate.opsForValue().set("AdminInvite:" + adminUserEntity.getEmail(),adminUserEntity.getRealName(),emailLinkExpiretime, TimeUnit.HOURS);
		//??????????????????
		simpleMailSender.sendRegisterEmail("mail/invite.html",adminUserEntity);
		map.put("result","true");
		return map;
	}
	
	/**
	* @Description: ????????????????????????
	 * @Param: [sysUserEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	@Override
	public Map<String,String> activation(AdminUserEntity adminUserEntity){
		Map<String, String> map = new HashMap<>();
		map.put("templateName","mail/activation.html");
		//???????????????????????????
		if(checkEmailExists(adminUserEntity.getEmail())){
			map.put("reason","??????????????????????????????");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis???????????????????????????
		String realName = String.valueOf(redisTemplate.opsForValue().get("AdminInvite:" + adminUserEntity.getEmail()));
		if("null".equals(realName)){
			map.put("reason","?????????????????????????????????????????????");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//????????????????????????
		String password = UUID.randomUUID().toString().substring(0, 6);
		map.put("password", password);
		adminUserEntity.setPassword(password);
		adminUserEntity.setRealName(realName);
		//user??????
		try{
			this.saveUser(adminUserEntity);
		}catch (Exception e){
			e.printStackTrace();
			map.put("reason","?????????????????????????????????????????????????????????????????????");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis????????????
		redisTemplate.delete("AdminInvite:" + adminUserEntity.getEmail());
		//???????????????
		AdminUserEntity noticeEntity = new AdminUserEntity();
		noticeEntity.setEmail(adminUserEntity.getEmail());
		noticeEntity.setPassword(password);
		simpleMailSender.sendRegisterEmail("mail/activation.html",noticeEntity);
		return map;
	}
	
	/**
	* @Description: ???????????????????????????
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
	* @Description: ??????????????????????????????
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
	* @Description: ????????????????????????????????????
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
			map.put("reason","?????????????????????@??????");
			return map;
		}
		if(checkUsernameExists(username)){
			map.put("result","false");
			map.put("code", String.valueOf(JSYError.REQUEST_PARAM.getCode()));
			map.put("reason","?????????????????????");
			return map;
		}
		AdminUserEntity adminUserEntity = new AdminUserEntity();
		adminUserEntity.setId(uid);
		adminUserEntity.setUsername(username);
		boolean result = this.updateUser(adminUserEntity);
		if(!result){
			map.put("result","false");
			map.put("code", String.valueOf(JSYError.INTERNAL.getCode()));
			map.put("reason","?????????????????????");
			return map;
		}
		map.put("result","true");
		return map;
	}
	
	//==================================== ??????????????????begin ====================================
	//================ ??????????????????begin =================
	/**
	 * @Description: ???????????????????????????????????????
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
	* @Description: ?????????????????????????????????
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
	* @Description: ??????????????????????????????
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
		AdminUserEntity adminUserEntity = adminUserMapper.selectOne(queryWrapper);
		Long companyId = adminUserMapper.queryCompanyId(adminUserEntity.getUid());
		adminUserEntity.setCompanyId(companyId);
		return adminUserEntity;
	}
	
	/**
	* @Description: ??????uid??????????????????
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
	* @Description: uid???????????????
	 * @Param: [uidList]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	@Override
	public Map<String, RealUserDetail> queryNameByUidBatch(Collection<String> uidList){
		if(CollectionUtils.isEmpty(uidList) || (uidList.size() == 1 && uidList.contains(null))){
			return new HashMap<>();
		}
		List<RealUserDetail> realUserDetails = baseUserInfoRpcService.getRealUserDetails(uidList);
		return realUserDetails.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
	}
	
	/**
	 * @Description: ??????????????????id??????
	 * @Param: [id]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	 **/
	@Override
	public List<String> queryUserMenuIdList(Long id){
		//???????????????UID
		String uid = queryUidById(id);
		if(StringUtils.isEmpty(uid)){
			return null;
		}
		//??????UID??????????????????
		return adminConfigService.queryUserMenuIdList(uid);
	}
	//================ ??????????????????end =================
	
	//============== ?????????????????????begin ===============
	/**
	* @Description: ??????????????????(?????????)???????????????idList
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
	 * @Description: ?????????????????????
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: chq459799974
	 * @Date: 2021/3/16
	 **/
	@Override
	public PageVO<AdminUserEntity> queryOperator(BaseQO<AdminUserQO> baseQO){
		AdminUserQO query = baseQO.getQuery();
		Page<AdminUserEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		
		PageVO<UserDetail> userDetailPageVO = userInfoRpcService.queryUser(query.getMobile(), query.getNickName(), query.getRoleId(),
			baseQO.getPage().intValue(), baseQO.getSize().intValue(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		
		if (CollectionUtils.isEmpty(userDetailPageVO.getData())) {
			return new PageVO<>();
		}

		PageVO<AdminUserEntity> pageVO = new PageVO<>();
		List<String> idList = userDetailPageVO.getData().stream().map(UserDetail::getAccount).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(idList)) {
			return new PageVO<>();
		}
		List<AdminCommunityEntity> adminCommunityEntities = adminCommunityMapper.selectList(new QueryWrapper<AdminCommunityEntity>().select("community_id, uid").in("uid", idList));
		Map<String, List<String>> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(adminCommunityEntities)) {
			map = adminCommunityEntities.stream()
					.collect(Collectors.groupingBy(AdminCommunityEntity::getUid,
							Collectors.mapping(AdminCommunityEntity::getCommunityId, Collectors.toList())));
		}

		// ????????????
		for (UserDetail userDetail : userDetailPageVO.getData()) {
			AdminUserEntity adminUserEntity = new AdminUserEntity();
			// ?????????
			List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
			StringBuilder sb = new StringBuilder();
			for (PermitRole permitRole : permitRoles) {
				if (permitRole.getScope() == BaseUserConstant.Login.DataBasePermitScope.PROPERTY_ADMIN) {
					adminUserEntity.setRoleId(String.valueOf(permitRole.getId()));
				}
				if (permitRole.getScope() == BaseUserConstant.Login.DataBasePermitScope.COMMUNITY_ADMIN) {
					adminUserEntity.setCommunityRoleId(String.valueOf(permitRole.getId()));
				}
				sb.append(permitRole.getName()).append(",");
			}
			adminUserEntity.setCommunityIdList(map.get(String.valueOf(userDetail.getId())));
			adminUserEntity.setId(Long.valueOf(userDetail.getAccount()));
			adminUserEntity.setIdStr(userDetail.getAccount());
			adminUserEntity.setNickName(userDetail.getNickName());
			adminUserEntity.setPermitRoles(permitRoles);
			adminUserEntity.setRoleName(sb.length() > 0 ? sb.deleteCharAt(sb.length()-1).toString() : "");
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
	* @Description: ???????????????
	 * @Param: [adminUserEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@Override
	@LcnTransaction
	public Integer addOperator(AdminUserQO adminUserQO){
//		//??????????????????????????????
//		String salt = RandomStringUtils.randomAlphanumeric(20);
//		//??????UUID ??? ID
//		String uid = UserUtils.randomUUID();
//		adminUserEntity.setId(SnowFlake.nextId());
//		adminUserEntity.setUid(uid);
//		//t_admin_user???????????????????????????
//		adminUserEntity.setPassword(new Sha256Hash(RSAUtil.privateDecrypt(adminUserEntity.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), salt).toHex());
////		adminUserEntity.setPassword(new Sha256Hash(adminUserEntity.getPassword(), salt).toHex());
//		adminUserEntity.setSalt(salt);
//		adminUserMapper.addOperator(adminUserEntity);
//		// TODO ??????????????????
//		AdminUserRoleEntity adminUserRoleEntity = new AdminUserRoleEntity();
//		adminUserRoleEntity.setUid(uid);
//		adminUserRoleEntity.setRoleId(adminUserEntity.getRoleId());
//		adminUserRoleEntity.setCreateTime(LocalDateTime.now());
//		adminUserRoleMapper.insert(adminUserRoleEntity);
////		//t_admin_user_menu??????????????????
////		adminConfigService.setUserMenus(adminUserEntity.getMenuIdList(), uid);
//		//t_admin_user_auth???????????????????????????
//		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
//		BeanUtils.copyProperties(adminUserEntity,adminUserAuthEntity);
//		adminUserAuthMapper.createLoginUser(adminUserAuthEntity);
////		//???????????????????????????????????????
////		SmsUtil.sendSmsPassword(adminUserEntity.getMobile(), randomPass);
// ?????????????????????
		// ???????????????(1)???????????????(2)???
		int result = 1;
		// ????????????
		UserDetail userDetail = null;
		// ??????????????????
		String password = RSAUtil.privateDecrypt(adminUserQO.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY));
		String pattern = "^(?=.*[A-Z0-9])(?=.*[a-z0-9])(?=.*[a-zA-Z])(.{6,12})$";
		if (!password.matches(pattern)) {
			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "????????????????????????6-12?????????,??????????????????????????????????????????????????????!");
		}
		
		try {
			// ????????????
			userDetail = baseAuthRpcService.userPhoneRegister(adminUserQO.getNickName(), adminUserQO.getMobile(), RSAUtil.privateDecrypt(adminUserQO.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)));
		} catch (BaseException e) {
			// ???????????????????????????
			if (e.getErrorEnum().getCode() == 103) {
				userDetail = userInfoRpcService.getUserDetailByPhone(adminUserQO.getMobile());
				result = 2;
			}
		}
		if (userDetail == null) {
			throw new PropertyException("??????????????????");
		}
		
		List<Long> roleIds = new ArrayList<>();
		if (adminUserQO.getCommunityRoleId() != null) {
			if (adminUserQO.getRoleId() == null) {
				throw new PropertyException("??????????????????????????????????????????????????????");
			}
			// TODO:??????????????????????????????????????????????????????
			roleIds.add(adminUserQO.getCommunityRoleId());
		}
		
		if (adminUserQO.getCommunityRoleId() != null) {
			baseAuthRpcService.addLoginTypeScope(userDetail.getId(), BusinessConst.COMMUNITY_ADMIN, false);
		}
		// ??????????????????????????????????????????????????????????????????
		baseAuthRpcService.addLoginTypeScope(userDetail.getId(), BusinessConst.PROPERTY_ADMIN, false);
		roleIds.add(adminUserQO.getRoleId());
		baseRoleRpcService.userJoinRole(roleIds, userDetail.getId(), Long.valueOf(adminUserQO.getUid()));
		
		// ????????????????????????????????????????????????
		AdminUserCompanyEntity entity = adminUserCompanyMapper.selectOne(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", userDetail.getAccount()).eq("company_id", adminUserQO.getCompanyId()));
		// ????????????
		if (entity == null) {
			// ???????????????????????????
			entity = new AdminUserCompanyEntity();
			entity.setId(SnowFlake.nextId());
			entity.setCompanyId(adminUserQO.getCompanyId());
			entity.setUid(userDetail.getAccount());
			adminUserCompanyMapper.insert(entity);
		}
		//??????????????????
		if(!CollectionUtils.isEmpty(adminUserQO.getCommunityIdList())){
			adminConfigService.updateAdminCommunityBatch(adminUserQO.getCommunityIdList(), userDetail.getAccount());
		}
		return result;
	}
	
	/**
	 * @Description: ???????????????
	 * @Param: [adminUserQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOperator(AdminUserQO adminUserQO, Long id){
		UserDetail userDetail = userInfoRpcService.getUserDetail(String.valueOf(adminUserQO.getId()));
		//??????????????????
		if(!CollectionUtils.isEmpty(adminUserQO.getCommunityIdList())){
			adminConfigService.updateAdminCommunityBatch(adminUserQO.getCommunityIdList(), userDetail.getAccount());
			//??????token??????????????????
			String token = String.valueOf(redisTemplate.opsForValue().get("Admin:LoginAccount:" + adminUserQO.getMobile()));
			String tokenValue = String.valueOf(redisTemplate.opsForValue().get("Admin:Login:" + token));
			AdminUserEntity userData = JSONObject.parseObject(tokenValue,AdminUserEntity.class);
			//????????????token???????????????????????????
			if(userData != null){
				userData.setCommunityIdList(adminUserQO.getCommunityIdList());
				redisTemplate.opsForValue().set("Admin:Login:" + token , JSON.toJSONString(userData) , loginExpireHour ,TimeUnit.HOURS);
				redisTemplate.opsForValue().set("Admin:LoginAccount:" + adminUserQO.getMobile() , token , loginExpireHour ,TimeUnit.HOURS);
			}
		}
		
		if (adminUserQO.getCommunityRoleId() != null) {
			if (adminUserQO.getRoleId() == null) {
				throw new PropertyException("??????????????????????????????????????????????????????");
			}
			// ????????????
			List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
			if (!CollectionUtils.isEmpty(permitRoles)) {
				Set<Long> roleIdSet = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
				QueryWrapper<AdminRoleCompanyEntity> adminRoleCompanyEntityQueryWrapper = new QueryWrapper<>();
				adminRoleCompanyEntityQueryWrapper.eq("company_id", adminUserQO.getCompanyId());
				adminRoleCompanyEntityQueryWrapper.in("role_id", roleIdSet);
				adminRoleCompanyEntityQueryWrapper.last("limit 2");
				List<AdminRoleCompanyEntity> adminRoleCompanyEntities = adminRoleCompanyMapper.selectList(adminRoleCompanyEntityQueryWrapper);
				if (!CollectionUtils.isEmpty(adminRoleCompanyEntities)) {
					List<Long> roles = new ArrayList<>();
					for (AdminRoleCompanyEntity adminRoleCompanyEntity : adminRoleCompanyEntities) {
						roles.add(adminRoleCompanyEntity.getRoleId());
					}
					// ????????????
					baseRoleRpcService.roleRemoveToUser(roles, userDetail.getId());
				}
			}
			baseAuthRpcService.addLoginTypeScope(userDetail.getId(), BusinessConst.COMMUNITY_ADMIN, false);
			// ????????????
			List<Long> addRoles = new ArrayList<>();
			addRoles.add(adminUserQO.getRoleId());
			addRoles.add(adminUserQO.getCommunityRoleId());
			baseRoleRpcService.userJoinRole(addRoles, userDetail.getId(), id);
			// ????????????
			baseUpdateUserRpcService.updateUserInfo(userDetail.getId(), adminUserQO.getNickName(),
				adminUserQO.getMobile(), (String) null, BusinessConst.COMMUNITY_ADMIN);
		} else {
			// ??????????????????????????????????????????
			// ????????????
			List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.PROPERTY_ADMIN);
			if (!CollectionUtils.isEmpty(permitRoles)) {
				Set<Long> roleIdSet = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
				QueryWrapper<AdminRoleCompanyEntity> adminRoleCompanyEntityQueryWrapper = new QueryWrapper<>();
				adminRoleCompanyEntityQueryWrapper.eq("company_id", adminUserQO.getCompanyId());
				adminRoleCompanyEntityQueryWrapper.in("role_id", roleIdSet);
				adminRoleCompanyEntityQueryWrapper.last("limit 1");
				AdminRoleCompanyEntity adminRoleCompanyEntity = adminRoleCompanyMapper.selectOne(adminRoleCompanyEntityQueryWrapper);
				if (adminRoleCompanyEntity != null) {
					roleIdSet.add(adminRoleCompanyEntity.getRoleId());
				}
				// ????????????
				baseRoleRpcService.roleRemoveToUser(roleIdSet, userDetail.getId());
			}
			// ????????????
			List<Long> addRoles = new ArrayList<>();
			addRoles.add(adminUserQO.getRoleId());
			baseRoleRpcService.userJoinRole(addRoles, userDetail.getId(), id);
			// ????????????
			baseUpdateUserRpcService.updateUserInfo(userDetail.getId(), adminUserQO.getNickName(),
				adminUserQO.getMobile(), (String) null, BusinessConst.PROPERTY_ADMIN);
		}
	}
	
	/**
	 * @Description: ???????????????
	 * @author: DKS
	 * @since: 2021/10/13 15:38
	 * @Author: DKS
	 * @Date: 2021/10/13
	 */
	public void deleteOperator(Long id) {
		UserDetail userDetail = userInfoRpcService.getUserDetail(String.valueOf(id));
		List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.PROPERTY_ADMIN, BusinessConst.COMMUNITY_ADMIN);
		// ??????????????????????????????
		Set<Long> roleIds = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
		baseRoleRpcService.roleRemoveToUser(roleIds, userDetail.getId());
		// ????????????????????????
		baseAuthRpcService.removeLoginTypeScope(userDetail.getId(), BusinessConst.PROPERTY_ADMIN, false);
		baseAuthRpcService.removeLoginTypeScope(userDetail.getId(), BusinessConst.COMMUNITY_ADMIN, false);
//		baseAuthRpcService.cancellation(id);
		// ????????????????????????????????????????????????????????????
		adminUserCompanyMapper.delete(new QueryWrapper<AdminUserCompanyEntity>().eq("uid", id));
	}
	
//	/**
//	* @Description: ????????????(??????)
//	 * @Param: [id, uid]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2021/3/18
//	**/
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public boolean resetPassword(Long id,String uid){
//		AdminUserEntity adminUser = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().select("mobile").eq("id", id));
//		if(adminUser == null){
//			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"???????????????");
//		}
//		//??????????????????
//		String randomPass = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
//		//TODO ????????????????????????????????????
//		randomPass = "22222222";
//		//??????????????????????????????
//		String salt = RandomStringUtils.randomAlphanumeric(20);
//		String password = new Sha256Hash(randomPass, salt).toHex();
//		//??????
//		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
//		adminUserAuthEntity.setPassword(password);
//		adminUserAuthEntity.setSalt(salt);
//		adminUserAuthEntity.setUpdateBy(uid);
//		int result = adminUserAuthMapper.update(adminUserAuthEntity, new UpdateWrapper<AdminUserAuthEntity>().eq("mobile", adminUser.getMobile()));
//		//??????????????????????????????
//		SmsUtil.resetPassword(adminUser.getMobile(), randomPass);
//		return result == 1;
//	}
	
	/**
	* @Description: ??????id??????uid
	 * @Param: [id]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	**/
	@Override
	public String queryUidById(Long id){
		UserEntity user = adminUserMapper.queryUidById(id);
		if(user == null){
			return null;
		}
		return user.getUid();
	}
	
	/**
	* @Description: ????????????????????????????????????????????????(t_admin_user)
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	@Override
	public boolean checkUserExists(String mobile){
		return adminUserMapper.countUser(mobile) != null;
	}
	
	/**
	* @Description: ??????uid???????????????
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	@Override
	public String queryMobileByUid(String uid){
		return adminUserMapper.queryMobileByUid(uid);
	}
	//============== ?????????????????????end ===============
	
	//============== ??????????????????start ===============
	/**
	* @Description: ??????????????????
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
	* @Description: ??????????????????
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Override
	public AdminUserEntity queryPersonalData(String uid){
		AdminUserEntity user = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().select("mobile,real_name,org_id,job,id_card,role_type,avatar_url").eq("uid",uid));
		user.setRoleTypeName(PropertyConstsEnum.RoleTypeEnum.ROLE_TYPE_MAP.get(user.getRoleType()));
		//????????????????????????
		user.setOrgName(organizationService.queryOrganizationNameById(user.getOrgId()));
		//????????????????????????(by?????????)
		Integer count = adminUserAuthMapper.selectCount(new QueryWrapper<AdminUserAuthEntity>().eq("mobile",user.getMobile()).isNotNull("password"));
		user.setHasPassword(count > 0 ? PropertyConsts.ACCOUNT_PASS_HAD : PropertyConsts.ACCOUNT_PASS_HAD_NOT);
		user.setOrgId(null);
		user.setRoleType(null);
		return user;
	}
	
	/**
	* @Description: ????????????
	 * @Param: [qo, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Override
	public boolean updatePassword(ResetPasswordQO qo,String uid){
		//????????????(?????????)??????????????????????????????????????????uid???????????????
		if(StringUtils.isEmpty(qo.getAccount())){
			String mobile = adminUserMapper.queryMobileByUid(uid);
			if(StringUtils.isEmpty(mobile)){
				throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"???????????????");
			}
			qo.setAccount(mobile);
		}
		//????????????????????????????????????????????????????????????????????????
		AdminUserAuthEntity userAuthEntity = adminUserAuthMapper.selectOne(new QueryWrapper<AdminUserAuthEntity>().select("mobile").eq("mobile", qo.getAccount()));
		if(userAuthEntity == null){
			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"???????????????");
		}
		//??????????????????????????????
		String salt = RandomStringUtils.randomAlphanumeric(20);
		String password = new Sha256Hash(qo.getPassword(), salt).toHex();
		//??????
		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
		adminUserAuthEntity.setPassword(password);
		adminUserAuthEntity.setSalt(salt);
		adminUserAuthEntity.setUpdateBy(uid);
		int result = adminUserAuthMapper.update(adminUserAuthEntity, new UpdateWrapper<AdminUserAuthEntity>().eq("mobile", userAuthEntity.getMobile()));
		return result == 1;
	}
	
	/**
	* @Description: ???????????????
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
	//============== ??????????????????end ===============
	//==================================== ??????????????????end ====================================
	
}