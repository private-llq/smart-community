package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * 大后台房屋信息Service
 * @author DKS
 * @since 2021-10-21
 */
public interface IHouseService extends IService<HouseEntity> {
	
	/**
	* @Description: 查询(单元/楼层/房间等)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/11
	**/
	PageInfo<HouseEntity> queryHouse(BaseQO<HouseQO> baseQO);
	
	/**
	 * @Description: 查询楼栋、单元、房屋导出数据
	 * @Param: HouseEntity
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
	 * @Author: DKS
	 * @Date: 2021/8/9
	 **/
	List<HouseEntity> queryExportHouseExcel(HouseEntity houseEntity);
}
