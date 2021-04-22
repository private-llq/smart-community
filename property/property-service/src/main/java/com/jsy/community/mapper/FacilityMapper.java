package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2021-03-13
 */
public interface FacilityMapper extends BaseMapper<FacilityEntity> {
	
	
	List<FacilityEntity> listFacility(@Param("qo") FacilityQO facilityQO, PageInfo<FacilityEntity> info);
	
	void insertFacilityStatus(@Param("id")long id,@Param("status") Integer status,@Param("facilityHandle") Integer facilityHandle,@Param("facilityId") Long facilityId);
	
	@Select("select facility_handle from t_facility_status where facility_id = #{id}")
	int selectFacilityHandle(Long id);
	
	@Select("select status from t_facility_status where facility_id = #{id}")
	int getStatus(Long id);
}
