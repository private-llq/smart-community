package com.jsy.community.api;

import com.jsy.community.qo.cebbank.*;
import com.jsy.community.vo.cebbank.*;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费服务
 * @Date: 2021/11/15 17:20
 * @Version: 1.0
 **/
public interface CebBankService {

    /**
     * @author: Pipi
     * @description: 光大银行用户注册
     * @param cebLoginQO:
     * @return: void
     * @date: 2021/11/15 17:25
     **/
    String login(CebLoginQO cebLoginQO);

    /**
     * @author: Pipi
     * @description: 获取云缴费sessionId
     * @param mobile: 手机号
         * @param devicetype: // 1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序-部分接口必填
     * @return: {@link String}
     * @date: 2021/12/6 9:56
     **/
    String getCebBankSessionId(String mobile, String devicetype);

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @param categoryQO:
     * @return: java.lang.String
     * @date: 2021/11/17 17:50
     **/
    CebQueryCityContributionCategoryVO queryCityContributionCategory(CebQueryCityContributionCategoryQO categoryQO);

    /**
     * @author: Pipi
     * @description: 查询缴费项目
     * @param projectQO:
     * @return: {@link CebQueryContributionProjectVO}
     * @date: 2021/11/23 15:37
     **/
    CebQueryContributionProjectVO queryContributionProject(CebQueryContributionProjectQO projectQO);

    /**
     * @author: Pipi
     * @description: 查询缴费账单信息
     * @param billInfoQO:
     * @return: {@link CebQueryBillInfoVO}
     * @date: 2021/11/23 17:14
     **/
    CebQueryBillInfoVO queryBillInfo(CebQueryBillInfoQO billInfoQO);

    /**
     * @author: Pipi
     * @description: 查询手机充值缴费账单
     * @param cebQueryMobileBillQO:
     * @return: {@link CebQueryMobileBillVO}
     * @date: 2021/11/23 18:06
     **/
//    CebQueryMobileBillVO queryMobileBill(CebQueryMobileBillQO cebQueryMobileBillQO);

    /**
     * @author: Pipi
     * @description: 查询缴费记录
     * @param recordQO:
     * @return: {@link CebQueryContributionRecordVO}
     * @date: 2021/11/23 18:11
     **/
//    CebQueryContributionRecordVO queryContributionRecord(CebQueryContributionRecordQO recordQO);

    /**
     * @author: Pipi
     * @description: 查询缴费记录详情
     * @param infoQO:
     * @return: {@link CebContributionRecordDetailVO}
     * @date: 2021/11/23 18:14
     **/
//    CebContributionRecordDetailVO queryContributionRecordInfo(CebQueryContributionRecordInfoQO infoQO);

    /**
     * @author: Pipi
     * @description: 创建收银台
     * @param deskQO:
     * @return: {@link CebCashierDeskVO}
     * @date: 2021/11/23 18:21
     **/
//    CebCashierDeskVO createCashierDesk(CebCreateCashierDeskQO deskQO);
}
