package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 楼栋房间
 * @since 2020-12-16 13:41
 **/
public interface HouseMapper extends BaseMapper<HouseEntity> {
	
	/**
	* @Description: 查询房间
	 * @Param: [list]
	 * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	List<HouseEntity> queryHouses(Collection<Long> list);
	
	/**
	* @Description: 查找父节点
	 * @Param: [tempEntity]
	 * @Return: com.jsy.community.entity.HouseEntity
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	@Select("select pid,id as buildingId,#{tempEntity.id} as id,#{tempEntity.communityId} as communityId,#{tempEntity.address} as address from t_house where id = #{tempEntity.pid}")
	HouseEntity getParent(@Param("tempEntity") HouseEntity tempEntity);
	
	/**
	* @Description: ids批量查询房屋
	 * @Param: [list]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@MapKey("id")
	Map<String,Map<String,Object>> queryHouseByIdBatch(Collection<Long> list);

	@MapKey("id")
	Map<String,Map<String,Object>> getRoomMap(Collection ids);
	
}
