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
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

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
		
		// 3. 开启设备功能
		// 根据设备的作用id，执行不同的作用功能[目前的需求有人脸比对，车牌抓拍]
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
	public PageInfo<FacilityEntity> listFacility(BaseQO<FacilityQO> baseQO) {
		FacilityQO qo = baseQO.getQuery();
		Page<FacilityEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		Page<FacilityEntity> pageData = facilityMapper.listFacility(qo, page);
		ArrayList<Long> ids = new ArrayList<>();
		Set<Long> typeIds = new HashSet<>();
		for (FacilityEntity facilityEntity : pageData.getRecords()) {
			ids.add(facilityEntity.getId());
			typeIds.add(facilityEntity.getFacilityTypeId());
		}
		//查询和设置设备状态、设备类型名
		Map<Long,Map<Long,Integer>> statusMap = facilityMapper.getStatusBatch(ids);
		Map<Long,Map<Long,String>> typeNameMap = facilityTypeMapper.queryIdAndNameMap(typeIds);
		for (FacilityEntity facilityEntity : pageData.getRecords()) {
			facilityEntity.setStatus(statusMap.get(BigInteger.valueOf(facilityEntity.getId())) == null ? null : statusMap.get(BigInteger.valueOf(facilityEntity.getId())).get("status"));
			facilityEntity.setFacilityTypeName(typeNameMap.get(BigInteger.valueOf(facilityEntity.getFacilityTypeId())) == null ? null : typeNameMap.get(BigInteger.valueOf(facilityEntity.getFacilityTypeId())).get("name"));
		}
		PageInfo<FacilityEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
	
	@Override
	public void deleteFacility(Long id) {
		// 根据设备id查询他的唯一布防句柄
		int alarmHandle = facilityMapper.getAlarmHandle(id);
		
		// 根据设备id查询他的唯一登录句柄
		int loginHandle = facilityMapper.getLoginHandle(id);
		
		// 撤防
		Map<String,Object> cancelResultMap = FacilityUtils.cancel(alarmHandle);
		if(!(Boolean)cancelResultMap.get("result")){
			log.error("摄像头：" + id +" 撤防失败，" + "原因：" +cancelResultMap.get("reason"));
			throw new FacilityException("摄像头撤防失败，删除失败");
		}
		
		// 注销设备
		Map<String,Object> logOutResultMap = FacilityUtils.logOut(loginHandle);
		if(!(Boolean)logOutResultMap.get("result")){
			log.error("摄像头：" + id +" 注销失败，" + "原因：" +logOutResultMap.get("reason"));
			throw new FacilityException("摄像头注销失败，删除失败");
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
			//ip，账号，密码，端口号 有至少一个发生了改变    ——>需要重新登录设备，开启功能
			String ip = facilityEntity.getIp();
			String username = facilityEntity.getUsername();
			String password = facilityEntity.getPassword();
			Short port = facilityEntity.getPort();
			
			// 1. 将原来的设备撤防
			int alarmHandle = facilityMapper.getAlarmHandle(facilityEntity.getId());
			Map<String,Object> cancelResultMap = FacilityUtils.cancel(alarmHandle);
			if(!(Boolean)cancelResultMap.get("result")){
				log.error("摄像头：" + facilityEntity.getId() +" 撤防失败，" + "原因：" +cancelResultMap.get("reason"));
				throw new FacilityException("摄像头撤防失败，修改失败");
			}
			// 2. 将原来的设备注销
			int loginHandle = facilityMapper.getLoginHandle(facilityEntity.getId());
			Map<String,Object> logOutResultMap = FacilityUtils.logOut(loginHandle);
			if(!(Boolean)logOutResultMap.get("result")){
				log.error("摄像头：" + facilityEntity.getId() +" 注销失败，" + "原因：" +logOutResultMap.get("reason"));
				throw new FacilityException("摄像头注销失败，修改失败");
			}
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
	public Map<String, Integer> getCount(Long typeId, Long communityId) {
		// 根据设备分类id查询其下设备的id集合
		List<Long> facilityIds = facilityMapper.getFacilityIdByTypeId(typeId,communityId);
		
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
//	@Transactional(rollbackFor = Exception.class)
	public void flushFacility(Integer page, Integer size, String facilityTypeId) {
		//1. 获取当前页的数据
		Page<FacilityEntity> entityPage = new Page<>(page, size);
		
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("facility_type_id", facilityTypeId);
		//TODO 查询条件带小区id
		Page<FacilityEntity> facilityEntityPage = facilityMapper.selectPage(entityPage, wrapper);
		List<FacilityEntity> list = facilityEntityPage.getRecords();
		
//		for (FacilityEntity facilityEntity : list) {
//			// 获取设备的唯一用户句柄
//			Long id = facilityEntity.getId();
//			int handle = facilityMapper.selectFacilityHandle(id);
//
//			// 更新该句柄的设备在线状态
//			int online = FacilityUtils.isOnline(handle);
//			facilityMapper.updateStatusByFacilityId(online, facilityEntity.getId());
//		}
		
		//组装批量入参id集合
		List<Long> idList = new ArrayList<>();
		for (FacilityEntity facilityEntity : list) {
			idList.add(facilityEntity.getId());
		}
		//批量查询，得到Map<设备ID,Map<设备ID,句柄>>
		Map<Long, Map<Long, Integer>> idAndHandelMap = facilityMapper.selectFacilityHandleBatch(idList);
		
		//组装Map<设备ID,在线状态>
		Map<Long,Integer> idAndStatusMap = new HashMap<>();
		for(Long facilityId : idList){
			Map<Long, Integer> facilityMap = idAndHandelMap.get(BigInteger.valueOf(facilityId));
			Integer handle = facilityMap.get("facility_handle");
			//调用SDK，查询最新在线状态单个结果
			int status = FacilityUtils.isOnline(handle);
			idAndStatusMap.put(facilityId,status);
		}
		
		//批量更新在线状态
		facilityMapper.updateStatusByFacilityIdBatch(idAndStatusMap);
		
		//TODO 下面代码留着 供参考
		//Set<设备ID>
//		Set<Long> ids = idAndHandelMap.keySet();
		//Map<设备ID，句柄>转list
//		List<Map<Long, Integer>> list2 = new ArrayList<>(idAndHandelMap.values());
		//批量更新在线状态
//		facilityMapper.updateStatusByFacilityIdBatch(list2,ids);

	
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
