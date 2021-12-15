package com.jsy.community.api;

import com.jsy.community.entity.lease.AiliAppPayRecordEntity;

/**
 * @program: com.jsy.community
 * @description: 支付宝退款
 * @author: Hu
 * @create: 2021-12-15 16:02
 **/
public interface IAliAppRefundService {
    /**
     * @Description: 单查详情
     * @author: Hu
     * @since: 2021/12/15 16:10
     * @Param:
     * @return:
     */
    AiliAppPayRecordEntity selectByOrder(String orderNo);

    /**
     * @Description: 退款
     * @author: Hu
     * @since: 2021/12/15 16:16
     * @Param:
     * @return:
     */
    void refund(AiliAppPayRecordEntity ailiAppPayRecordEntity);
}
