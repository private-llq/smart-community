package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.UserInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * YuLF
 * 2020-11-28
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	UserEntity queryUserInfoByUid(String uid);
	
	@Select("select mobile from t_user where uid = #{uid}")
	String queryUserMobileByUid(String uid);
	
	//TODO user表householder_id字段暂未使用
	@Update("update t_user set householder_id = #{householderId} where id = #{uid}")
	int setUserBelongTo(Long householderId,Long uid);

	/**
	 * 【用户】业主信息更新接口、
	 * @param proprietorQO 参数实体
	 * @return			 返回更新行数
	 */
    int proprietorUpdate(ProprietorQO proprietorQO);
	
    /**
    * @Description: 更换手机号
     * @Param: [newMobile, uid]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2021/1/29
    **/
	@Update("update t_user set mobile = #{newMobile} where uid = #{uid}")
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
	@Select("select house_id as householderId from t_user_house  where check_status = 1 and deleted = 0 and uid = #{uid} ORDER BY create_time limit 1")
	Long getLatestHouseId(String uid);


	/**
	 * 根据 房屋id 拿到房屋基本信息地址
	 * @param houseId 		房屋id
	 * @return				返回房屋信息
	 */
	@Select("select h.id,h.community_id,c.name as communityName,h.building,h.unit,h.floor,h.door,CONCAT(c.`name`,h.building,h.unit,h.floor,h.door) as mergeName from t_community as c LEFT JOIN t_house as h on c.id = h.community_id where\n" +
			"h.id = #{houseId} and c.deleted = 0 and h.deleted = 0 ")
    HouseVo getHouseInfoById(Long houseId);
}
