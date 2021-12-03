package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.sys.SysRoleEntity;
import com.jsy.community.entity.sys.SysUserAuthEntity;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.entity.sys.SysUserRoleEntity;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 系统用户
 */
@Slf4j
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
	public PageInfo queryOperator(BaseQO<SysUserQO> baseQO){
		SysUserQO query = baseQO.getQuery();
		/*AdminUserQO query = baseQO.getQuery();
		Page<AdminUserEntity> page = new Page();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper<AdminUserEntity> queryWrapper = new QueryWrapper<AdminUserEntity>().select("id,uid,real_name,mobile,create_time");
		//是否查详情
		Integer menuCount = null;
		if(query.getId() != null){
			queryWrapper.eq("id",query.getId());
			//TODO 换成查角色 回显到详情
//			String uid = sysUserMapper.queryUidById(query.getId());
//			menuCount = adminConfigService.countUserMenu(uid);
		}
		
		if(!StringUtils.isEmpty(query.getName())){
			queryWrapper.and(wrapper -> wrapper
				.like("real_name",query.getName())
				.or().like("mobile",query.getName())
			);
		}
		queryWrapper.orderByDesc("create_time");
		Page<AdminUserEntity> pageData = sysUserMapper.selectPage(page,queryWrapper);

		if(CollectionUtils.isEmpty(pageData.getRecords())){
			return new PageInfo<>();
		}
		Set<String> uidSet = pageData.getRecords().stream().map(adminUserEntity -> adminUserEntity.getUid()).collect(Collectors.toSet());
		List<AdminUserRoleEntity> userRoleEntities = adminUserRoleMapper.queryByUids(uidSet, query.getCompanyId());
		if (!CollectionUtils.isEmpty(userRoleEntities)) {
			for (AdminUserEntity record : pageData.getRecords()) {
				for (AdminUserRoleEntity userRoleEntity : userRoleEntities) {
					if (record.getUid().equals(userRoleEntity.getUid())) {
						record.setRoleId(userRoleEntity.getRoleId());
						record.setRoleName(userRoleEntity.getRoleName());
						break;
					}
				}
			}
		}*/
		List<SysUserEntity> sysUserEntities = sysUserMapper.queryPageUserEntity(query, (baseQO.getPage() - 1) * baseQO.getSize(), baseQO.getSize());
		Integer integer = sysUserMapper.countPageUserEntity(query);
		if (integer == null) {
			integer = 0;
		}
		// 补充角色名称和角色idStr
		for (SysUserEntity sysUserEntity : sysUserEntities) {
			SysRoleEntity sysRoleEntity = sysRoleMapper.selectById(sysUserEntity.getRoleId());
			sysUserEntity.setRoleIdStr(String.valueOf(sysUserEntity.getRoleId()));
			sysUserEntity.setRoleName(sysRoleEntity.getName());
		}
		PageInfo<SysUserEntity> pageInfo = new PageInfo<>();
		pageInfo.setRecords(sysUserEntities);
		pageInfo.setTotal(integer);
		pageInfo.setSize(baseQO.getSize());
		pageInfo.setCurrent(baseQO.getPage());

//		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
	/**
	 * @Description: 添加操作员
	 * @Param: [sysUserEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public void addOperator(SysUserEntity sysUserEntity){
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		//生成UUID 和 ID
		sysUserEntity.setId(SnowFlake.nextId());
		//t_sys_user用户资料表插入数据
		sysUserEntity.setPassword(new Sha256Hash(RSAUtil.privateDecrypt(sysUserEntity.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), salt).toHex());
//		sysUserEntity.setPassword(new Sha256Hash(sysUserEntity.getPassword(), salt).toHex());
		sysUserEntity.setSalt(salt);
		sysUserMapper.addOperator(sysUserEntity);
		// TODO 变为添加角色
		SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
		sysUserRoleEntity.setUserId(sysUserEntity.getId());
		sysUserRoleEntity.setRoleId(sysUserEntity.getRoleId());
		sysUserRoleEntity.setCreateTime(LocalDateTime.now());
		sysUserRoleMapper.insert(sysUserRoleEntity);
//		//t_sys_user_menu添加菜单权限
//		sysConfigService.setUserMenus(sysUserEntity.getMenuIdList(), uid);
		//t_sys_user_auth用户登录表插入数据
		SysUserAuthEntity sysUserAuthEntity = new SysUserAuthEntity();
		BeanUtils.copyProperties(sysUserEntity,sysUserAuthEntity);
		sysUserAuthMapper.createLoginUser(sysUserAuthEntity);
//		//发短信通知，并发送初始密码
//		SmsUtil.sendSmsPassword(sysUserEntity.getMobile(), randomPass);
	}
	
	/**
	 * @Description: 编辑操作员
	 * @Param: [sysUserEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public void updateOperator(SysUserEntity sysUserEntity){
		//查询uid
		UserEntity user = sysUserMapper.queryUidById(sysUserEntity.getId());
		if(user == null){
			throw new AdminException("用户不存在！");
		}
		//更新密码
		if(!StringUtils.isEmpty(sysUserEntity.getPassword())){
			//生成盐值并对密码加密
			String salt = RandomStringUtils.randomAlphanumeric(20);
			String password = new Sha256Hash(RSAUtil.privateDecrypt(sysUserEntity.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), salt).toHex();
//			String password = new Sha256Hash(sysUserEntity.getPassword(), salt).toHex();
			//更新
			SysUserAuthEntity sysUserAuthEntity = new SysUserAuthEntity();
			sysUserAuthEntity.setPassword(password);
			sysUserAuthEntity.setSalt(salt);
			sysUserAuthMapper.update(sysUserAuthEntity, new UpdateWrapper<SysUserAuthEntity>().eq("mobile",user.getMobile()));
		}
		//修改手机号
		if(!StringUtils.isEmpty(sysUserEntity.getMobile()) && !sysUserEntity.getMobile().equals(user.getMobile())){
			//用户是否已注册
			boolean exists = checkUserExists(sysUserEntity.getMobile());
			if(exists){
				throw new AdminException(JSYError.DUPLICATE_KEY.getCode(),"该手机号已被注册");
			}
			//更换手机号操作
			boolean b = changeMobile(sysUserEntity.getMobile(), user.getMobile());
			if(b){
				//旧手机账号退出登录
				UserUtils.destroyToken("Sys:Login",String.valueOf(redisTemplate.opsForValue().get("Sys:LoginAccount:" + user.getMobile())));
				UserUtils.destroyToken("Sys:LoginAccount",user.getMobile());
			}
		}
		//更新菜单权限
//		adminConfigService.setUserMenus(sysUserEntity.getMenuIdList(), uid);
		// 更新操作员角色
		if (sysUserEntity.getRoleId() != null) {
			sysUserRoleMapper.updateOperatorRole(sysUserEntity.getId(), sysUserEntity.getRoleId());
		}
		//更新资料
		sysUserMapper.updateOperator(sysUserEntity);
	}
	
	/**
	 * @Description: 删除操作员
	 * @author: DKS
	 * @since: 2021/10/13 15:38
	 * @Author: DKS
	 * @Date: 2021/10/13
	 */
	public void deleteOperator(Long id) {
		SysUserEntity sysUserEntity = sysUserMapper.selectOne(new QueryWrapper<SysUserEntity>().eq("id", id));
		if (sysUserEntity == null) {
			throw new AdminException(JSYError.OPERATOR_INFORMATION_NOT_OBTAINED.getCode(),"未获取到操作员信息");
		}
		int i = sysUserMapper.deleteById(id);
		if (i != 1) {
			throw new AdminException(JSYError.INTERNAL.getCode(),"删除失败");
		}
		SysUserAuthEntity sysUserAuthEntity = sysUserAuthMapper.selectOne(new QueryWrapper<SysUserAuthEntity>().eq("mobile", sysUserEntity.getMobile()));
		sysUserAuthMapper.deleteById(sysUserAuthEntity.getId());
	}
	
	/**
	 * @Description: 根据手机号检查小区用户是否已存在(t_sys_user)
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Override
	public boolean checkUserExists(String mobile){
		return sysUserMapper.countUser(mobile) != null;
	}
	
	/**
	 * @Description: 根据uid查询手机号
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: DKS
	 * 	 * @Date: 2021/10/13
	 **/
	@Override
	public String queryMobileByUid(String uid){
		return sysUserMapper.queryMobileByUid(uid);
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
		return sysUserMapper.querySysNameByUid(userId);
	}
}