//package com.jsy.community.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.jsy.community.entity.UserEntity;
//import com.jsy.community.entity.admin.AdminUserAuthEntity;
//import com.jsy.community.entity.admin.AdminUserEntity;
//import com.jsy.community.exception.JSYError;
//import com.jsy.community.mapper.AdminUserAuthMapper;
//import com.jsy.community.mapper.AdminUserMapper;
//import com.jsy.community.qo.BaseQO;
//import com.jsy.community.qo.admin.AdminUserQO;
//import com.jsy.community.service.AdminException;
//import com.jsy.community.service.IAccountService;
//import com.jsy.community.utils.PageInfo;
//import com.jsy.community.utils.RSAUtil;
//import com.jsy.community.utils.SnowFlake;
//import com.jsy.community.utils.UserUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.shiro.crypto.hash.Sha256Hash;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//
///**
// * 系统用户
// */
//@Slf4j
//@Service
//public class AccountServiceImpl extends ServiceImpl<AdminUserMapper, AdminUserEntity> implements IAccountService {
//
//	@Resource
//	private RedisTemplate redisTemplate;
//
//	@Resource
//	private AdminUserMapper adminUserMapper;
//
//	@Resource
//	private AdminUserAuthMapper adminUserAuthMapper;
//
//	@Resource
//	private UserUtils userUtils;
//
//	@Value("${propertyLoginExpireHour}")
//	private long loginExpireHour = 12;
//
//	/**
//	* @Description: 操作员条件查询
//	 * @Param: [baseQO]
//	 * @Return: com.jsy.community.utils.PageInfo
//	 * @Author: chq459799974
//	 * @Date: 2021/3/16
//	**/
//	@Override
//	public PageInfo queryOperator(BaseQO<AdminUserQO> baseQO){
//		AdminUserQO query = baseQO.getQuery();
//		List<AdminUserEntity> adminUserEntities = adminUserMapper.queryPageUserEntity(query, (baseQO.getPage() - 1) * baseQO.getSize(), baseQO.getSize());
//		Integer integer = adminUserMapper.countPageUserEntity(query);
//		if (integer == null) {
//			integer = 0;
//		}
//		PageInfo<AdminUserEntity> pageInfo = new PageInfo<>();
//		pageInfo.setRecords(adminUserEntities);
//		pageInfo.setTotal(integer);
//		pageInfo.setSize(baseQO.getSize());
//		pageInfo.setCurrent(baseQO.getPage());
//
////		BeanUtils.copyProperties(pageData,pageInfo);
//		return pageInfo;
//	}
//
//	/**
//	* @Description: 添加操作员
//	 * @Param: [adminUserEntity]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2021/3/17
//	**/
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public void addOperator(AdminUserEntity adminUserEntity){
//		//生成盐值并对密码加密
//		String salt = RandomStringUtils.randomAlphanumeric(20);
//		//生成UUID 和 ID
//		String uid = UserUtils.randomUUID();
//		adminUserEntity.setId(SnowFlake.nextId());
//		adminUserEntity.setUid(uid);
//		//t_admin_user用户资料表插入数据
//		adminUserEntity.setPassword(new Sha256Hash(RSAUtil.privateDecrypt(adminUserEntity.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), salt).toHex());
//		adminUserEntity.setSalt(salt);
//		adminUserMapper.addOperator(adminUserEntity);
//		// TODO 变为添加角色
////		AdminUserRoleEntity adminUserRoleEntity = new AdminUserRoleEntity();
////		adminUserRoleEntity.setUid(uid);
////		adminUserRoleEntity.setRoleId(adminUserEntity.getRoleId());
////		adminUserRoleEntity.setCreateTime(LocalDateTime.now());
////		adminUserRoleMapper.insert(adminUserRoleEntity);
////		//t_admin_user_menu添加菜单权限
////		adminConfigService.setUserMenus(adminUserEntity.getMenuIdList(), uid);
//		//t_admin_user_auth用户登录表插入数据
//		AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
//		BeanUtils.copyProperties(adminUserEntity,adminUserAuthEntity);
//		adminUserAuthMapper.createLoginUser(adminUserAuthEntity);
////		//发短信通知，并发送初始密码
////		SmsUtil.sendSmsPassword(adminUserEntity.getMobile(), randomPass);
//		//添加社区权限
//		if(!CollectionUtils.isEmpty(adminUserEntity.getCommunityIdList())){
//			adminConfigService.updateAdminCommunityBatch(adminUserEntity.getCommunityIdList(),uid);
//		}
//	}
//
//	/**
//	* @Description: 编辑操作员
//	 * @Param: [adminUserEntity]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2021/3/17
//	**/
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public void updateOperator(AdminUserEntity adminUserEntity){
//		//查询uid
//		UserEntity user = adminUserMapper.queryUidById(adminUserEntity.getId());
//		if(user == null){
//			throw new AdminException("用户不存在！");
//		}
//		//更新密码
//		if(!StringUtils.isEmpty(adminUserEntity.getPassword())){
//			//生成盐值并对密码加密
//			String salt = RandomStringUtils.randomAlphanumeric(20);
//			String password = new Sha256Hash(RSAUtil.privateDecrypt(adminUserEntity.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), salt).toHex();
////			String password = new Sha256Hash(adminUserEntity.getPassword(), salt).toHex();
//			//更新
//			AdminUserAuthEntity adminUserAuthEntity = new AdminUserAuthEntity();
//			adminUserAuthEntity.setPassword(password);
//			adminUserAuthEntity.setSalt(salt);
//			adminUserAuthMapper.update(adminUserAuthEntity, new UpdateWrapper<AdminUserAuthEntity>().eq("mobile",user.getMobile()));
//		}
//		//更新社区权限
//		if(!CollectionUtils.isEmpty(adminUserEntity.getCommunityIdList())){
//			adminConfigService.updateAdminCommunityBatch(adminUserEntity.getCommunityIdList(),user.getUid());
//			//刷新token中的社区权限
//			String token = String.valueOf(redisTemplate.opsForValue().get("Admin:LoginAccount:" + user.getMobile()));
//			String tokenValue = String.valueOf(redisTemplate.opsForValue().get("Admin:Login:" + token));
//			AdminUserEntity userData = JSONObject.parseObject(tokenValue,AdminUserEntity.class);
//			//如果此时token刚好过期，则不操作
//			if(userData != null){
//				userData.setCommunityIdList(adminUserEntity.getCommunityIdList());
//				redisTemplate.opsForValue().set("Admin:Login:" + token , JSON.toJSONString(userData) , loginExpireHour ,TimeUnit.HOURS);
//				redisTemplate.opsForValue().set("Admin:LoginAccount:" + user.getMobile() , token , loginExpireHour ,TimeUnit.HOURS);
//			}
//		}
//		//修改手机号
//		if(!StringUtils.isEmpty(adminUserEntity.getMobile()) && !adminUserEntity.getMobile().equals(user.getMobile())){
//			//用户是否已注册
//			boolean exists = checkUserExists(adminUserEntity.getMobile());
//			if(exists){
//				throw new AdminException(JSYError.DUPLICATE_KEY.getCode(),"该手机号已被注册");
//			}
//			//更换手机号操作
//			boolean b = changeMobile(adminUserEntity.getMobile(), user.getMobile());
//			if(b){
//				//旧手机账号退出登录
//				userUtils.destroyToken("Admin:Login",String.valueOf(redisTemplate.opsForValue().get("Admin:LoginAccount:" + user.getMobile())));
//				userUtils.destroyToken("Admin:LoginAccount",user.getMobile());
//			}
//		}
//		//更新菜单权限
////		adminConfigService.setUserMenus(adminUserEntity.getMenuIdList(), uid);
//		//更新资料
//		adminUserMapper.updateOperator(adminUserEntity);
//	}
//
//	/**
//	 * @Description: 删除操作员
//	 * @author: DKS
//	 * @since: 2021/10/13 15:38
//	 * @Author: DKS
//	 * @Date: 2021/10/13
//	 */
//	@Override
//	public void deleteOperator(Long id) {
//		AdminUserEntity adminUserEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("id", id));
//		if (adminUserEntity == null) {
//			throw new AdminException(JSYError.OPERATOR_INFORMATION_NOT_OBTAINED.getCode(),"未获取到操作员信息");
//		}
//		int i = adminUserMapper.deleteById(id);
//		if (i != 1) {
//			throw new AdminException(JSYError.INTERNAL.getCode(),"删除失败");
//		}
//	}
//}