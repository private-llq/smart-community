package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceOrderOperationQO;
import com.jsy.community.qo.property.FinanceOrderQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:28
 **/
public interface IPropertyFinanceOrderService extends IService<PropertyFinanceOrderEntity> {
    
    
    /**
     * @Description: 根据收款单号批量查询列表
     * @Param: [receiptNums,query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
     **/
    List<PropertyFinanceOrderEntity> queryByReceiptNums(Collection<String> receiptNums,PropertyFinanceOrderEntity query);
    
    /**
     * @Description: 账单号模糊查询收款单号列表
     * @Param: [orderNum]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/22
     **/
    List<String> queryReceiptNumsListByOrderNumLike(String orderNum);
    
    
    /**
     * @Description: 查询房间所有未缴账单
     * @author: Hu
     * @since: 2021/4/22 10:23
     * @Param:
     * @return:
     */
    Map<String, Object> findList(AdminInfoVo userInfo, BaseQO<FinanceOrderQO> baseQO);
    
    /**
     * @Description: 分页查询 (财务模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    PageInfo<PropertyFinanceOrderEntity> queryPage(BaseQO<PropertyFinanceOrderEntity> baseQO);
    
    /**
     *@Author: Pipi
     *@Description: 财务模块查询导出账单表数据
     *@Param: propertyFinanceOrderEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/25 15:52
     **/
    List<PropertyFinanceOrderEntity> queryExportExcelList(PropertyFinanceOrderEntity propertyFinanceOrderEntity);
    
    /**
     * @Description: 查询一条账单详情
     * @author: Hu
     * @since: 2021/4/23 17:14
     * @Param:
     * @return:
     */
    PropertyFinanceOrderVO getOrderNum(AdminInfoVo userInfo, Long id);
    
    /**
     * @Description: 分页查询已缴费 (缴费模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/24
     **/
    PageInfo<PropertyFinanceOrderEntity> queryPaid(BaseQO<PropertyFinanceOrderEntity> baseQO);
    
    /**
     *@Author: Pipi
     *@Description: 分页查询结算单的账单列表
     *@Param: baseQO:
     *@Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/24 11:44
     **/
    Page<PropertyFinanceOrderEntity> queryPageByStatemenNum(BaseQO<StatementNumQO> baseQO);
    
    /**
     * @Description: 根据用户查询所有账单
     * @author: Hu
     * @since: 2021/7/5 11:19
     * @Param:
     * @return:
     */
    List<PropertyFinanceOrderEntity> selectByUserList(PropertyFinanceOrderEntity qo);
    
    /**
     * @Description: 查询应缴总金额
     * @author: Hu
     * @since: 2021/7/5 16:11
     * @Param:
     * @return:
     */
    BigDecimal getTotalMoney(String ids);
    
    
    /**
     * @Description: 批量修改物业账单
     * @Param: [payType, tripartiteOrder, ids]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/7/7
     **/
    void updateOrderStatusBatch(Integer payType, String tripartiteOrder , String[] ids);
    
    /**
     * @Description: 查询一条物业账单详情
     * @author: Hu
     * @since: 2021/7/6 11:14
     * @Param:
     * @return:
     */
    PropertyFinanceOrderEntity findOne(Long orderId);
    
    
    /**
     * @Description: 修改订单优惠金额
     * @author: Hu
     * @since: 2021/8/7 14:24
     * @Param:
     * @return:
     */
    void updateOrder(Long id, BigDecimal coupon);
    
    /**
     * @Description: 删除一条账单信息
     * @author: Hu
     * @since: 2021/8/7 14:34
     * @Param:
     * @return:
     */
    void delete(Long id);
    
    /**
     * @Description: 删除多条账单
     * @author: Hu
     * @since: 2021/8/7 14:36
     * @Param:
     * @return:
     */
    void deletes(FinanceOrderOperationQO financeOrderOperationQO);
    
