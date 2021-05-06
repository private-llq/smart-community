package com.jsy.community.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.sdk.HCNetSDK;
import com.jsy.community.util.facility.FacilityUtils;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 91李寻欢
 * @ClassName ApplicationRunnerImpl
 * @Date 2021/4/22  16:25
 * @Description TODO
 * @Version 1.0
 **/
@Component
@Slf4j
public class ApplicationRunnerImpl implements ApplicationRunner {
	
	@Resource
	private FacilityMapper facilityMapper;
	
	// 加载HK库
	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	
	// TODO: 2021/4/22 在项目启动的时候，登录所有摄像头。
	// TODO: 2021/4/22 缺点：随着摄像头的增多，项目一启动的时候就要登录所有摄像头会影响启动时间，可能会导致很长
	// TODO: 2021/4/22 解决方案[暂定]：利用rabbitMQ，用户一登录物业后台，就传社区id给消费者，消费者根据响应的社区id去只登录该社区的摄像头  或者 应该加个开启摄像头功能的按钮
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		//1. 初始化SDK    TRUE表示成功，FALSE表示失败。  【每个摄像头登录的时候都要初始化，删除摄像头的时候要释放SDK】
		if (!hCNetSDK.NET_DVR_Init()) {
			log.info("初始化SDK失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
		}
		
		//1. 获取根据社区id查询所有摄像id
		int communityId = 1;
		
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		List<FacilityEntity> facilityEntityList = facilityMapper.selectList(wrapper);
		List<Long> ids = new ArrayList<>();
		for (FacilityEntity facilityEntity : facilityEntityList) {
			ids.add(facilityEntity.getId());
		}
		
		//2. 将这些摄像头的在线状态删除掉
		if (!CollectionUtils.isEmpty(ids)) {
			facilityMapper.deleteMiddleFacilityIds(ids);
		}
		
		//3. 登录摄像头
		for (FacilityEntity facilityEntity : facilityEntityList) {
			log.info(facilityEntity.getIp() + ": 开始登录摄像头==============================================");
			// 设备ip地址
			String ip = facilityEntity.getIp();
			// 设备账号
			String username = facilityEntity.getUsername();
			// 设备密码
			String password = facilityEntity.getPassword();
			// 端口号
			Short port = facilityEntity.getPort();
			
			// 登录设备
			Map<String, Integer> map = FacilityUtils.login(ip, port, username, password, -1);
			Integer loginStatus = map.get("status");
			Integer handle = map.get("facilityHandle");
			int facilityAlarmHandle = FacilityUtils.toEffect(loginStatus, handle, facilityEntity.getFacilityEffectId());
			
			Integer status = map.get("status");
			Integer facilityHandle = map.get("facilityHandle");
			Long facilityId = facilityEntity.getId();
			long id = SnowFlake.nextId();
			facilityMapper.insertFacilityStatus(id, status, facilityHandle, facilityId, facilityAlarmHandle);
			
			//*******
			FacilityUtils.uploadFaceLibrary(handle,"1");
			log.info(facilityEntity.getIp() + ": 摄像头登录完毕==============================================");
		}
	}
}
