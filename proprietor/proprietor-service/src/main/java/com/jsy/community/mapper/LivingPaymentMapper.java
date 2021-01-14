package com.jsy.community.mapper;

import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.vo.DefaultHouseOwnerVO;
import com.jsy.community.vo.GroupVO;
import com.jsy.community.vo.PaymentRecordsVO;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 生活缴费mapper层
 * @author: Hu
 * @create: 2020-12-11 09:31
 **/
public interface LivingPaymentMapper {

    /**
     * @Description: 默认查询所有缴费信息
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    List<DefaultHouseOwnerVO> selectList(String userId);

    /**
     * @Description: 查询每月订单记录
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    List<PaymentRecordsVO> selectOrder(PaymentRecordsQO paymentRecordsQO);

    /**
     * @Description: 查询组下面已经缴过费的户号
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    List<GroupVO> selectGroup(GroupQO groupQO);
    /**
     * @Description: 查询已经缴过费全部的户号
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    List<GroupVO> selectGroupAll(String userId);
}
