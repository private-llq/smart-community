package com.jsy.community.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.ICarPositionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.mapper.CarPositionMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/*定时任务判断车位绑定是否过期，过期解绑*/
@Configuration
@EnableScheduling
public class Sche {

    @Resource
    private CarPositionMapper carPositionMapper;

    @Scheduled(fixedDelay =10000 )//固定的延迟时间点为为毫秒
    public  void  sche(){
        List<CarPositionEntity> list = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().eq("binding_status",1).eq("car_pos_status",2));
        if (list!=null) {
            for (CarPositionEntity carPositionEntity : list) {
                LocalDateTime endTime = carPositionEntity.getEndTime();
                long end= endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                LocalDateTime nowTime = LocalDateTime.now();
                long now = nowTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                if(end<now){
                    int relieve = carPositionMapper.relieve(carPositionEntity.getId());
                }

            }
        }
    }

}