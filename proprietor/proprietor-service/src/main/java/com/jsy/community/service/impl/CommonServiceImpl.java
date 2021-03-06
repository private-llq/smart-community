package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.CommonMapper;
import com.jsy.community.mapper.RegionMapper;
import com.jsy.community.mapper.WeatherIconMapper;
import com.jsy.community.utils.LunarCalendarFestivalUtils;
import com.jsy.community.utils.WeatherUtils;
import com.jsy.community.vo.WeatherForecastVO;
import com.jsy.community.vo.WeatherHourlyVO;
import com.jsy.community.vo.WeatherLiveIndexVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 公共的
 *
 * @author ling
 * @since 2020-11-13 14:59
 */
@Slf4j
@Primary
@DubboService(version = Const.version, group = Const.group_proprietor)
public class CommonServiceImpl implements ICommonService {
    
    private static final Integer QUERY_TYPE_CITY = 2;
    private static final Integer QUERY_TYPE_AREA = 3;

    @Resource
    private CommonMapper commonMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private WeatherUtils weatherUtils;
    
    @Autowired
    private WeatherIconMapper weatherIconMapper;

    @Override
    public void checkVerifyCode(String account, String code) {
        
        //苹果审核用账号
        if("18183132010".equals(account) || "18888888888".equals(account)){
            return;
        }
        
        Object oldCode = redisTemplate.opsForValue().get("vCode:" + account);
        if (oldCode == null) {
            throw new ProprietorException("验证码已失效");
        }

        if (!oldCode.equals(code)) {
            throw new ProprietorException("验证码错误");
        }
    
        
        // 验证通过后删除验证码
        redisTemplate.delete("vCode:" + account);
    }

    /**
     * houseLevelMode没有用到的原因 是因为控制层需要使用反射 统一调度业务方法，所以参数类型个数必须一样
     * 根据城市id查询下面所有社区
     * @param id 传入的城市id
     * @return 返回社区集合
     * @author YuLF
     * @since 2020/12/8 16:39
     */
    /**
     *  queryType = 1
     */
    @Override
    public List<Map<String, Object>> getAllCommunityFormCityId(Long id, Integer page, Integer pageSize) {
        return commonMapper.getAllCommunityFormCityId(id, page , pageSize);
    }

    /**
     * 按社区Id查询下面的楼栋或单元
     * @author YuLF
     * @since  2020/12/29 15:08
     * @Param
     */
    /**
     *  queryType = 2
     */
//    @Override
//    public List<Map<String, Object>> getBuildingOrUnitByCommunityId(Long id, Integer page, Integer pageSize) {
//        //按社区id 查询 下面的所有 楼栋
//        List<Map<String, Object>> buildingList = commonMapper.getAllBuild(id, 1);
//        if( CollectionUtil.isNotEmpty(buildingList) ){
//            return buildingList;
//        }
//        //按社区id 查询 下面的所有 单元
//        return commonMapper.getAllBuild(id, 2);
//    }
    
    /**
    * @Description: 根据社区id 查询下面的所有单元 或 所有楼栋
     * @Param: [id, page, pageSize]
     * @Return: java.util.Map<java.lang.String,java.util.List<java.util.Map<java.lang.String,java.lang.Object>>>
     * @Author: chq459799974
     * @Date: 2021/5/26
    **/
    @Override
    public Map<String,List<Map<String, Object>>> getBuildingOrUnitByCommunityId(Long id, Integer page, Integer pageSize) {
        //按社区id 查询 下面的所有 楼栋
        List<Map<String, Object>> buildingList = commonMapper.getAllBuild(id, 1);
        //按社区id 查询 下面的所有 单元
        List<Map<String, Object>> unitList = commonMapper.getAllBuild(id, 2);
        Map<String, List<Map<String, Object>>> returnMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(buildingList)){
            returnMap.put("buildingList",buildingList);
        }
        if(!CollectionUtils.isEmpty(unitList)){
            returnMap.put("unitList",unitList);
        }
        return returnMap;
    }
    
    
    /**
     *  queryType = 3
     */
