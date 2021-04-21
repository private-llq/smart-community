package com.jsy.community.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @Author: Pipi
 * @Description: 财务定时产生结算单任务
 * @Date: 2021/4/21 14:50
 * @Version: 1.0
 **/
@Component
public class FinanceStatementJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(1);
    }
}
