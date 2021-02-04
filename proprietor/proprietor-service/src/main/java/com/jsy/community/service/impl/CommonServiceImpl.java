package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.mapper.CommonMapper;
import com.jsy.community.mapper.RegionMapper;
import com.jsy.community.utils.WeatherUtils;
import com.jsy.community.utils.es.RecordFlag;
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

    /**
    * @Description: 天气整合接口
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/12/24
    **/
    @Override
    public JSONObject getWeather(double lon, double lat){
        JSONObject weatherNow = getWeatherNow(lon, lat);
        JSONObject weatherForDays = getWeatherForDays(lon, lat);
        try{
            String cityId1 = weatherNow.getJSONObject("city").getString("cityId");
            String cityId2 = weatherForDays.getJSONObject("city").getString("cityId");
            if(cityId1.equals(cityId2)){
                JSONArray forecast = weatherForDays.getJSONArray("forecast");
                weatherNow.put("forecast",forecast);
            }
        }catch (Exception e){
            log.error("查询天气 结果解析出错");
        }
        return weatherNow;
    }
    
    /**
    * @Description: 获取天气实况
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/12/24
    **/
    @Override
    public JSONObject getWeatherNow(double lon, double lat){
        return weatherUtils.getWeatherNow(String.valueOf(lon),String.valueOf(lat));
    }
    
    /**
    * @Description: 获取天气预报
     * @Param: [lon, lat]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: chq459799974
     * @Date: 2020/12/24
    **/
    @Override
    public JSONObject getWeatherForDays(double lon, double lat){
        return weatherUtils.getWeatherForDays(String.valueOf(lon),String.valueOf(lat));
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
