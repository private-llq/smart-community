package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitingCar;

import java.util.List;

/**
 * <p>
 * 来访车辆 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-12
 */
public interface IVisitingCarService extends IService<VisitingCar> {
	
	/**
	* @Description: 根据关联的访客表ID 列表查询
	 * @Param: [visitorid]
	 * @Return: java.util.List<com.jsy.community.entity.VisitingCar>
	 * @Author: chq459799974
	 * @Date: 2020/11/12
	**/
	 List<VisitingCar> queryCarList(Long visitorid);
	 
}
