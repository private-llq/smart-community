package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.sys.SysPlatformSettingEntity;
import com.jsy.community.mapper.SysPlatformSettingMapper;
import com.jsy.community.service.ISysPlatformSettingService;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author DKS
 * @description 平台设置
 * @since 2021-11-13 14:48
 **/
@Slf4j
@Service
public class SysPlatformSettingServiceImpl extends ServiceImpl<SysPlatformSettingMapper, SysPlatformSettingEntity> implements ISysPlatformSettingService {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Resource
	private SysPlatformSettingMapper sysPlatformSettingMapper;
	
	/**
	 * @Description: 缓存大后台平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:18
	 * @Param: []
	 * @return: void
	 */
	@PostConstruct
	private void cachePlatformToRedis(){
		stringRedisTemplate.opsForValue().set("Sys:Platform", JSON.toJSONString(queryPlatform()));
	}
	
	/**
	 * @Description: 查询大后台平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:18
	 * @Param: []
	 * @return: com.jsy.community.entity.sys.SysPlatformSettingEntity
	 */
	private SysPlatformSettingEntity queryPlatform(){
		return sysPlatformSettingMapper.selectOne(new QueryWrapper<SysPlatformSettingEntity>().select("*").orderByDesc("create_time").last("limit 1"));
	}
	
	/**
	 * @Description: 新增平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:19
	 * @Param: [sysPlatformSettingEntity]
	 * @return: boolean
	 */
	@Override
	public boolean editPlatform(SysPlatformSettingEntity sysPlatformSettingEntity){
		sysPlatformSettingEntity.setId(SnowFlake.nextId());
		int result = sysPlatformSettingMapper.insert(sysPlatformSettingEntity);
		if(result == 1){
			cachePlatformToRedis(); //刷新redis
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 查询平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:33
	 * @Param: []
	 * @return: com.jsy.community.entity.sys.SysPlatformSettingEntity
	 */
	@Override
	public SysPlatformSettingEntity selectPlatform() {
		SysPlatformSettingEntity entity = new SysPlatformSettingEntity();
		try{
			String platformStr = stringRedisTemplate.opsForValue().get("Sys:Platform");
			JSONObject platformJSONObject = JSON.parseObject(platformStr);
			if (platformJSONObject != null) {
				entity.setId(platformJSONObject.getLong("id"));
				entity.setIdStr(platformJSONObject.getString("idStr"));
				entity.setCreateTime(LocalDateTime.parse(platformJSONObject.getString("createTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				entity.setUpdateTime(LocalDateTime.parse(platformJSONObject.getString("updateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				entity.setDeleted(platformJSONObject.getLong("deleted"));
				if (platformJSONObject.containsKey("mapKey")) {
					entity.setMapKey(platformJSONObject.getString("mapKey"));
				}
				if (platformJSONObject.containsKey("userAgreement")) {
					entity.setUserAgreement(platformJSONObject.getString("userAgreement"));
				}
				if (platformJSONObject.containsKey("privacyPolicy")) {
					entity.setPrivacyPolicy(platformJSONObject.getString("privacyPolicy"));
				}
				if (platformJSONObject.containsKey("aboutAs")) {
					entity.setAboutAs(platformJSONObject.getString("aboutAs"));
				}
			}
		}catch (Exception e){
			log.error("redis获取平台设置失败");
			return queryPlatform();//从mysql获取
		}
		return entity;
	}
}