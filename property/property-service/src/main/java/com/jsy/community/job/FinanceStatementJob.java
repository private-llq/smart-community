package com.jsy.community.job;

import com.jsy.community.api.IPropertyFinanceStatementService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: Pipi
 * @Description: 财务定时产生结算单任务
 * @Date: 2021/4/21 14:50
 * @Version: 1.0
 **/
@Component
@Slf4j
public class FinanceStatementJob extends QuartzJobBean {

    @Resource
    private IPropertyFinanceStatementService statementService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("开始执行定时结算任务!");
        statementService.timingStatement();
        log.info("执行定时结算任务结束!");
    }
}
