package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.callback.FMSGCallBack_V31Impl;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.mapper.FacilityTypeMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.sdk.HCNetSDK;
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
	
	// 人脸比对
	private static final Long EFFECT = 41565L;
	
	// 车牌抓拍
	private static final Long EFFECT_CAR = 456L;
	
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
		facilityMapper.insert(facilityEntity);
		
		// 登录设备
		Map<String, Integer> map = FacilityUtils.login(ip, port, username, password, -1, true);
		
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
		// 撤防
		FacilityUtils.cancel(alarmHandle);
		// 注销设备
		FacilityUtils.logOut(loginHandle);
		
		facilityMapper.deleteById(id);
		// 删除设备状态信息
		facilityMapper.deleteMiddleFacility(id);
		
	}

	@Override
	public void updateFacility(FacilityEntity facilityEntity) {
		facilityMapper.updateById(facilityEntity);
		
		Integer status = facilityEntity.getStatus();
		// 更新状态信息
		if (status != null) {
			Long id = facilityEntity.getId();
			facilityMapper.updateMiddleStatus(id, status);
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
	public void flushFacility(Integer page, Integer size) {
		//1. 获取当前页的数据
		Page<FacilityEntity> entityPage = new Page<>(page, size);
		Page<FacilityEntity> facilityEntityPage = facilityMapper.selectPage(entityPage, null);
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
	
	
	public static void main(String[] args) {
		
		// 加载HK库
		HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
		FMSGCallBack_V31Impl dVRMessageCallBack;
		
		//1. 初始化SDK    TRUE表示成功，FALSE表示失败。                          //////////////  1. 先初始化SDK（只需要调用一次，在程序起来时候调用一次）
		if (!hCNetSDK.NET_DVR_Init()) {
			log.info("初始化SDK失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
		}
		
		int login = 0;
		boolean b = hCNetSDK.NET_DVR_RemoteControl(login, 20005, null, 0);
		System.out.println(b);
	}
}
