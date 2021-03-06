package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseConstService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.mapper.HouseConstMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author YuLF
 * @since 2020-12-11 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class HouseConstServiceImpl extends ServiceImpl<HouseConstMapper, HouseLeaseConstEntity> implements IHouseConstService {

    @Resource
    private HouseConstMapper houseConstMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 根据常量类型 获取属于这个类型的List数据
     *
     * @return 返回这个类型对应的List
     * @author YuLF
     * @Param type                常量类型
     * @since 2020/12/11 11:36
     */
    @Override
    public List<HouseLeaseConstEntity> getHouseConstListByType(String type) {
        var list = JSON.parseObject(String.valueOf(redisTemplate.opsForValue().get("houseConst:" + type)), List.class);
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<HouseLeaseConstEntity> houseLeaseConstEntityList = new ArrayList<>(list.size());
        for (Object o : list) {
            JSONObject jsonObject = JSON.parseObject(String.valueOf(o));
            HouseLeaseConstEntity entity = new HouseLeaseConstEntity(jsonObject.getLong("id"),
                    jsonObject.getLong("houseConstCode"),
                    jsonObject.getString("houseConstName"),
                    jsonObject.getString("houseConstType"),
                    jsonObject.getString("annotation"));
            if (!isEmpty(jsonObject.getString("houseConstValue"))) {
                entity.setHouseConstValue(jsonObject.getString("houseConstValue"));
            }
            houseLeaseConstEntityList.add(entity);
        }
        return houseLeaseConstEntityList;
    }

    /**
     * 通过 常量代码 和常量类型 从缓存中取 名称
     *
     * @param code 常量标识码
     * @param type 常量类型
     * @return 返回常量名称
     */
    @Override
    public String getConstNameByConstTypeCode(Long code, Long type) {
        List<HouseLeaseConstEntity> houseConstListByType = getHouseConstListByType(String.valueOf(type));
        for (HouseLeaseConstEntity e : houseConstListByType) {
            if (e.getHouseConstCode().equals(code)) {
                return e.getHouseConstName();
            }
        }
        return null;
    }

    /**
     * 通过 常量代码 和常量类型 从缓存中取 名称 list
     *
     * @param codes 常量标识码
     * @param type  常量类型
     * @return 返回常量名称和常量id   返回Map key = constCode value = constName
     */
    @Override
    public Map<String, Long> getConstByTypeCodeForList(List<Long> codes, Long type) {
        if (codes == null || codes.isEmpty()) {
            return null;
        }
        Map<String, Long> maps = new HashMap<>(codes.size());
        List<HouseLeaseConstEntity> ht = getHouseConstListByType(String.valueOf(type));
        if (ht == null) {
            return null;
        }
        for (Long code : codes) {
            for (HouseLeaseConstEntity entity : ht) {
                if (entity.getHouseConstCode().equals(code)) {
                    maps.put(entity.getHouseConstName(), entity.getHouseConstCode());
                }
            }
        }
        return maps;
    }

    /**
     * 通过 常量代码 和常量类型 从缓存中取 名称 list
     *
     * @param codes 常量标识码
     * @param type  常量类型
     * @return 返回常量名称和常量id  返回list<String> constName
     */
    @Override
    public List<String> getConstByTypeCodeForString(List<Long> codes, Long type) {
        if (codes == null || codes.isEmpty()) {
            return null;
        }
        List<String> list = new ArrayList<>(codes.size());
        List<HouseLeaseConstEntity> ht = getHouseConstListByType(String.valueOf(type));
        if (ht == null) {
            return null;
        }
        for (Long code : codes) {
            for (HouseLeaseConstEntity entity : ht) {
                if (entity.getHouseConstCode().equals(code)) {
                    list.add(entity.getHouseConstName());
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getConstNameByConstId(Long[] shopTypeIds) {
        List<Long> list = Arrays.asList(shopTypeIds);
        List<HouseLeaseConstEntity> houseLeaseConstEntities = houseConstMapper.selectBatchIds(list);
        List<String> arrayList = new ArrayList<>();
        for (HouseLeaseConstEntity houseLeaseConstEntity : houseLeaseConstEntities) {
            arrayList.add(houseLeaseConstEntity.getHouseConstName());
        }
        return arrayList;
    }

    @Override
    public List<Long> getConstIdByType(Integer i) {
        return houseConstMapper.getConstIdByType(i);
    }

    @Override
    public List<String> getShopTags(Long shopFacility) {
        return houseConstMapper.getConstNameByCode(shopFacility);
    }

    @Override
    public Map<String, Object> getAddShopTags(Integer facilityType, Integer peopleType) {
        Map<String, Object> map = new HashMap<>(16);
        // 获取配套设施
        QueryWrapper<HouseLeaseConstEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("house_const_type", facilityType);
        List<HouseLeaseConstEntity> facilityList = houseConstMapper.selectList(wrapper);

        // 获取客流人群
        QueryWrapper<HouseLeaseConstEntity> query = new QueryWrapper<>();
        query.eq("house_const_type", peopleType);
        List<HouseLeaseConstEntity> peopleList = houseConstMapper.selectList(query);

        map.put("facility", facilityList);
        map.put("people", peopleList);

        return map;
    }

    @Override
    public Map<String, Object> getTag() {
        Map<String, Object> map = new HashMap<>(32);

        // 封装所属类型标签
        QueryWrapper<HouseLeaseConstEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("house_const_type", 7).select("id", "house_const_name");
        List<HouseLeaseConstEntity> typeList = houseConstMapper.selectList(wrapper);
        map.put("type", typeList);
        // 封装所属行业标签
        QueryWrapper<HouseLeaseConstEntity> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("house_const_type", 8).select("id", "house_const_name");
        List<HouseLeaseConstEntity> business = houseConstMapper.selectList(wrapper2);
        map.put("business", business);

        return map;
    }

    private boolean isEmpty(String str) {
        return str == null || "".equals(str.trim()) || "null".equals(str) || "undefined".equals(str);
    }


}
