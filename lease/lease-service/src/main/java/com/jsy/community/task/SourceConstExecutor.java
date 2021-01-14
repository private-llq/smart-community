package com.jsy.community.task;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.mapper.HouseConstMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author YuLF
 * @since 2020-12-11 10:17
 * 资源常量执行器：1.负责 t_house_const  常量的 存取
 */
@Service
@Order()
public class SourceConstExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SourceConstExecutor.class);

    @Resource
    public RedisTemplate<String, Object> redisTemplate;

    @Resource
    private HouseConstMapper houseConstMapper;

    @PostConstruct
    public void initSourceConst(){
        logger.info("com.jsy.community.task.SourceConstExecutor：{}","服务启动：装载houseConst常量数据");
        setRedisForHouseConst(getAllHouseConstForDatabases());
    }

    /**
     * 从数据库获取所有t_house_const 常量
     */
    private List<HouseLeaseConstEntity> getAllHouseConstForDatabases(){
        return houseConstMapper.getAllHouseConstForDatabases();
    }

    private void setRedisForHouseConst(List<HouseLeaseConstEntity> list){
        //1.把所有 house_const_type 存入Set去重 得到所有常量类型
        Set<String>  houseConstType = list.stream().map(HouseLeaseConstEntity::getHouseConstType).collect(Collectors.toSet());
        //2.遍历所有 house_const_type 类型   存入 redis
        //相同类型的数据
        for(String constType : houseConstType){
            List<HouseLeaseConstEntity> alikeType = new ArrayList<>();
            for(HouseLeaseConstEntity entity : list ){
                if( entity.getHouseConstType().equals(constType) ){
                    alikeType.add(entity);
                }
            }
            //相同类型的数据存入redis
            redisTemplate.opsForValue().set("houseConst:"+constType, JSONObject.toJSONString(alikeType));
            list.removeAll(alikeType);
        }
    }

    /**
     * 每周一 凌晨4点执行 定时任务
     * @author YuLF
     * @since  2020/12/11 14:19
     */
    @Scheduled(cron = "0 0 4 ? * mon")
    public void updateSourceConst(){
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：资源常量定时任务启动：从数据库获取资源更新至Redis!");
        setRedisForHouseConst(getAllHouseConstForDatabases());
    }


}
