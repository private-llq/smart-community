package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseLeaseConstEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋租售 Mapper 接口
 * @author YuLF
 * @since 2020-12-10
 */
public interface HouseConstMapper extends BaseMapper<HouseLeaseConstEntity> {

    /**
     * 从t_house_const获取所有房屋常量
     */
    @Select("select id,house_const_code,house_const_name,house_const_value,house_const_type,annotation from t_house_const")
    List<HouseLeaseConstEntity> getAllHouseConstForDatabases();
	
	
	/**
	 * 根据常量类型从t_house_const获取所有房屋常量id
	 */
	@Select("select id from t_house_const where house_const_type = #{i}")
	List<Long> getConstIdByType(Integer i);
}
