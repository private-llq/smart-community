package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

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
	
}