package com.jsy.community.task;

import com.jsy.community.api.IHouseReserveService;
import com.jsy.community.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: Pipi
 * @Description: 自动完成看房定时任务
 * @Date: 2021/3/31 10:04
 * @Version: 1.0
 **/
@Service
@Slf4j
public class CompleteCheckingScheduler {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseReserveService iHouseReserveService;

    /**
     * @Author: Pipi
     * @Description: 每天凌晨1秒时, 自动完成上一天的接受看房的预约修改为完成看房状态
     * @param: :
     * @Return: void
     * @Date: 2021/3/31 11:10
     **/
    @Scheduled(cron = "01 0 0 * * ?")
    private void completeChecking() {
        log.info("执行完成看房任务,执行时间{}!", new Date().toString());
        Integer completeNum = iHouseReserveService.timingCompleteChecking();
        log.info("一共自动确认完成看房{}条预约!", completeNum);
    }

}
