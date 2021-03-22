package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.IOrganizationService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.util.Constant;
import com.jsy.community.util.SimpleMailSender;
import com.jsy.community.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
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
	
//	@Value("${email.linkExpiretime}")
	public long emailLinkExpiretime = 24;
	
	@Autowired
	private SimpleMailSender simpleMailSender;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Resource
	private AdminUserMapper adminUserMapper;
	
	@Autowired
	private IOrganizationService organizationService;
	
	/**
	* @Description: 设置用户角色
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
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
	
	@Override
	public AdminUserEntity queryByUserName(String username) {
		return baseMapper.queryByUserName(username);
	}
	
	@Override
	public AdminUserEntity queryByEmail(String email) {
		return baseMapper.queryByEmail(email);
	}
	
	@Override
	public AdminUserEntity queryByMobile(String mobile) {
		return baseMapper.queryByMobile(mobile);
	}
	
	@Override
	@Transactional
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
	
	@Override
	public void deleteBatch(Long[] userId) {
		this.removeByIds(Arrays.asList(userId));
	}
	
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
		QueryWrapper<AdminUserEntity> queryWrapper = new QueryWrapper<AdminUserEntity>().select("id,number,real_name,mobile,id_card,status,role_type,org_id,job,create_by,create_time,update_by,update_time");
		queryWrapper.eq("community_id",query.getCommunityId());
		if(!StringUtils.isEmpty(query.getName())){
			queryWrapper.and(wrapper -> wrapper.like("number",query.getName())
				.or().like("real_name",query.getName())
				.or().like("mobile",query.getName())
				.or().like("id_card",query.getName())
			);
		}
		if(query.getStatus() != null){
			queryWrapper.eq("status",query.getStatus());
		}
		queryWrapper.orderByAsc("role_type","status");
		queryWrapper.orderByDesc("create_time");
		Page<AdminUserEntity> pageData = adminUserMapper.selectPage(page,queryWrapper);
		//设置组织机构名称
		if(pageData.getRecords().size() > 0){
			LinkedList<Long> list = new LinkedList<>();
			for(AdminUserEntity entity : pageData.getRecords()){
				list.add(entity.getOrgId());
			}
			Map<Long, Map<Long, Object>> orgMap = organizationService.queryOrganizationNameByIdBatch(list);
			for(AdminUserEntity entity : pageData.getRecords()){
				entity.setOrgName(String.valueOf(orgMap.get(BigInteger.valueOf(entity.getOrgId())).get("name")));
			}
		}
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
		Long communityId = 2L;
//		adminUserEntity.setCommunityId(1);
		adminUserEntity.setCommunityId(communityId);
		//查询组织机构是否存在
		if(!organizationService.isExists(adminUserEntity.getOrgId(),communityId)){
			throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"组织机构不存在！");
		}
		adminUserEntity.setId(SnowFlake.nextId());
		adminUserEntity.setUid(UserUtils.randomUUID());
		adminUserEntity.setStatus(adminUserEntity.getStatus() != null ? adminUserEntity.getStatus() : 0);
		//生成随机密码
		String randomPass = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		adminUserEntity.setPassword(new Sha256Hash(randomPass, salt).toHex());
		adminUserEntity.setSalt(salt);
		int result = adminUserMapper.addOperator(adminUserEntity);
		//发短信通知，并发送初始密码
		boolean b = SmsUtil.sendSmsPassword(adminUserEntity.getMobile(), randomPass);
//		if(!b){
//			throw new PropertyException(JSYError.INTERNAL.getCode(),"短信通知失败，用户添加失败");
//		}
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
		adminUserEntity.setUpdateBy("1a7a182d711e441fbb24659090daf5cb");
		adminUserEntity.setCommunityId(2L);
		if(adminUserEntity.getOrgId() != null){
			//查询组织机构是否存在
			if(!organizationService.isExists(adminUserEntity.getOrgId(),adminUserEntity.getCommunityId())){
				throw new PropertyException(JSYError.REQUEST_PARAM.getCode(),"组织机构不存在！");
			}
		}
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
		Long communityId = 2L;
		//生成随机密码
		String randomPass = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
		//生成盐值并对密码加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		String password = new Sha256Hash(randomPass, salt).toHex();
		//更新
		AdminUserEntity adminUserEntity = new AdminUserEntity();
		adminUserEntity.setPassword(password);
		adminUserEntity.setSalt(salt);
		adminUserEntity.setUpdateBy(uid);
		int result = adminUserMapper.update(adminUserEntity, new UpdateWrapper<AdminUserEntity>().eq("id", id).eq("community_id",communityId));
		//发短信通知初始密码
		boolean b = SmsUtil.sendSmsPassword(adminUserEntity.getMobile(), randomPass);
//		if(!b){
//			throw new PropertyException(JSYError.INTERNAL.getCode(),"短信通知失败，用户添加失败");
//		}
		return result == 1;
	}
	
	//==================================== 物业端（新）end ====================================
	
}