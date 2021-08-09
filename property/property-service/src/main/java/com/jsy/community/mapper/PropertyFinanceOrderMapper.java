package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.PropertyFinanceCountEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.qo.property.FinanceOrderOperationQO;
import com.jsy.community.qo.property.FinanceOrderQO;
import com.jsy.community.vo.StatementOrderVO;
import com.jsy.community.vo.property.FinanceOrderEntityVO;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import com.jsy.community.vo.property.UserPropertyFinanceOrderVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.*;

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
     * @Description:  查询一个小区下的所有id
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
    Integer updateStatementStatusByIdS(@Param("map")Map<String, List<Long>> statementOrderUpdateMap);

    /**
    * @Description: 根据收款单号批量查询列表
     * @Param: [receiptNums,query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    List<PropertyFinanceOrderEntity> queryByReceiptNums(@Param("receiptNums") Collection<String> receiptNums, @Param("query")PropertyFinanceOrderEntity query);

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

    /**
    * @Description: 查出当前社区所有订单中所有不重复uid
     * @Param: [communityId]
     * @Return: java.util.Set<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    @Select("select distinct uid from t_property_finance_order where community_id = #{communityId}")
    Set<String> queryUidSetByCommunityId(Long communityId);


    /**
     *@Author: Pipi
     *@Description: 根据账单号模糊查询相关的结算单号列表
     *@Param: orderNum:
     *@Return: java.util.List<java.lang.String>
     *@Date: 2021/4/23 17:42
     **/
    @Select("select statement_num from t_property_finance_order where order_num like concat('%', #{orderNum}, '%')")
    List<String> queryStatementNumLikeOrderNum(String orderNum);

    /**
     *@Author: Pipi
     *@Description: 根据结算单号查询相关账单列表
     *@Param: statementNums:
     *@Return: java.util.List<com.jsy.community.vo.StatementOrderVO>
     *@Date: 2021/4/24 16:22
     **/
    List<StatementOrderVO> queryOrderByStatementNum(Set<String> statementNums);
    
    List<BigDecimal> test1(@Param("outList")ArrayList<ArrayList<String>> outList);

    /**
     *@Author: Pipi
     *@Description: 缴费按月统计
     *@Param: query:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/27 10:33
     **/
    List<FinanceOrderEntityVO> statisticsReceipt(@Param("query") PropertyFinanceCountEntity query);

    /**
     *@Author: Pipi
     *@Description: 缴费按月应收统计
     *@Param: query: 
     *@Return: java.util.List<com.jsy.community.vo.property.FinanceOrderEntityVO>
     *@Date: 2021/4/27 14:58
     **/
    List<FinanceOrderEntityVO> statisticsReceivable(@Param("query") PropertyFinanceCountEntity query);

    /**
     *@Author: Pipi
     *@Description: 缴费按月已结算统计
     *@Param: query:
     *@Return: java.util.List<com.jsy.community.vo.property.FinanceOrderEntityVO>
     *@Date: 2021/4/28 9:25
     **/
    List<FinanceOrderEntityVO> settledStatistics(@Param("query") PropertyFinanceCountEntity query);

    /**
     *@Author: Pipi
     *@Description: 缴费按月未结算统计
     *@Param: query:
     *@Return: java.util.List<com.jsy.community.vo.property.FinanceOrderEntityVO>
     *@Date: 2021/4/28 9:29
     **/
    List<FinanceOrderEntityVO> unsettlementStatistics(@Param("query") PropertyFinanceCountEntity query);

    /**
     * @Description: 批量新增
     * @author: Hu
     * @since: 2021/6/2 10:47
     * @Param:
     * @return:
     */
    void saveList(@Param("list")List<PropertyFinanceOrderEntity> list);

    /**
     * @Description: 查询物业费金额
     * @author: Hu
     * @since: 2021/7/5 16:17
     * @Param:
     * @return:
     */
    BigDecimal getTotalMoney(String[] orderIds);
    /**
     * @Description: 根据传来的id集合查询账单
     * @author: Hu
     * @since: 2021/7/5 17:27
     * @Param:
     * @return:
     */
    List<PropertyFinanceOrderEntity> selectByIdsList(String[] ids);
    
    /**
    * @Description: 支付完成后-批量修改物业账单
     * @Param: [payType, tripartiteOrder, ids]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2021/7/7
    **/
    int updateOrderBatch(@Param("payType")Integer payType, @Param("tripartiteOrder")String tripartiteOrder, @Param("ids")String[] ids);

    /**
     * @Description: 查询所有账单
     * @author: Hu
     * @since: 2021/8/6 15:48
     * @Param:
     * @return:
     */
    List<PropertyFinanceOrderEntity> findList(@Param("page") long page, @Param("size") Long size, @Param("query") FinanceOrderQO query);

    /**
     * @Description: 查询总条数
     * @author: Hu
     * @since: 2021/8/6 16:18
     * @Param:
     * @return:
     */
    Integer getTotal(@Param("query") FinanceOrderQO query);

    /**
     * @Description: 修改一条订单状态
     * @author: Hu
     * @since: 2021/8/9 14:10
     * @Param:
     * @return:
     */
    @Update("update t_property_finance_order set hide=2 where id=#{id}")
    void updateStatus(Long id);

    /**
     * @Description: 条件修改订单状态
     * @author: Hu
     * @since: 2021/8/9 14:13
     * @Param:
     * @return:
     */
    void updates(FinanceOrderOperationQO financeOrderOperationQO);
}
