package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;

import java.util.List;

/**
 * <p>
 * 社区楼栋 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-20
 */
public interface IHouseService extends IService<HouseEntity> {
	
	/**
	* @Description: 查询子级楼栋(单元/楼层/房间等)
	 * @Param: [baseQO]
	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	Page<HouseEntity> queryHousePage(BaseQO<HouseQO> baseQO);
	
	/**
	* @Description: 新增楼栋(单元/楼层/房间等)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	boolean addHouse(HouseEntity houseEntity);
	
	/**
	* @Description: 删除楼栋(单元/楼层/房间等)
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	boolean deleteHouse(Long id);
	
	/**
	* @Description: 修改楼栋(单元/楼层/房间等)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	boolean updateHouse(HouseEntity houseEntity);

	/**
	 * 通过社区ID查出所有 楼栋、单元、楼层、门牌
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @Param  communityId	社区ID
	 */
	List<HouseEntity> getCommunityArchitecture(long communityId);

}
