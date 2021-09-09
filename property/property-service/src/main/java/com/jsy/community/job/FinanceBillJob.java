package com.jsy.community.job;

import com.jsy.community.api.IFinanceBillService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: com.jsy.community
 * @description:  财务账单定时任务
 * @author: Hu
 * @create: 2021-04-22 09:21
 **/
@Component
@Slf4j
public class FinanceBillJob extends QuartzJobBean {

    @Resource
    IFinanceBillService financeBillService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("开始更新账单");
        financeBillService.updateAnnual();
        financeBillService.updateMonth();
        financeBillService.updatePenalSum();
        financeBillService.updateTemporary();
        log.info("账单更新完成");
    }



}
