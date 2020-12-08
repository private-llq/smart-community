package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.mapper.CommonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * 公共的
 *
 * @author ling
 * @since 2020-11-13 14:59
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group)
public class CommonServiceImpl implements ICommonService {

    @Resource
    private CommonMapper commonMapper;

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
     * @author YuLF
     * @since  2020/12/8 16:39
     * @param id   传入的城市id
     * @return     返回社区集合
     */
    @Override
    public List<Map> getAllCommunityFormCityId(Integer id, Integer houseLevelMode) {
        return commonMapper.getAllCommunityFormCityId(id);
    }

    @Override
    public List<Map> getBuildingOrUnitByCommunityId(Integer id, Integer houseLevelMode) {
        return commonMapper.getBuildingOrUnitByCommunityId(id, houseLevelMode);
    }

    @Override
    public List<Map> getBuildingOrUnitOrFloorById(Integer id, Integer houseLevelMode) {
        return commonMapper.getBuildingOrUnitOrFloorById(id, houseLevelMode);
    }

    @Override
    public List<Map> getAllDoorFormFloor(Integer id, Integer houseLevelMode) {
        return commonMapper.getAllDoorFormFloor(id);
    }
    
    @Override
    public List<RegionEntity> getSubRegion(Integer id){
        return JSONArray.parseObject(String.valueOf(redisTemplate.opsForHash().get("Region:", String.valueOf(id))), List.class);
    }
    
    @Override
    public Map<String,RegionEntity> getCityMap(){
        return JSONObject.parseObject(String.valueOf(redisTemplate.opsForValue().get("cityMap")), Map.class);
    }
    
    @Override
    public List<RegionEntity> getCityList(){
        return JSONArray.parseObject(String.valueOf(redisTemplate.opsForValue().get("cityList")), List.class);
    }
    
    @Override
    public List<RegionEntity> getHotCityList(){
        return JSONArray.parseObject(String.valueOf(redisTemplate.opsForValue().get("hotCityList")), List.class);
    }
}
