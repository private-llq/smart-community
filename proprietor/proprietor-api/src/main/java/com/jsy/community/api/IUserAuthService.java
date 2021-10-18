package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.qo.proprietor.AddPasswordQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;

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
	String checkUser(LoginQO qo);
	
	/**
	 * 注册添加密码/修改密码
	 *
	 * @param uid 业主ID
	 * @param qo  参数
	 * @return boolean
	 */
	boolean addPassword(String uid, AddPasswordQO qo);
	
	/**
	 * 注册添加支付密码/修改支付密码
	 *
	 * @param uid 业主ID
	 * @param qo  参数
	 * @return boolean
	 */
	boolean addPayPassword(String uid, AddPasswordQO qo);
	
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
	* @Description: 更换手机号
	 * @Param: [newMobile, uid]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/7
	**/
	void changeMobile(String newMobile,String uid);
	
	/**
	 * 通过用户ID 查询用户手机号码
	 * @param id 	用户id
	 * @return		返回消息实体
	 */
    String selectContactById(String id);
	
    /**
    * @Description: 手机号查用户ID
     * @Param: [mobile]
     * @Return: java.lang.String
     * @Author: chq459799974
     * @Date: 2021/1/12
    **/
	String queryUserIdByMobile(String mobile);

	/**
	 * @Description: 查询当前用户是否设置支付密码
	 * @author: Hu
	 * @since: 2021/10/13 14:46
	 * @Param:
	 * @return:
	 */
    UserAuthEntity selectByPayPassword(String uid);

    /**
     * @Description: 查询当前登录用户是否绑定微信
     * @author: Hu
     * @since: 2021/10/16 10:26
     * @Param:
     * @return:
     */
	UserAuthEntity selectByIsWeChat(String userId);

	/**
	 * @Description: 设置微信openid
	 * @author: Hu
	 * @since: 2021/10/16 10:29
	 * @Param:
	 * @return:
	 */
	void updateByWechat(UserAuthEntity userAuthEntity);

	/**
	 * @Description: 清除微信三方绑定
	 * @author: Hu
	 * @since: 2021/10/18 11:02
	 * @Param:
	 * @return:
	 */
    void updateByOpenId(Long id);
}
