package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.payment.UnionPayOrderRecordEntity;
import com.jsy.community.qo.ConsumeApplyOrderNotifyQO;
import com.jsy.community.qo.QueryBillInfoQO;
import com.jsy.community.qo.QueryTransListQO;
import com.jsy.community.vo.QueryBillInfoListVO;
import com.jsy.community.vo.UnionPayOrderVO;
import com.jsy.community.vo.UnionPayTransListVO;

/**
 * @Author: Pipi
 * @Description: 银联支付订单服务
 * @Date: 2021/4/26 16:39
 * @Version: 1.0
 **/
public interface UnionPayOrderRecordService extends IService<UnionPayOrderRecordEntity> {

    /**
     *@Author: Pipi
     *@Description: 银联消费下单
     *@Param: unionPayOrderRecordEntity:
     *@Return: com.jsy.community.vo.UnionPayOrderVO
     *@Date: 2021/4/26 16:56
     **/
    UnionPayOrderVO generateOrder(UnionPayOrderRecordEntity unionPayOrderRecordEntity);

    /**
     *@Author: Pipi
     *@Description: 支付完成后的订单逻辑
     *@Param: notifyQO:
     *@Return: void
     *@Date: 2021/5/6 14:48
     **/
    void updateOrderStatus(ConsumeApplyOrderNotifyQO notifyQO);

    /**
     *@Author: Pipi
     *@Description: 查询交易明细
     *@Param: queryTransListQO:
     *@Return: com.jsy.community.vo.UnionPayTransListVO
     *@Date: 2021/5/12 10:08
     **/
    UnionPayTransListVO queryTransList(QueryTransListQO queryTransListQO);

    /**
     *@Author: Pipi
     *@Description: 账单查询
     *@Param: queryBillInfoQO: 
     *@Return: com.jsy.community.vo.QueryBillInfoListVO
     *@Date: 2021/5/12 11:27
     **/
    QueryBillInfoListVO queryBillInfo(QueryBillInfoQO queryBillInfoQO);
}
