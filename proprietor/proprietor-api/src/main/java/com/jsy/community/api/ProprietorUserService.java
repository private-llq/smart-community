package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.UserThirdPlatformQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.vo.ControlVO;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 业主接口
 *
 * @author ling
 * @since 2020-11-11 17:41
 */
public interface ProprietorUserService extends IService<UserEntity> {
	
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
	 * @Description: 第二版注册
	 * @author: Hu
	 * @since: 2021/8/17 13:55
	 * @Param:
	 * @return:
	 */
	String registerV2(RegisterQO qo);

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
	 * @return			返回状态 和 最新的一个房屋id
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

	/**
	 * @Description: 删除业主车辆
	 * @author: Hu
	 * @since: 2021/7/8 14:00
	 * @Param: [houseId]
	 * @return: com.jsy.community.vo.CommonResult<com.jsy.community.vo.UserInfoVo>
	 */
    void deleteCar(String userId, Long id);

    /**
     * @Description: 获取当前登录用户权限
     * @author: Hu
     * @since: 2021/8/16 15:14
     * @Param:
     * @return:
     */
	ControlVO control(Long communityId, String uid);

	/**
	 * @Description: 查询业主是否实名认证
	 * @author: Hu
	 * @since: 2021/8/18 15:16
	 * @Param:
	 * @return:
	 */
	Integer userIsRealAuth(String userId);

	/**
	 * @Description: 查询人脸
	 * @author: Hu
	 * @since: 2021/8/23 13:46
	 * @Param:
	 * @return:
	 */
    String getFace(String userId);

    /**
     * @Description: 修改用户人脸
     * @author: Hu
     * @since: 2021/8/23 13:51
     * @Param:
     * @return:
     */
	void saveFace(String userId,String faceUrl);


	/**
	 * @Description: 删除业主人脸
	 * @author: Hu
	 * @since: 2021/8/24 16:58
	 * @Param:
	 * @return:
	 */
	void deleteFaceAvatar(String userId);

	/**
	 * @Description: 查询家属拥有的小区
	 * @author: Hu
	 * @since: 2021/9/22 13:53
	 * @Param:
	 * @return:
	 */
	Collection<Map<String, Object>> queryRelationHousesOfCommunity(String uid);

	/**
	 * @Description: uid查询详情
	 * @author: Hu
	 * @since: 2021/9/25 15:37
	 * @Param:
	 * @return:
	 */
	UserEntity getUser(String tenantUid);

	/**
	 * @Description: 查询当前用户所有是业主身份的小区
	 * @author: Hu
	 * @since: 2021/9/29 15:36
	 * @Param:
	 * @return:
	 */
	Collection<Map<String, Object>> queryCommunityUserList(String uid);

	/**
	 * @Description: 查询当前用户所有身份的房屋信息
	 * @author: Hu
	 * @since: 2021/9/29 16:08
	 * @Param:
	 * @return:
	 */
	List<HouseEntity> queryUserHouseListAll(String userId);

	/**
	 * @Description: 用户绑定微信
	 * @author: Hu
	 * @since: 2021/10/15 10:05
	 * @Param:
	 * @return:
	 */
    String bindingWechat(String userId, String openid);

    /**
     * @Description: 解除微信绑定
     * @author: Hu
     * @since: 2021/10/18 10:56
     * @Param:
     * @return:
     */
	void relieveBindingWechat(RegisterQO registerQO, String userId);
}
