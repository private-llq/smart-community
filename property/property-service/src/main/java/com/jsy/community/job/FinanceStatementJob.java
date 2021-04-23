package com.jsy.community.job;

import com.jsy.community.api.IPropertyFinanceStatementService;
import com.jsy.community.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
@Slf4j
public class FinanceStatementJob extends QuartzJobBean {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceStatementService statementService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        statementService.timingStatement();
    }
}
