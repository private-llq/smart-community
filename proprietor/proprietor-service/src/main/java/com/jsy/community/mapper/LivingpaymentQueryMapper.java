package com.jsy.community.mapper;

import com.jsy.community.entity.PayGroupEntity;
import com.jsy.community.qo.livingpayment.PaymentRecordsQO;
import com.jsy.community.vo.livingpayment.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-02-26 13:59
 **/
public interface LivingpaymentQueryMapper {
    /**
     * @Description: 默认查询所有缴费信息
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    List<DefaultHouseOwnerVO> selectList(@Param("userId") String userId, @Param("page")Integer page, @Param("size")Integer size);

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
    List<GroupVO> selectGroup(@Param("groupName") String groupName, @Param("userId") String userId);
    /**
     * @Description: 查询已经缴过费全部的户号
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    List<GroupVO> selectGroupAll(String userId);

    /**
     * @Description: 查询一条缴费详情
     * @author: Hu
     * @since: 2021/1/15 15:39
     * @Param:
     * @return:
     */
    PaymentDetailsVO selectPaymentDetailsVO(@Param("id") Long id, @Param("userId")String userId);

    /**
     * @Description: 查询所有组
     * @author: Hu
     * @since: 2021/2/3 11:08
     * @Param:
     * @return:
     */
    List<PayGroupEntity> findGroup(String userId);
    /**
     * @Description: 查询一条账单详情
     * @author: Hu
     * @since: 2021/2/20 16:04
     * @Param:
     * @return:
     */
    TheBillingDetailsVO selectOrderId(Long id);

    /**
     * @Description: 查询全部户号
     * @author: Hu
     * @since: 2021/2/27 16:55
     * @Param:
     * @return:
     */
    List<FamilyIdVO> selectFamilyId(@Param("uid") String uid);
}