    /**
     * @Description: 修改一条订单状态
     * @author: Hu
     * @since: 2021/8/9 14:09
     * @Param:
     * @return:
     */
    void update(Long id);
    
    /**
     * @Description: 条件修改订单状态
     * @author: Hu
     * @since: 2021/8/9 14:09
     * @Param:
     * @return:
     */
    void updates(FinanceOrderOperationQO financeOrderOperationQO);
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收入
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/17 16:00
     **/
    List<PropertyFinanceFormEntity> getFinanceFormCommunityIncome(PropertyFinanceFormEntity propertyFinanceFormEntity, List<Long> communityIdList);
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收费报表-账单生成时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/18 11:08
     **/
    List<PropertyFinanceFormChargeEntity> getFinanceFormCommunityChargeByOrderGenerateTime(PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity, List<Long> communityIdList);
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收费报表-账单周期时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/18 11:08
     **/
    List<PropertyFinanceFormChargeEntity> getFinanceFormCommunityChargeByOrderPeriodTime(PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity, List<Long> communityIdList);
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-收款报表
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/19 9:31
     **/
    List<PropertyCollectionFormEntity> getCollectionFormCollection(PropertyCollectionFormEntity propertyCollectionFormEntity, List<Long> communityIdList);
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-账单统计-账单生成时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/19 11:08
     **/
    PropertyCollectionFormEntity getCollectionFormOrderByOrderGenerateTime(PropertyCollectionFormEntity propertyCollectionFormEntity);
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-账单统计-账单周期时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/19 11:08
     **/
    PropertyCollectionFormEntity getCollectionFormOrderByOrderPeriodTime(PropertyCollectionFormEntity propertyCollectionFormEntity);
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收入数据
     *@Param: propertyFinanceFormEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceFormEntity>
     *@Date: 2021/8/19 15:52
     **/
    List<PropertyFinanceFormEntity> queryExportExcelFinanceFormList(PropertyFinanceFormEntity propertyFinanceFormEntity, List<Long> communityIdList);
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收费报表
     *@Param: propertyFinanceFormChargeEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceFormChargeEntity>
     *@Date: 2021/8/19 15:52
     **/
    List<PropertyFinanceFormChargeEntity> queryExportExcelChargeList(PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity, List<Long> communityIdList);
    
    /**
     *@Author: DKS
     *@Description: 导出收款报表-收款报表
     *@Param: propertyCollectionFormEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyCollectionFormEntity>
     *@Date: 2021/8/19 15:52
     **/
    List<PropertyCollectionFormEntity> queryExportExcelCollectionFormList(PropertyCollectionFormEntity propertyCollectionFormEntity, List<Long> communityIdList);
    
    /**
     *@Author: DKS
     *@Description: 导出收款报表-账单统计
     *@Param: propertyCollectionFormEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyCollectionFormEntity>
     *@Date: 2021/8/19 15:52
     **/
    List<PropertyCollectionFormEntity> queryExportExcelCollectionFormOrderList(PropertyCollectionFormEntity propertyCollectionFormEntity);
    
    /**
     * @Description: 新增物业账单临时收费
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/26 09:35
     **/
    boolean addTemporaryCharges(PropertyFinanceOrderEntity propertyFinanceOrderEntity);

    /**
     * @Description: 批量修改账单状态
     * @author: Hu
     * @since: 2021/8/31 14:42
     * @Param:
     * @return:
     */
    void updateStatusIds(String ids,Integer hide);

    /**
     * @Description: 批量删除账单
     * @author: Hu
     * @since: 2021/8/31 14:49
     * @Param:
     * @return:
     */
    void deleteIds(String ids);

    /**
     * @Description: 查询当前小区缴费项目
     * @author: Hu
     * @since: 2021/8/31 15:01
     * @Param:
     * @return:
     */
    List<PropertyFeeRuleEntity> getFeeList(Long adminCommunityId);
}
