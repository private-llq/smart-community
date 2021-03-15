package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社区楼栋 Mapper 接口
 * </p>
 *
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
	 * 通过社区ID查出所有 楼栋、单元、楼层、未被登记的门牌
	 * @author YuLF
	 * @since  2020/11/26 9:38
	 * @Param  communityId	社区ID
	 */
    List<HouseEntity> getCommunityArchitecture(@Param("communityId") long communityId);
    
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
}
