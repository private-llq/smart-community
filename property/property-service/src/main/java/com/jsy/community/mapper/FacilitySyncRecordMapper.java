package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.hk.FacilitySyncRecordEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 设备数据同步状态Mapper
 * @since 2021-06-23 10:03
 **/
public interface FacilitySyncRecordMapper extends BaseMapper<FacilitySyncRecordEntity> {
	
	/**
	* @Description: 统计成功失败数
	 * @Param: [communityId]
	 * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/6/24
	**/
	@Select("select 'successCount' as type, count(1) as amount from t_facility_sync_record where community_id = #{communityId} and is_success = 0\n" +
		"union all\n" +
		"select 'failCount' as type, count(1) as amount from t_facility_sync_record where community_id = #{communityId} and is_success = 1")
	List<Map<String,Object>> countSuccessAndFail(Long communityId);
	
}
