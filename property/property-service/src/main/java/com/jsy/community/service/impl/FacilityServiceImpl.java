package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.util.facility.LoginFacility;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional
	public void addFacility(FacilityEntity facilityEntity) {
		// 1. 开启设备
		// 设备ip地址
		String ip = facilityEntity.getIp();
		// 设备账号
		String username = facilityEntity.getUsername();
		// 设备密码
		String password = facilityEntity.getPassword();
		// 端口号
		String port = facilityEntity.getPort();
		
		// 登录账号
		LoginFacility loginFacility = new LoginFacility();
		
		
		
		// 2. 保存设备基本信息
		
		// 3. 保存设备状态信息
		facilityMapper.insert(facilityEntity);
	}
	
	@Override
	public List<FacilityEntity> listFacility(FacilityQO facilityQO) {
		// 获取其状态
		
		
		return facilityMapper.listFacility(facilityQO);
	}
	
	@Override
	public void deleteFacility(Long id) {
		facilityMapper.deleteById(id);
	}
}
