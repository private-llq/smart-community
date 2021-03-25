package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2021-03-13
 */
@DubboService(version = Const.version, group = Const.group_property)
public class FacilityServiceImpl extends ServiceImpl<FacilityMapper, FacilityEntity> implements IFacilityService {
	
	@Autowired
	private FacilityMapper facilityMapper;
	
	@Override
	public void addFacility(FacilityEntity facilityEntity) {
		// 添加设备信息
		facilityEntity.setId(SnowFlake.nextId());
		// TODO: 2021/3/13 先写死
		facilityEntity.setPersonId(520L);
		facilityEntity.setCreatePerson("黄其云");
		facilityMapper.insert(facilityEntity);
	}
	
	@Override
	public List<FacilityEntity> listFacility(FacilityQO facilityQO) {
		return facilityMapper.listFacility(facilityQO);
	}
	
	@Override
	public void deleteFacility(Long id) {
		facilityMapper.deleteById(id);
	}
}
