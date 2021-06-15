package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2021-03-12
 */
public interface FacilityTypeMapper extends BaseMapper<FacilityTypeEntity> {
	
	/**
	* @Description: 查询设备分类名称 批量
	 * @Param: [idSet]
	 * @Return: java.util.Map<java.lang.Long,java.util.Map<java.lang.Long,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/6/11
	**/
	@MapKey("id")
	Map<Long, Map<Long,String>> queryIdAndNameMap(@Param("idSet")Set<Long> idSet);
}
