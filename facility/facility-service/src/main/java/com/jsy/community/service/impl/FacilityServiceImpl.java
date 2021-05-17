package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.api.FacilityException;
import com.jsy.community.callback.FDSearch;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.mapper.FacilityTypeMapper;
import com.jsy.community.mapper.UserHouseMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.util.facility.FacilityUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
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
@DubboService(version = Const.version, group = Const.group_facility)
@Slf4j
public class FacilityServiceImpl extends ServiceImpl<FacilityMapper, FacilityEntity> implements IFacilityService {
	
	@Autowired
	private FacilityMapper facilityMapper;
	
	@Autowired
	private FacilityTypeMapper facilityTypeMapper;
	
	@Autowired
	private UserHouseMapper houseMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	@Transactional
	public void addFacility(FacilityEntity facilityEntity) {
		// 1. 保存设备基本信息
		// 设备ip地址
		String ip = facilityEntity.getIp();
		// 设备账号
		String username = facilityEntity.getUsername();
		// 设备密码
		String password = facilityEntity.getPassword();
		// 端口号
		Short port = facilityEntity.getPort();
		
		// 有可能不同类型的设备会有相同的ip，所以这里表中对ip地址没有用唯一索引
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("ip", ip).eq("facility_type_id", facilityEntity.getFacilityTypeId());
		FacilityEntity one = facilityMapper.selectOne(wrapper);
		if (one != null) {
			throw new FacilityException("您已添加相同ip的设备");
		}
		
		facilityEntity.setId(SnowFlake.nextId());
		facilityEntity.setIsConnectData(0);
		facilityMapper.insert(facilityEntity);
		
		// 2. 登录设备
		Map<String, Integer> map = FacilityUtils.login(ip, port, username, password, -1);
		
		// 3. 开启设备
		// 根据设备的作用id，执行不同的作用[目前的需求有人脸比对，车牌抓拍]
		Long facilityEffectId = facilityEntity.getFacilityEffectId();
		Integer loginStatus = map.get("status");
		Integer handle = map.get("facilityHandle");
		int facilityAlarmHandle = FacilityUtils.toEffect(loginStatus, handle, facilityEffectId);
		
		// 4. 保存设备状态信息
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
		
		if (alarmHandle != -1) {
			// 撤防
			FacilityUtils.cancel(alarmHandle);
		}
		if (loginHandle != -1) {
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
		if (facility == null) {
			throw new FacilityException("你选择的设备不存在");
		}
		// ip，账号，密码，端口号 都没有发生改变  只更新一些不重要的信息
		if (facility.getIp().equals(facilityEntity.getIp()) &&
			facility.getUsername().equals(facilityEntity.getUsername()) &&
			facility.getPassword().equals(facilityEntity.getPassword()) &&
			facility.getPort().equals(facilityEntity.getPort())) {
			facilityMapper.updateById(facilityEntity);
		} else {
			if (!facility.getFacilityEffectId().equals(facilityEntity.getFacilityEffectId())) {
				throw new FacilityException("不可以改变设备作用功能");
			}
			//ip，账号，密码，端口号 有至少一个发生了改变    ——>需要重新登录设备，开启功能
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
			int facilityAlarmHandle = FacilityUtils.toEffect(status, handle, facility.getFacilityEffectId());
			
			
			// 更新设备信息表
			facilityMapper.updateById(facilityEntity);
			// 更新设备状态表[这里采用的不是更新表，而是直接删除原本条数据，重新新增数据]
			facilityMapper.deleteMiddleFacility(facilityId);
			long id = SnowFlake.nextId();
			facilityMapper.insertFacilityStatus(id, status, handle, facilityId, facilityAlarmHandle);
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
		map.put("onlineCount", onlineCount);
		map.put("failCount", failCount);
		return map;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void flushFacility(Integer page, Integer size, String facilityTypeId) {
		//1. 获取当前页的数据
		Page<FacilityEntity> entityPage = new Page<>(page, size);
		
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("facility_type_id", facilityTypeId);
		Page<FacilityEntity> facilityEntityPage = facilityMapper.selectPage(entityPage, wrapper);
		List<FacilityEntity> list = facilityEntityPage.getRecords();
		
		for (FacilityEntity facilityEntity : list) {
			// 获取设备的唯一用户句柄
			Long id = facilityEntity.getId();
			int handle = facilityMapper.selectFacilityHandle(id);
			
			// 更新该句柄的设备在线状态
			int online = FacilityUtils.isOnline(handle);
			facilityMapper.updateStatusByFacilityId(online, facilityEntity.getId());
		}
		
	}
	
	@Override
	public FacilityEntity listByIp(String ip) {
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("ip", ip);
		return facilityMapper.selectOne(wrapper);
	}
	
	@Override
	public void connectData(Long id, Long communityId) {
		//0. 查询该设备的用户句柄
		int handle = facilityMapper.selectFacilityHandle(id);
		
		//判断是否是一个全新的摄像机
		FDSearch fdSearch = new FDSearch();
		boolean flag = fdSearch.getFaceLibSpace(handle);
		if (!flag) {
			log.info("不是一个全新的摄像机");
			//不是
			//1. 查询redis里面
			
			
			
		} else {
			//是
			//1. 查询该社区的所有已认证的用户id
			List<String> ids = houseMapper.listAuthUserId(communityId);
			//2. 批量查询已认证的用户数据
			List<UserEntity> userList = userMapper.listAuthUserInfo(ids);
			//2. 上传数据
			for (UserEntity user : userList) {
				try {
					log.info("开始上传人脸");
					if (StringUtils.isEmpty(user.getFaceUrl())) {
						continue;
					}
					FacilityUtils.uploadFaceLibrary(handle, "1", user);
				} catch (IOException e) {
					e.printStackTrace();
					log.info("上传人脸失败");
				}
			}
		}
	}
}
