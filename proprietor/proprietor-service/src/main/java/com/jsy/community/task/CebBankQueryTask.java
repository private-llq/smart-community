package com.jsy.community.task;

import com.jsy.community.api.UserLivingExpensesAccountService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: Pipi
 * @Description: 光大云缴费定时查询账单任务
 * @Date: 2022/1/11 9:47
 * @Version: 1.0
 **/
@Component
@Slf4j
public class CebBankQueryTask extends QuartzJobBean {
    @Resource
    private UserLivingExpensesAccountService accountService;
    /**
     * Execute the actual job. The job data map will already have been
     * applied as bean property values by execute. The contract is
     * exactly the same as for the standard Quartz execute method.
     * 光大云缴费定时查询账单任务
     * @param context
     * @see #execute
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("执行任务:光大云缴费定时查询账单任务,任务时间:{}", LocalDateTime.now());
        accountService.cebBankQueryTask();
        log.info("执行任务完成:光大云缴费定时查询账单任务,完成时间:{}", LocalDateTime.now());
    }
}
