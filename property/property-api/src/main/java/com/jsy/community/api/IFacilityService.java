package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.PageInfo;

import java.util.Map;

/**
 * @author chq459799974
 * @description 设备相关Service
 * @since 2021-06-12 10:29
 **/
public interface IFacilityService extends IService<FacilityEntity> {
	
	/**
	* @Description: 单条更新设备状态
	 * @Param: [facilityId, status]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/16
	**/
	void changeStatus(Integer status, Long facilityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加设备信息
	 * @Date 2021/3/13 14:28
	 * @Param [facilityEntity]
	 **/
	void addFacility(FacilityEntity facilityEntity);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.hk.FacilityEntity>
	 * @Author lihao
	 * @Description 分页查询设备
	 * @Date 2021/3/13 16:02
	 * @Param [facilityEntity, page, size]
	 **/
	PageInfo<FacilityEntity> listFacility(BaseQO<FacilityQO> facilityQO);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 删除设备
	 * @Date 2021/3/15 9:40
	 * @Param [id]
	 **/
	void deleteFacility(Long id);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 编辑设备
	 * @Date 2021/4/23 9:36
	 * @Param [facilityEntity]
	 **/
	void updateFacility(FacilityEntity facilityEntity);
	
	/**
	 * @return java.util.Map<java.lang.String,java.lang.Integer>
	 * @Author 91李寻欢
	 * @Description 获取设备在线离线数
	 * @Date 2021/4/23 17:03
	 * @Param [typeId,communityId]
	 **/
	Map<String, Integer> getCount(Long typeId,Long communityId);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 刷新设备
	 * @Date 2021/4/23 18:11
	 * @Param [page, size]
	 **/
	void flushFacility(Integer page, Integer size, String facilityTypeId);
	
	/**
	 * @return com.jsy.community.entity.hk.FacilityEntity
	 * @Author 91李寻欢
	 * @Description 根据ip地址查询出这个设备的基本信息
	 * @Date 2021/4/25 9:19
	 * @Param [ip]
	 **/
	FacilityEntity listByIp(String ip);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 同步数据
	 * @Date 2021/4/29 10:56
	 * @Param [id]
	 **/
	void connectData(Long id, Long communityId);
}
