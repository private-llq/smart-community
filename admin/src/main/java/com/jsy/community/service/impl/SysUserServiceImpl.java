package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.SysUserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.NameAndCreatorQO;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.utils.Constant;
import com.jsy.community.utils.Query;
import com.jsy.community.utils.SimpleMailSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Override
	public boolean updatePassword(Long userId, String password, String newPassword) {
		SysUserEntity userEntity = new SysUserEntity();
		userEntity.setPassword(newPassword);
		return this.update(userEntity,
			new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
	}
	
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
//		Long userId = JwtUtils.getUserId();
		Long userId = 1L;
		sysUserEntity.setId(userId);
		String invitor = "张先森";
		//redis暂存邮件邀请
		redisTemplate.opsForValue().set("AdminInvite:" + sysUserEntity.getEmail(),sysUserEntity.getRealName(),emailLinkExpiretime, TimeUnit.HOURS);
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
		String realName = String.valueOf(redisTemplate.opsForValue().get("AdminInvite:" + sysUserEntity.getEmail()));
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
		redisTemplate.delete("AdminInvite:" + sysUserEntity.getEmail());
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
	private boolean checkUsernameExists(String username){
		Integer count = baseMapper.selectCount(new QueryWrapper<SysUserEntity>().eq("username", username));
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
	
}