package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilitySyncRecordEntity;
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
	* @Description: 单条更新设备在线状态
	 * @Param: [facilityId, status, time]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/16
	**/
	void changeStatus(Integer status, Long facilityId, Long time);
	
	/**
	* @Description: 批量更新设备在线状态
	 * @Param: [mapBody]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/17
	**/
	void changeStatusBatch(Map<String,Object> mapBody);
	
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
	 * @Param [id,communityId]
	 **/
	void deleteFacility(Long id,Long communityId);
	
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
	void flushFacility(Integer page, Integer size, String facilityTypeId, Long communityId);
	
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
	 * @Param [id,communityId]
	 **/
	void syncFaceData(Long id, Long communityId);
	
	/**
	* @Description: 设备数据同步后处理
	 * @Param: [resultCode,facilityId,communityId,msg]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/23
	**/
	void dealDataBysyncResult(Integer resultCode, Long facilityId, Long communityId, String msg);
	
	/**
	* @Description: 分页查询数据同步记录 和 成功失败数统计
	 * @Param: [baseQO]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/6/24
	**/
	Map<String,Object> querySyncRecordPage(BaseQO<Long> baseQO);
}
