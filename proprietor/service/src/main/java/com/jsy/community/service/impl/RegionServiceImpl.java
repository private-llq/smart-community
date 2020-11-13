package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.IRegionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.mapper.RegionDao;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 省市区服务类
 * @Author chq459799974
 * @Date 2020/11/13 10:38
 **/
@DubboService(version = Const.version, group = Const.group)
public class RegionServiceImpl implements IRegionService {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Resource
	private RegionDao regionDao;
	
	/**
	* @Description: 结果缓存进redis 启动自动执行
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/11/13
	**/
	@PostConstruct
	public void setRegionToRedis(){
		List<RegionEntity> regionList = getRegionList();
		stringRedisTemplate.opsForValue().set("regionList", JSONObject.toJSONString(regionList));
	}
	
	/**
	* @Description: 获取子区域
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq45799974
	 * @Date: 2020/11/13
	**/
	@Override
	public List<RegionEntity> getSubRegion(String id){
		return regionDao.getSubRegion(id);
	}
	
	/*获取分级封装后的所有区域id,name,pid*/
	private List<RegionEntity> getRegionList(){
		List<RegionEntity> allRegion = regionDao.getAllRegion();
		List<RegionEntity> regionList  = new ArrayList<RegionEntity>();//封装好的返回结果(regionList)
		List<RegionEntity> cityList = new ArrayList<>();
		for (RegionEntity regionEntity : allRegion) {
			//找到所有零级区域(国家)
			//if("0".equals(regionEntity.getPid())){
			if(regionEntity.getPid() == 0){
				regionList.add(regionEntity);
			}
			//找到二级区域(市)
			//if("2".equals(regionEntity.getLevel())){
			if(regionEntity.getLevel() == 2){
				cityList.add(regionEntity);
			}
		}
		//封装二级区域(市)
		stringRedisTemplate.opsForValue().set("cityList", JSONObject.toJSONString(cityList));
		//用一级区域查找子节点
		for (RegionEntity regionEntity : regionList) {
			//调用查找子节点递归方法
			regionEntity.setChildren(getChildren(regionEntity,allRegion));//一定要记得set
		}
		return regionList;
	}
	
	private List<RegionEntity> getChildren(RegionEntity argEntity,List<RegionEntity> allRegion){
		List<RegionEntity> childrenList = new ArrayList<RegionEntity>();
		//阶段① 比较当前id所有数据的pid,添加childrenList
		for (RegionEntity regionEntity : allRegion) {
			if(!StringUtils.isEmpty(regionEntity.getPid()) && regionEntity.getPid().equals(argEntity.getId())){
				childrenList.add(regionEntity);
			}
		}
		//level在3级以上(2级)的就在redis添加childrenList
		//if(!"3".equals(argEntity.getLevel())){
		if(argEntity.getLevel() != 3){
			stringRedisTemplate.opsForHash().put("Region:",String.valueOf(argEntity.getId()),JSONObject.toJSONString(childrenList));
		}
		List<RegionEntity> children;
		//阶段② 递归查找
		for (RegionEntity regionEntity : childrenList) {
			children = getChildren(regionEntity,allRegion);
			//没有子节点的如三级节点，则set null
			regionEntity.setChildren(children.size() == 0?null:children);
		}
		return childrenList;
	}
}
