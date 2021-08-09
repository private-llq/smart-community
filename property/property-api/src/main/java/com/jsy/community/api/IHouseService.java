package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseBuildingTypeEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseBuildingTypeQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.property.ProprietorVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 物业端社区楼栋Service
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
	
	/**
	* @Description: 删除楼栋/单元/房屋
	 * @Param: [id,communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/15
	**/
	boolean deleteHouse(Long id,Long communityId);
	//=========================== 基础增删改查 结束 ==============================

	/**
	 * @Description: 新增楼宇分类
	 * @Param: [houseBuildingTypeEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	boolean addHouseBuildingType(HouseBuildingTypeEntity houseBuildingTypeEntity);

	/**
	 * @Description: 修改楼宇分类
	 * @Param: [houseBuildingTypeEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	boolean updateHouseBuildingType(HouseBuildingTypeEntity houseBuildingTypeEntity);

	/**
	 * @Description: 删除楼宇分类
	 * @Param: [id,communityId]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	boolean deleteHouseBuildingType(Long id,Long communityId);

	/**
	 * @Description: 查询楼宇分类
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseBuildingTypeEntity>
	 * @Author: DKS
	 * @Date: 2021/08/05
	 **/
	PageInfo<HouseBuildingTypeEntity> queryHouseBuildingType(BaseQO<HouseBuildingTypeQO> baseQO);

	/**
	 * 通过社区ID查出所有 房屋编号
	 *
	 * @param communityId 	社区id
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @return				所有的房屋信息
	 */
	List<HouseEntity> getCommunityHouseNumber(long communityId);

	/**
	 *@Author: Pipi
	 *@Description: 通过社区ID查询出所有楼栋和单元
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 *@Date: 2021/5/19 15:34
	 **/
	List<HouseEntity> getBuildingAndUnitList(long communityId);

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


	/**
	 * 通过社区id 获得 社区内未被登记的房屋
	 * @param communityId 		社区id
	 * @return					返回 该社区未被登记的房屋编号 + house_id
	 */
	List<ProprietorVO> getCommunityHouseById(Long communityId);
	
	/**
	* @Description: 房屋ids 批量查 id-address对应关系
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Long,com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	Map<Long,HouseEntity> queryIdAndHouseMap(Collection<Long> ids);

	/**
	 * @Description: 查询小区下所有房间
	 * @author: Hu
	 * @since: 2021/4/24 14:42
	 * @Param:
	 * @return:
	 */
    List<HouseEntity> selectHouseAll(Long communityId);

    /**
     *@Author: Pipi
     *@Description: excel导入时,批量新增房屋数据
     *@Param: houseEntityList: 
     *@Return: java.lang.Integer
     *@Date: 2021/5/21 14:26
     **/
	Integer saveHouseBatch(List<HouseEntity> houseEntityList, Long communityId, String uid);

	/**
	 *@Author: Pipi
	 *@Description: 查询小区的所有房间
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 *@Date: 2021/5/21 17:17
	 **/
	List<HouseEntity> getAllHouse(Long communityId);
	
	/**
	* @Description: 检查楼栋单元数据真实性
	 * @Param: [buildingId, unitId, communityId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	void checkBuildingAndUnit(Long buildingId, Long unitId, Long communityId);

	/**
	 * @Description: 查询所有房间
	 * @author: Hu
	 * @since: 2021/8/6 16:21
	 * @Param:
	 * @return:
	 */
    List<HouseEntity> selectAll();
	
	/**
	 * @Description: 查询楼栋、单元、房屋导出数据
	 * @Param: HouseEntity
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.HouseEntity>
	 * @Author: DKS
	 * @Date: 2021/8/9
	 **/
	List<HouseEntity> queryExportHouseExcel(HouseEntity houseEntity);
}
