package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.vo.UserHouseVO;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * <p>
 * 业主房屋认证 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-11-25
 */
public interface IUserHouseService extends IService<UserHouseEntity> {




	/**
	 * @return com.jsy.community.utils.PageInfo<com.jsy.community.vo.UserHouseVO>
	 * @Author lihao
	 * @Description
	 * @Date 2020/11/26 15:23
	 * @Param [baseQO, communityId]
	 **/
	PageInfo<UserHouseVO> selectUserHouse(BaseQO<UserHouseEntity> baseQO, Long communityId);
	
	/**  
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description 
	 * @Date 2020/12/15 15:07
	 * @Param [uid, houseEntityList] 
	 **/
	Boolean saveUserHouse(String uid, List<HouseEntity> houseEntityList);
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description 通过审核
	 * @Date 2020/11/26 15:39
	 * @Param [id]
	 **/
	Boolean pass(Long id);
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description 不通过审核
	 * @Date 2020/11/26 16:32
	 * @Param [id]
	 **/
	Boolean notPass(Long id);
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description 根据业主id删除房屋审核列表
	 * @Date 2020/11/28 9:46
	 * @Param [id]
	 **/
	Boolean removeUserHouse(Long id);
	
}
