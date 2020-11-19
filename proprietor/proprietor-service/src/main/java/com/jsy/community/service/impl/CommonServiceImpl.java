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
        String oldCode = redisTemplate.opsForValue().get(account);
        if (oldCode == null) {
            throw new ProprietorException("验证码已失效");
        }

        if (!oldCode.equals(code)) {
            throw new ProprietorException("验证码错误");
        }

        // 验证通过后删除验证码
        redisTemplate.delete(account);
    }

    @Override
    public List<Map> getAllCommunityFormCityId(Integer id) {
        //todo 查询社区修改
        log.info("查询所有社区 ID是 {}", id);
        return commonMapper.getAllCommunityFormCityId(id);

    }

    @Override
    public List<Map> getAllUnitFormCommunity(Integer id) {
        //todo 查询社区单元修改
        log.info("查询社区单元 社区ID是 {}", id);
        return commonMapper.getAllUnitFormCommunity(id);
    }

    @Override
    public List<Map> getAllBuildingFormUnit(Integer id) {
        //todo 后续修改
        log.info("查询单元楼栋 单元ID是 {}", id);
        return commonMapper.getAllBuildingFormUnit(id);
    }

    @Override
    public List<Map> getAllFloorFormBuilding(Integer id) {
        //todo 后续修改
        log.info("查询楼栋楼层 楼层ID是 {}", id);
        return commonMapper.getAllFloorFormBuilding(id);
    }

    @Override
    public List<Map> getAllDoorFormFloor(Integer id) {
        //todo 后续修改
        log.info("查询楼层门牌 楼层ID是 {}", id);
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
