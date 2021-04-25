package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.PageInfo;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lihao
 * @since 2021-03-13
 */
public interface IFacilityService extends IService<FacilityEntity> {
	
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
	 * @Param [communityId]
	 **/
	Map<String, Integer> getCount(Long typeId);
	
	/**
	 * @return void
	 * @Author 91李寻欢
	 * @Description 刷新设备
	 * @Date 2021/4/23 18:11
	 * @Param [page, size]
	 **/
	void flushFacility(Integer page, Integer size);
}
