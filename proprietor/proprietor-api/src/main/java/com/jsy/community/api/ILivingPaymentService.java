package com.jsy.community.api;

import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.LivingPaymentQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.vo.GroupVO;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 生活缴费service
 * @author: Hu
 * @create: 2020-12-11 09:30
 **/
public interface ILivingPaymentService {

    /**
     * 生活缴费生成订单保存数据
     * @param livingPaymentQO
     * @return
     */
    void add(LivingPaymentQO livingPaymentQO);

    /**
     * 通过组户号查询订单详情
     * @param groupQO
     * @return
     */
    List<GroupVO> selectGroup(GroupQO groupQO);


    /**
     * 查询每月订单详情
     * @param paymentRecordsQO
     * @return
     */
    Map<String, Object> selectOrder(PaymentRecordsQO paymentRecordsQO);

    /**
     * 默认查询所有缴费信息
     * @param
     * @return
     */
    List selectList(String userId);
}
