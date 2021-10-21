package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 物业端社区楼栋 Mapper 接口
 * @author jsy
 * @since 2020-11-20
 */
@Mapper
public interface HouseMapper extends BaseMapper<HouseEntity> {
	
	/**
	* @Description: 查询下级house
	 * @Param: [list]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	List<Long> getSubIdList(List<Long> list);
	
	/**
	* @Description: 批量查询楼栋已绑定单元数
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Long,java.util.Map<java.lang.String,java.lang.Long>>
	 * @Author: chq459799974
	 * @Date: 2021/3/13
	**/
	@MapKey("pid")
	Map<Long,Map<String,Long>> queryBindUnitCountBatch(@Param("list") List<Long> ids);
	
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
}
