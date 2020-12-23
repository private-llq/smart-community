package com.jsy.community.mapper;

import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.vo.UserInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


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
	
	@Update("update t_user set mobile = #{newMobile} where uid = #{uid}")
	int changeMobile(@Param("newMobile")String newMobile, @Param("uid")String uid);

	/**
	 * 通过用户id查出业主详情
	 * @param userId	用户ID
	 * @author YuLF
	 * @since  2020/12/18 11:39
	 * @return			返回业主详情信息
	 */
	@Select("select uid,real_name,is_real_auth,sex,id_card from t_user where uid = #{userId}")
	UserInfoVo selectUserInfoById(@Param("userId") String userId);
}
