package com.jsy.community.api;

import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.LivingPaymentQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.qo.proprietor.RemarkQO;
import com.jsy.community.vo.*;

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
    PaymentDetailsVO add(LivingPaymentQO livingPaymentQO);

    /**
     * 通过组户号查询订单详情
     * @param groupQO
     * @return
     */
    List<GroupVO> selectGroup(GroupQO groupQO);


    /**
     * 查询每月订单详情
     * @param baseQO
     * @return
     */
    Map<String, List<PaymentRecordsVO>> selectOrder(BaseQO<PaymentRecordsQO> baseQO);

    /**
     * 默认查询所有缴费信息
     * @param
     * @return
     */
    List<DefaultHouseOwnerVO>  selectList(String userId);
    /**
     * @Description: 添加订单备注
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    void addRemark(RemarkQO remarkQO);

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
    List<GroupVO> selectGroupAll(String userId);
}
