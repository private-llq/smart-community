package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.vo.UserInfoVo;

/**
 * 业主接口
 *
 * @author ling
 * @since 2020-11-11 17:41
 */
public interface IUserService extends IService<UserEntity> {
	
	/**
	 * 登录接口
	 *
	 * @param qo 参数
	 * @return 登录信息
	 */
	UserInfoVo login(LoginQO qo);
	
	/**
	 * 注册接口
	 *
	 * @param qo 参数
	 * @return 登录信息
	 */
	UserInfoVo register(RegisterQO qo);

	/**
	 * 业主信息登记
	 * @param proprietorQO	登记实体参数
	 * @return				返回是否登记成功
	 */
	Boolean proprietorRegister(ProprietorQO proprietorQO);

	/**
	 * 业主信息更新
	 * @param proprietorQO  更新实体
	 * @return				返回更新行数
	 */
    Integer proprietorUpdate(ProprietorQO proprietorQO);
}
