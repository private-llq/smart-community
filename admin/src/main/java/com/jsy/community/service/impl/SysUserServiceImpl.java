package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.sys.SysUserAuthEntity;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.SysRoleMapper;
import com.jsy.community.mapper.SysUserAuthMapper;
import com.jsy.community.mapper.SysUserMapper;
import com.jsy.community.mapper.SysUserRoleMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.qo.sys.NameAndCreatorQO;
import com.jsy.community.qo.sys.SysUserQO;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.utils.*;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * ????????????
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserEntity> implements ISysUserService {
	
	@Value("${email.linkExpiretime}")
	public long emailLinkExpiretime;
	
	@Autowired
	private SimpleMailSender simpleMailSender;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Resource
	private SysUserMapper sysUserMapper;
	
	@Resource
	private SysUserAuthMapper sysUserAuthMapper;
	
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	
	@Resource
	private SysRoleMapper sysRoleMapper;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseAuthRpcService baseAuthRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseRoleRpcService baseRoleRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUpdateUserRpcService baseUpdateUserRpcService;
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService userInfoRpcService;
	
	/**
	* @Description: ??????????????????
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	public boolean setUserRoles(List<Long> roleIds,Long userId){
		//??????
		List<Long> userRoles = sysUserMapper.getUserRole(userId);
		//??????
		sysUserMapper.clearUserRole(userId);
		//??????
		int rows = sysUserMapper.addUserRoleBatch(roleIds, userId);
		//??????
		if(rows != roleIds.size()){
			log.error("???????????????????????????" + userId,"???????????????" + rows);
			sysUserMapper.clearUserRole(userId);
			sysUserMapper.addUserRoleBatch(userRoles, userId);
			return false;
		}
		return true;
	}
	
	@Override
	public IPage<SysUserEntity> queryPage(BaseQO<NameAndCreatorQO> qo) {
		String username = qo.getQuery().getName();
		Long createUserId = qo.getQuery().getCreateUserId();
		
		return page(
			new Query<SysUserEntity>().getPage(qo),
			new LambdaQueryWrapper<SysUserEntity>()
				.like(StrUtil.isNotBlank(username), SysUserEntity::getUsername, username)
				.eq(createUserId != null, SysUserEntity::getCreateUserId, createUserId)
		);
	}
	
	@Override
	public List<String> queryAllPerms(Long userId) {
		return baseMapper.queryAllPerms(userId);
	}
	
	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return baseMapper.queryAllMenuId(userId);
	}
	
	@Override
	public SysUserEntity queryByUserName(String username) {
		return baseMapper.queryByUserName(username);
	}
	
	@Override
	public SysUserEntity queryByEmail(String email) {
		return baseMapper.queryByEmail(email);
	}
	
	@Override
	@Transactional
	public void saveUser(SysUserEntity user) {
		user.setCreateTime(LocalDateTime.now());
		//sha256??????
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
		user.setSalt(salt);
		this.save(user);
		
		//????????????????????????
//		checkRole(user);
		
		// TODO ???????????????????????????
	}
	
	@Override
//	@Transactional
	public boolean updateUser(SysUserEntity user) {
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
	
	@Override
	public void deleteBatch(Long[] userId) {
		this.removeByIds(Arrays.asList(userId));
	}
	
//	@Override
//	public boolean updatePassword(Long userId, String password, String newPassword) {
//		SysUserEntity userEntity = new SysUserEntity();
//		userEntity.setPassword(newPassword);
//		return this.update(userEntity,
//			new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
//	}
	
	/**
	 * ????????????????????????
	 */
	private void checkRole(SysUserEntity user) {
		if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
			return;
		}
		//??????????????????????????????????????????????????????????????????????????????
		if (user.getCreateUserId() == Constant.SUPER_ADMIN) {
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
	public Map<String,String> invitation(SysUserEntity sysUserEntity){
		Map<String, String> map = new HashMap<>();
		if(checkEmailExists(sysUserEntity.getEmail())){
			map.put("result","false");
			map.put("reason","??????????????????????????????");
			return map;
		}
		//TODO token??????uid????????????????????????invitor
//		Long userId = UserUtils.getUserId();
		Long userId = 1L;
		sysUserEntity.setId(userId);
		String invitor = "?????????";
		//redis??????????????????
		redisTemplate.opsForValue().set("SysInvite:" + sysUserEntity.getEmail(),sysUserEntity.getRealName(),emailLinkExpiretime, TimeUnit.HOURS);
		//??????????????????
		simpleMailSender.sendRegisterEmail("mail/invite.html",sysUserEntity,invitor);
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
	public Map<String,String> activation(SysUserEntity sysUserEntity){
		Map<String, String> map = new HashMap<>();
		map.put("templateName","mail/activation.html");
		//???????????????????????????
		if(checkEmailExists(sysUserEntity.getEmail())){
			map.put("reason","??????????????????????????????");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis???????????????????????????
		String realName = String.valueOf(redisTemplate.opsForValue().get("SysInvite:" + sysUserEntity.getEmail()));
		if("null".equals(realName)){
			map.put("reason","?????????????????????????????????????????????");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//????????????????????????
		String password = UUID.randomUUID().toString().substring(0, 6);
		map.put("password", password);
		sysUserEntity.setPassword(password);
		sysUserEntity.setRealName(realName);
		//user??????
		try{
			this.saveUser(sysUserEntity);
		}catch (Exception e){
			e.printStackTrace();
			map.put("reason","?????????????????????????????????????????????????????????????????????");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis????????????
		redisTemplate.delete("SysInvite:" + sysUserEntity.getEmail());
		//???????????????
		SysUserEntity noticeEntity = new SysUserEntity();
		noticeEntity.setEmail(sysUserEntity.getEmail());
		noticeEntity.setPassword(password);
		simpleMailSender.sendRegisterEmail("mail/activation.html",noticeEntity,null);
		return map;
	}
	
	/**
	* @Description: ???????????????????????????
	 * @Param: [email]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	private boolean checkEmailExists(String email){
		Integer count = baseMapper.selectCount(new QueryWrapper<SysUserEntity>().eq("email", email));
        return count > 0;
    }
	
	/**
	* @Description: ??????????????????????????????
	 * @Param: [username]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	private boolean checkUsernameExists(String username){
		Integer count = baseMapper.selectCount(new QueryWrapper<SysUserEntity>().eq("username", username));
        return count > 0;
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
		SysUserEntity sysUserEntity = new SysUserEntity();
		sysUserEntity.setId(uid);
		sysUserEntity.setUsername(username);
		boolean result = this.updateUser(sysUserEntity);
		if(!result){
			map.put("result","false");
			map.put("code", String.valueOf(JSYError.INTERNAL.getCode()));
			map.put("reason","?????????????????????");
			return map;
		}
		map.put("result","true");
		return map;
	}
	
	/**
	 * @Description: ???????????????????????????????????????
	 * @author: DKS
	 * @since: 2021/10/12 16:16
	 * @Param: mobile
	 * @return:
	 */
	@Override
	public boolean isExistsByMobile(String mobile){
		return sysUserAuthMapper.selectCount(new QueryWrapper<SysUserAuthEntity>().eq("mobile",mobile)) == 1;
	}
	
	/**
	 * @Description: ?????????????????????????????????
	 * @Param: [mobile]
	 * @Return: com.jsy.community.entity.sys.SysUserAuthEntity
	 * @Author: DKS
	 * @Date: 2021/10/12 16:37
	 **/
	@Override
	public SysUserAuthEntity queryLoginUserByMobile(String mobile) {
		return sysUserAuthMapper.selectOne(new QueryWrapper<SysUserAuthEntity>().select("*").eq("mobile",mobile));
	}
	
	/**
	 * @Description: ??????????????????????????????
	 * @Param: [mobile, communityId]
	 * @Return: com.jsy.community.entity.sys.SysUserEntity
	 * @Author: DKS
	 * @Date: 2021/10/12 16:45
	 **/
	@Override
	public SysUserEntity queryUserByMobile(String mobile, Long communityId){
		QueryWrapper queryWrapper = new QueryWrapper<SysUserEntity>();
		queryWrapper.select("*");
		queryWrapper.eq("mobile",mobile);
		if(communityId != null){
			queryWrapper.eq("community_id",communityId);
		}
		return sysUserMapper.selectOne(queryWrapper);
	}
	
	/**
	 * @Description: ??????uid??????????????????
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.sys.SysUserEntity
	 * @Author: DKS
	 * @Date: 2021/11/30
	 **/
	@Override
	public SysUserEntity queryByUid(String id){
		return sysUserMapper.queryById(id);
	}
	
	/**
	 * @Description: ?????????????????????
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public PageVO<SysUserEntity> queryOperator(BaseQO<SysUserQO> baseQO){
		SysUserQO query = baseQO.getQuery();
		Page<SysUserEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page, baseQO);
		
		PageVO<UserDetail> userDetailPageVO = userInfoRpcService.queryUser(query.getPhone(), query.getNickName(), BusinessConst.ULTIMATE_ADMIN, query.getRoleId(), baseQO.getPage().intValue(), baseQO.getSize().intValue());
		
		if (CollectionUtils.isEmpty(userDetailPageVO.getData())) {
			return new PageVO<>();
		}
		PageVO<SysUserEntity> pageVO = new PageVO<>();
		// ????????????
		for (UserDetail userDetail : userDetailPageVO.getData()) {
			SysUserEntity sysUserEntity = new SysUserEntity();
			List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.ULTIMATE_ADMIN);
			sysUserEntity.setId(Long.valueOf(userDetail.getAccount()));
			sysUserEntity.setIdStr(userDetail.getAccount());
			sysUserEntity.setNickname(userDetail.getNickName());
			if (!CollectionUtils.isEmpty(permitRoles)) {
				sysUserEntity.setRoleId(permitRoles.get(0).getId());
				sysUserEntity.setRoleIdStr(String.valueOf(permitRoles.get(0).getId()));
				sysUserEntity.setRoleName(permitRoles.get(0).getName());
			}
			sysUserEntity.setMobile(userDetail.getPhone());
			sysUserEntity.setCreateTime(LocalDateTime.parse(userDetail.getUtcCreate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			pageVO.getData().add(sysUserEntity);
		}
		pageVO.setPageNum(userDetailPageVO.getPageNum());
		pageVO.setPageSize(userDetailPageVO.getPageSize());
		pageVO.setPages(userDetailPageVO.getPages());
		pageVO.setTotal(userDetailPageVO.getTotal());
		return pageVO;
	}
	
	/**
	 * @Description: ???????????????
	 * @Param: [sysUserQO]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public Integer addOperator(SysUserQO sysUserQO){
		// ???????????????(1)???????????????(2)???
		int result = 1;
		// ????????????
		UserDetail userDetail = null;
		// ??????????????????
		String password = RSAUtil.privateDecrypt(sysUserQO.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY));
		String pattern = "^(?=.*[A-Z0-9])(?=.*[a-z0-9])(?=.*[a-zA-Z])(.{6,12})$";
		if (!password.matches(pattern)) {
			throw new AdminException(JSYError.REQUEST_PARAM.getCode(), "????????????????????????6-12?????????,??????????????????????????????????????????????????????!");
		}
		
		try {
			userDetail = baseAuthRpcService.userPhoneRegister(sysUserQO.getNickName(), sysUserQO.getPhone(), RSAUtil.privateDecrypt(sysUserQO.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)));
		} catch (BaseException e) {
			// ???????????????????????????
			if (e.getErrorEnum().getCode() == 103) {
				userDetail = userInfoRpcService.getUserDetailByPhone(sysUserQO.getPhone());
				result = 2;
			}
		}
		if (userDetail == null) {
			throw new AdminException("??????????????????");
		}
		// ??????????????????????????????????????????
		baseAuthRpcService.addLoginTypeScope(userDetail.getId(), BusinessConst.ULTIMATE_ADMIN, false);
		// ?????????????????????????????????????????????????????????
//		List<Long> roleId = new ArrayList<>();
//		roleId.add(1463327674104250369L);
//		baseRoleRpcService.roleRemoveToUser(roleId, userDetail.getUserId());
		List<Long> roleIds = new ArrayList<>();
		roleIds.add(sysUserQO.getRoleId());
		baseRoleRpcService.userJoinRole(roleIds, userDetail.getId(), sysUserQO.getId());
		return result;
	}
	
	/**
	 * @Description: ???????????????
	 * @Param: [sysUserQO]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public void updateOperator(SysUserQO sysUserQO){
		UserDetail userDetail = userInfoRpcService.getUserDetail(String.valueOf(sysUserQO.getId()));
		// ????????????
		List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.ULTIMATE_ADMIN);
		if (!CollectionUtils.isEmpty(permitRoles)) {
			Set<Long> roleIds = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
			// ????????????
			baseRoleRpcService.roleRemoveToUser(roleIds, userDetail.getId());
		}
		// ????????????
		List<Long> addRoles = new ArrayList<>();
		addRoles.add(sysUserQO.getRoleId());
		baseRoleRpcService.userJoinRole(addRoles, userDetail.getId(), sysUserQO.getUpdateUid());
		// ????????????
		baseUpdateUserRpcService.updateUserInfo(userDetail.getId(), sysUserQO.getNickName(),
			sysUserQO.getPhone(), "", BusinessConst.ULTIMATE_ADMIN);
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
		List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.ULTIMATE_ADMIN);
		// ??????????????????????????????
		Set<Long> roleIds = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
		baseRoleRpcService.roleRemoveToUser(roleIds, userDetail.getId());
		// ????????????????????????
		baseAuthRpcService.removeLoginTypeScope(userDetail.getId(), BusinessConst.ULTIMATE_ADMIN, false);
//		baseAuthRpcService.cancellation(id);
	}
	
	/**
	 * @Description: ????????????
	 * @Param: [qo, uid]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public boolean updatePassword(ResetPasswordQO qo, String uid){
		//????????????(?????????)??????????????????????????????????????????uid???????????????
		if(StringUtils.isEmpty(qo.getAccount())){
			String mobile = sysUserMapper.queryMobileByUid(uid);
			if(StringUtils.isEmpty(mobile)){
				throw new AdminException(JSYError.REQUEST_PARAM.getCode(),"???????????????");
			}
			qo.setAccount(mobile);
		}
		//????????????????????????????????????????????????????????????????????????
		SysUserAuthEntity userAuthEntity = sysUserAuthMapper.selectOne(new QueryWrapper<SysUserAuthEntity>().select("mobile").eq("mobile", qo.getAccount()));
		if(userAuthEntity == null){
			throw new AdminException(JSYError.REQUEST_PARAM.getCode(),"???????????????");
		}
		//??????????????????????????????
		String salt = RandomStringUtils.randomAlphanumeric(20);
		String password = new Sha256Hash(qo.getPassword(), salt).toHex();
		//??????
		SysUserAuthEntity sysUserAuthEntity = new SysUserAuthEntity();
		sysUserAuthEntity.setPassword(password);
		sysUserAuthEntity.setSalt(salt);
		sysUserAuthEntity.setUpdateBy(uid);
		int result = sysUserAuthMapper.update(sysUserAuthEntity, new UpdateWrapper<SysUserAuthEntity>().eq("mobile", userAuthEntity.getMobile()));
		return result == 1;
	}
	
	/**
	 * @Description: ???????????????
	 * @Param: [newMobile, oldMobile]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean changeMobile(String newMobile,String oldMobile){
		int result1 = sysUserAuthMapper.changeMobile(newMobile, oldMobile);
		int result2 = sysUserMapper.changeMobile(newMobile, oldMobile);
		if(result1 == 1 && result2 > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public String getSysRealName(String userId) {
		return userInfoRpcService.getUserDetail(userId).getNickName();
	}
}