package com.jsy.community.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.vo.DefaultHouseOwnerVO;
import com.jsy.community.vo.PaymentRecordsVO;
import org.apache.ibatis.annotations.Param;
import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.vo.GroupVO;

import java.util.List;
import java.util.Map;

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
    Page<PaymentRecordsVO> selectOrder(Page<PaymentRecordsVO> page, @Param("query") PaymentRecordsQO paymentRecordsQO);

    /**
     * @Description: 查询组下面已经缴过费的户号
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    List<GroupVO> selectGroup(GroupQO groupQO);
}
