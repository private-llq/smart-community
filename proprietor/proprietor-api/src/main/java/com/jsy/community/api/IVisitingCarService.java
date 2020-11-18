package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitingCarEntity;

import java.util.List;

/**
 * <p>
 * 来访车辆 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-12
 */
public interface IVisitingCarService extends IService<VisitingCarEntity> {
	
	/**
	* @Description: 根据关联的访客表ID 列表查询
	 * @Param: [visitorid]
	 * @Return: java.util.List<com.jsy.community.entity.VisitingCarEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	**/
	 List<VisitingCarEntity> queryCarList(Long visitorid);
	 
}
