package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.qo.ThirdPlatformQo;
import com.jsy.community.qo.proprietor.AddPasswordQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.vo.ThirdPlatformVo;

import java.util.List;

/**
 * 用户认证接口
 *
 * @author ling
 * @since 2020-11-11 15:47
 */
public interface IUserAuthService extends IService<UserAuthEntity> {
	List<UserAuthEntity> getList(boolean a);
	
	/**
	 * 通过指定字段查询业主认证信息
	 *
	 * @param qo 登录信息
	 * @return 业主ID
	 */
	Long checkUser(LoginQO qo);
	
	/**
	 * 注册添加密码
	 *
	 * @param uid 业主ID
	 * @param qo  参数
	 * @return boolean
	 */
	boolean addPassword(Long uid, AddPasswordQO qo);
	
	/**
	 * 根据指定字段，检查业主是否存在
	 *
	 * @param account 账号
	 * @return boolean
	 */
	boolean checkUserExists(String account, String field);
	
	/**
	 * 重置密码
	 *
	 * @param qo 参数
	 * @return boolean
	 */
	boolean resetPassword(ResetPasswordQO qo);
	
	/**
	 * 获取三方平台登录信息
	 */
	List<ThirdPlatformVo> getThirdPlatformInfo();
	
	/**
	 * 登录
	 *
	 * @param oauthType 平台名
	 */
	String thirdPlatformLogin(String oauthType);
	
	/**
	 * 请求三方登录
	 *
	 * @param oauthType 登录名
	 * @param callback  回调
	 */
	Object thirdPlatformLoginCallback(String oauthType, ThirdPlatformQo callback);
}
