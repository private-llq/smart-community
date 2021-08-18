package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.*;
import com.jsy.community.mapper.PropertyAdvanceDepositRecordMapper;
import com.jsy.community.mapper.PropertyDepositMapper;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.mapper.PropertyFinanceOrderMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceOrderOperationQO;
import com.jsy.community.qo.property.FinanceOrderQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @program: com.jsy.community
 * @description:  物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:31
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PropertyFinanceOrderServiceImpl extends ServiceImpl<PropertyFinanceOrderMapper, PropertyFinanceOrderEntity> implements IPropertyFinanceOrderService {
    @Autowired
    private PropertyFinanceOrderMapper propertyFinanceOrderMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseService houseService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserService userService;

    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceReceiptService propertyFinanceReceiptService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceStatementService propertyFinanceStatementService;
    
    @Autowired
    private PropertyDepositMapper propertyDepositMapper;
    
    @Autowired
    private PropertyAdvanceDepositRecordMapper propertyAdvanceDepositRecordMapper;

    /**
     * @Description: 查询房间所有未缴账单
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [userInfo, houseId]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object> findList(AdminInfoVo userInfo,BaseQO<FinanceOrderQO> baseQO) {
        //查询所有房间
        List<HouseEntity> list = houseService.selectAll();
        Map<Long, String> map = new HashMap<>();
        for (HouseEntity houseEntity : list) {
            map.put(houseEntity.getId(),houseEntity.getBuilding()+houseEntity.getUnit()+houseEntity.getNumber());
        }
        if(baseQO.getPage()==null||baseQO.getPage()==0){
            baseQO.setPage(1L);
        }
        List<PropertyFinanceOrderEntity> orderEntities = propertyFinanceOrderMapper.findList((baseQO.getPage()-1)*baseQO.getSize(),baseQO.getSize(),baseQO.getQuery());
        for (PropertyFinanceOrderEntity entity : orderEntities) {
            if (entity.getAssociatedType()==1){
                entity.setAddress(map.get(entity.getTargetId()));
            }
            entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum()).subtract(entity.getCoupon()).subtract(entity.getDeduction()));
        }
        Integer total = propertyFinanceOrderMapper.getTotal(baseQO.getQuery());
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("list",orderEntities);
        hashMap.put("total",total);
        return hashMap;
    }




    /**
    * @Description: 根据收款单号批量查询列表
     * @Param: [receiptNums,query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    @Override
    public List<PropertyFinanceOrderEntity> queryByReceiptNums(Collection<String> receiptNums, PropertyFinanceOrderEntity query){
        return propertyFinanceOrderMapper.queryByReceiptNums(receiptNums,query);
    }



    /**
     * @Description: 查询一条已缴费详情
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [userInfo, orderNum]
     * @return: com.jsy.community.vo.property.PropertyFinanceOrderVO
     */
    @Override
    public PropertyFinanceOrderVO getOrderNum(AdminInfoVo userInfo, Long id) {
        PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderMapper.selectById(id);
        HouseEntity houseEntity = houseService.getOne(new QueryWrapper<HouseEntity>().eq("id", propertyFinanceOrderEntity.getTargetId()));
        PropertyFinanceOrderVO financeOrderVO = new PropertyFinanceOrderVO();
        BeanUtils.copyProperties(propertyFinanceOrderEntity,financeOrderVO);
        BeanUtils.copyProperties(houseEntity,financeOrderVO);
        financeOrderVO.setHouseTypeText(houseEntity.getHouseType()==1?"商铺":"住宅");
        financeOrderVO.setId(propertyFinanceOrderEntity.getId());
        return financeOrderVO;
    }

    /**
    * @Description: 账单号模糊查询收款单号列表
     * @Param: [orderNum]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    @Override
    public List<String> queryReceiptNumsListByOrderNumLike(String orderNum){
        return propertyFinanceOrderMapper.queryReceiptNumsListByOrderNumLike(orderNum);
    }

    /**
    * @Description: 查询已缴费账单 (缴费模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/24
    **/
    @Override
    public PageInfo<PropertyFinanceOrderEntity> queryPaid(BaseQO<PropertyFinanceOrderEntity> baseQO){
        PropertyFinanceOrderEntity query = baseQO.getQuery();
        Page<PropertyFinanceOrderEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page,baseQO);
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq("community_id",query.getCommunityId());
        queryWrapper.eq("order_status", PropertyConstsEnum.OrderStatusEnum.ORDER_STATUS_PAID.getCode());
        queryWrapper.orderByDesc("create_time");
        if(query.getTargetId() != null){
            queryWrapper.eq("house_id",query.getTargetId());
        }
        if(query.getOrderStartDate() != null){
            queryWrapper.ge("order_time",query.getOrderStartDate());
        }
        if(query.getOrderEndDate() != null){
            queryWrapper.le("order_time",query.getOrderEndDate());
        }
        if(query.getReceiptStartDate() != null || query.getReceiptEndDate() != null){
            PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
            receiptEntity.setStartDate(query.getReceiptStartDate());
            receiptEntity.setEndDate(query.getReceiptEndDate());
            List<String> receiptNums = propertyFinanceReceiptService.queryReceiptNumsByCondition(receiptEntity);
            if(CollectionUtils.isEmpty(receiptNums)){
                return new PageInfo<>();
            }
            queryWrapper.in("receipt_num",receiptNums);
        }
        if(!StringUtils.isEmpty(query.getRealName())){
            //查出当前社区所有订单中所有不重复uid
            Set<String> allUidSet = propertyFinanceOrderMapper.queryUidSetByCommunityId(query.getCommunityId());
            LinkedList<String> allUidSetList = new LinkedList<>(allUidSet);
            //判断数量，in条件超过999，分割查询
            int size = 999;
            if(!CollectionUtils.isEmpty(allUidSet)){
                //确定查询次数
                int times = allUidSet.size()%size == 0 ? allUidSet.size()/size : allUidSet.size()/size + 1;
                //符合条件的uid
                List<String> uids = new LinkedList<>();
                int remain = allUidSet.size(); //剩余数据长度
                for(int i=0;i<times;i++){
                    List<String> targetUid = userService.queryUidOfNameLike(allUidSetList.subList(i * size, (i * size) + remain), query.getRealName());
                    if(!CollectionUtils.isEmpty(targetUid)){
                        uids.addAll(targetUid);
                    }
                    remain = remain > size ? remain : remain - size;
                }
                //添加查询条件
                if(CollectionUtils.isEmpty(uids)){
                    return new PageInfo<>();
                }
                queryWrapper.in("uid",uids);
            }
        }
        //分页查询
        Page<PropertyFinanceOrderEntity> pageData = propertyFinanceOrderMapper.selectPage(page,queryWrapper);
        if(CollectionUtils.isEmpty(pageData.getRecords())){
            return new PageInfo<>();
        }
        //后续查询参数
        Set<Long> houseIds = new HashSet<>();
        Set<String> uids = new HashSet<>();
        Set<String> receiptNums = new HashSet<>();
        for(PropertyFinanceOrderEntity entity : pageData.getRecords()){
            houseIds.add(entity.getTargetId());
            uids.add(entity.getUid());
            receiptNums.add(entity.getReceiptNum());
        }
        //查房屋全称映射 (houseService)
        Map<Long,HouseEntity> houseMap = houseService.queryIdAndHouseMap(houseIds);
        //查业主姓名映射 (houseService)
        Map<String,Map<String, String>> realNameMap = userService.queryNameByUidBatch(uids);
        //查收款单数据映射 (propertyFinanceReceiptService)
        Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
        //设置数据
        for(PropertyFinanceOrderEntity entity : pageData.getRecords()){
            entity.setAddress(houseMap.get(entity.getTargetId()) == null ? null : houseMap.get(entity.getTargetId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setUid(null);
            entity.setTargetId(null);
        }
        PageInfo<PropertyFinanceOrderEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData,pageInfo);
        return pageInfo;
    }

    /**
    * @Description: 分页查询 (财务模块)
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    @Override
    public PageInfo<PropertyFinanceOrderEntity> queryPage(BaseQO<PropertyFinanceOrderEntity> baseQO){
        PropertyFinanceOrderEntity query = baseQO.getQuery();
        Page<PropertyFinanceOrderEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page,baseQO);
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq("community_id",query.getCommunityId());
        queryWrapper.orderByDesc("create_time");
        //本表条件查询
        if(!StringUtils.isEmpty(query.getOrderNum())){
            queryWrapper.like("order_num",query.getOrderNum());
        }
        if(!StringUtils.isEmpty(query.getReceiptNum())){
            queryWrapper.like("receipt_num",query.getReceiptNum());
        }
        if(!StringUtils.isEmpty(query.getStatementNum())){
            queryWrapper.like("statement_num",query.getStatementNum());
        }
        if(query.getOrderStatus() != null){
            queryWrapper.eq("order_status",query.getOrderStatus());
        }
        if(query.getStatementStatus() != null){
            queryWrapper.eq("statement_status",query.getStatementStatus());
        }
        if(query.getTargetId() != null){
            queryWrapper.eq("house_id",query.getTargetId());
        }
        if(query.getOrderStartDate() != null){
            queryWrapper.ge("order_time",query.getOrderStartDate());
        }
        if(query.getOrderEndDate() != null){
            queryWrapper.le("order_time",query.getOrderEndDate());
        }
        //其他表条件查询
        if(!StringUtils.isEmpty(query.getRealName())){
            //查出当前社区所有订单中所有不重复uid
            Set<String> allUidSet = propertyFinanceOrderMapper.queryUidSetByCommunityId(query.getCommunityId());
            LinkedList<String> allUidSetList = new LinkedList<>(allUidSet);
            //判断数量，in条件超过999，分割查询
            int size = 999;
            if(!CollectionUtils.isEmpty(allUidSet)){
                //确定查询次数
                int times = allUidSet.size()%size == 0 ? allUidSet.size()/size : allUidSet.size()/size + 1;
                //符合条件的uid
                List<String> uids = new LinkedList<>();
                int remain = allUidSet.size(); //剩余数据长度
                for(int i=0;i<times;i++){
                    List<String> targetUid = userService.queryUidOfNameLike(allUidSetList.subList(i * size, (i * size) + remain), query.getRealName());
                    if(!CollectionUtils.isEmpty(targetUid)){
                        uids.addAll(targetUid);
                    }
                    remain = remain > size ? remain : remain - size;
                }
                //添加查询条件
                if(CollectionUtils.isEmpty(uids)){
                    return new PageInfo<>();
                }
                queryWrapper.in("uid",uids);
            }
        }
        if(query.getReceiptStartDate() != null || query.getReceiptEndDate() != null){
            PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
            receiptEntity.setStartDate(query.getReceiptStartDate());
            receiptEntity.setEndDate(query.getReceiptEndDate());
            List<String> receiptNums = propertyFinanceReceiptService.queryReceiptNumsByCondition(receiptEntity);
            if(CollectionUtils.isEmpty(receiptNums)){
                return new PageInfo<>();
            }
            queryWrapper.in("receipt_num",receiptNums);
        }
        if(query.getStatementStartDate() != null || query.getStatementEndDate() != null){
            PropertyFinanceStatementEntity statementEntity = new PropertyFinanceStatementEntity();
            statementEntity.setCreateStartDate(query.getStatementStartDate());
            statementEntity.setCreateEndDate(query.getStatementEndDate());
            List<String> statementNums = propertyFinanceStatementService.queryStatementNumsByCondition(statementEntity);
            if(CollectionUtils.isEmpty(statementNums)){
                return new PageInfo<>();
            }
            queryWrapper.in("statement_num",statementNums);
        }
        //分页查询
        Page<PropertyFinanceOrderEntity> pageData = propertyFinanceOrderMapper.selectPage(page,queryWrapper);
        if(CollectionUtils.isEmpty(pageData.getRecords())){
            return new PageInfo<>();
        }
        //后续查询参数
        Set<Long> houseIds = new HashSet<>();
        Set<String> uids = new HashSet<>();
        Set<String> receiptNums = new HashSet<>();
        Set<String> statementNums = new HashSet<>();
        for(PropertyFinanceOrderEntity entity : pageData.getRecords()){
            houseIds.add(entity.getTargetId());
            uids.add(entity.getUid());
            receiptNums.add(entity.getReceiptNum());
            statementNums.add(entity.getStatementNum());
        }
        //查房屋全称映射 (houseService)
        Map<Long,HouseEntity> houseMap = houseService.queryIdAndHouseMap(houseIds);
        //查业主姓名映射 (houseService)
        Map<String,Map<String, String>> realNameMap = userService.queryNameByUidBatch(uids);
        //查收款单数据映射 (propertyFinanceReceiptService)
        Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
        //查结算单数据映射 (propertyFinanceStatementService)
        Map<String, PropertyFinanceStatementEntity> statementEntityMap = propertyFinanceStatementService.queryByStatementNumBatch(statementNums);
        //设置数据
        for(PropertyFinanceOrderEntity entity : pageData.getRecords()){
            entity.setAddress(houseMap.get(entity.getTargetId()) == null ? null : houseMap.get(entity.getTargetId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setStatementEntity(statementEntityMap.get(entity.getStatementNum()) == null ? null : statementEntityMap.get(entity.getStatementNum()));
	        entity.setUid(null);
	        entity.setTargetId(null);
        }
        //金额统计数据(账单)
        BigDecimal totalOrder = new BigDecimal(0);//应收合计
        BigDecimal notReceipt = new BigDecimal(0);//0.待收款
        BigDecimal receipted = new BigDecimal(0);//1.已收款
        //金额统计数据(结算单)
        BigDecimal notStatement = new BigDecimal(0);//1.待结算
        BigDecimal statementing = new BigDecimal(0);//2.结算中
        BigDecimal statemented = new BigDecimal(0);//3.已结算
        BigDecimal statementReject = new BigDecimal(0);//4.驳回
        //统计数据查询
        //收款金额统计(总金额)
        queryWrapper.select("sum(total_money) as totalOrder");
        List<Map<String, Object>> totalOrderMoneyListMap = propertyFinanceOrderMapper.selectMaps(queryWrapper);
        if(totalOrderMoneyListMap.get(0) != null){
            totalOrder = totalOrder.add(new BigDecimal(String.valueOf(totalOrderMoneyListMap.get(0).get("totalOrder"))));
        }
        //收款金额统计(已收、待收)
        queryWrapper.select("order_status, sum(total_money) as total_money, now() as create_time");
        queryWrapper.groupBy("order_status");
        List<PropertyFinanceOrderEntity> receiptData = propertyFinanceOrderMapper.selectList(queryWrapper);
        for(PropertyFinanceOrderEntity entity : receiptData){
            switch (entity.getOrderStatus()){
                case 0:
                    notReceipt = notReceipt.add(entity.getTotalMoney());
                    break;
                case 1:
                    receipted = receipted.add(entity.getTotalMoney());
                    break;
                default:
            }
        }
        //结算金额统计
        queryWrapper.select("statement_status, sum(total_money) as total_money, now() as create_time");
        queryWrapper.groupBy("statement_status");
        List<PropertyFinanceOrderEntity> statementData = propertyFinanceOrderMapper.selectList(queryWrapper);
        for(PropertyFinanceOrderEntity entity : statementData){
            if(entity.getStatementStatus() != null){
                switch (entity.getStatementStatus()){
                    case 1:
                        notStatement = notStatement.add(entity.getTotalMoney());
                        break;
                    case 2:
                        statementing = statementing.add(entity.getTotalMoney());
                        break;
                    case 3:
                        statemented = statemented.add(entity.getTotalMoney());
                        break;
                    case 4:
                        statementReject = statementReject.add(entity.getTotalMoney());
                        break;
                    default:
                }
            }
        }
        PageInfo<PropertyFinanceOrderEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData,pageInfo);
        Map<String,Object> extra = new HashMap<>();
        extra.put("totalOrder",totalOrder);
        extra.put("notReceipt",notReceipt);
        extra.put("receipted",receipted);
        extra.put("notStatement",notStatement);
        extra.put("statementing",statementing);
        extra.put("statemented",statemented);
        extra.put("statementReject",statementReject);
        pageInfo.setExtra(extra);
        return pageInfo;
    }

    /**
     *@Author: Pipi
     *@Description: 财务模块查询导出账单表数据
     *@Param: propertyFinanceOrderEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/25 15:52
     **/
    @Override
    public List<PropertyFinanceOrderEntity> queryExportExcelList(PropertyFinanceOrderEntity query) {
        List<PropertyFinanceOrderEntity> orderEntities = new ArrayList<>();
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq("community_id",query.getCommunityId());
        queryWrapper.orderByDesc("create_time");
        //本表条件查询
        if(!StringUtils.isEmpty(query.getOrderNum())){
            queryWrapper.like("order_num",query.getOrderNum());
        }
        if(!StringUtils.isEmpty(query.getReceiptNum())){
            queryWrapper.like("receipt_num",query.getReceiptNum());
        }
        if(!StringUtils.isEmpty(query.getStatementNum())){
            queryWrapper.like("statement_num",query.getStatementNum());
        }
        if(query.getOrderStatus() != null){
            queryWrapper.eq("order_status",query.getOrderStatus());
        }
        if(query.getStatementStatus() != null){
            queryWrapper.eq("statement_status",query.getStatementStatus());
        }
        if(query.getTargetId() != null){
            queryWrapper.eq("house_id",query.getTargetId());
        }
        if(query.getOrderStartDate() != null){
            queryWrapper.ge("order_time",query.getOrderStartDate());
        }
        if(query.getOrderEndDate() != null){
            queryWrapper.le("order_time",query.getOrderEndDate());
        }
        //其他表条件查询
        if(!StringUtils.isEmpty(query.getRealName())){
            //查出当前社区所有订单中所有不重复uid
            Set<String> allUidSet = propertyFinanceOrderMapper.queryUidSetByCommunityId(query.getCommunityId());
            LinkedList<String> allUidSetList = new LinkedList<>(allUidSet);
            //判断数量，in条件超过999，分割查询
            int size = 999;
            if(!CollectionUtils.isEmpty(allUidSet)){
                //确定查询次数
                int times = allUidSet.size()%size == 0 ? allUidSet.size()/size : allUidSet.size()/size + 1;
                //符合条件的uid
                List<String> uids = new LinkedList<>();
                int remain = allUidSet.size(); //剩余数据长度
                for(int i=0;i<times;i++){
                    uids.addAll(userService.queryUidOfNameLike(allUidSetList.subList(i*size,(i*size)+remain), query.getRealName()));
                    remain = remain > size ? remain : remain - size;
                }
                //添加查询条件
                if(CollectionUtils.isEmpty(uids)){
                    uids.add("0");
                }
                queryWrapper.in("uid",uids);
            }
        }
        if(query.getReceiptStartDate() != null || query.getReceiptEndDate() != null){
            PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
            receiptEntity.setStartDate(query.getReceiptStartDate());
            receiptEntity.setEndDate(query.getReceiptEndDate());
            List<String> receiptNums = propertyFinanceReceiptService.queryReceiptNumsByCondition(receiptEntity);
            if(CollectionUtils.isEmpty(receiptNums)){
                return orderEntities;
            }
            queryWrapper.in("receipt_num",receiptNums);
        }
        if(query.getStatementStartDate() != null || query.getStatementEndDate() != null){
            PropertyFinanceStatementEntity statementEntity = new PropertyFinanceStatementEntity();
            statementEntity.setCreateStartDate(query.getStatementStartDate());
            statementEntity.setCreateEndDate(query.getStatementEndDate());
            List<String> statementNums = propertyFinanceStatementService.queryStatementNumsByCondition(statementEntity);
            if(CollectionUtils.isEmpty(statementNums)){
                return orderEntities;
            }
            queryWrapper.in("statement_num",statementNums);
        }
        orderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(orderEntities)){
            return orderEntities;
        }
        //后续查询参数
        Set<Long> houseIds = new HashSet<>();
        Set<String> uids = new HashSet<>();
        Set<String> receiptNums = new HashSet<>();
        Set<String> statementNums = new HashSet<>();
        for(PropertyFinanceOrderEntity entity : orderEntities){
            houseIds.add(entity.getTargetId());
            uids.add(entity.getUid());
            receiptNums.add(entity.getReceiptNum());
            statementNums.add(entity.getStatementNum());
        }
        //查房屋全称映射 (houseService)
        Map<Long,HouseEntity> houseMap = houseService.queryIdAndHouseMap(houseIds);
        //查业主姓名映射 (houseService)
        Map<String,Map<String, String>> realNameMap = userService.queryNameByUidBatch(uids);
        //查收款单数据映射 (propertyFinanceReceiptService)
        Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
        //查结算单数据映射 (propertyFinanceStatementService)
        Map<String, PropertyFinanceStatementEntity> statementEntityMap = propertyFinanceStatementService.queryByStatementNumBatch(statementNums);
        //设置数据
        for(PropertyFinanceOrderEntity entity : orderEntities){
            entity.setAddress(houseMap.get(entity.getTargetId()) == null ? null : houseMap.get(entity.getTargetId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setStatementEntity(statementEntityMap.get(entity.getStatementNum()) == null ? null : statementEntityMap.get(entity.getStatementNum()));
        }
        return orderEntities;
    }


    @Override
    public void update(Long id) {
        propertyFinanceOrderMapper.updateStatus(id);
    }

    @Override
    public void updates(FinanceOrderOperationQO financeOrderOperationQO) {
        if (financeOrderOperationQO.getOrderTimeOver()!=null){
            financeOrderOperationQO.setOrderTimeOver(financeOrderOperationQO.getOrderTimeOver().plusDays(1));
        }
        if (financeOrderOperationQO.getOverTime()!=null){
            financeOrderOperationQO.setOverTime(financeOrderOperationQO.getOverTime().plusMonths(1));
        }
        propertyFinanceOrderMapper.updates(financeOrderOperationQO);
    }

    /**
     * @Description: 删除多条账单
     * @author: Hu
     * @since: 2021/8/7 14:37
     * @Param: [ids]
     * @return: void
     */
    @Override
    public void deletes(FinanceOrderOperationQO financeOrderOperationQO) {
        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        if (financeOrderOperationQO.getOrderTimeBegin()!=null){
            wrapper.ge("order_time",financeOrderOperationQO.getOrderTimeBegin());
        }
        if (financeOrderOperationQO.getOrderTimeOver()!=null){
            financeOrderOperationQO.setOrderTimeOver(financeOrderOperationQO.getOrderTimeOver().plusDays(1));
            wrapper.le("order_time",financeOrderOperationQO.getOrderTimeOver());
        }
        if (financeOrderOperationQO.getType()!=null){
            wrapper.eq("type",financeOrderOperationQO.getType());
        }
        propertyFinanceOrderMapper.delete(wrapper);
    }

    /**
     * @Description: 删除一条账单信息
     * @author: Hu
     * @since: 2021/8/7 14:35
     * @Param: [id]
     * @return: void
     */
    @Override
    public void delete(Long id) {
        propertyFinanceOrderMapper.deleteById(id);
    }

    /**
     * @Description: 修改订单优惠金额
     * @author: Hu
     * @since: 2021/8/7 14:24
     * @Param: [id, coupon]
     * @return: void
     */
    @Override
    public void updateOrder(Long id, BigDecimal coupon) {
        PropertyFinanceOrderEntity orderEntity = propertyFinanceOrderMapper.selectById(id);
        if (orderEntity!=null){
            orderEntity.setTotalMoney(coupon);
            orderEntity.setUpdateTime(LocalDateTime.now());
            propertyFinanceOrderMapper.updateById(orderEntity);
        }
    }

    /**
     * @Description: 查询一条物业账单详情
     * @author: Hu
     * @since: 2021/7/6 11:14
     * @Param: [userId, orderId]
     * @return: java.lang.Object
     */
    @Override
    public PropertyFinanceOrderEntity findOne(Long orderId) {
        return propertyFinanceOrderMapper.selectById(orderId);
    }



    /**
    * @Description: 支付完成后-批量修改物业账单
     * @Param: [payType, tripartiteOrder, ids]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/7/7
    **/
    public void updateOrderStatusBatch(Integer payType, String tripartiteOrder , String[] ids) {
        int rows = propertyFinanceOrderMapper.updateOrderBatch(payType,tripartiteOrder,ids);
        if(rows != ids.length){
            log.info("物业账单支付后处理失败，单号：" + tripartiteOrder + " 账单ID：" + Arrays.toString(ids));
        }
    }

    /**
     * @Description: 查询物业账单总金额
     * @author: Hu
     * @since: 2021/7/5 16:12
     * @Param: [orderIds]
     * @return: java.math.BigDecimal
     */
    @Override
    public BigDecimal getTotalMoney(String ids) {
        BigDecimal totalMoney = propertyFinanceOrderMapper.getTotalMoney(ids.split(","));
        return totalMoney == null ? new BigDecimal("0.00") : totalMoney;
    }

    /**
     * @Description: 根据用户查询所有账单
     * @author: Hu
     * @since: 2021/7/5 11:19
     * @Param: [userId]
     * @return: void
     */
    @Override
    public List<PropertyFinanceOrderEntity> selectByUserList(PropertyFinanceOrderEntity qo) {
        QueryWrapper queryWrapper = new QueryWrapper<PropertyFinanceOrderEntity>()
            .select("id,total_money,order_time,house_id")
            .eq("uid", qo.getUid())
            .eq("community_id",qo.getCommunityId());
        if(qo.getOrderStatus() != null && (qo.getOrderStatus() == 0 || qo.getOrderStatus() == 1)){
            queryWrapper.eq("order_status",qo.getOrderStatus());
        }
        return propertyFinanceOrderMapper.selectList(queryWrapper);
    }

    /**
     *@Author: Pipi
     *@Description: 分页查询结算单的账单列表
     *@Param: baseQO:
     *@Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/24 11:44
     **/
    @Override
    public Page<PropertyFinanceOrderEntity> queryPageByStatemenNum(BaseQO<StatementNumQO> baseQO) {
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        Page<PropertyFinanceOrderEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        StatementNumQO query = baseQO.getQuery();
        queryWrapper.select("id, order_num, order_time, '物业费' as orderType, total_money, receipt_num");
        queryWrapper.eq("statement_num", query.getStatementNum());
        queryWrapper.orderByDesc("create_time");
        Page<PropertyFinanceOrderEntity> pageData = propertyFinanceOrderMapper.selectPage(page,queryWrapper);
        if (!CollectionUtils.isEmpty(pageData.getRecords())) {
            Set<String> receiptNums = new HashSet<>();
            pageData.getRecords().forEach(orderEntity -> {
                receiptNums.add(orderEntity.getReceiptNum());
            });
            //查收款单数据映射 (propertyFinanceReceiptService)
            Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
            pageData.getRecords().forEach(orderEntity -> {
                orderEntity.setReceiptEntity(receiptEntityMap.get(orderEntity.getReceiptNum()) == null ? null : receiptEntityMap.get(orderEntity.getReceiptNum()));
            });
        }
        return pageData;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收入
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/17 16:00
     **/
    @Override
    public PropertyFinanceFormEntity getFinanceFormCommunityIncome(PropertyFinanceFormEntity qo) {
        // 返回给前端实体
        PropertyFinanceFormEntity propertyFinanceFormEntity = new PropertyFinanceFormEntity();
        // 押金查询
        QueryWrapper<PropertyDepositEntity> DepositWrapper = new QueryWrapper<>();
        DepositWrapper.ge("create_time", qo.getStartTime());
        DepositWrapper.le("create_time", qo.getEndTime());
        DepositWrapper.eq("community_id", qo.getCommunityId());
        // 查询一段时间内押金实体
        List<PropertyDepositEntity> propertyDepositEntities = propertyDepositMapper.selectList(DepositWrapper);
        // 押金线上收费合计
        BigDecimal depositSum = new BigDecimal("0.00");
        // 押金退款
        BigDecimal depositRefund = new BigDecimal("0.00");
        for (PropertyDepositEntity propertyDepositEntity : propertyDepositEntities) {
            depositSum = depositSum.add(propertyDepositEntity.getBillMoney());
            if (propertyDepositEntity.getStatus() == 3) {
                depositRefund = depositRefund.add(propertyDepositEntity.getBillMoney());
            }
        }
        // 押金线上收费
        propertyFinanceFormEntity.setDepositOnlineCharging(depositSum);
        // 押金退款
        propertyFinanceFormEntity.setDepositRefund(depositRefund);
        // 押金合计
        propertyFinanceFormEntity.setAdvanceDepositTotal(depositSum);
    
        // 预存款查询
        QueryWrapper<PropertyAdvanceDepositRecordEntity> advanceDepositRecordWrapper = new QueryWrapper<>();
        advanceDepositRecordWrapper.ge("create_time", qo.getStartTime());
        advanceDepositRecordWrapper.le("create_time", qo.getEndTime());
        advanceDepositRecordWrapper.eq("community_id", qo.getCommunityId());
        // 查询一段时间内预存款实体
        List<PropertyAdvanceDepositRecordEntity> propertyAdvanceDepositRecordEntities = propertyAdvanceDepositRecordMapper.selectList(advanceDepositRecordWrapper);
        // 预存款线上收费合计
        BigDecimal advanceDepositSum = new BigDecimal("0.00");
        // 预存款提现
        BigDecimal advanceDepositWithdrawal = new BigDecimal("0.00");
        for (PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity : propertyAdvanceDepositRecordEntities) {
            advanceDepositSum = advanceDepositSum.add(propertyAdvanceDepositRecordEntity.getDepositAmount());
            advanceDepositWithdrawal = advanceDepositWithdrawal.add(propertyAdvanceDepositRecordEntity.getPayAmount());
        }
        // 预存款线上收费
        propertyFinanceFormEntity.setAdvanceDepositOnlineCharging(advanceDepositSum);
        // 预存款提现
        propertyFinanceFormEntity.setAdvanceDepositWithdrawal(advanceDepositWithdrawal);
        // 预存款合计
        propertyFinanceFormEntity.setAdvanceDepositTotal(advanceDepositSum);
        
        // 小区账单查询
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderWrapper = new QueryWrapper<>();
        financeOrderWrapper.ge("create_time", qo.getStartTime());
        financeOrderWrapper.le("create_time", qo.getEndTime());
        financeOrderWrapper.eq("community_id", qo.getCommunityId());
        // 查询一段时间内小区账单实体
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(financeOrderWrapper);
        // 小区账单线上收费合计
        BigDecimal communitySum = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            communitySum = communitySum.add(propertyFinanceOrderEntity.getTotalMoney());
        }
        // 小区账单线上收费
        propertyFinanceFormEntity.setCommunityOnlineCharging(communitySum);
        // 小区账单合计
        propertyFinanceFormEntity.setCommunityTotal(communitySum);
        
        return propertyFinanceFormEntity;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收费报表-账单生成时间
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/18 11:08
     **/
    @Override
    public PropertyFinanceFormChargeEntity getFinanceFormCommunityChargeByOrderGenerateTime(PropertyFinanceFormChargeEntity qo) {
        // 返回给前端实体
        PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity = new PropertyFinanceFormChargeEntity();
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", qo.getStartTime());
        queryWrapper.le("create_time", qo.getEndTime());
        queryWrapper.eq("community_id", qo.getCommunityId());
        // 小区账单查询
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        // 所有优惠金额
        BigDecimal couponMoney = new BigDecimal("0.00");
        // 所有滞纳金（违约金）
        BigDecimal receivablePenalMoney = new BigDecimal("0.00");
        // 所有账单总金额
        BigDecimal totalMoney = new BigDecimal("0.00");
        // 所有预存款抵扣
        BigDecimal deductionMoney = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            totalMoney = totalMoney.add(propertyFinanceOrderEntity.getTotalMoney());
            couponMoney = couponMoney.add(propertyFinanceOrderEntity.getCoupon());
            receivablePenalMoney = receivablePenalMoney.add(propertyFinanceOrderEntity.getPenalSum());
            deductionMoney = deductionMoney.add(propertyFinanceOrderEntity.getDeduction());
        }
        // 合计应收、本月应收
        propertyFinanceFormChargeEntity.setTotalMoney(totalMoney);
        // 优惠金额
        propertyFinanceFormChargeEntity.setCouponMoney(couponMoney);
        // 违约应收
        propertyFinanceFormChargeEntity.setReceivablePenalMoney(receivablePenalMoney);
        // 预存款抵扣
        propertyFinanceFormChargeEntity.setDeductionMoney(deductionMoney);
    
        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.ge("create_time", qo.getStartTime());
        wrapper.le("create_time", qo.getEndTime());
        wrapper.eq("community_id", qo.getCommunityId());
        wrapper.eq("order_status",1);
        // 小区已支付账单查询
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(wrapper);
        // 已支付账单违约金
        BigDecimal collectPenalMoney = new BigDecimal("0.00");
        // 所有已支付账单
        BigDecimal communityOnlineCharging = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity entity : entities) {
            collectPenalMoney = collectPenalMoney.add(entity.getPenalSum());
            communityOnlineCharging = communityOnlineCharging.add(entity.getTotalMoney());
        }
        // 违约实收
        propertyFinanceFormChargeEntity.setCollectPenalMoney(collectPenalMoney);
        // 线上收款、合计实收
        propertyFinanceFormChargeEntity.setCommunityOnlineCharging(communityOnlineCharging);
    
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderQueryWrapper = new QueryWrapper<>();
        financeOrderQueryWrapper.lt("create_time", qo.getStartTime());
        financeOrderQueryWrapper.eq("community_id", qo.getCommunityId());
        financeOrderQueryWrapper.eq("order_status",0);
        // 小区往月待支付账单查询
        List<PropertyFinanceOrderEntity> lastMonthEntities = propertyFinanceOrderMapper.selectList(financeOrderQueryWrapper);
        // 往月所有待支付的总金额
        BigDecimal arrearsMoney = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity lastMonthEntity : lastMonthEntities) {
            arrearsMoney = arrearsMoney.add(lastMonthEntity.getTotalMoney());
        }
        // 往月欠收
        propertyFinanceFormChargeEntity.setArrearsMoney(arrearsMoney);
    
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderEntityQueryWrapper = new QueryWrapper<>();
        financeOrderEntityQueryWrapper.ge("create_time", qo.getStartTime());
        financeOrderEntityQueryWrapper.le("create_time", qo.getEndTime());
        financeOrderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        financeOrderEntityQueryWrapper.eq("order_status",0);
        // 小区本月待支付账单查询
        List<PropertyFinanceOrderEntity> entityList = propertyFinanceOrderMapper.selectList(financeOrderEntityQueryWrapper);
        // 本月欠收
        BigDecimal thisMonthArrearsMoney = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entityList) {
            thisMonthArrearsMoney = thisMonthArrearsMoney.add(propertyFinanceOrderEntity.getTotalMoney());
        }
        // 合计欠收
        propertyFinanceFormChargeEntity.setArrearsMoneySum(arrearsMoney.add(thisMonthArrearsMoney));
        
        return propertyFinanceFormChargeEntity;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收费报表-账单周期时间
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/18 11:08
     **/
    @Override
    public PropertyFinanceFormChargeEntity getFinanceFormCommunityChargeByOrderPeriodTime(PropertyFinanceFormChargeEntity qo) {
        // 返回给前端实体
        PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity = new PropertyFinanceFormChargeEntity();
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", qo.getStartTime());
        queryWrapper.le("create_time", qo.getEndTime());
        queryWrapper.eq("community_id", qo.getCommunityId());
        queryWrapper.ne("build_type", 2);
        // 小区账单查询不包含临时收费
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        // 所有优惠金额
        BigDecimal couponMoney = new BigDecimal("0.00");
        // 所有滞纳金（违约金）
        BigDecimal receivablePenalMoney = new BigDecimal("0.00");
        // 所有账单总金额
        BigDecimal totalMoney = new BigDecimal("0.00");
        // 所有预存款抵扣
        BigDecimal deductionMoney = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            totalMoney = totalMoney.add(propertyFinanceOrderEntity.getTotalMoney());
            couponMoney = couponMoney.add(propertyFinanceOrderEntity.getCoupon());
            receivablePenalMoney = receivablePenalMoney.add(propertyFinanceOrderEntity.getPenalSum());
            deductionMoney = deductionMoney.add(propertyFinanceOrderEntity.getDeduction());
        }
        // 合计应收、本月应收
        propertyFinanceFormChargeEntity.setTotalMoney(totalMoney);
        // 优惠金额
        propertyFinanceFormChargeEntity.setCouponMoney(couponMoney);
        // 违约应收
        propertyFinanceFormChargeEntity.setReceivablePenalMoney(receivablePenalMoney);
        // 预存款抵扣
        propertyFinanceFormChargeEntity.setDeductionMoney(deductionMoney);
    
        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.ge("create_time", qo.getStartTime());
        wrapper.le("create_time", qo.getEndTime());
        wrapper.eq("community_id", qo.getCommunityId());
        wrapper.eq("order_status",1);
        wrapper.ne("build_type", 2);
        // 小区已支付账单查询不包含临时收费
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(wrapper);
        // 已支付账单违约金
        BigDecimal collectPenalMoney = new BigDecimal("0.00");
        // 所有已支付账单
        BigDecimal communityOnlineCharging = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity entity : entities) {
            collectPenalMoney = collectPenalMoney.add(entity.getPenalSum());
            communityOnlineCharging = communityOnlineCharging.add(entity.getTotalMoney());
        }
        // 违约实收
        propertyFinanceFormChargeEntity.setCollectPenalMoney(collectPenalMoney);
        // 线上收款、合计实收
        propertyFinanceFormChargeEntity.setCommunityOnlineCharging(communityOnlineCharging);
    
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderQueryWrapper = new QueryWrapper<>();
        financeOrderQueryWrapper.lt("create_time", qo.getStartTime());
        financeOrderQueryWrapper.eq("community_id", qo.getCommunityId());
        financeOrderQueryWrapper.eq("order_status",0);
        financeOrderQueryWrapper.ne("build_type", 2);
        // 小区往月待支付账单查询不包含临时收费
        List<PropertyFinanceOrderEntity> lastMonthEntities = propertyFinanceOrderMapper.selectList(financeOrderQueryWrapper);
        // 往月所有待支付的总金额
        BigDecimal arrearsMoney = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity lastMonthEntity : lastMonthEntities) {
            arrearsMoney = arrearsMoney.add(lastMonthEntity.getTotalMoney());
        }
        // 往月欠收
        propertyFinanceFormChargeEntity.setArrearsMoney(arrearsMoney);
    
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderEntityQueryWrapper = new QueryWrapper<>();
        financeOrderEntityQueryWrapper.ge("create_time", qo.getStartTime());
        financeOrderEntityQueryWrapper.le("create_time", qo.getEndTime());
        financeOrderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        financeOrderEntityQueryWrapper.eq("order_status",0);
        financeOrderEntityQueryWrapper.ne("build_type", 2);
        // 小区本月待支付账单查询不包含临时收费
        List<PropertyFinanceOrderEntity> entityList = propertyFinanceOrderMapper.selectList(financeOrderEntityQueryWrapper);
        // 本月欠收
        BigDecimal thisMonthArrearsMoney = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entityList) {
            thisMonthArrearsMoney = thisMonthArrearsMoney.add(propertyFinanceOrderEntity.getTotalMoney());
        }
        // 合计欠收
        propertyFinanceFormChargeEntity.setArrearsMoneySum(arrearsMoney.add(thisMonthArrearsMoney));
    
        return propertyFinanceFormChargeEntity;
    }
}

