package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;

import java.util.List;

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
    UserInfoVo proprietorQuery(String userId);
	
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
	 * @Param communityId	社区id
	 * @author YuLF
	 * @since  2020/12/18 11:39
	 * @return			返回业主详情信息
	 */
    UserInfoVo proprietorDetails(String userId, Long communityId);
}
