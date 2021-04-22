package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.mapper.FacilityTypeMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.util.facility.FacilityUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
	
	@Autowired
	private FacilityTypeMapper facilityTypeMapper;
	
	@Override
	@Transactional
	public void addFacility(FacilityEntity facilityEntity){
		// 1. 开启设备
		// 设备ip地址
		String ip = facilityEntity.getIp();
		// 设备账号
		String username = facilityEntity.getUsername();
		// 设备密码
		String password = facilityEntity.getPassword();
		// 端口号
		Short port = facilityEntity.getPort();

		// 登录设备
		Map<String, Integer> map  = FacilityUtils.login(ip, port, username, password, -1);
		
		// 2. 保存设备基本信息
		facilityEntity.setId(SnowFlake.nextId());
		facilityMapper.insert(facilityEntity);
		
		// 3. 保存设备状态信息
		Integer status = map.get("status");
		Integer facilityHandle = map.get("facilityHandle");
		Long facilityId = facilityEntity.getId();
		long id = SnowFlake.nextId();
		facilityMapper.insertFacilityStatus(id,status,facilityHandle,facilityId);
	}
	
	@Override
	public PageInfo<FacilityEntity> listFacility(BaseQO<FacilityQO> facilityQO) {
		PageInfo<FacilityEntity> info = new PageInfo<>(facilityQO.getPage(), facilityQO.getSize());
		FacilityQO qo = facilityQO.getQuery();
		List<FacilityEntity> facilityEntityList = facilityMapper.listFacility(qo,info);
		for (FacilityEntity facilityEntity : facilityEntityList) {
			
			// 根据id判断其在线状态
			Long id = facilityEntity.getId();
			int status = facilityMapper.getStatus(id);
			facilityEntity.setStatus(status);
			
			// 根据设备分类id查询设备分类名称
			Long facilityTypeId = facilityEntity.getFacilityTypeId();
			FacilityTypeEntity typeEntity = facilityTypeMapper.selectById(facilityTypeId);
			facilityEntity.setFacilityTypeName(typeEntity.getName());
		}
		PageInfo<FacilityEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(info,pageInfo);
		pageInfo.setRecords(facilityEntityList);
		return pageInfo;
	}
	
	@Override
	public void deleteFacility(Long id) {
		// 1. 撤防
		
		
		facilityMapper.deleteById(id);
	}
}
