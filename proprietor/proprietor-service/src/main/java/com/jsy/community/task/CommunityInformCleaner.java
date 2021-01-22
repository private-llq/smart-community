package com.jsy.community.task;

import com.jsy.community.annotation.RedisSingleInstanceLock;
import com.jsy.community.api.IUserInformService;
import com.jsy.community.constant.Const;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author YuLF
 * @since 2021-1-9 10:17
 * 社区推送消息清理器
 * 避免数据库保存的老旧无用数据 堆积
 */
@Service
@ConditionalOnProperty(value = "jsy.sys.clear.inform.enable", havingValue = "true")
public class CommunityInformCleaner {

    private static final Logger logger = LoggerFactory.getLogger(CommunityInformCleaner.class);

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserInformService userInformService;

    @Resource
    public RedisTemplate<String, Object> redisTemplate;

    @Value("${jsy.sys.clear.inform.expire}")
    private Integer clearInformExpireDay;

    @PostConstruct
    public void initSourceConst(){
        clearPushInform();
    }


    public boolean isStart(){
        return true;
    }


    /**
     * 每周一 凌晨1点执行 定时任务
     * @author YuLF
     * @since  2020/1/9 14:19
     */
    @RedisSingleInstanceLock(lockKey = "communityInform", waitTimout = 50)
    @Scheduled(cron = "0 0 1 ? * mon")
    public void clearPushInform(){

        //向redis 存一把锁 让此模块
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：清理社区推送消息执行开始:");
        //1.获取 clearInformExpireTime 之前的时间
        String beforeTime = getClearInformExpireTime(clearInformExpireDay);
        //如果 数据库 推送消息 小于 beforeTime 的都是超过过期时间的消息 都将删除
        Integer delRow = userInformService.RegularCleaning(beforeTime);
        logger.info("本次清理社区推送消息" + beforeTime + "之前的数据共" + delRow + "条!");
    }

    /**
     * 拿到多少天之前的时间
     * 根据 清理时间 秒 获取到  clearInformExpireTime 天之前的 时间
     * @author YuLF
     * @since  2021/1/11 11:31
     * @Param  clearInformExpireTime（过期时间）   超过多少天 将被清理掉推送消息
     */
    private  String getClearInformExpireTime(Integer clearInformExpireTime) {
        /* 转换为时间格式 */
        DateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        /* 日历对象 */
        Calendar ca = Calendar.getInstance();
        /* 当前时间 -clearInformExpireTime */
        ca.add( Calendar.DATE, - clearInformExpireTime );
        Date expireTime = ca.getTime();
        return fmt.format(expireTime);
    }

}
