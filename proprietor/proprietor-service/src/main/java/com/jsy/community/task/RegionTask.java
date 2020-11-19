package com.jsy.community.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.mapper.RegionMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Description: 省市区划 自动执行任务类
 * @Author chq459799974
 * @Date 2020/11/13 10:38
 **/
@Service
public class RegionTask {
	
	private static final Logger logger = LoggerFactory.getLogger(RegionTask.class);
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Resource
	private RegionMapper regionMapper;
	
	/**
	* @Description: 结果缓存进redis 启动自动执行
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/11/13
	**/
	//TODO 测试阶段 暂时开机启动
	@PostConstruct
	public void setRegionToRedis(){
		logger.info("服务启动任务执行：更新区域表");
		try {
			dealQQMapData();//获取腾讯地图数据，插入本地数据库
			List<RegionEntity> regionList = getRegionList();//组装redis-hash(查子区域),citylist(城市列表)
			redisTemplate.opsForValue().set("regionList", JSONObject.toJSONString(regionList));
		} catch (Exception e){
			e.printStackTrace();
			logger.error("省市区划同步失败，今日取消更新省市区数据");
		}
	}
	
	/*获取分级封装后的所有区域id,name,pid*/
	private List<RegionEntity> getRegionList(){
		List<RegionEntity> allRegion = regionMapper.getAllRegion();
		List<RegionEntity> regionList  = new ArrayList<>();//封装好的返回结果(regionList)
		List<RegionEntity> cityList = new ArrayList<>();
		Map<String, List<RegionEntity>> cityMap = new HashMap<>();
		for(int i=0;i<26;i++) {
			char c = (char)('A'+i);
			cityMap.put(String.valueOf(c), new LinkedList<>());
		}
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
				if(!StringUtils.isEmpty(regionEntity.getInitials())){
					cityMap.get(regionEntity.getInitials()).add(regionEntity);
				}
			}
		}
		//拼音排序
		Collections.sort(cityList, new Comparator<RegionEntity>() {
			@Override
			public int compare(RegionEntity o1, RegionEntity o2) {
				if (o1.getInitials() != null && o2.getInitials() != null) {
					return o1.getInitials().compareTo(o2.getInitials());
				}
				return 0;
			}
		});
		//封装二级区域(市)
		redisTemplate.opsForValue().set("cityList", JSONObject.toJSONString(cityList));
		//封装带拼音的cityMap
		redisTemplate.opsForValue().set("cityMap", JSONObject.toJSONString(cityMap));
		//用一级区域查找子节点
		for (RegionEntity regionEntity : regionList) {
			//调用查找子节点递归方法
			regionEntity.setChildren(getChildren(regionEntity,allRegion));//一定要记得set
		}
		return regionList;
	}
	
	private List<RegionEntity> getChildren(RegionEntity argEntity,List<RegionEntity> allRegion){
		List<RegionEntity> childrenList = new ArrayList<>();
		//阶段① 比较当前id所有数据的pid,添加childrenList
		for (RegionEntity regionEntity : allRegion) {
			if(!StringUtils.isEmpty(regionEntity.getPid()) && regionEntity.getPid().equals(argEntity.getId())){
				childrenList.add(regionEntity);
			}
		}
		//level在3级以上(2级)的就在redis添加childrenList
		//if(!"3".equals(argEntity.getLevel())){
		if(argEntity.getLevel() != 3){
			redisTemplate.opsForHash().put("Region:",String.valueOf(argEntity.getId()),JSONObject.toJSONString(childrenList));
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
	
	/**
	 * -- 《id规则》
	 * -- 国 固定写入100000    level 0  pid 0
	 * -- 省 11-99 后四位0000  level 1  pid 100000
	 * -- 市 01-99 中两位非00 后两位00  level 2  pid id前两位+0000
	 * -- 区 01-99 中两位非00 后两位非00  level 3  pid id前4位+00
	 */
	/**
	* @Description: 同步腾讯地图 行政区划(省市区表)
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/11/14
	**/
	@Scheduled(cron = "0 0 3 * * ?")
	@Transactional(rollbackFor = Exception.class)
	public void updateRegion(){
		logger.info("定时任务执行：更新区域表");
		dealQQMapData();//获取腾讯地图数据，插入本地数据库
		List<RegionEntity> regionList = getRegionList();//组装redis-hash(查子区域),citylist(城市列表)
		redisTemplate.opsForValue().set("regionList", JSONObject.toJSONString(regionList));
	}
	
	private void dealQQMapData(){
		//获取
		JSONObject jSONObject = queryQQMapTest();
		JSONArray resultArray = jSONObject.getJSONArray("result");
//		if(resultArray == null){
//			logger.error("获取腾讯地图省市区划失败：" + LocalDateTime.now());
//			return;
//		}
		List<RegionEntity> list = new ArrayList<>();
		//补数据
		fillLostData(list);
		//解析
		for (int i=0;i<resultArray.size();i++) {
			JSONArray jsonArray = resultArray.getJSONArray(i);
			for (int j=0;j<jsonArray.size();j++) {
				JSONObject jsonObject = jsonArray.getJSONObject(j);
				RegionEntity entity = new RegionEntity();
				//设置id
				Integer id = jsonObject.getInteger("id");
				entity.setId(id);
				//设置拼音首字母
				JSONArray pinyinArray = jsonObject.getJSONArray("pinyin");
				if(pinyinArray != null && pinyinArray.size() >= 2){
					String initials = String.valueOf(pinyinArray.get(0)).substring(0, 1).toUpperCase();
					entity.setInitials(initials);
				}
				//设置pid和level
				if(id%10000 == 0){ //省
					entity.setLevel(1);
					entity.setPid(100000);
				}else if(id%100 == 0){ // 市
					entity.setLevel(2);
					entity.setPid(id/10000*10000);
				}else{ //区
					entity.setLevel(3);
					entity.setPid(id/100*100);
				}
				//设置name
				entity.setName(jsonObject.getString("fullname"));
				list.add(entity);
			}
		}
		//排序
		Collections.sort(list, new Comparator<RegionEntity>() {
			@Override
			public int compare(RegionEntity o1, RegionEntity o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		//清理旧数据 新数据入库
		regionMapper.deleteAll();
		int insertResult = regionMapper.insertRegion(list);
		logger.info("本次更新数据"+insertResult+"条");
	}
	
	//腾讯地图接口未提供
	private void fillLostData(List<RegionEntity> list){
		list.add(new RegionEntity(100000,"中国",0,0,"Z"));
		list.add(new RegionEntity(500100,"重庆市",500000,2,"C"));
		list.add(new RegionEntity(110100,"北京市",110000,2,"B"));
		list.add(new RegionEntity(120100,"天津市",120000,2,"T"));
		list.add(new RegionEntity(310100,"上海市",310000,2,"S"));
	}
	
	//调用腾讯地图-行政区划
	//TODO key暂时用的个人的 且腾讯控制台白名单只有本地
	private JSONObject queryQQMapTest() {
		URIBuilder builder = null;
		try {
			builder = new URIBuilder("http://apis.map.qq.com/ws/district/v1/list");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		builder.setParameter("output","json");
		builder.setParameter("key","NZZBZ-L6DCP-GFQDB-V66BW-KN5RH-R7BV7");
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(builder.build());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		httpGet.setConfig(RequestConfig.custom()
			.setConnectTimeout(2000)
			.setConnectionRequestTimeout(2000)
			.setSocketTimeout(2000)
			.build());

		CloseableHttpClient conn = HttpClientBuilder.create().build();
		System.out.println("============= conn =============");
		System.out.println(conn);
		HttpResponse response = null;
		try {
			response = conn.execute(httpGet);
		} catch (Exception e) {
			e.printStackTrace();
			httpGet.abort();
		}
		JSONObject result = null;//返回值
		int statusCode = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {// 如果响应码是 200
				try {
					result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(result);
			} else {
				System.out.println("状态码：" + statusCode);
				httpGet.abort();
			}
		return result;
	}

}
