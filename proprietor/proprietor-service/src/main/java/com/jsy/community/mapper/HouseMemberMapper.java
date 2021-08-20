package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseMemberEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 房间成员表 Mapper 接口
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-23
 */
public interface HouseMemberMapper extends BaseMapper<HouseMemberEntity> {

//	@Update("update t_house_member set is_confirm = 1 where id = #{id}")
//	int confirmJoin(@Param("id")Long id);
	
	/**
	* @Description: 房间ID 查 成员List
	 * @Param: [houseId]
	 * @Return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@Select("select id,householder_id,community_id,house_id,name,sex,mobile,identification_type,id_card from t_house_member where house_id = #{houseId} and person_type = 1")
	List<HouseMemberEntity> queryByHouseId(@Param("houseId") Long houseId);
	
	/**
	* @Description: ids批量查询
	 * @Param: [list]
	 * @Return: java.util.Map<java.lang.Long,com.jsy.community.entity.HouseMemberEntity>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@MapKey("id")
	Map<Long,HouseMemberEntity> queryByIdsBatch(Collection<Long> list);


	/**
	 * @Description: 批量新增
	 * @author: Hu
	 * @since: 2021/8/18 9:23
	 * @Param:
	 * @return:
	 */
    void saveBatch(@Param("save") LinkedList<HouseMemberEntity> save);

}