//    @Override
//    public List<Map<String, Object>> getUnitOrFloorById(Long id, Integer page, Integer pageSize) {
//        //1. 不管他是楼栋id还是单元id、第一种方式先按 他传的楼栋id来查单元
//        List<Map<String, Object>> unitList = commonMapper.getUnitByBuildingId(id);
//        if( CollectionUtil.isNotEmpty(unitList) ){
//            return unitList;
//        }
//        //2. 如果 按 楼栋id 查到的单元为空 则 按 楼栋id 查询所有楼层
//        List<Map<String, Object>> floorByBuildingId = commonMapper.getFloorByBuildingId(id);
//        if( CollectionUtil.isNotEmpty(floorByBuildingId) ){
//            return floorByBuildingId;
//        }
//        //3. 如果 按 以上id 当做楼栋去查 楼层都为空 那说明传的是一个 单元id 则按单元id查楼层
////        return getFloorByUnitId(id, page, pageSize);
//        return commonMapper.getFloorByUnitId(id, page, pageSize);
//    }
    
    /**
    * @Description: 根据单元或楼栋id查询单元或楼层
     * @Param: [id, page, pageSize]
     * @Return: java.util.Map<java.lang.String,java.util.List<java.util.Map<java.lang.String,java.lang.Object>>>
     * @Author: chq459799974
     * @Date: 2021/5/26
    **/
    @Override
    public Map<String,List<Map<String, Object>>> getUnitOrFloorById2(Long id, Integer page, Integer pageSize) {
        //1. 楼栋id查单元
        List<Map<String, Object>> unitList = commonMapper.getUnitByBuildingId(id);
        //2.1 楼栋id查楼层
        List<Map<String, Object>> floorList = commonMapper.getFloorByBuildingId(id);
        //2.2 单元id查楼层
        List<Map<String, Object>> floorByUnit = commonMapper.getFloorByUnitId(id, page, pageSize);
        if(CollectionUtils.isEmpty(floorList)){
            floorList.addAll(floorByUnit);
        }
        Map<String, List<Map<String, Object>>> returnMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(unitList)){
            returnMap.put("unitList",unitList);
        }
        if(!CollectionUtils.isEmpty(floorList)){
            returnMap.put("floorList",floorList);
        }
        return returnMap;
    }


