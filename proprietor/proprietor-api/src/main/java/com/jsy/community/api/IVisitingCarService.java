package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.VisitingCarEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitingCarQO;

import java.util.List;

/**
 * <p>
 * 来访车辆 服务类
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-12
 */
public interface IVisitingCarService extends IService<VisitingCarEntity> {
	
	/**
	 * @Description: 添加随行车辆
	 * @Param: [visitingCarEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	boolean addVisitingCar(VisitingCarEntity visitingCarEntity);
	
	/**
	 * @Description: 修改随行车辆
	 * @Param: [visitingCarQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	boolean updateVisitingCarById(VisitingCarQO visitingCarQO);
	
	/**
	 * @Description: 删除随行车辆
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	boolean deleteVisitingCarById(Long id);
	
	/**
	* @Description: 随行车辆 分页查询
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitingCarEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	**/
	Page<VisitingCarEntity> queryVisitingCarPage(BaseQO<String> baseQO);
	 
}
