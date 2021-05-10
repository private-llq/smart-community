package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.mapper.FacilityTypeMapper;
import com.jsy.community.mapper.UserHouseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.util.facility.FacilityUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
@Slf4j
public class FacilityServiceImpl extends ServiceImpl<FacilityMapper, FacilityEntity> implements IFacilityService {
	
	@Autowired
	private FacilityMapper facilityMapper;
	
	@Autowired
	private FacilityTypeMapper facilityTypeMapper;
	
	@Autowired
	private UserHouseMapper houseMapper;
	
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
		Short port = facilityEntity.getPort();
		
		// 2. 保存设备基本信息
		// 有可能不同类型的设备会有相同的ip，所以这里没有用唯一索引
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("ip", ip).eq("facility_type_id", facilityEntity.getFacilityTypeId());
		FacilityEntity one = facilityMapper.selectOne(wrapper);
		if (one != null) {
			throw new PropertyException("您已添加相同ip的设备");
		}
		
		facilityEntity.setId(SnowFlake.nextId());
		facilityEntity.setIsConnectData(0);
		facilityMapper.insert(facilityEntity);
		
		// 登录设备
		Map<String, Integer> map = FacilityUtils.login(ip, port, username, password, -1);
		
		// 根据设备的作用id，执行不同的作用[目前有人脸比对，车牌抓拍]
		Long facilityEffectId = facilityEntity.getFacilityEffectId();
		Integer loginStatus = map.get("status");
		Integer handle = map.get("facilityHandle");
		int facilityAlarmHandle = FacilityUtils.toEffect(loginStatus, handle, facilityEffectId);
		
