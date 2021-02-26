package com.jsy.community.api;

import com.jsy.community.qo.livingpayment.PaymentRecordsQO;
import com.jsy.community.vo.livingpayment.*;
import com.jsy.community.vo.shop.PaymentRecordsMapVO;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-02-26 13:58
 **/
public interface ILivingpaymentQueryService {
    /**
     * @Description: 生成假账单
     * @author: Hu
     * @since: 2021/2/26 14:35
     * @Param:
     * @return:
     */
    Map getPayDetails(String doorNo, Long id);

    /**
     * 通过组户号查询订单详情
     * @param
     * @return
     */
    List<GroupVO> selectGroup(String groupName, String userId);


    /**
     * 查询每月订单详情
     * @param paymentRecordsQO
     * @return
     */
    PaymentRecordsMapVO selectOrder(PaymentRecordsQO paymentRecordsQO);

    /**
     * 默认查询所有缴费信息
     * @param
     * @return
     */
    List<DefaultHouseOwnerVO>  selectList(String userId);

    /**
     * 查询当前登录人员自定义的分组
     * @param
     * @return
     */
    List<UserGroupVO> selectUserGroup(String userId);

    /**
     * @Description: 缴费凭证
     * @author: Hu
     * @since: 2020/12/28 15:53
     * @Param:
     * @return:
     */
    PayVoucherVO getOrderID(Long id);

    /**
     * 选择分组查询下面缴过费的水电气户号
     * @param
     * @return
     */
    PaymentRecordsMapVO selectGroupAll(String userId);

    /**
     * 缴费成功后返回的数据，暂时没用
     * @param id
     * @return
     */
    PaymentDetailsVO selectPaymentDetailsVO(Long id, String userId);

    /**
     * @Description: 查询一条订单详情
     * @author: Hu
     * @since: 2021/2/20 16:02
     * @Param:
     * @return:
     */
    TheBillingDetailsVO selectOrderId(Long id);
}
