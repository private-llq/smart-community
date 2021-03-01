package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.mapper.CommonMapper;
import com.jsy.community.mapper.RegionMapper;
import com.jsy.community.utils.WeatherUtils;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.WeatherForecastVO;
import com.jsy.community.vo.WeatherHourlyVO;
import com.jsy.community.vo.WeatherLiveIndexVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.transformer.MessageTransformingHandler;

import javax.annotation.Resource;
import java.util.*;


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

    @Resource
    private CommonMapper commonMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private WeatherUtils weatherUtils;

    @Override
    public void checkVerifyCode(String account, String code) {
        String oldCode = redisTemplate.opsForValue().get("vCode:" + account);
        if (oldCode == null) {
            throw new ProprietorException("验证码已失效");
        }

        if (!oldCode.equals(code)) {
            throw new ProprietorException("验证码错误");
        }

        // 验证通过后删除验证码
//        redisTemplate.delete(account);
    }

    /**
     * houseLevelMode没有用到的原因 是因为控制层需要使用反射 统一调度业务方法，所以参数类型个数必须一样
     * 根据城市id查询下面所有社区
     * @param id 传入的城市id
     * @return 返回社区集合
     * @author YuLF
     * @since 2020/12/8 16:39
     */
    @Override
    public List<Map<String, Object>> getAllCommunityFormCityId(Long id, Integer houseLevelMode, Integer page, Integer pageSize) {
        page = (page - 1) * pageSize;
        return commonMapper.getAllCommunityFormCityId(id, page , pageSize);
    }

    /**
     * 按社区Id查询下面的楼栋或单元
     * @author YuLF
     * @since  2020/12/29 15:08
     * @Param
     */
    @Override
    public List<Map<String, Object>> getBuildingOrUnitByCommunityId(Long id, Integer houseLevelMode, Integer page, Integer pageSize) {
        List<Map<String, Object>> buildingOrUnitByCommunityId = commonMapper.getBuildingOrUnitByCommunityId(id, houseLevelMode);
        return setHouseLevelMode(buildingOrUnitByCommunityId, houseLevelMode);
    }

    /**
     * 按楼栋Id查询 单元 或 按 单元id查询楼栋 只对 社区结构为 楼栋单元 或单元楼栋有效
     * @author YuLF
     * @since  2020/12/29 15:08
     * @Param
     */
    @Override
    public List<Map<String, Object>> getBuildingOrUnitById(Long id, Integer houseLevelMode, Integer page, Integer pageSize) {
        List<Map<String, Object>> buildingOrUnitOrFloorById = commonMapper.getBuildingOrUnitById(id, houseLevelMode);
        return setHouseLevelMode(buildingOrUnitOrFloorById, houseLevelMode);
    }

    /**
     * 按按单元id或楼栋id查询  楼层
     * @author YuLF
     * @since  2020/12/29 15:08
     * @Param
     */
    @Override
    public List<Map<String, Object>> getFloorByBuildingOrUnitId(Long id, Integer houseLevelMode, Integer page, Integer pageSize) {
        List<Map<String, Object>> maps = commonMapper.getFloorByBuildingOrUnitId(id, houseLevelMode);
        return setHouseLevelMode(maps, houseLevelMode);
    }

    /**
     * 按楼层id获取门牌
     * @author YuLF
     * @since  2020/12/29 15:08
     * @Param
     */
    @Override
    public List<Map<String, Object>> getAllDoorFormFloor(Long id, Integer houseLevelMode, Integer page, Integer pageSize) {
        List<Map<String, Object>> allDoorFormFloor = commonMapper.getAllDoorFormFloor(id);
        return setHouseLevelMode(allDoorFormFloor, houseLevelMode);
    }

    /**
     * 批量设置 返回值得 社区层级结构CODE     方便前端请求接口时调用标识
     *
     * @author YuLF
     * @Param map                        数据库查询结果
     * @since 2020/12/9 9:30
     */
    private List<Map<String, Object>> setHouseLevelMode(List<Map<String, Object>> map, Integer houseLevelId) {
        for (Map<String, Object> value : map) {
            value.put("houseLevelMode", houseLevelId);
        }
        return map;
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
        return JSONArray.parseObject(String.valueOf(redisTemplate.opsForValue().get("hotCityList")), List.class);
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
        return regionMapper.vagueQueryCity(searchStr);
    }

    //天气假数据
    public JSONObject getTempWeather(){
        return WeatherUtils.getTempWeather();
    }
    
    //天气详情假数据
    public JSONObject getTempWeatherDetails(){
        return WeatherUtils.getTempWeatherDetails();
    }
    
    /**
    * @Description: 首页天气
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2021/2/25
    **/
    public JSONObject getWeather(double lon, double lat){
        //接口调用
        JSONObject weatherNow = getWeatherNow(lon, lat);  //天气实况
        List<WeatherForecastVO> weatherForDays = getWeatherForDays(lon, lat);  //15天天气预报
        weatherNow.put("forecast",weatherForDays);
        
        //处理数据
        WeatherUtils.dealForecastToAnyDays(weatherNow,3);  //截取未来3天天气预报
        WeatherUtils.addDayOfWeek(weatherNow); //补星期几
        
        return weatherNow;
    }
    
    /**
    * @Description: 天气详情整合接口
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/2/25
    **/
    @Override
    public JSONObject getWeatherDetails(double lon, double lat){
        try {
            //接口调用
            JSONObject weatherNow = getWeatherNow(lon, lat);  //天气实况
            List<WeatherForecastVO> weatherForDays = getWeatherForDays(lon, lat);  //15天天气预报
            weatherNow.put("forecast",weatherForDays);
            List<WeatherHourlyVO> weatherFor24hours = getWeatherFor24hours(lon, lat);  //24h天气预报
            JSONObject airQuality = getAirQuality(lon, lat);  //空气质量
            ArrayList<WeatherLiveIndexVO> livingIndex = getLivingIndex(lon, lat);  //生活指数
            
            //处理数据
            WeatherUtils.addDayOfWeek(weatherNow); //补星期几
//            WeatherUtils.addAQINameByAQIValue(airQuality,lon,lat,null);  //补空气质量名称(优、良、轻度污染等)
            
            //组装返回
            weatherNow.put("hourly",weatherFor24hours);
            weatherNow.put("aqi",airQuality.getJSONObject("aqi"));
            weatherNow.put("liveIndex",livingIndex);
            return weatherNow;
        }catch (Exception e){
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
    private List<WeatherForecastVO> getWeatherForDays(double lon, double lat){
        JSONObject weatherForDays = weatherUtils.getWeatherForDays(String.valueOf(lon), String.valueOf(lat));
        JSONArray forecast = weatherForDays.getJSONArray("forecast");
        List<WeatherForecastVO> newForecastList = forecast.toJavaList(WeatherForecastVO.class);
        return newForecastList;
//        return weatherUtils.getWeatherForDays(String.valueOf(lon),String.valueOf(lat));
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
        JSONArray jsonArray = livingIndex.getJSONArray(originKey);
        List<WeatherLiveIndexVO> weatherParamList = jsonArray.toJavaList(WeatherLiveIndexVO.class);
        //筛选指定数据(code代表相应数据项 0万年历 12感冒 17洗车 20穿衣 21紫外线 26运动 28钓鱼)
        Integer[] codeArr = {0,12,17,20,21,26,28};
        ArrayList<Integer> codeList = new ArrayList<>();
        Collections.addAll(codeList,codeArr);
        ArrayList<WeatherLiveIndexVO> returnList = new ArrayList<>();
        //TODO 插入万年历数据
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


}
