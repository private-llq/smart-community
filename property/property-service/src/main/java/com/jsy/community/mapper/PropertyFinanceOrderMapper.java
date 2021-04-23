package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import com.jsy.community.vo.property.UserPropertyFinanceOrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业缴费账单
 * @author: Hu
 * @create: 2021-04-20 15:56
 **/
public interface PropertyFinanceOrderMapper extends BaseMapper<PropertyFinanceOrderEntity> {
    /**
     * @Description: 查询认证过的所有id集合
     * @author: Hu
     * @since: 2021/4/21 13:34
     * @Param:
     * @return:
     */
    List<Long> communityIdList();

    /**
     * @Description:
     * @author: Hu
     * @since: 2021/4/21 14:04
     * @Param:
     * @return:
     */
    List<HouseEntity> selectHouseAll(Long id);

    /**
     * @Author: Pipi
     * @Description: 获取上一个月的需要结算和被驳回的账单
     * @Param: :社区ID列表
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Date: 2021/4/22 10:26
     **/
    List<PropertyFinanceOrderEntity> queryNeedStatementOrderListByCommunityIdAndOrderTime(List<Long> communityIdS);

    /**
     *@Author: Pipi
     *@Description: 批量更新结算状态为待审核,更新对象为被驳回的
     *@Param: statementOrderNumS:
     *@Return: java.lang.Integer
     *@Date: 2021/4/22 17:33
     **/
    Integer updateRejectStatementStatusByIdS(List<Long> statementOrderNumS);
    
    /**
     *@Author: Pipi
     *@Description: 批量更新结算状态为待审核,更新对象为未结算的
     *@Param: statementOrderUpdateMap: 
     *@Return: java.lang.Integer
     *@Date: 2021/4/23 10:41
     **/
    Integer updateStatementStatusByIdS(@Param("map")HashMap<String, List<Long>> statementOrderUpdateMap);

    /**
    * @Description: 根据收款单号批量查询列表
     * @Param: [receiptNums,query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    List<PropertyFinanceOrderEntity> queryByReceiptNums(@Param("receiptNums")Collection<String> receiptNums, @Param("query")PropertyFinanceOrderEntity query);

    /**
    * @Description: 账单号模糊查询收款单号列表
     * @Param: [orderNum]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    @Select("select distinct re.receipt_num from t_property_finance_order o \n" +
        "join t_property_finance_receipt re on o.receipt_num = re.receipt_num \n" +
        "where o.receipt_num is not null and o.order_num like concat('%',#{orderNum},'%')")
    List<String> queryReceiptNumsListByOrderNumLike(String orderNum);

    /**
     * @Description: 查询房间所有未缴账单
     * @author: Hu
     * @since: 2021/4/22 10:24
     * @Param:
     * @return:
     */
    List<PropertyFinanceOrderVO> houseCost(@Param("houseId") Long houseId);
    /**
     * @Description: 查询房间用户姓名
     * @author: Hu
     * @since: 2021/4/22 10:24
     * @Param:
     * @return:
     */
    UserPropertyFinanceOrderVO findUser(Long houseId);
}
