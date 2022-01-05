package com.jsy.community.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.entity.LivingExpensesTypeEntity;
import com.jsy.community.mapper.LivingExpensesTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Pipi
 * @Description: 生活缴费图标任务
 * @Date: 2022/1/5 15:30
 * @Version: 1.0
 **/
@Service
@Slf4j
public class CostIconTask {
    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private LivingExpensesTypeMapper typeMapper;

    /**
     * @author: Pipi
     * @description: 项目启动时,更新缴费图标缓存
     * @date: 2022/1/5 15:38
     **/
    @PostConstruct
    public void setRegionToRedis(){
        log.info("服务启动任务执行：更新缴费图标缓存");
        QueryWrapper<LivingExpensesTypeEntity> queryWrapper = new QueryWrapper<>();
        List<LivingExpensesTypeEntity> livingExpensesTypeEntities = typeMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(livingExpensesTypeEntities)) {
            Map<Long, String> map = livingExpensesTypeEntities.stream().collect(Collectors.toMap(LivingExpensesTypeEntity::getId, LivingExpensesTypeEntity::getTypeName));
            redisTemplate.opsForValue().set("costIcon", JSON.toJSONString(map));
        }
    }
}
