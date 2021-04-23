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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	* @Description: 更新用户极光ID
	 * @Param: [regId, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	**/
	boolean updateUserRegId(String regId, String uid);
	
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
	UserAuthVo thirdPlatformLogin(UserThirdPlatformQO qo);
	
	/**
	* @Description: 三方绑定手机
	 * @Param: [userThirdPlatformQO]
	 * @Return: com.jsy.community.vo.UserAuthVo
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	UserAuthVo bindThirdPlatform(UserThirdPlatformQO qo);



	/**
	 * 业主信息更新
	 * 新方法至 -> updateImprover
	 * @param qo  更新实体
	 * @return				返回更新布尔值
	 */
	@Deprecated
    Boolean proprietorUpdate(ProprietorQO qo);



	/**
	 * 业主信息更新
	 * @param qo  更新实体
	 * @return				返回更新布尔值
	 */
	Boolean updateImprover(ProprietorQO qo);
    /**
     * 根据业主id查询业主信息及业主家属信息
     * @param userId  		用户id
	 * @param houseId		房屋id
	 * @author YuLF
     * @since  2020/12/10 16:25
	 * @return				房屋业主信息 和 家属信息
     */
    UserInfoVo proprietorQuery(String userId, Long houseId);
	
    /**
    * @Description: 查询用户社区(房屋已认证的)
     * @Param: [uid]
     * @Return: java.util.Collection<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author: chq459799974
     * @Date: 2021/3/31
    **/
	Collection<Map<String, Object>> queryUserHousesOfCommunity(String uid);
    
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


	/**
	 * 根据id获取用户实名认证信息和最新的房屋id
	 * @param userId 		用户id
	 * @return			返回实名认证状态 和 最新的一个房屋id
	 */
	UserEntity getRealAuthAndHouseId(String userId);


	/**
	 * 根据用户id 和 房屋id 查出 用户信息和用户家属信息
	 * @param uid 				用户id
	 * @param houseId			房屋id
	 * @return					返回用户信息和用户家属信息
	 */
	UserInfoVo getUserAndMemberInfo(String uid, Long houseId);


	/**
	 * 新的接口调用 ：proprietorDetails
	 * 根据社区id 和 房屋id 查出用户房屋信息详情
	 * @param cid 		社区id
	 * @param hid		房屋id
	 * @param uid		用户id
	 * @return			返回基本查询信息
	 */
	@Deprecated
	UserInfoVo userInfoDetails(Long cid, Long hid, String uid);
	
	/**
	* @Description: 实名认证后修改用户信息
	 * @Param: [userEntity]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/3/2
	**/
	void updateUserAfterRealnameAuth(UserEntity userEntity);
	
	/**
	* @Description: uids批量查询 uid-姓名映射
	 * @Param: [uids]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	Map<String, Map<String,String>> queryNameByUidBatch(Collection<String> uids);
	
	/**
	* @Description: 在固定的uid范围内筛选姓名满足模糊匹配条件的uid
	 * @Param: [uids, nameLike]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	List<String> queryUidOfNameLike(List<String> uids, String nameLike);
}
