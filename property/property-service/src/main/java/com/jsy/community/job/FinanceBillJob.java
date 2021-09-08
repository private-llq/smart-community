package com.jsy.community.job;

import com.jsy.community.api.IFinanceBillService;
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
public class FinanceBillJob extends QuartzJobBean {

    @Resource
    IFinanceBillService financeBillService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("操作执行了");
//        financeBillService.updateDays();
        financeBillService.updatePenalSum();
        System.out.println(financeBillService);
        System.out.println("操作成功了");
    }



}
