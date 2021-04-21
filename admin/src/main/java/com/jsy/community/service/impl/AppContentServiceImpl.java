package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.AppContentMapper;
import com.jsy.community.service.AppContentService;
import com.jsy.community.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description APP内容控制服务类
 * @since 2020-11-19 13:38
 **/
@Slf4j
@Service
public class AppContentServiceImpl implements AppContentService {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Resource
	private AppContentMapper appContentMapper;
	
	/**
	* @Description: 设置推荐城市
	 * @Param: [hotCityList]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/19
	**/
	@Override
	public boolean setHotCity(List<RegionEntity> hotCityList){
		if(CollectionUtils.isEmpty(hotCityList)){
			return false;
		}
		//备份
		List<RegionEntity> hotCitys = appContentMapper.getHotCity();
		//清空
		appContentMapper.clearHotCity();
		//新增
		int rows = appContentMapper.insertHotCity(hotCityList);
		//还原
		if(rows != hotCityList.size()){
			log.error("设置推荐城市出错：" + hotCityList,"成功条数：" + rows);
			appContentMapper.clearHotCity();
			appContentMapper.insertHotCity(hotCitys);
			return false;
		}
		//设置到redis
		redisTemplate.opsForValue().set("hotCityList", JSONObject.toJSONString(hotCityList));
		return true;
	}
	
	/**
	* @Description: 从文件目录加载天气图标上传并写库
	 * @Param: [filepath]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/27
	**/
	//TODO 还差个静态加载 供天气接口使用
	@Override
	public int addWeatherIconFromFileDirectory(String filepath){
		List<Map<String,String>> list = new ArrayList<>();
		File fileDir = new File(filepath);
		if (!fileDir.isDirectory()) {
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"目标路径不是文件夹");
		}else{
			try{
				File[] files = fileDir.listFiles();
				if(files.length == 0){
					throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"目标文件夹空");
				}
				for(File file : files){
					Map<String, String> iconMap = new HashMap<>();
					FileInputStream in = new FileInputStream(file);
					MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/png", IOUtils.toByteArray(in));
					String name = multipartFile.getOriginalFilename();
					String url = MinioUtils.upload(multipartFile, "weather-icon");
					iconMap.put("num",name.substring(0,name.lastIndexOf(".")));
					iconMap.put("url",url);
					list.add(iconMap);
					in.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		//查询最新版本
		int leastEdition = appContentMapper.getLeastEdition();
		//添加一版天气图标
		int result = appContentMapper.addWeatherIconBatch(leastEdition + 1, list);
		return result;
	}
	
	public static void main(String[] args) {
//		addWeatherIconFromFileDirectory("D:/weather_icon2");
	}
	
}
