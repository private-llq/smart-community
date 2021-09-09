package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.vo.FeeRelevanceTypeVo;
import com.jsy.community.vo.property.ProprietorVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 物业端社区楼栋 Mapper 接口
 * @author jsy
 * @since 2020-11-20
 */
public interface HouseMapper extends BaseMapper<HouseEntity> {
	
	/**
	* @Description: 查询下级house
	 * @Param: [list]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	List<Long> getSubIdList(List<Long> list);
	
//	/**
//	* @Description: 新增次级楼宇信息
//	 * @Param: [houseEntity]
//	 * @Return: int
//	 * @Author: chq459799974
//	 * @Date: 2021/1/21
//	**/
//	int addSub(@Param("houseEntity") HouseEntity houseEntity);

	/**
	 * 按社区ID获取 社区名称和 当前社区住户房间数量
	 * @param communityId 		社区id
	 * @author YuLF
	 * @since  2020/12/3 11:06
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
	 * 通过社区ID查出所有 楼栋、单元、楼层、未被登记的门牌
	 * @param communityId 	社区id
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @return				返回该社区所有的房屋编号
	 */
    List<HouseEntity> getCommunityHouseNumber(@Param("communityId") long communityId);

    /**
	 *@Author: Pipi
	 *@Description: 通过社区ID查询出所有楼栋和单元
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 *@Date: 2021/5/19 15:35
	 **/
	List<HouseEntity> getBuildingAndUnitList(@Param("communityId") Long communityId);
	
	/**
	 *@Author: DKS
	 *@Description: 通过社区ID查询出所有楼栋
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 *@Date: 2021/8/10 11:35
	 **/
	List<HouseEntity> getBuildingList(@Param("communityId") Long communityId);

    //============================================ 物业端产品原型确定后新加的 开始  ===========================================================
	/**
	* @Description: 单元绑定楼栋
	 * @Param: [unitIdList, buildingId]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/8
	**/
	int unitBindBuilding(@Param("list") List<Long> unitIdList,@Param("entity") HouseEntity houseEntity);
	
	/**
	 * @Description: 更新单元数据
	 * @Param: [houseEntity]
	 * @Return: int
	 * @Author: DKS
	 * @Date: 2021/8/6
	 **/
	int unitBindBuildingUpdate(@Param("entity") HouseEntity houseEntity);
	
	/**
	* @Description: 单元解绑楼栋
	 * @Param: [id]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/3/11
	**/
	@Update("update t_house set pid = 0,building = '' where pid = #{id} and type = 2")
	void unitUnBindBuilding(Long id);
	
	/**
	* @Description: 添加房屋
	 * @Param: [houseEntity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/9
	**/
	int addRoom(@Param("houseEntity") HouseEntity houseEntity);
	
	/**
	* @Description: 根据任意字段查询楼栋、房屋
	 * @Param: [field, param]
	 * @Return: com.jsy.community.entity.HouseEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/11
	**/
	@Select("select * from t_house where ${field} = #{param}")
	HouseEntity queryHouseByAnyProperty(@Param("field")Object field, @Param("param")Object param);
	
	/**
	* @Description: 修改子节点
	 * @Param: [list, entity]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/3/13
	**/
	void updateSub(@Param("list")List list, @Param("entity")HouseEntity entity);
	
	/**
	* @Description: 修改楼栋、单元、房屋
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/9
	**/
	int updateHouse(@Param("entity") HouseEntity entity);
	
	/**
	* @Description: 批量查询楼栋已绑定单元数
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Long,java.util.Map<java.lang.String,java.lang.Long>>
	 * @Author: chq459799974
	 * @Date: 2021/3/13
	**/
	@MapKey("pid")
	Map<Long,Map<String,Long>> queryBindUnitCountBatch(@Param("list")List<Long> ids);
	
	/**
	* @Description: 查询楼栋已绑定单元id列表
	 * @Param: [id]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/19
	**/
	@Select("select id from t_house where pid = #{id} and type = 2")
	List<Long> queryBindUnitList(Long id);
	
	/**
	 * @Description: 查询单元已绑定房屋列表
	 * @Param: [id]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: DKS
	 * @Date: 2021/8/12
	 **/
	@Select("select * from t_house where pid = #{id} and type = 4")
	List<HouseEntity> queryBindDoorList(Long id);
	
