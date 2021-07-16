package com.jsy.community.job;

import com.jsy.community.api.ICommunityFunService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: com.jsy.community
 * @description:  每天0点更新数据库社区趣事浏览量
 * @author: Hu
 * @create: 2021-03-18 14:58
 **/
@Component
public class DateTimeJob extends QuartzJobBean{

    @Resource
    private ICommunityFunService communityFunService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        communityFunService.listByUpdate();
    }
}
