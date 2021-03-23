package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.hk.FacilityQO;

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
	
	
	List<FacilityEntity> listFacility(FacilityQO facilityQO);
}
