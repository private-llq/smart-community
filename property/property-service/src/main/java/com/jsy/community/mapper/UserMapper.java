package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业主 Mapper 接口
 * </p>
 *
 * @author YuLF
 * @since 2020-11-25
 */
public interface UserMapper extends BaseMapper<UserEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.UserEntity>
	 * @Author 91李寻欢
	 * @Description 批量查询业主数据
	 * @Date 2021/5/10 9:49
	 * @Param [ids]
	 **/
	List<UserEntity> listAuthUserInfo(Collection<String> ids);

	/**
	 * @author: Pipi
	 * @description: 人脸管理查询人脸分页列表
	 * @param userEntity: 用户实体
     * @param startNum: 分页起始数量
     * @param endNum: 分页结束数量
	 * @return: java.util.List<com.jsy.community.entity.UserEntity>
	 * @date: 2021/9/8 16:40
	 **/
	List<UserEntity> queryFacePageList(@Param("userEntity") UserEntity userEntity,
									   @Param("startNum") Integer startNum,
									   @Param("endNum") Integer endNum);
}
