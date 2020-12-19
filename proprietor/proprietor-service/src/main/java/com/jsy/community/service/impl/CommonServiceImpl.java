package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.mapper.CommonMapper;
import com.jsy.community.mapper.RegionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

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
    public List<Map<String, Object>> getAllCommunityFormCityId(Integer id, Integer houseLevelMode) {
        return commonMapper.getAllCommunityFormCityId(id);
    }

    //按社区Id查询下面的楼栋或单元
    @Override
    public List<Map<String, Object>> getBuildingOrUnitByCommunityId(Integer id, Integer houseLevelMode) {
        List<Map<String, Object>> buildingOrUnitByCommunityId = commonMapper.getBuildingOrUnitByCommunityId(id, houseLevelMode);
        return setHouseLevelMode(buildingOrUnitByCommunityId, houseLevelMode);
    }

    //按楼栋Id查询 单元 或 按 单元id查询楼栋 只对 社区结构为 楼栋单元 或单元楼栋有效
    @Override
    public List<Map<String, Object>> getBuildingOrUnitById(Integer id, Integer houseLevelMode) {
        List<Map<String, Object>> buildingOrUnitOrFloorById = commonMapper.getBuildingOrUnitById(id, houseLevelMode);
        return setHouseLevelMode(buildingOrUnitOrFloorById, houseLevelMode);
    }

    //按单元id或楼栋id查询  楼层
    @Override
    public List<Map<String, Object>> getFloorByBuildingOrUnitId(Integer id, Integer houseLevelMode) {
        List<Map<String, Object>> maps = commonMapper.getFloorByBuildingOrUnitId(id, houseLevelMode);
        return setHouseLevelMode(maps, houseLevelMode);
    }

    //按楼层id获取门牌
    @Override
    public List<Map<String, Object>> getAllDoorFormFloor(Integer id, Integer houseLevelMode) {
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

}
