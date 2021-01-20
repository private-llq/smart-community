package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.entity.UserThirdPlatformEntity;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.UserThirdPlatformQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 业主接口
 *
 * @author ling
 * @since 2020-11-11 17:41
 */
public interface IUserService extends IService<UserEntity> {
	
	/**
	* @Description: 生成带token的UserAuthVo
	 * @Param: []
	 * @Return: com.jsy.community.vo.UserAuthVo
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
	UserAuthVo createAuthVoWithToken(UserInfoVo userInfoVo);
	
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
	String register(RegisterQO qo);
	
	/**
	* @Description: 三方登录
	 * @Param: [userThirdPlatformQO]
	 * @Return: com.jsy.community.vo.UserAuthVo
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	UserAuthVo thirdPlatformLogin(UserThirdPlatformQO userThirdPlatformQO);
	
	/**
	* @Description: 三方绑定手机
	 * @Param: [userThirdPlatformQO]
	 * @Return: com.jsy.community.vo.UserAuthVo
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	UserAuthVo bindThirdPlatform(UserThirdPlatformQO userThirdPlatformQO);

	/**
	 * 业主信息登记
	 * @param proprietorQO	登记实体参数
	 * @return				返回是否登记成功
	 */
	Boolean proprietorRegister(ProprietorQO proprietorQO);

	/**
	 * 业主信息更新
	 * @param proprietorQO  更新实体
	 * @return				返回更新布尔值
	 */
    Boolean proprietorUpdate(ProprietorQO proprietorQO);

    /**
     * 根据业主id查询业主信息及业主家属信息
     * @author YuLF
     * @since  2020/12/10 16:25
     */
    UserInfoVo proprietorQuery(String userId, Long houseId);
	
    /**
    * @Description: 查询业主所有社区的房屋
     * @Param: [uid]
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Author: chq459799974
     * @Date: 2020/12/16
    **/
	List<HouseEntity> queryUserHouseList(String uid);


	/**
	 * 业主详情查看
	 * @param userId		用户ID
	 * @author YuLF
	 * @since  2020/12/18 11:39
	 * @return			返回业主详情信息
	 */
    UserInfoVo proprietorDetails(String userId);
	
	/**
	* @Description: 小区业主/家属获取门禁权限
	 * @Param: [uid, communityId]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/12/23
	**/
	Map<String,String> getAccess(String uid, Long communityId);
	
	/**
	* @Description: 查询用户是否存在
	 * @Param: [uid]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/1/13
	**/
	Map<String,Object> checkUserAndGetUid(String uid);
	
	/**
	* @Description: 单表查询用户信息
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.UserEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/20
	**/
	UserEntity queryUserDetailByUid(String uid);
	
}