//    /**
//     * 按单元id获取楼层
//     * @author YuLF
//     * @since  2020/12/29 15:08
//     * @Param
//     */
//    @Override
//    private List<Map<String, Object>> getFloorByUnitId(Long id,  Integer page, Integer pageSize) {
//        return commonMapper.getFloorByUnitId(id, page, pageSize);
//    }
    
    /**
     *  楼层文本查房屋
     */
    @Override
    public List<Map<String, Object>> getHouseByFloor(Long id, String floor) {
        return commonMapper.getHouseByFloor( id, floor);
    }


    /**
    * @Description: 获取子区域
     * @Param: [id]
     * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
     * @Author: chq459799974
     * @Date: 2020/12/12
    **/
    @Override
    public List<RegionEntity> getSubRegion(Integer id) {
        return JSONArray.parseObject(String.valueOf(redisTemplate.opsForHash().get("Region:", String.valueOf(id))), List.class);
    }

    /**
    * @Description: 城市字典
     * @Param: []
     * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.RegionEntity>
     * @Author: chq459799974
     * @Date: 2020/12/12
    **/
    @Override
    public TreeMap<String, RegionEntity> getCityMap() {
        return JSONObject.parseObject(String.valueOf(redisTemplate.opsForValue().get("cityMap")), TreeMap.class);
    }

    /**
    * @Description: 城市列表
     * @Param: []
     * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
     * @Author: chq459799974
     * @Date: 2020/12/12
    **/
    @Override
    public List<RegionEntity> getCityList() {
        return JSONArray.parseObject(String.valueOf(redisTemplate.opsForValue().get("cityList")), List.class);
    }

    /**
    * @Description: 热门城市
     * @Param: []
     * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
     * @Author: chq459799974
     * @Date: 2020/12/12
    **/
    @Override
    public List<RegionEntity> getHotCityList() {
        List<RegionEntity> hotCityList = JSONArray.parseObject(String.valueOf(redisTemplate.opsForValue().get("hotCityList")), List.class);
        if(CollectionUtils.isEmpty(hotCityList)){
            return commonMapper.queryHotCity();
        }
        return hotCityList;
    }

    /**
    * @Description: 城市模糊查询
     * @Param: [searchStr]
     * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
     * @Author: chq459799974
     * @Date: 2020/12/12
    **/
    @Override
    public List<RegionEntity> vagueQueryCity(String searchStr) {
        return regionMapper.vagueQueryRegion(searchStr,QUERY_TYPE_CITY,null);
    }
    
    /**
    * @Description: 区域模糊查询
     * @Param: [searchStr]
     * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
     * @Author: chq459799974
     * @Date: 2021/3/22
    **/
    @Override
    public List<RegionEntity> vagueQueryArea(String searchStr, Integer cityId) {
        return regionMapper.vagueQueryRegion(searchStr,QUERY_TYPE_AREA,cityId);
    }

    //天气假数据
    public JSONObject getTempWeather(){
        return WeatherUtils.getTempWeather();
    }
    
    //天气详情假数据
    public JSONObject getTempWeatherDetails(){
        Map<String,Map<String, String>> latestIcon = weatherIconMapper.getLatestIcon();
        JSONObject tempWeatherDetails = WeatherUtils.getTempWeatherDetails();
        JSONArray hourlyArr = tempWeatherDetails.getJSONArray("hourly");
        for(int i=0;i<hourlyArr.size();i++){
            hourlyArr.getJSONObject(i).put("iconUrl",latestIcon.get(hourlyArr.getJSONObject(i).getString("iconDay")).get("url"));
        }
        JSONArray forecastArr = tempWeatherDetails.getJSONArray("forecast");
        for(int i=0;i<forecastArr.size();i++){
            forecastArr.getJSONObject(i).put("iconUrlDay",latestIcon.get(forecastArr.getJSONObject(i).getString("conditionIdDay")).get("url"));
            forecastArr.getJSONObject(i).put("iconUrlNight",latestIcon.get(forecastArr.getJSONObject(i).getString("conditionIdNight")).get("url"));
        }
        return tempWeatherDetails;
    }
    
    /**
    * @Description: 首页天气
     * @Param: [cityName]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2021/2/25
    **/
    public JSONObject getWeather(String cityName){
    
        //走缓存数据
        JSONObject jsonCache = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("CityWeatherInfo:" + cityName));
        String cityNameLike = null;
        RegionEntity cityLike = null;
        if(jsonCache == null){
            //尝试寻找相似地区
            List<RegionEntity> cityLikeList = regionMapper.getCityNameLike(cityName);
            if(cityLikeList != null && cityLikeList.size() == 1){
                //找到相似地区且唯一，继续尝试从缓存获取数据
                cityLike = cityLikeList.get(0);
                jsonCache = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("CityWeatherInfo:" + cityLike.getName()));
            }
        }
        if(jsonCache != null){
            return jsonCache;
        }
        
        //缓存无数据
        //1.根据传入的城市名称cityName获取经纬度
        JSONObject lonAndLat = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("RegionLL:"+cityName));
        double lon;
        double lat;
        if(lonAndLat != null){
            lon = lonAndLat.getDoubleValue("lon");
            lat = lonAndLat.getDoubleValue("lat");
        }else{
            //没有找到或找到多个相似城市
            if(cityLike == null){
                throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(),"暂不支持该城市！");
            }
            //使用相似城市
            cityNameLike = cityLike.getName();
            lon = cityLike.getLng();
            lat = cityLike.getLat();
        }
        
        //2.用经纬度调用三方天气接口
        JSONObject weatherNow = getWeatherNow(lon, lat);  //天气实况
        JSONArray weatherForDays = getWeatherForDays(lon, lat);  //15天天气预报
        weatherNow.put("forecast",weatherForDays);

        //处理数据
        WeatherUtils.dealForecastToAnyDays(weatherNow,3);  //截取未来3天天气预报
        WeatherUtils.addDayOfWeek(weatherNow); //补星期几

        //去除不需要属性
        List<WeatherForecastVO> newForecastList = weatherForDays.toJavaList(WeatherForecastVO.class);
        weatherNow.put("forecast",newForecastList);
    
        //放入缓存
        //TODO 缓存时间写到配置
        stringRedisTemplate.opsForValue().set("CityWeatherInfo:" + (cityNameLike != null ? cityNameLike : cityName),JSON.toJSONString(weatherNow),30, TimeUnit.MINUTES);
        return weatherNow;
    }
    
    /**
    * @Description: 天气详情整合接口
     * @Param: [cityName]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/2/25
    **/
    @Override
    public JSONObject getWeatherDetails(String cityName){
    
        //走缓存数据
        JSONObject jsonCache = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("CityWeather:" + cityName));
        String cityNameLike = null;
        RegionEntity cityLike = null;
        if(jsonCache == null){
            //尝试寻找相似地区
            List<RegionEntity> cityLikeList = regionMapper.getCityNameLike(cityName);
            if(cityLikeList != null && cityLikeList.size() == 1){
                //找到相似地区且唯一，继续尝试从缓存获取数据
                cityLike = cityLikeList.get(0);
                jsonCache = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("CityWeather:" + cityLike.getName()));
            }
        }
        if(jsonCache != null){
            return jsonCache;
        }
        
        //根据城市名称获取经纬度
        JSONObject lonAndLat = JSONObject.parseObject(stringRedisTemplate.opsForValue().get("RegionLL:"+cityName));
        double lon;
        double lat;
        if(lonAndLat != null){
            lon = lonAndLat.getDoubleValue("lon");
            lat = lonAndLat.getDoubleValue("lat");
        }else{
            //没有找到或找到多个相似城市
            if(cityLike == null){
                throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(),"暂不支持该城市！");
            }
            //使用相似城市
            cityNameLike = cityLike.getName();
            lon = cityLike.getLng();
            lat = cityLike.getLat();
        }
        
        try {
            //接口调用
            JSONObject weatherNow = getWeatherNow(lon, lat);  //天气实况
            JSONArray weatherForDays = getWeatherForDays(lon, lat);  //15天天气预报
            weatherNow.put("forecast",weatherForDays);
            List<WeatherHourlyVO> weatherFor24hours = getWeatherFor24hours(lon, lat);  //24h天气预报
            JSONObject airQuality = getAirQuality(lon, lat);  //空气质量
            ArrayList<WeatherLiveIndexVO> livingIndex = getLivingIndex(lon, lat);  //生活指数

            //处理数据
            WeatherUtils.addDayOfWeek(weatherNow); //补星期几
            //去除不需要属性
            List<WeatherForecastVO> newForecastList = weatherForDays.toJavaList(WeatherForecastVO.class);
            weatherNow.put("forecast",newForecastList);

            //查询天气图标
            Map<String,Map<String, String>> latestIcon = weatherIconMapper.getLatestIcon();
            for(int i=0;i<weatherFor24hours.size();i++){
                weatherFor24hours.get(i).setIconUrl(latestIcon.get(weatherFor24hours.get(i).getIconDay()).get("url"));
            }
            for(int i=0;i<newForecastList.size();i++){
                newForecastList.get(i).setIconUrlDay(latestIcon.get(newForecastList.get(i).getConditionIdDay()).get("url"));
                newForecastList.get(i).setIconUrlNight(latestIcon.get(newForecastList.get(i).getConditionIdNight()).get("url"));
            }

            //组装返回
            weatherNow.put("hourly",weatherFor24hours);
            weatherNow.put("aqi",airQuality);
            weatherNow.put("liveIndex",livingIndex);
            
            //放入缓存
            stringRedisTemplate.opsForValue().set("CityWeather:" + (cityNameLike != null ? cityNameLike : cityName),JSON.toJSONString(weatherNow),30, TimeUnit.MINUTES);
            return weatherNow;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
    
    /**
    * @Description: 获取天气实况
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/12/24
    **/
    private JSONObject getWeatherNow(double lon, double lat){
        JSONObject weatherNow = weatherUtils.getWeatherNow(String.valueOf(lon), String.valueOf(lat));
        JSONObject condition = weatherNow.getJSONObject("condition");
        JSONObject returnCondition = new JSONObject();
        returnCondition.put("condition",condition.getString("condition"));
        returnCondition.put("temp",condition.getString("temp"));
        returnCondition.put("tips",condition.getString("tips"));
        returnCondition.put("updatetime",condition.getString("updatetime"));
        weatherNow.put("condition",returnCondition);
        return weatherNow;
//        return weatherUtils.getWeatherNow(String.valueOf(lon),String.valueOf(lat));
    }
    
    /**
    * @Description: 获取天气预报15天
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/12/24
    **/
    private JSONArray getWeatherForDays(double lon, double lat){
        JSONObject weatherForDays = weatherUtils.getWeatherForDays(String.valueOf(lon), String.valueOf(lat));
        JSONArray forecast = weatherForDays.getJSONArray("forecast");
        return forecast;
    }
    
    /**
     * @Description: 获取天气预报24h
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/2/25
     **/
    private List<WeatherHourlyVO> getWeatherFor24hours(double lon, double lat){
        JSONObject weatherFor24hours = weatherUtils.getWeatherFor24hours(String.valueOf(lon), String.valueOf(lat));
        JSONArray hourly = weatherFor24hours.getJSONArray("hourly");
        List<WeatherHourlyVO> newHourlyList = hourly.toJavaList(WeatherHourlyVO.class);
        return newHourlyList;
//        return weatherUtils.getWeatherFor24hours(String.valueOf(lon),String.valueOf(lat));
    }
    
    /**
     * @Description: 获取空气质量
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/2/25
     **/
    private JSONObject getAirQuality(double lon, double lat){
        JSONObject airQuality = weatherUtils.getAirQuality(String.valueOf(lon), String.valueOf(lat));
        JSONObject aqi = airQuality.getJSONObject("aqi");
        JSONObject returnAqi = new JSONObject();
        returnAqi.put("value",aqi.getString("value"));
        returnAqi.put("aqiName", BusinessEnum.AQIEnum.getAQIName(aqi.getIntValue("value"),null,null,null));
//        return weatherUtils.getAirQuality(String.valueOf(lon),String.valueOf(lat));
        return returnAqi;
    }
    
    /**
     * @Description: 获取生活指数
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/2/25
     **/
    private ArrayList<WeatherLiveIndexVO> getLivingIndex(double lon, double lat){
        JSONObject livingIndex = weatherUtils.getLivingIndex(String.valueOf(lon), String.valueOf(lat));
        Set<String> keys = livingIndex.keySet();
        String originKey = "";
        for(String key : keys){
            originKey = key;
        }
        JSONObject jsonObject = livingIndex.getJSONObject(originKey);
        String jsonStr = JSON.toJSONString(jsonObject.values());
        List<WeatherLiveIndexVO> weatherParamList = JSONArray.parseArray(jsonStr.substring(1,jsonStr.length()-1),WeatherLiveIndexVO.class);
//        JSONArray jsonArray = livingIndex.getJSONArray(originKey);
//        List<WeatherLiveIndexVO> weatherParamList = jsonArray.toJavaList(WeatherLiveIndexVO.class);
        //筛选指定数据(code代表相应数据项 12感冒 17洗车 20穿衣 21紫外线 26运动 28钓鱼)
        Integer[] codeArr = {12,17,20,21,26,28};
        ArrayList<Integer> codeList = new ArrayList<>();
        Collections.addAll(codeList,codeArr);
        ArrayList<WeatherLiveIndexVO> returnList = new ArrayList<>();
        //插入万年历数据
        LunarCalendarFestivalUtils lunarCalendarUtils = new LunarCalendarFestivalUtils();
        lunarCalendarUtils.initLunarCalendarInfo(LocalDate.now().toString());
        WeatherLiveIndexVO lunarCalendar = new WeatherLiveIndexVO();
        lunarCalendar.setCode(0);
        lunarCalendar.setName("万年历");
        lunarCalendar.setStatus(lunarCalendarUtils.getLunarMonth()+"月"+lunarCalendarUtils.getLunarDay());
        returnList.add(lunarCalendar);
        for(WeatherLiveIndexVO weatherLiveIndexVO : weatherParamList){
            if(codeList.contains(weatherLiveIndexVO.getCode())){
                returnList.add(weatherLiveIndexVO);
            }
        }
        //排序
        Collections.sort(returnList,new Comparator<WeatherLiveIndexVO>() {
            @Override
            public int compare(WeatherLiveIndexVO o1, WeatherLiveIndexVO o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
//        return livingIndex.getJSONArray(originKey);
        return returnList;
    }
    

    /**
     * 此方法 不会应用于 业务，
     * 全文搜索 首次导入数据库 所有数据
     * @author YuLF
     * @since  2021/2/7 14:54
     * @return      返回自定义结构好的 数据
     */
    @Override
    public List<FullTextSearchEntity> fullTextSearchEntities(){
        //商铺所有数据
        List<FullTextSearchEntity> shopList = commonMapper.getAllShop();
        //租赁房屋所有数据
        List<FullTextSearchEntity> houseList = commonMapper.getAllHouseLease();
        //社区消息所有数据
        List<FullTextSearchEntity> informList = commonMapper.getAllInform();
        //趣事所有数据
        List<FullTextSearchEntity> funList = commonMapper.getAllFun();
        List<FullTextSearchEntity> list = new ArrayList<>();
        list.addAll(shopList);
        list.addAll(houseList);
        list.addAll(informList);
        list.addAll(funList);
        //查缩略图
        list.forEach( l -> {
            switch (l.getFlag()){
                case LEASE_HOUSE:
                    l.setPicture(commonMapper.getLeaseHousePicture(l.getId()));
                    break;
                case LEASE_SHOP:
                    l.setPicture(commonMapper.getLeaseShopPicture(l.getId()));
                    break;
                case FUN:
                    l.setPicture(commonMapper.getFunPicture(l.getId()));
                    break;
                case INFORM:
                    l.setPicture(commonMapper.getInformPicture(l.getId()));
                    break;
                default:
                    break;
            }
        });
        return list;
    }


    /**
     * 全文搜索 添加热词
     * @author YuLF
     * @since  2021/3/10 14:34
     */
    @Override
    public void addFullTextSearchHotKey(String hotKey) {
        if( Objects.nonNull(hotKey) ){
            String hotKeyPrefix = BusinessConst.HOT_KEY_PREFIX;
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            //获得有序集合中key元素的索引index
            Long index = zSetOperations.rank(hotKeyPrefix, hotKey);
            if ( Objects.isNull(index) ) {
                //添加缓存,默认排序值 1
                zSetOperations.add(hotKeyPrefix, hotKey, 1.0);
            } else {
                //已存在 该热词 如果key已存在,则获取排序值并且+1
                int score = Objects.requireNonNull(zSetOperations.score(hotKeyPrefix, hotKey)).intValue();
                zSetOperations.incrementScore(hotKeyPrefix, hotKey, 1);
            }
            ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
            //记录存入 更新存入时间
            stringObjectValueOperations.getAndSet(BusinessConst.HOT_KEY_TIME_PREFIX + ":" + hotKey, System.currentTimeMillis() + "");
        }



    }

    /**
     * 全文搜索热词清理
     * 超过hotKeyActiveDay天的都得清理掉
     * @author YuLF
     * @since  2021/3/10 15:46
     */
    @Override
    public void cleanHotKey(Integer hotKeyActiveDay) {
        long dayMillisecond = (hotKeyActiveDay * 86400000);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Long now = System.currentTimeMillis();
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        Set<Object> allHotKey = zSet.reverseRange(BusinessConst.HOT_KEY_PREFIX, 0, Integer.MAX_VALUE);
        if( Objects.isNull(allHotKey) ){
            return;
        }
        allHotKey.forEach( hotKey -> {
            String word = String.valueOf(hotKey);
            Long wordTime = Long.valueOf(String.valueOf(valueOperations.get(BusinessConst.HOT_KEY_TIME_PREFIX + ":" + word)));
            if ((now - wordTime) > dayMillisecond) {
                zSet.remove(BusinessConst.HOT_KEY_PREFIX, word);
                valueOperations.getOperations().delete(BusinessConst.HOT_KEY_TIME_PREFIX + ":" + word);
            }
        });
    }

    /**
     * @param regionName : 区域名称
     * @author: Pipi
     * @description: 通过区域名称模糊匹配区域
     * @return: {@link RegionEntity}
     * @date: 2022/1/4 15:25
     **/
    @Override
    public RegionEntity queryRegionByName(String regionName) {
        return regionMapper.queryRegionByName(regionName);
    }

    /**
     * 全文搜索 获取热词
     * @author YuLF
     * @since  2021/3/10 14:55
     * @Param  num      获取热词数量
     * @return  返回热词集合
     */
    @Override
    public Set<Object> getFullTextSearchHotKey(Integer num) {
        //获取从开始到结束的范围内的元素，其中分数介于排序集“高->低”的最小值和最大值之间。
        return redisTemplate.opsForZSet()
                .reverseRangeByScore(BusinessConst.HOT_KEY_PREFIX, 0, Integer.MAX_VALUE, 0, num);
    }


}
