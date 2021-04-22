package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.PageInfo;

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
}
