package com.jsy.community.job;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.jsy.community.api.IPropertyFinanceCycleService;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import org.apache.dubbo.config.annotation.DubboReference;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 财务定时产生结算单任务
 * @Date: 2021/4/21 14:50
 * @Version: 1.0
 **/
@Component
public class FinanceStatementJob extends QuartzJobBean {

//    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceCycleService cycleService;

//    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService financeOrderService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 获取今天的号数
        Integer dayOfMonth = LocalDateTimeUtil.now().getDayOfMonth();
        List<Long> communityIdS = cycleService.needStatementCommunityId(dayOfMonth);
        if (communityIdS != null) {
            // 1获取这些社区的上个月的已收款的未结算的账单和被驳回的账单
            List<PropertyFinanceOrderEntity> needStatementOrderList = financeOrderService.getNeedStatementOrderList(communityIdS);
            // 2.1将已收款的未结算的账单生成结算单
            // 2.2将被驳回的账单的状态更新为待结算
        }

        System.out.println(1);
    }
}