	/**
	* @Description: 验证是否有房屋关联数据
	 * @Param: [id]
	 * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/3/15
	**/
	@Select("select 'proprietor' as item,count(0) as count from t_proprietor where house_id = #{id}\n" +
		"union all\n" +
		"select 'userHouse' as item,count(0) as count from t_user_house where house_id = #{id}\n" +
		"union all\n" +
		"select 'houseMember' as item,count(0) as count from t_house_member where house_id = #{id}\n" +
		"union all\n" +
		"select 'houseLease' as item,count(0) as count from t_house_lease where house_id = #{id}")
	List<Map<String,Object>> verifyRelevance(Long id);


	//============================================ 物业端产品原型确定后新加的 结束  ===========================================================
	/**
	* @Description: 房屋ids 批量查 id-address对应关系
	 * @Param: [houseIds]
	 * @Return: java.util.Map<java.lang.Long,com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	@MapKey("houseId")
	Map<Long,HouseEntity> queryIdAndHouseMap(Collection<Long> houseIds);


	/**
	 * @Description: 查询小区下所有房间
	 * @author: Hu
	 * @since: 2021/4/24 14:47
	 * @Param:
	 * @return:
	 */
	List<HouseEntity> selectHouseAll(Long communityId);

	/**
	 *@Author: Pipi
	 *@Description: 查询小区下所有的房间
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 *@Date: 2021/5/21 17:14
	 **/
	List<HouseEntity> getAllHouse(Long communityId);

	/**
	 * @Author: Pipi
	 * @Description: 批量新增房屋数据
	 * @Param: houseEntityList:
	 * @Return: java.lang.Integer
	 * @Date: 2021/5/21 16:48
	 **/
	Integer saveHouseBatch(@Param("list") List<HouseEntity> houseEntityList);
	
	/**
	 * @Author: DKS
	 * @Description: 批量新增楼栋数据
	 * @Param: houseEntityList:
	 * @Return: java.lang.Integer
	 * @Date: 2021/8/10 14:00
	 **/
	Integer saveBuildingBatch(@Param("list") List<HouseEntity> houseEntityList);

	/**
	 * @Description: 传入id数组查询房间
	 * @author: Hu
	 * @since: 2021/8/5 16:11
	 * @Param:
	 * @return:
	 */
	List<HouseEntity> selectInIds(@Param("split") List split);

	/**
	 * @Description: 查询认证的房间数据
	 * @author: Hu
	 * @since: 2021/8/5 16:48
	 * @Param:
	 * @return:
	 */
	List<HouseEntity> selectUserHouseAuth(@Param("split") List<String> split);
	
	/**
	 * @Description: 查询住户数量
	 * @author: DKS
	 * @since: 2021/8/6 16:38
	 * @Param:
	 * @return:
	 */
	@MapKey("houseId")
	Map<Long,Map<String,Long>> selectHouseNumberCount(@Param("list") Collection<Long> houseIds);
	
	/**
	 * @Description: 查询小区下所有楼栋、单元、房屋
	 * @author: DKS
	 * @since: 2021/8/13 14:08
	 * @Param: communityId
	 * @return: java.util.List<com.jsy.community.entity.HouseEntity>
	 */
	List<HouseEntity> selectAllBuildingUnitDoor(Long communityId);
	
	
	/**
	 * @Description: 查询communityIds下所有房屋数量
	 * @author: DKS
	 * @since: 2021/8/25 14:36
	 * @Param: communityIdList
	 * @return: Integer
	 */
	Integer selectAllHouseByCommunityIds(@Param("list") List<Long> communityIdList);

	/**
	 * @Description: 查询小区下所有认证过的房屋
	 * @author: Hu
	 * @since: 2021/9/7 11:01
	 * @Param:
	 * @return:
	 */
	@Select("select th.id,concat(th.building,th.unit,th.door) as name from t_house th join t_user_house tuh on th.id=tuh.house_id where th.deleted=0 and tuh.deleted=0 and th.community_id=#{communityId}")
	List<FeeRelevanceTypeVo> getUserHouse(@Param("communityId") Long communityId);

	/**
	 * @Description: 查询所有房间包括房间没有业主认证的
	 * @author: Hu
	 * @since: 2021/9/8 14:41
	 * @Param:
	 * @return:
	 */
	List<HouseEntity> getHouseAll(Long communityId);
}
