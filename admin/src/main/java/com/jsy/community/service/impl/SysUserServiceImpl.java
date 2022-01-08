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
 * 系统用户
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
	* @Description: 设置用户角色
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	public boolean setUserRoles(List<Long> roleIds,Long userId){
		//备份
		List<Long> userRoles = sysUserMapper.getUserRole(userId);
		//清空
		sysUserMapper.clearUserRole(userId);
		//新增
		int rows = sysUserMapper.addUserRoleBatch(roleIds, userId);
		//还原
		if(rows != roleIds.size()){
			log.error("设置用户角色出错：" + userId,"成功条数：" + rows);
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
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
		user.setSalt(salt);
		this.save(user);
		
		//检查角色是否越权
//		checkRole(user);
		
		// TODO 保存用户与角色关系
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
		
		//检查角色是否越权
//		checkRole(user);
		
		//TODO 保存用户与角色关系
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
	 * 检查角色是否越权
	 */
	private void checkRole(SysUserEntity user) {
		if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
			return;
		}
		//如果不是超级管理员，则需要判断用户的角色是否自己创建
		if (user.getCreateUserId() == Constant.SUPER_ADMIN) {
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
	public Map<String,String> invitation(SysUserEntity sysUserEntity){
		Map<String, String> map = new HashMap<>();
		if(checkEmailExists(sysUserEntity.getEmail())){
			map.put("result","false");
			map.put("reason","用户已注册，无需邀请");
			return map;
		}
		//TODO token获取uid，查询邀请者姓名invitor
//		Long userId = UserUtils.getUserId();
		Long userId = 1L;
		sysUserEntity.setId(userId);
		String invitor = "张先森";
		//redis暂存邮件邀请
		redisTemplate.opsForValue().set("SysInvite:" + sysUserEntity.getEmail(),sysUserEntity.getRealName(),emailLinkExpiretime, TimeUnit.HOURS);
		//发送邀请邮件
		simpleMailSender.sendRegisterEmail("mail/invite.html",sysUserEntity,invitor);
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
	public Map<String,String> activation(SysUserEntity sysUserEntity){
		Map<String, String> map = new HashMap<>();
		map.put("templateName","mail/activation.html");
		//检测邮箱是否已注册
		if(checkEmailExists(sysUserEntity.getEmail())){
			map.put("reason","您已注册，请直接登录");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis取出暂存的邮件邀请
		String realName = String.valueOf(redisTemplate.opsForValue().get("SysInvite:" + sysUserEntity.getEmail()));
		if("null".equals(realName)){
			map.put("reason","邀请过期，请联系邀请者重新邀请");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//生成随机初始密码
		String password = UUID.randomUUID().toString().substring(0, 6);
		map.put("password", password);
		sysUserEntity.setPassword(password);
		sysUserEntity.setRealName(realName);
		//user存库
		try{
			this.saveUser(sysUserEntity);
		}catch (Exception e){
			e.printStackTrace();
			map.put("reason","账户激活失败，请联系邀请者重新邀请或联系管理员");
			map.put("templateName","mail/activation_fail.html");
			return map;
		}
		//redis销毁邀请
		redisTemplate.delete("SysInvite:" + sysUserEntity.getEmail());
		//发邮件通知
		SysUserEntity noticeEntity = new SysUserEntity();
		noticeEntity.setEmail(sysUserEntity.getEmail());
		noticeEntity.setPassword(password);
		simpleMailSender.sendRegisterEmail("mail/activation.html",noticeEntity,null);
		return map;
	}
	
	/**
	* @Description: 检测邮箱是否已注册
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
	* @Description: 检测用户名是否已存在
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
		SysUserEntity sysUserEntity = new SysUserEntity();
		sysUserEntity.setId(uid);
		sysUserEntity.setUsername(username);
		boolean result = this.updateUser(sysUserEntity);
		if(!result){
			map.put("result","false");
			map.put("code", String.valueOf(JSYError.INTERNAL.getCode()));
			map.put("reason","设置用户名失败");
			return map;
		}
		map.put("result","true");
		return map;
	}
	
	/**
	 * @Description: 根据手机号查询用户是否存在
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
	 * @Description: 根据手机号查询登录用户
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
	 * @Description: 查询用户小区账户资料
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
	 * @Description: 根据uid查询用户信息
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
	 * @Description: 操作员条件查询
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
		// 补充数据
		for (UserDetail userDetail : userDetailPageVO.getData()) {
			SysUserEntity sysUserEntity = new SysUserEntity();
			List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(userDetail.getId(), BusinessConst.ULTIMATE_ADMIN);
			sysUserEntity.setId(userDetail.getId());
			sysUserEntity.setIdStr(String.valueOf(userDetail.getId()));
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
	 * @Description: 添加操作员
	 * @Param: [sysUserQO]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public Integer addOperator(SysUserQO sysUserQO){
		// 判断是新增(1)的还是原有(2)的
		int result = 1;
		// 增加用户
		UserDetail userDetail = null;
		// 密码正则匹配
		String password = RSAUtil.privateDecrypt(sysUserQO.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY));
		String pattern = "^(?=.*[A-Z0-9])(?=.*[a-z0-9])(?=.*[a-zA-Z])(.{6,12})$";
		if (!password.matches(pattern)) {
			throw new AdminException(JSYError.REQUEST_PARAM.getCode(), "请输入一个正确的6-12位密码,至少包含大写字母或小写字母或数字两种!");
		}
		
		try {
			userDetail = baseAuthRpcService.userPhoneRegister(sysUserQO.getNickName(), sysUserQO.getPhone(), RSAUtil.privateDecrypt(sysUserQO.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)));
		} catch (BaseException e) {
			// 手机号是否已经注册
			if (e.getErrorEnum().getCode() == 103) {
				userDetail = new UserDetail();
				userDetail.setId(userInfoRpcService.getUserDetailByPhone(sysUserQO.getPhone()).getId());
				result = 2;
			}
		}
		if (userDetail == null) {
			throw new AdminException("用户添加失败");
		}
		// 增加登录类型范围为物业大后台
		baseAuthRpcService.addLoginTypeScope(userDetail.getId(), BusinessConst.ULTIMATE_ADMIN, false);
		// 先移除大后台默认角色，再给用户添加角色
//		List<Long> roleId = new ArrayList<>();
//		roleId.add(1463327674104250369L);
//		baseRoleRpcService.roleRemoveToUser(roleId, userDetail.getId());
		List<Long> roleIds = new ArrayList<>();
		roleIds.add(sysUserQO.getRoleId());
		baseRoleRpcService.userJoinRole(roleIds, userDetail.getId(), sysUserQO.getId());
		return result;
	}
	
	/**
	 * @Description: 编辑操作员
	 * @Param: [sysUserQO]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public void updateOperator(SysUserQO sysUserQO){
		// 密码正则匹配
		String password = RSAUtil.privateDecrypt(sysUserQO.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY));
		String pattern = "^(?=.*[A-Z0-9])(?=.*[a-z0-9])(?=.*[a-zA-Z])(.{6,12})$";
		if (!password.matches(pattern)) {
			throw new AdminException(JSYError.REQUEST_PARAM.getCode(), "请输入一个正确的6-12位密码,至少包含大写字母或小写字母或数字两种!");
		}
		
		//更新资料
		baseUpdateUserRpcService.updateUserInfo(sysUserQO.getId(), sysUserQO.getNickName(),
			sysUserQO.getPhone(), sysUserQO.getPassword(), BusinessConst.ULTIMATE_ADMIN);
	}
	
	/**
	 * @Description: 删除操作员
	 * @author: DKS
	 * @since: 2021/10/13 15:38
	 * @Author: DKS
	 * @Date: 2021/10/13
	 */
	public void deleteOperator(Long id) {
		List<PermitRole> permitRoles = baseRoleRpcService.listAllRolePermission(id, BusinessConst.ULTIMATE_ADMIN);
		// 移除用户角色绑定关系
		Set<Long> roleIds = permitRoles.stream().map(PermitRole::getId).collect(Collectors.toSet());
		baseRoleRpcService.roleRemoveToUser(roleIds, id);
		// 移除登录类型范围
		baseAuthRpcService.removeLoginTypeScope(id, BusinessConst.ULTIMATE_ADMIN, false);
//		baseAuthRpcService.cancellation(id);
	}
	
	/**
	 * @Description: 修改密码
	 * @Param: [qo, uid]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public boolean updatePassword(ResetPasswordQO qo, String uid){
		//没传账号(手机号)，属于在线修改密码操作，根据uid查出手机号
		if(StringUtils.isEmpty(qo.getAccount())){
			String mobile = sysUserMapper.queryMobileByUid(uid);
			if(StringUtils.isEmpty(mobile)){
				throw new AdminException(JSYError.REQUEST_PARAM.getCode(),"账号不存在");
			}
			qo.setAccount(mobile);
		}
		//目前只有手机号账户，查询用户登录账户固定用手机号
		SysUserAuthEntity userAuthEntity = sysUserAuthMapper.selectOne(new QueryWrapper<SysUserAuthEntity>().select("mobile").eq("mobile", qo.getAccount()));
		if(userAuthEntity == null){
			throw new AdminException(JSYError.REQUEST_PARAM.getCode(),"账号不存在");
		}
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		String password = new Sha256Hash(qo.getPassword(), salt).toHex();
		//更新
		SysUserAuthEntity sysUserAuthEntity = new SysUserAuthEntity();
		sysUserAuthEntity.setPassword(password);
		sysUserAuthEntity.setSalt(salt);
		sysUserAuthEntity.setUpdateBy(uid);
		int result = sysUserAuthMapper.update(sysUserAuthEntity, new UpdateWrapper<SysUserAuthEntity>().eq("mobile", userAuthEntity.getMobile()));
		return result == 1;
	}
	
	/**
	 * @Description: 修改手机号
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
		return userInfoRpcService.getUserDetail(Long.parseLong(userId)).getNickName();
	}
}