		// 3. 保存设备状态信息
		Integer status = map.get("status");
		Integer facilityHandle = map.get("facilityHandle");
		Long facilityId = facilityEntity.getId();
		long id = SnowFlake.nextId();
		facilityMapper.insertFacilityStatus(id, status, facilityHandle, facilityId, facilityAlarmHandle);
	}
	
	
	@Override
	public PageInfo<FacilityEntity> listFacility(BaseQO<FacilityQO> facilityQO) {
		Page<FacilityEntity> info = new Page<>(facilityQO.getPage(), facilityQO.getSize());
		FacilityQO qo = facilityQO.getQuery();
		List<FacilityEntity> facilityEntityList = facilityMapper.listFacility(qo, info);
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
		BeanUtils.copyProperties(info, pageInfo);
		pageInfo.setRecords(facilityEntityList);
		return pageInfo;
	}
	
	@Override
	public void deleteFacility(Long id) {
		// 根据设备id查询他的唯一布防句柄
		int alarmHandle = facilityMapper.getAlarmHandle(id);
		
		// 根据设备id查询他的唯一登录句柄
		int loginHandle = facilityMapper.getLoginHandle(id);
		
		// TODO: 2021/4/23 海康那个栽舅子客服给我说的必须撤防和注销设备。最后程序关闭的时候释放SDK
		if (alarmHandle==-1) { // 说明根本就没布防或者说他没布防成功，就没必要去撤防
			// 撤防
			FacilityUtils.cancel(alarmHandle);
		}
		if (loginHandle==-1) {// 说明根本就没登录或者说他没登录成功，就没必要去注销
			// 注销设备
			FacilityUtils.logOut(loginHandle);
		}
		
		facilityMapper.deleteById(id);
		// 删除设备状态信息
		facilityMapper.deleteMiddleFacility(id);
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFacility(FacilityEntity facilityEntity) {
		// 判断此次修改是否没有修改ip，账号，密码，端口号。若没有修改就只更新基本信息即可
		Long facilityId = facilityEntity.getId();
		FacilityEntity facility = facilityMapper.selectById(facilityId);
		if (facility==null) {
			throw new PropertyException("请选择你要更改的设备,或该设备不存在");
		}
		// ip，账号，密码，端口号 都没有发生改变
		if (facility.getIp().equals(facilityEntity.getIp()) &&
			facility.getUsername().equals(facilityEntity.getUsername()) &&
			facility.getPassword().equals(facilityEntity.getPassword()) &&
			facility.getPort().equals(facilityEntity.getPort())) {
			
			// 判断这次更改输入的 ip是否有
			
			facilityMapper.updateById(facilityEntity);
		} else {
			//ip，账号，密码，端口号 有至少一个发生了改变     so，需要重新登录设备
			String ip = facilityEntity.getIp();
			String username = facilityEntity.getUsername();
			String password = facilityEntity.getPassword();
			Short port = facilityEntity.getPort();
			
			// 1. 将原来的设备撤防
			int alarmHandle = facilityMapper.getAlarmHandle(facilityEntity.getId());
			FacilityUtils.cancel(alarmHandle);
			// 2. 将原来的设备注销
			int loginHandle = facilityMapper.getLoginHandle(facilityEntity.getId());
			FacilityUtils.logOut(loginHandle);
			
			// 3. 登录设备
			Map<String, Integer> login = FacilityUtils.login(ip, port, username, password, -1);
			Integer status = login.get("status");
			Integer handle = login.get("facilityHandle");
			// 4. 布防设备
			// todo 注意看这里的设备作用来源，更新的时候目前做的是不准她选择作用，现在设备作用来源是原本这个设备的作用来源
			// TODO: 2021/4/27 如果后面修改可以更新设备作用的话，facility.getFacilityEffectId() 应该由前端传来
			int facilityAlarmHandle = FacilityUtils.toEffect(status, handle, facility.getFacilityEffectId());
			
			
			// 更新设备表
			facilityMapper.updateById(facilityEntity);
			// 更新状态表[这里采用的不是更新表，而是直接删除原本条数据，重新新增数据]
			facilityMapper.deleteMiddleFacility(facilityId);
			long id = SnowFlake.nextId();
			facilityMapper.insertFacilityStatus(id,status,handle,facilityId,facilityAlarmHandle);
		}
	}
	
	@Override
	public Map<String, Integer> getCount(Long typeId) {
		// 根据设备分类id查询其下设备的id集合
		List<Long> facilityIds = facilityMapper.getFacilityIdByTypeId(typeId);
		
		int onlineCount = 0;
		int failCount = 0;
		// 根据设备id查询设备状态表的状态
		for (Long facilityId : facilityIds) {
			int status = facilityMapper.getStatus(facilityId);
			if (status == 0) {
				onlineCount += 1;
			} else {
				failCount += 1;
			}
		}
		HashMap<String, Integer> map = new HashMap<>();
		map.put("onlineCount",onlineCount);
		map.put("failCount",failCount);
		return map;
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void flushFacility(Integer page, Integer size,String facilityTypeId) {
		//1. 获取当前页的数据
		Page<FacilityEntity> entityPage = new Page<>(page, size);
		
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("facility_type_id",facilityTypeId);
		Page<FacilityEntity> facilityEntityPage = facilityMapper.selectPage(entityPage, wrapper);
		List<FacilityEntity> list = facilityEntityPage.getRecords();
		
		for (FacilityEntity facilityEntity : list) {
			// 获取设备的唯一用户句柄
			Long id = facilityEntity.getId();
			int handle = facilityMapper.selectFacilityHandle(id);
			
			// 更新该句柄的设备在线状态
			int online = FacilityUtils.isOnline(handle);
			facilityMapper.updateStatusByFacilityId(online,facilityEntity.getId());
		}
		
	}
	
	@Override
	public FacilityEntity listByIp(String ip) {
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("ip",ip);
		return facilityMapper.selectOne(wrapper);
	}
	
	@Override
	public void connectData(Long id,Long communityId) {
		//判断是否是一个全新的摄像机
		//不是
		
		//是
		//1. 查询该社区的所有已认证的用户数据
//		houseMapper.listAuthUser
	}
}
