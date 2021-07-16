package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.UserInfoVo;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * YuLF
 * 2020-11-28
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
	/**
	* @Description: 更新用户regId
	 * @Param: [regId, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	**/
	@Update("update t_user set reg_id = #{regId} where uid = #{uid} and deleted = 0")
	int updateUserRegId(String regId, String uid);
	
	/**
	* @Description: uid查用户
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.UserEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	**/
	@Select("select * from t_user where uid = #{uid} and deleted = 0")
	UserEntity queryUserInfoByUid(String uid);
	
	/**
	* @Description: uid查手机号
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	**/
	@Select("select mobile from t_user where uid = #{uid} and deleted = 0")
	String queryUserMobileByUid(String uid);
	
	//TODO user表householder_id字段暂未使用
	@Update("update t_user set householder_id = #{householderId} where id = #{uid}")
	int setUserBelongTo(Long householderId,Long uid);

    /**
    * @Description: 更换手机号
     * @Param: [newMobile, uid]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2021/1/29
    **/
	@Update("update t_user set mobile = #{newMobile} where uid = #{uid} and deleted = 0")
	int changeMobile(@Param("newMobile")String newMobile, @Param("uid")String uid);

	/**
	 * 通过用户id查出业主详情
	 * @param userId	用户ID
	 * @author YuLF
	 * @since  2020/12/18 11:39
	 * @return			返回业主详情信息
	 */
	@Select("select uid,real_name,is_real_auth,sex,id_card from t_user where uid = #{userId} and deleted = 0")
	UserInfoVo selectUserInfoById(@Param("userId") String userId);

	/**
	 * 查询用户拥有房屋
	 * @param userId	用户id
	 * @param houseId	房屋id
	 * @return			返回房屋列表
	 */
    List<HouseVo> queryUserHouseById(@Param("uid") String userId,@Param("houseId") Long houseId);


	/**
	 * 根据id获取用户实名认证信息状态
	 * @param uid 	用户id
	 * @return			返回实名认证状态 和 最新的一个房屋id
	 */
	@Select("select u.is_real_auth from t_user as u  where u.uid = #{uid}  and u.deleted = 0 ")
	Integer getRealAuthStatus(String uid);

	/**
	 * 根据用户id 和 房屋id 查出 用户姓名、性别
	 * @param uid 				用户id
	 * @return					返回用户信息和用户家属信息
	 */
	@Select("select u.real_name,u.sex from t_user as u  WHERE u.deleted = 0 and u.uid = #{uid}")
	UserInfoVo selectUserNameAndHouseAddr(String uid);

	/**
	 * 根据用户id查出 房屋地址
	 * @param houseId 	房屋id
	 * @return			返回房屋地址组成的字符串
	 */
	@Select("select CONCAT(c.`name`,h.building,h.unit,h.floor,h.door) as detailAddress,c.id as communityId  from t_community as c JOIN t_house as h on c.id = h.community_id where \n" +
			"c.deleted = 0 and h.deleted = 0 and h.id = #{houseId}")
	UserInfoVo selectHouseAddr(Long houseId);

	/**
	 * 通过用户id 拿到用户最新的房屋id
	 * @param uid 				用户id
	 * @return			返回最新的用户房屋id 可能为空
	 */
//	@Select("select house_id as householderId from t_user_house  where check_status = 1 and deleted = 0 and uid = #{uid} ORDER BY create_time DESC limit 1")
	@Select("select house_id as householderId from t_user_house  where deleted = 0 and uid = #{uid} ORDER BY create_time DESC limit 1")
	Long getLatestHouseId(String uid);


	/**
	 * 根据 房屋id 拿到房屋基本信息地址
	 * @param houseId 		房屋id
	 * @return				返回房屋信息
	 */
	@Select("select h.id,h.community_id,c.name as communityName,h.building,h.unit,h.floor,h.door,CONCAT(c.`name`,h.building,h.unit,h.floor,h.door) as mergeName from t_community as c LEFT JOIN t_house as h on c.id = h.community_id where\n" +
			"h.id = #{houseId} and c.deleted = 0 and h.deleted = 0 ")
    HouseVo getHouseInfoById(Long houseId);


	/**
	 * 根据业主身份证 获取 业主表 所有房屋id + 社区id
	 * @param idCard 		证件号码
	 * @return				返回房屋id + 社区 id 集合列表
	 */
	List<UserHouseQo> getProprietorInfo(String idCard);
	
	/**
	* @Description: uids批量查 uid-姓名映射
	 * @Param: [uids]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	@MapKey("uid")
	Map<String, Map<String,String>> queryNameByUidBatch(Collection<String> uids);

	/**
	* @Description: 在固定的uid范围内筛选姓名满足模糊匹配条件的uid
	 * @Param: [uids, nameLike]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	List<String> queryUidOfNameLike(@Param("uids")Collection<String> uids, @Param("nameLike")String nameLike);
}
