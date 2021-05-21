package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.vo.hk.FacilityTypeVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lihao
 * @since 2021-03-12
 */
public interface IFacilityTypeService extends IService<FacilityTypeEntity> {
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加设备类别
	 * @Date 2021/3/12 13:22
	 * @Param []
	 **/
	void addFacilityType(FacilityTypeEntity facilityTypeEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 修改设备类别
	 * @Date 2021/3/12 13:55
	 * @Param [facilityTypeEntity]
	 **/
	void updateFacilityType(FacilityTypeEntity facilityTypeEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 删除设备类别
	 * @Date 2021/3/12 13:58
	 * @Param [id]
	 **/
	void deleteFacilityType(Long id, Long communityId);
	
	/**
	 * @return java.util.List<com.jsy.community.vo.hk.FacilityTypeVO>
	 * @Author lihao
	 * @Description 树形结构查询设备分类
	 * @Date 2021/3/12 15:52
	 * @Param [communityId]
	 **/
	List<FacilityTypeVO> listFacilityType(Long communityId);
	
	/**
	 * @return com.jsy.community.entity.hk.FacilityTypeEntity
	 * @Author lihao
	 * @Description 根据id查询设备分类信息
	 * @Date 2021/3/22 16:12
	 * @Param [id]
	 **/
	FacilityTypeEntity getFacilityType(Long id);
}
