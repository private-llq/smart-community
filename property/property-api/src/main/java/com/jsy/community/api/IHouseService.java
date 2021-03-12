package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社区楼栋 服务类
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-20
 */
public interface IHouseService extends IService<HouseEntity> {
	
//	/**
//	* @Description: 查询子级楼栋(单元/楼层/房间等)
//	 * @Param: [baseQO]
//	 * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.HouseEntity>
//	 * @Author: chq459799974
//	 * @Date: 2020/11/20
//	**/
//	PageInfo<HouseEntity> queryHousePage(BaseQO<HouseQO> baseQO);
	
//	/**
//	* @Description: 删除楼栋(单元/楼层/房间等)
//	 * @Param: [id]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2020/11/20
//	**/
//	boolean deleteHouse(Long id);
	
	//=========================== 基础增删改查 开始 ==============================
	/**
	* @Description: 新增楼栋(单元/楼层/房间等)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	boolean addHouse(HouseEntity houseEntity);
	
	/**
	* @Description: 修改楼栋(单元/楼层/房间等)
	 * @Param: [houseEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/20
	**/
	boolean updateHouse(HouseEntity houseEntity);
	
	/**
	* @Description: 查询(单元/楼层/房间等)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/11
	**/
	PageInfo<HouseEntity> queryHouse(BaseQO<HouseQO> baseQO);
	//=========================== 基础增删改查 结束 ==============================

	/**
	 * 通过社区ID查出所有 楼栋、单元、楼层、门牌
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @Param  communityId	社区ID
	 */
	List<HouseEntity> getCommunityArchitecture(long communityId);

	/**
	 * 按社区ID获取 社区名称和 当前社区住户房间数量
	 * @author YuLF
	 * @since  2020/12/3 11:06
	 * @Param  communityId   社区id
	 * @return				 返回社区名称和 当前社区住户房间数量
	 */
    Map<String, Object> getCommunityNameAndUserAmountById(long communityId);

	/**
	 * 按社区ID获取 社区名称 社区用户名和社区用户uid
	 * @author YuLF
	 * @since  2020/12/7 11:06
	 * @param communityId 			社区id
	 * @return						返回社区名称和 当前社区所有住户名称，住户uid
	 */
    List<UserEntity> getCommunityNameAndUserInfo(long communityId);
}
