package com.jsy.community.job;

import com.jsy.community.api.IPropertyFinanceOrderService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @program: com.jsy.community
 * @description:  财务账单定时任务
 * @author: Hu
 * @create: 2021-04-22 09:21
 **/
@Component
public class FinanceBillJob extends QuartzJobBean {

//    @Resource
    private IPropertyFinanceOrderService propertyFinanceOrderService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        propertyFinanceOrderService.updateDays();
        propertyFinanceOrderService.updatePenalSum();
    }
}
