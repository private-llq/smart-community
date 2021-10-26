package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.CarQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * 大后台住户信息Service
 * @author DKS
 * @since 2021-10-22
 */
public interface ICarService extends IService<CarEntity> {
	
	/**
	* @Description: 【住户】条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.CarEntity>
	 * @Author: DKS
	 * @Date: 2021/10/26
	**/
	PageInfo<CarEntity> queryCar(BaseQO<CarQO> baseQO);
	
	/**
	 * @Description: 查询住户
	 * @Param: HouseEntity
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.CarEntity>
	 * @Author: DKS
	 * @Date: 2021/10/26 10:40
	 **/
	List<CarEntity> queryExportCarExcel(CarQO carQO);
}
