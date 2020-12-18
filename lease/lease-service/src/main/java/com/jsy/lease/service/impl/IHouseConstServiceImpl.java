package com.jsy.lease.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.lease.api.IHouseConstService;
import com.jsy.lease.mapper.HouseConstMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-11 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class IHouseConstServiceImpl extends ServiceImpl<HouseConstMapper, HouseLeaseConstEntity> implements IHouseConstService {

    @Resource
    private HouseConstMapper houseConstMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 根据常量类型 获取属于这个类型的List数据
     * @author YuLF
     * @Param type                常量类型
     * @return 返回这个类型对应的List
     * @since 2020/12/11 11:36
     */
    @Override
    public List<HouseLeaseConstEntity> getHouseConstListByType(String type) {
        var list = JSON.parseObject(String.valueOf(redisTemplate.opsForValue().get("houseConst:" + type)), List.class);
        List<HouseLeaseConstEntity> houseLeaseConstEntityList = new ArrayList<>(list.size());
        for (Object o : list) {
            JSONObject jsonObject = JSON.parseObject(String.valueOf(o));
            HouseLeaseConstEntity entity = new HouseLeaseConstEntity(jsonObject.getLong("houseConstCode"),
                    jsonObject.getString("houseConstName"),
                    jsonObject.getString("houseConstType"),
                    jsonObject.getString("annotation"));
            if ( !isEmpty(jsonObject.getString("houseConstValue")) ) {
                entity.setHouseConstValue(jsonObject.getString("houseConstValue"));
            }
            houseLeaseConstEntityList.add(entity);
        }
        return houseLeaseConstEntityList;
    }
    
    @Override
    public List<HouseLeaseConstEntity> getTag(Integer id) {
        QueryWrapper<HouseLeaseConstEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("house_const_type",id);
        return houseConstMapper.selectList(wrapper);
    }
    
    private boolean isEmpty(String str){
        return str == null || str.trim().equals("") || "null".equals(str) || "undefined".equals(str);
    }


}