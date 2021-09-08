package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceOrderOperationQO;
import com.jsy.community.qo.property.FinanceOrderQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.utils.DateCalculateUtil;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private ProprietorUserService userService;

    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceReceiptService propertyFinanceReceiptService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceStatementService propertyFinanceStatementService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyAdvanceDepositRecordService PropertyAdvanceDepositRecordService;
    
    @Autowired
    private PropertyDepositMapper propertyDepositMapper;
    
    @Autowired
    private PropertyAdvanceDepositRecordMapper propertyAdvanceDepositRecordMapper;
    
    @Autowired
    private HouseMapper houseMapper;
    
    @Autowired
    private CommunityMapper communityMapper;
    
    @Autowired
    private PropertyAdvanceDepositMapper propertyAdvanceDepositMapper;
    
    @Autowired
    private CarPositionMapper carPositionMapper;

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
            orderEntity.setCoupon(coupon);
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
    public List<PropertyFinanceFormEntity> getFinanceFormCommunityIncome(PropertyFinanceFormEntity qo, List<Long> communityIdList) {
        // 返回给前端实体
        List<PropertyFinanceFormEntity> propertyFinanceFormEntityList = new LinkedList<>();
        // 押金查询
        QueryWrapper<PropertyDepositEntity> DepositWrapper = new QueryWrapper<>();
        if (qo.getStartTime() != null) {
            DepositWrapper.ge("create_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            DepositWrapper.le("create_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            DepositWrapper.eq("community_id", qo.getCommunityId());
        } else {
            DepositWrapper.in("community_id", communityIdList);
        }
        DepositWrapper.eq("deleted", 0);
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
        PropertyFinanceFormEntity propertyFinanceFormEntity = new PropertyFinanceFormEntity();
        // 类型
        propertyFinanceFormEntity.setTypeName("物业押金");
        // 押金线上收费
        propertyFinanceFormEntity.setOnlineCharging(depositSum);
        // 押金线下收费
        propertyFinanceFormEntity.setOfflineCharging(new BigDecimal("0.00"));
        // 押金退款
        propertyFinanceFormEntity.setRefundOrWithdrawal(depositRefund);
        // 押金合计
        propertyFinanceFormEntity.setTotal(depositSum);
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity);
    
        // 预存款查询
        QueryWrapper<PropertyAdvanceDepositRecordEntity> advanceDepositRecordWrapper = new QueryWrapper<>();
        if (qo.getStartTime() != null) {
            advanceDepositRecordWrapper.ge("create_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            advanceDepositRecordWrapper.le("create_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            advanceDepositRecordWrapper.eq("community_id", qo.getCommunityId());
        } else {
            advanceDepositRecordWrapper.in("community_id", communityIdList);
        }
        advanceDepositRecordWrapper.eq("deleted", 0);
        // 查询一段时间内预存款实体
        List<PropertyAdvanceDepositRecordEntity> propertyAdvanceDepositRecordEntities = propertyAdvanceDepositRecordMapper.selectList(advanceDepositRecordWrapper);
        // 预存款线上收费合计
        BigDecimal advanceDepositSum = new BigDecimal("0.00");
        // 预存款提现
        BigDecimal advanceDepositWithdrawal = new BigDecimal("0.00");
        for (PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity : propertyAdvanceDepositRecordEntities) {
            if (propertyAdvanceDepositRecordEntity.getDepositAmount() != null) {
                advanceDepositSum = advanceDepositSum.add(propertyAdvanceDepositRecordEntity.getDepositAmount());
            }
            if (propertyAdvanceDepositRecordEntity.getPayAmount() != null) {
                advanceDepositWithdrawal = advanceDepositWithdrawal.add(propertyAdvanceDepositRecordEntity.getPayAmount());
            }
        }
        PropertyFinanceFormEntity propertyFinanceFormEntity1 = new PropertyFinanceFormEntity();
        // 类型
        propertyFinanceFormEntity1.setTypeName("预存款充值");
        // 预存款线上收费
        propertyFinanceFormEntity1.setOnlineCharging(advanceDepositSum);
        // 预存款线下收费
        propertyFinanceFormEntity1.setOfflineCharging(new BigDecimal("0.00"));
        // 预存款提现
        propertyFinanceFormEntity1.setRefundOrWithdrawal(advanceDepositWithdrawal);
        // 预存款合计
        propertyFinanceFormEntity1.setTotal(advanceDepositSum);
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity1);
        
        // 小区账单查询
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderWrapper = new QueryWrapper<>();
        if (qo.getStartTime() != null) {
            financeOrderWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            financeOrderWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderWrapper.in("community_id", communityIdList);
        }
        financeOrderWrapper.eq("deleted", 0);
        // 查询一段时间内小区账单实体
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(financeOrderWrapper);
        // 小区账单线上收费合计
        BigDecimal communitySum = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            if (propertyFinanceOrderEntity.getTotalMoney() != null) {
                communitySum = communitySum.add(propertyFinanceOrderEntity.getTotalMoney());
            }
        }
        PropertyFinanceFormEntity propertyFinanceFormEntity2 = new PropertyFinanceFormEntity();
        // 类型
        propertyFinanceFormEntity2.setTypeName("小区收费");
        // 小区账单线上收费
        propertyFinanceFormEntity2.setOnlineCharging(communitySum);
        // 小区账单线下收费
        propertyFinanceFormEntity2.setOfflineCharging(new BigDecimal("0.00"));
        // 小区账单退款或者体现
        propertyFinanceFormEntity2.setRefundOrWithdrawal(new BigDecimal("0.00"));
        // 小区账单合计
        propertyFinanceFormEntity2.setTotal(communitySum);
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity2);
        
        // 线上收费合计
        PropertyFinanceFormEntity propertyFinanceFormEntity3 = new PropertyFinanceFormEntity();
        // 类型
        propertyFinanceFormEntity3.setTypeName("合计");
        // 合计线上收费
        propertyFinanceFormEntity3.setOnlineCharging(propertyFinanceFormEntity.getOnlineCharging().add(propertyFinanceFormEntity1.getOnlineCharging()).add(propertyFinanceFormEntity2.getOnlineCharging()));
        // 合计线下收费
        propertyFinanceFormEntity3.setOfflineCharging(new BigDecimal("0.00"));
        // 合计退款或者提现
        propertyFinanceFormEntity3.setRefundOrWithdrawal(propertyFinanceFormEntity.getRefundOrWithdrawal().add(propertyFinanceFormEntity1.getRefundOrWithdrawal().add(propertyFinanceFormEntity2.getRefundOrWithdrawal())));
        // 总合计
        propertyFinanceFormEntity3.setTotal(propertyFinanceFormEntity.getTotal().add(propertyFinanceFormEntity1.getTotal()).add(propertyFinanceFormEntity2.getTotal()));
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity3);
        
        return propertyFinanceFormEntityList;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收费报表-账单生成时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/18 11:08
     **/
    @Override
    public List<PropertyFinanceFormChargeEntity> getFinanceFormCommunityChargeByOrderGenerateTime(PropertyFinanceFormChargeEntity qo, List<Long> communityIdList) {
        // 返回给前端实体
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = new LinkedList<>();
        
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as totalMoney,SUM( penal_sum ) as receivablePenalMoney,SUM( coupon ) as couponMoney,SUM( deduction ) as deductionMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            queryWrapper.in("community_id", communityIdList);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.groupBy("fee_rule_id");
        // 小区账单查询
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity;
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            propertyFinanceFormChargeEntity = new PropertyFinanceFormChargeEntity();
            propertyFinanceFormChargeEntity.setFeeRuleId(propertyFinanceOrderEntity.getFeeRuleId());
            propertyFinanceFormChargeEntity.setTotalMoney(propertyFinanceOrderEntity.getTotalMoney());
            propertyFinanceFormChargeEntity.setCouponMoney(propertyFinanceOrderEntity.getCouponMoney());
            propertyFinanceFormChargeEntity.setReceivablePenalMoney(propertyFinanceOrderEntity.getReceivablePenalMoney());
            propertyFinanceFormChargeEntity.setDeductionMoney(propertyFinanceOrderEntity.getDeductionMoney());
            propertyFinanceFormChargeEntity.setArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setThisMonthArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCollectPenalMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOnlineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOfflineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntityList.add(propertyFinanceFormChargeEntity);
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("fee_rule_id as feeRuleId,SUM( penal_sum ) as collectPenalMoney,SUM( total_money ) as communityOnlineCharging");
        if (qo.getStartTime() != null) {
            wrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper.eq("community_id", qo.getCommunityId());
        } else {
            wrapper.in("community_id", communityIdList);
        }
        wrapper.eq("order_status",1);
        wrapper.eq("deleted",0);
        wrapper.groupBy("fee_rule_id");
        // 小区已支付账单查询
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(wrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                if (financeFormChargeEntity.getFeeRuleId().equals(entity.getFeeRuleId())) {
                    financeFormChargeEntity.setCollectPenalMoney(entity.getCollectPenalMoney());
                    financeFormChargeEntity.setCommunityOnlineCharging(entity.getCommunityOnlineCharging());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderQueryWrapper = new QueryWrapper<>();
        financeOrderQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as arrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderQueryWrapper.lt("order_time", qo.getStartTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderQueryWrapper.eq("order_status",0);
        financeOrderQueryWrapper.eq("deleted",0);
        financeOrderQueryWrapper.groupBy("fee_rule_id");
        // 小区往月待支付账单查询
        List<PropertyFinanceOrderEntity> lastMonthEntities = propertyFinanceOrderMapper.selectList(financeOrderQueryWrapper);
        for (PropertyFinanceOrderEntity lastMonthEntity : lastMonthEntities) {
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                if (financeFormChargeEntity.getFeeRuleId().equals(lastMonthEntity.getFeeRuleId())) {
                    financeFormChargeEntity.setArrearsMoney(lastMonthEntity.getArrearsMoney());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderEntityQueryWrapper = new QueryWrapper<>();
        financeOrderEntityQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as thisMonthArrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderEntityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            financeOrderEntityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderEntityQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderEntityQueryWrapper.eq("order_status",0);
        financeOrderEntityQueryWrapper.eq("deleted",0);
        financeOrderEntityQueryWrapper.groupBy("fee_rule_id");
        // 小区本月待支付账单查询
        List<PropertyFinanceOrderEntity> entityList = propertyFinanceOrderMapper.selectList(financeOrderEntityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entityList) {
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                if (financeFormChargeEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    financeFormChargeEntity.setThisMonthArrearsMoney(propertyFinanceOrderEntity.getThisMonthArrearsMoney());
                }
            }
        }
        //合计欠收
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            if (financeFormChargeEntity.getArrearsMoney() == null && financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            } else if (financeFormChargeEntity.getArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getThisMonthArrearsMoney());
            } else if (financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney());
            } else {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney().add(financeFormChargeEntity.getThisMonthArrearsMoney()));
            }
        }
        // 补充项目名称
        List<Long> paramList = new ArrayList<>();
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            paramList.add(financeFormChargeEntity.getFeeRuleId());
        }
        if (paramList.size() > 0) {
            Map<String, Map<String, Object>> longMapMap = propertyFeeRuleMapper.selectFeeRuleIdName(paramList);
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                Map<String, Object> countMap = longMapMap.get(financeFormChargeEntity.getFeeRuleId());
                financeFormChargeEntity.setFeeRuleName(countMap != null ? String.valueOf(countMap.get("name")) : "");
            }
        }
    
        return propertyFinanceFormChargeEntityList;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取财务报表-小区收费报表-账单周期时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/18 11:08
     **/
    @Override
    public List<PropertyFinanceFormChargeEntity> getFinanceFormCommunityChargeByOrderPeriodTime(PropertyFinanceFormChargeEntity qo, List<Long> communityIdList) {
        // 返回给前端实体
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = new LinkedList<>();
    
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as totalMoney,SUM( penal_sum ) as receivablePenalMoney,SUM( coupon ) as couponMoney,SUM( deduction ) as deductionMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            queryWrapper.in("community_id", communityIdList);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.ne("build_type",2);
        queryWrapper.groupBy("fee_rule_id");
        // 小区账单查询
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity;
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            propertyFinanceFormChargeEntity = new PropertyFinanceFormChargeEntity();
            propertyFinanceFormChargeEntity.setFeeRuleId(propertyFinanceOrderEntity.getFeeRuleId());
            propertyFinanceFormChargeEntity.setTotalMoney(propertyFinanceOrderEntity.getTotalMoney());
            propertyFinanceFormChargeEntity.setCouponMoney(propertyFinanceOrderEntity.getCouponMoney());
            propertyFinanceFormChargeEntity.setReceivablePenalMoney(propertyFinanceOrderEntity.getReceivablePenalMoney());
            propertyFinanceFormChargeEntity.setDeductionMoney(propertyFinanceOrderEntity.getDeductionMoney());
            propertyFinanceFormChargeEntity.setArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setThisMonthArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCollectPenalMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOnlineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOfflineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntityList.add(propertyFinanceFormChargeEntity);
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("fee_rule_id as feeRuleId,SUM( penal_sum ) as collectPenalMoney,SUM( total_money ) as communityOnlineCharging");
        if (qo.getStartTime() != null) {
            wrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper.eq("community_id", qo.getCommunityId());
        } else {
            wrapper.in("community_id", communityIdList);
        }
        wrapper.eq("order_status",1);
        wrapper.eq("deleted",0);
        wrapper.ne("build_type",2);
        wrapper.groupBy("fee_rule_id");
        // 小区已支付账单查询
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(wrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                if (financeFormChargeEntity.getFeeRuleId().equals(entity.getFeeRuleId())) {
                    financeFormChargeEntity.setCollectPenalMoney(entity.getCollectPenalMoney());
                    financeFormChargeEntity.setCommunityOnlineCharging(entity.getCommunityOnlineCharging());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderQueryWrapper = new QueryWrapper<>();
        financeOrderQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as arrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderQueryWrapper.lt("order_time", qo.getStartTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderQueryWrapper.eq("order_status",0);
        financeOrderQueryWrapper.eq("deleted",0);
        financeOrderQueryWrapper.ne("build_type",2);
        financeOrderQueryWrapper.groupBy("fee_rule_id");
        // 小区往月待支付账单查询
        List<PropertyFinanceOrderEntity> lastMonthEntities = propertyFinanceOrderMapper.selectList(financeOrderQueryWrapper);
        for (PropertyFinanceOrderEntity lastMonthEntity : lastMonthEntities) {
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                if (financeFormChargeEntity.getFeeRuleId().equals(lastMonthEntity.getFeeRuleId())) {
                    financeFormChargeEntity.setArrearsMoney(lastMonthEntity.getArrearsMoney());
                }
            }
        }
        
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderEntityQueryWrapper = new QueryWrapper<>();
        financeOrderEntityQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as thisMonthArrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderEntityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            financeOrderEntityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderEntityQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderEntityQueryWrapper.eq("order_status",0);
        financeOrderEntityQueryWrapper.eq("deleted",0);
        financeOrderEntityQueryWrapper.ne("build_type",2);
        financeOrderEntityQueryWrapper.groupBy("fee_rule_id");
        // 小区本月待支付账单查询
        List<PropertyFinanceOrderEntity> entityList = propertyFinanceOrderMapper.selectList(financeOrderEntityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entityList) {
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                if (financeFormChargeEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    financeFormChargeEntity.setThisMonthArrearsMoney(propertyFinanceOrderEntity.getThisMonthArrearsMoney());
                }
            }
        }
        //合计欠收
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            if (financeFormChargeEntity.getArrearsMoney() == null && financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            } else if (financeFormChargeEntity.getArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getThisMonthArrearsMoney());
            } else if (financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney());
            } else {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney().add(financeFormChargeEntity.getThisMonthArrearsMoney()));
            }
        }
        // 补充项目名称
        List<Long> paramList = new ArrayList<>();
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            paramList.add(financeFormChargeEntity.getFeeRuleId());
        }
        if (paramList.size() > 0) {
            Map<String, Map<String, Object>> longMapMap = propertyFeeRuleMapper.selectFeeRuleIdName(paramList);
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                Map<String, Object> countMap = longMapMap.get(financeFormChargeEntity.getFeeRuleId());
                financeFormChargeEntity.setFeeRuleName(countMap != null ? String.valueOf(countMap.get("name")) : "");
            }
        }
        
        return propertyFinanceFormChargeEntityList;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-收款报表
     *@Param:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/8/19 9:31
     **/
    @Override
    public List<PropertyCollectionFormEntity> getCollectionFormCollection(PropertyCollectionFormEntity qo, List<Long> communityIdList) {
        // 返回前端实体
        List<PropertyCollectionFormEntity> propertyCollectionFormEntityList = new LinkedList<>();
        // 模糊查询收费项目名称找到全部收费项目id
        List<Long> FeeRuleIdList = new ArrayList<>();
        if (qo.getFeeRuleName() != null) {
            if (qo.getCommunityId() != null) {
                List<Long> communityIds = new ArrayList<>();
                communityIds.add(qo.getCommunityId());
                FeeRuleIdList = propertyFeeRuleMapper.selectFeeRuleIdList(communityIds, qo.getFeeRuleName());
            } else {
                FeeRuleIdList = propertyFeeRuleMapper.selectFeeRuleIdList(communityIdList, qo.getFeeRuleName());
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS totalSum");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            queryWrapper.in("community_id", communityIdList);
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            queryWrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null){
            queryWrapper.eq("fee_rule_id", 0);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.groupBy("fee_rule_id");
        // 查询合计支付
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        PropertyCollectionFormEntity propertyCollectionFormEntity;
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            propertyCollectionFormEntity = new PropertyCollectionFormEntity();
            propertyCollectionFormEntity.setFeeRuleId(propertyFinanceOrderEntity.getFeeRuleId());
            propertyCollectionFormEntity.setTotalSum(propertyFinanceOrderEntity.getTotalSum());
            propertyCollectionFormEntity.setWeChatPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setAliPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setBalancePaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setCashPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setUnionPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setBankPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntityList.add(propertyCollectionFormEntity);
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> query = new QueryWrapper<>();
        query.select("fee_rule_id as feeRuleId,SUM(total_money) AS weChatPaySum");
        if (qo.getStartTime() != null) {
            query.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            query.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            query.eq("community_id", qo.getCommunityId());
        } else {
            query.in("community_id", communityIdList);
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            query.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null){
            query.eq("fee_rule_id", 0);
        }
        query.eq("deleted", 0);
        query.eq("pay_type", 1);
        query.groupBy("fee_rule_id");
        // 查询微信支付
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(query);
        for (PropertyFinanceOrderEntity entity : entities) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(entity.getFeeRuleId())) {
                    collectionFormEntity.setWeChatPaySum(entity.getWeChatPaySum());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS aliPaySum");
        if (qo.getStartTime() != null) {
            wrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper.eq("community_id", qo.getCommunityId());
        } else {
            wrapper.in("community_id", communityIdList);
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            wrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null){
            wrapper.eq("fee_rule_id", 0);
        }
        wrapper.eq("deleted", 0);
        wrapper.eq("pay_type", 2);
        wrapper.groupBy("fee_rule_id");
        // 查询支付宝支付
        List<PropertyFinanceOrderEntity> entities1 = propertyFinanceOrderMapper.selectList(wrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities1) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setAliPaySum(propertyFinanceOrderEntity.getAliPaySum());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> entityQueryWrapper = new QueryWrapper<>();
        entityQueryWrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS balancePaySum");
        if (qo.getStartTime() != null) {
            entityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            entityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            entityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            entityQueryWrapper.in("community_id", communityIdList);
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            entityQueryWrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null){
            entityQueryWrapper.eq("fee_rule_id", 0);
        }
        entityQueryWrapper.eq("deleted", 0);
        entityQueryWrapper.eq("pay_type", 3);
        entityQueryWrapper.groupBy("fee_rule_id");
        // 查询余额支付
        List<PropertyFinanceOrderEntity> entities2 = propertyFinanceOrderMapper.selectList(entityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities2) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setBalancePaySum(propertyFinanceOrderEntity.getBalancePaySum());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> orderEntityQueryWrapper = new QueryWrapper<>();
        orderEntityQueryWrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS cashPaySum");
        if (qo.getStartTime() != null) {
            orderEntityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            orderEntityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            orderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            orderEntityQueryWrapper.in("community_id", communityIdList);
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            orderEntityQueryWrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null){
            orderEntityQueryWrapper.eq("fee_rule_id", 0);
        }
        orderEntityQueryWrapper.eq("deleted", 0);
        orderEntityQueryWrapper.eq("pay_type", 4);
        orderEntityQueryWrapper.groupBy("fee_rule_id");
        // 查询现金支付
        List<PropertyFinanceOrderEntity> entities3 = propertyFinanceOrderMapper.selectList(orderEntityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities3) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setCashPaySum(propertyFinanceOrderEntity.getCashPaySum());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> wrapper1 = new QueryWrapper<>();
        wrapper1.select("fee_rule_id as feeRuleId,SUM(total_money) AS UnionPaySum");
        if (qo.getStartTime() != null) {
            wrapper1.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper1.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper1.eq("community_id", qo.getCommunityId());
        } else {
            wrapper1.in("community_id", communityIdList);
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            wrapper1.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null){
            wrapper1.eq("fee_rule_id", 0);
        }
        wrapper1.eq("deleted", 0);
        wrapper1.eq("pay_type", 5);
        wrapper1.groupBy("fee_rule_id");
        // 查询银联刷卡支付
        List<PropertyFinanceOrderEntity> entities4 = propertyFinanceOrderMapper.selectList(wrapper1);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities4) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setUnionPaySum(propertyFinanceOrderEntity.getUnionPaySum());
                }
            }
        }
    
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("fee_rule_id as feeRuleId,SUM(total_money) AS bankPaySum");
        if (qo.getStartTime() != null) {
            queryWrapper1.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper1.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper1.eq("community_id", qo.getCommunityId());
        } else {
            queryWrapper1.in("community_id", communityIdList);
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            queryWrapper1.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null){
            queryWrapper1.eq("fee_rule_id", 0);
        }
        queryWrapper1.eq("deleted", 0);
        queryWrapper1.eq("pay_type", 6);
        queryWrapper1.groupBy("fee_rule_id");
        // 查询银行代扣支付
        List<PropertyFinanceOrderEntity> entities5 = propertyFinanceOrderMapper.selectList(queryWrapper1);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities5) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setBankPaySum(propertyFinanceOrderEntity.getBankPaySum());
                }
            }
        }
        // 补充项目名称
        List<Long> paramList = new ArrayList<>();
        for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
            paramList.add(collectionFormEntity.getFeeRuleId());
        }
        if (paramList.size() > 0 && paramList != null) {
            Map<String, Map<String, Object>> longMapMap = propertyFeeRuleMapper.selectFeeRuleIdName(paramList);
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                Map<String, Object> countMap = longMapMap.get(collectionFormEntity.getFeeRuleId());
                collectionFormEntity.setFeeRuleName(countMap != null ? String.valueOf(countMap.get("name")) : "");
            }
        }
    
        // 补充小区名称
        for (PropertyCollectionFormEntity entity : propertyCollectionFormEntityList) {
            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(entity.getFeeRuleId());
            CommunityEntity communityEntity = communityMapper.selectById(propertyFeeRuleEntity.getCommunityId());
            entity.setCommunityName(communityEntity.getName());
        }
    
        return propertyCollectionFormEntityList;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-账单统计-账单生成时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/19 11:08
     **/
    @Override
    public PropertyCollectionFormEntity getCollectionFormOrderByOrderGenerateTime(PropertyCollectionFormEntity qo) {
        // 返回给前端实体
        PropertyCollectionFormEntity propertyCollectionFormEntity = new PropertyCollectionFormEntity();
        propertyCollectionFormEntity.setStatementReceivableMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementArrearsMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementCollectMoney(new BigDecimal("0.00"));
    
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("order_status AS orderStatus,SUM(total_money) AS totalMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getTargetId() != null) {
            queryWrapper.eq("target_id", qo.getTargetId());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.groupBy("order_status");
        // 查询账单统计
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(queryWrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            // 已收款-实收
            if (entity.getOrderStatus() == 1) {
                propertyCollectionFormEntity.setStatementCollectMoney(entity.getTotalMoney());
            } else if (entity.getOrderStatus() == 0) {
                // 待收款-欠收
                propertyCollectionFormEntity.setStatementArrearsMoney(entity.getTotalMoney());
            }
        }
        // 总计-应收
        if (propertyCollectionFormEntity.getStatementCollectMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementArrearsMoney());
        } else if (propertyCollectionFormEntity.getStatementArrearsMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney());
        } else {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney().add(propertyCollectionFormEntity.getStatementArrearsMoney()));
        }
        // 补充房屋名称
        HouseEntity houseEntity = houseMapper.selectById(propertyCollectionFormEntity.getTargetId());
        if (houseEntity != null) {
            propertyCollectionFormEntity.setTargetIdName(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
        }
        
        return propertyCollectionFormEntity;
    }
    
    /**
     *@Author: DKS
     *@Description: 获取收款报表-账单统计-账单周期时间
     *@Param:
     *@Return: PropertyFinanceFormChargeEntity
     *@Date: 2021/8/19 11:08
     **/
    @Override
    public PropertyCollectionFormEntity getCollectionFormOrderByOrderPeriodTime(PropertyCollectionFormEntity qo) {
        // 返回给前端实体
        PropertyCollectionFormEntity propertyCollectionFormEntity = new PropertyCollectionFormEntity();
        propertyCollectionFormEntity.setStatementReceivableMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementArrearsMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementCollectMoney(new BigDecimal("0.00"));
    
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("order_status AS orderStatus,SUM(total_money) AS totalMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getTargetId() != null) {
            queryWrapper.eq("target_id", qo.getTargetId());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.ne("build_type",2);
        queryWrapper.groupBy("order_status");
        // 查询账单统计
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(queryWrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            // 已收款-实收
            if (entity.getOrderStatus() == 1) {
                propertyCollectionFormEntity.setStatementCollectMoney(entity.getTotalMoney());
            } else if (entity.getOrderStatus() == 0) {
                // 待收款-欠收
                propertyCollectionFormEntity.setStatementArrearsMoney(entity.getTotalMoney());
            }
        }
        // 总计-应收
        if (propertyCollectionFormEntity.getStatementCollectMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementArrearsMoney());
        } else if (propertyCollectionFormEntity.getStatementArrearsMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney());
        } else {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney().add(propertyCollectionFormEntity.getStatementArrearsMoney()));
        }
        // 补充房屋名称
        HouseEntity houseEntity = houseMapper.selectById(propertyCollectionFormEntity.getTargetId());
        if (houseEntity != null) {
            propertyCollectionFormEntity.setTargetIdName(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
        }
    
        return propertyCollectionFormEntity;
    }
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收入数据
     *@Param: propertyFinanceFormEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceFormEntity>
     *@Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyFinanceFormEntity> queryExportExcelFinanceFormList(PropertyFinanceFormEntity propertyFinanceFormEntity, List<Long> communityIdList) {
        List<PropertyFinanceFormEntity> propertyFinanceFormEntityList = new LinkedList<>();
        try {
        if (propertyFinanceFormEntity.getYear() != null) {
            String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyFinanceFormEntity.getYear());
            String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyFinanceFormEntity.getYear());
            propertyFinanceFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            propertyFinanceFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        if (propertyFinanceFormEntity.getMonth() != null) {
            String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyFinanceFormEntity.getMonth());
            String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyFinanceFormEntity.getMonth());
            propertyFinanceFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            propertyFinanceFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        propertyFinanceFormEntityList = getFinanceFormCommunityIncome(propertyFinanceFormEntity, communityIdList);
        if (propertyFinanceFormEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(),"查询为空");
        }
        return propertyFinanceFormEntityList;
    }
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收费报表
     *@Param: propertyFinanceFormChargeEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceFormChargeEntity>
     *@Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyFinanceFormChargeEntity> queryExportExcelChargeList(PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity, List<Long> communityIdList) {
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = new LinkedList<>();
        try {
            if (propertyFinanceFormChargeEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyFinanceFormChargeEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyFinanceFormChargeEntity.getYear());
                propertyFinanceFormChargeEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormChargeEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyFinanceFormChargeEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyFinanceFormChargeEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyFinanceFormChargeEntity.getMonth());
                propertyFinanceFormChargeEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormChargeEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (propertyFinanceFormChargeEntity.getType()) {
            case 1:
                // 按账单生成时间
                propertyFinanceFormChargeEntityList = getFinanceFormCommunityChargeByOrderGenerateTime(propertyFinanceFormChargeEntity, communityIdList);
                break;
            case 2:
                // 按账单周期时间
                propertyFinanceFormChargeEntityList = getFinanceFormCommunityChargeByOrderPeriodTime(propertyFinanceFormChargeEntity, communityIdList);
                break;
            default:
                break;
        }
        if (propertyFinanceFormChargeEntityList == null || propertyFinanceFormChargeEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(),"查询为空");
        }
        return propertyFinanceFormChargeEntityList;
    }

    /**
     *@Author: DKS
     *@Description: 导出收款报表-收款报表
     *@Param: propertyCollectionFormEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyCollectionFormEntity>
     *@Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyCollectionFormEntity> queryExportExcelCollectionFormList(PropertyCollectionFormEntity propertyCollectionFormEntity, List<Long> communityIdList) {
        try {
            if (propertyCollectionFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyCollectionFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyCollectionFormEntity.getYear());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getDateTime() != null) {
                ZoneId zone = ZoneId.systemDefault();
                Instant instant = propertyCollectionFormEntity.getDateTime().atStartOfDay().atZone(zone).toInstant();
                String firstDate = DateCalculateUtil.getFirstDate(Date.from(instant));
                String lastDate = DateCalculateUtil.getLastDate(Date.from(instant));
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<PropertyCollectionFormEntity> propertyFinanceFormEntityList = getCollectionFormCollection(propertyCollectionFormEntity, communityIdList);
        if (propertyFinanceFormEntityList == null || propertyFinanceFormEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(),"查询为空");
        }
        return propertyFinanceFormEntityList;
    }

    /**
     *@Author: DKS
     *@Description: 导出收款报表-账单统计
     *@Param: propertyCollectionFormEntity:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyCollectionFormEntity>
     *@Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyCollectionFormEntity> queryExportExcelCollectionFormOrderList(PropertyCollectionFormEntity propertyCollectionFormEntity) {
        List<PropertyCollectionFormEntity> propertyCollectionFormEntityList = new LinkedList<>();
        try {
            if (propertyCollectionFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyCollectionFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyCollectionFormEntity.getYear());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PropertyCollectionFormEntity entity = new PropertyCollectionFormEntity();
        switch (propertyCollectionFormEntity.getType()) {
            case 1:
                // 按账单生成时间
                entity = getCollectionFormOrderByOrderGenerateTime(propertyCollectionFormEntity);
                break;
            case 2:
                // 按账单周期时间
                entity = getCollectionFormOrderByOrderPeriodTime(propertyCollectionFormEntity);
                break;
            default:
                break;
        }
        propertyCollectionFormEntityList.add(entity);
        if (propertyCollectionFormEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(),"查询为空");
        }
        
        return propertyCollectionFormEntityList;
    }
    
    /**
     * @Description: 新增物业账单临时收费
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/26 09:35
     **/
    @Override
    public boolean addTemporaryCharges(PropertyFinanceOrderEntity propertyFinanceOrderEntity) {
        // 设置id
        propertyFinanceOrderEntity.setId(SnowFlake.nextId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = sdf.format(new Date());
        // 设置账单日期
        propertyFinanceOrderEntity.setOrderTime(LocalDate.parse(dateNow, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(propertyFinanceOrderEntity.getFeeRuleId());
        // 设置账单号
        propertyFinanceOrderEntity.setOrderNum(FinanceBillServiceImpl.getOrderNum(String.valueOf(propertyFinanceOrderEntity.getCommunityId()), propertyFeeRuleEntity.getSerialNumber()));
        // 设置总金额
        propertyFinanceOrderEntity.setTotalMoney(propertyFinanceOrderEntity.getPropertyFee());
        // 设置临时收费类型
        propertyFinanceOrderEntity.setBuildType(2);
        int row = propertyFinanceOrderMapper.insert(propertyFinanceOrderEntity);
        return row == 1;
    }

    /**
     * @Description: 批量修改账单状态
     * @author: Hu
     * @since: 2021/8/31 14:43
     * @Param: [ids]
     * @return: void
     */
    @Override
    public void updateStatusIds(String ids,Integer hide) {
        propertyFinanceOrderMapper.updateStatusIds(ids.split(","),hide);
    }



    /**
     * @Description: 查询车主在当前小区是否存在未交的物业费账单
     * @author: Hu
     * @since: 2021/9/3 9:51
     * @Param: [communityId]
     * @return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     */
    @Override
    public List<PropertyFinanceOrderEntity> FeeOrderList(Long communityId,String uid) {
        return propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("uid",uid).eq("community_id",communityId));
    }

    /**
     * @Description: 查询当前小区缴费项目
     * @author: Hu
     * @since: 2021/8/31 15:01
     * @Param: [adminCommunityId]
     * @return: java.util.List<com.jsy.community.entity.property.PropertyFeeRuleEntity>
     */
    @Override
    public List<PropertyFeeRuleEntity> getFeeList(Long adminCommunityId) {
        return propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>().select("id,name").eq("community_id",adminCommunityId));
    }

    /**
     * @Description: 批量删除账单
     * @author: Hu
     * @since: 2021/8/31 14:50
     * @Param: [ids]
     * @return: void
     */
    @Override
    public void deleteIds(String ids) {
        propertyFinanceOrderMapper.delete(new QueryWrapper<PropertyFinanceOrderEntity>().in("id", (Object) ids.split(",")));
    }
    
    /**
     * @Description: 收款
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/09/06 10:46
     **/
    @Override
    public Boolean collection(List<Long> ids, Long communityId, Integer payType) {
        int row;
        
        for (Long id : ids) {
            PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderMapper.selectById(id);
            if (propertyFinanceOrderEntity.getOrderStatus() == 1) {
                throw new PropertyException(JSYError.DUPLICATE_KEY.getCode(),"已收款,请勿重复支付！");
            }
            // 支付类型为预存款抵扣,需要判断预存款余额是否充足
            if (payType == 7) {
                // 关联类型是车位的话，需查出车位绑定的房屋
                if (propertyFinanceOrderEntity.getAssociatedType() == 2) {
                    CarPositionEntity carPositionEntity = carPositionMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                    PropertyAdvanceDepositEntity propertyAdvanceDepositEntity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(carPositionEntity.getHouseId(), communityId);
                    if (propertyAdvanceDepositEntity != null) {
                        if (propertyAdvanceDepositEntity.getBalance().add(propertyFinanceOrderEntity.getTotalMoney()).compareTo(BigDecimal.ZERO) == -1) {
                            HouseEntity houseEntity = houseMapper.selectById(carPositionEntity.getHouseId());
                            throw new PropertyException(JSYError.NOT_ENOUGH.getCode(),houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getFloor() + "-" + houseEntity.getDoor() + "余额不足！");
                        }
                    }
                } else if (propertyFinanceOrderEntity.getAssociatedType() == 1) {
                    // 关联类型是房屋的话，根据id查询当前余额是否充足
                    PropertyAdvanceDepositEntity entity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(propertyFinanceOrderEntity.getTargetId(), communityId);
                    if (entity != null) {
                        if (entity.getBalance().add(propertyFinanceOrderEntity.getTotalMoney()).compareTo(BigDecimal.ZERO) == -1) {
                            HouseEntity houseEntity = houseMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                            throw new PropertyException(JSYError.NOT_ENOUGH.getCode(),houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getFloor() + "-" + houseEntity.getDoor() + "余额不足！");
                        }
                    }
                }
            }
        }
        
        //更新收款状态为已支付
        row = propertyFinanceOrderMapper.collection(ids, payType);
    
        if (row == 1) {
            for (Long id : ids) {
                if (payType == 7) {
                    PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderMapper.selectById(id);
                    
                    // 关联类型是车位的话，需查出车位绑定的房屋
                    if (propertyFinanceOrderEntity.getAssociatedType() == 2) {
                        CarPositionEntity carPositionEntity = carPositionMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                        PropertyAdvanceDepositEntity propertyAdvanceDepositEntity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(carPositionEntity.getHouseId(), communityId);
                        // 放入这次抵扣的金额
                        propertyAdvanceDepositEntity.setBalanceRecord(propertyFinanceOrderEntity.getTotalMoney());
                        propertyAdvanceDepositEntity.setBalance(propertyAdvanceDepositEntity.getBalance().add(propertyFinanceOrderEntity.getTotalMoney().negate()));
                        propertyAdvanceDepositEntity.setUpdateTime(LocalDateTime.now());
                        // 更新预存款余额
                        propertyAdvanceDepositMapper.updateById(propertyAdvanceDepositEntity);
                        // 抵扣成功后，立即生成预存款变更明细记录
                        PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
                        propertyAdvanceDepositRecordEntity.setCommunityId(propertyAdvanceDepositEntity.getCommunityId());
                        propertyAdvanceDepositRecordEntity.setType(1);
                        propertyAdvanceDepositRecordEntity.setOrderId(id);
                        // 查最新一次记录并设置余额明细
                        PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity1 = propertyAdvanceDepositRecordMapper.queryMaxCreateTimeRecord(
                            propertyAdvanceDepositEntity.getId(), propertyAdvanceDepositEntity.getCommunityId());
                        propertyAdvanceDepositRecordEntity.setPayAmount(propertyAdvanceDepositEntity.getBalanceRecord());
                        propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositRecordEntity1.getBalanceRecord().add(propertyAdvanceDepositEntity.getBalanceRecord().negate()));
                        propertyAdvanceDepositRecordEntity.setAdvanceDepositId(propertyAdvanceDepositEntity.getId());
                        propertyAdvanceDepositRecordEntity.setComment(propertyAdvanceDepositEntity.getComment());
                        propertyAdvanceDepositRecordEntity.setUpdateBy(propertyAdvanceDepositEntity.getUpdateBy());
                        PropertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
                    } else if (propertyFinanceOrderEntity.getAssociatedType() == 1) {
                        PropertyAdvanceDepositEntity propertyAdvanceDepositEntity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(propertyFinanceOrderEntity.getTargetId(), communityId);
                        // 放入这次抵扣的金额
                        propertyAdvanceDepositEntity.setBalanceRecord(propertyFinanceOrderEntity.getTotalMoney());
                        propertyAdvanceDepositEntity.setBalance(propertyAdvanceDepositEntity.getBalance().add(propertyFinanceOrderEntity.getTotalMoney().negate()));
                        propertyAdvanceDepositEntity.setUpdateTime(LocalDateTime.now());
                        // 更新预存款余额
                        propertyAdvanceDepositMapper.updateById(propertyAdvanceDepositEntity);
                        // 抵扣成功后，立即生成预存款变更明细记录
                        PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
                        propertyAdvanceDepositRecordEntity.setCommunityId(propertyAdvanceDepositEntity.getCommunityId());
                        propertyAdvanceDepositRecordEntity.setType(1);
                        propertyAdvanceDepositRecordEntity.setOrderId(id);
                        // 查最新一次记录并设置余额明细
                        PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity1 = propertyAdvanceDepositRecordMapper.queryMaxCreateTimeRecord(
                            propertyAdvanceDepositEntity.getId(), propertyAdvanceDepositEntity.getCommunityId());
                        propertyAdvanceDepositRecordEntity.setPayAmount(propertyAdvanceDepositEntity.getBalanceRecord());
                        propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositRecordEntity1.getBalanceRecord().add(propertyAdvanceDepositEntity.getBalanceRecord().negate()));
                        propertyAdvanceDepositRecordEntity.setAdvanceDepositId(propertyAdvanceDepositEntity.getId());
                        propertyAdvanceDepositRecordEntity.setComment(propertyAdvanceDepositEntity.getComment());
                        propertyAdvanceDepositRecordEntity.setUpdateBy(propertyAdvanceDepositEntity.getUpdateBy());
                        PropertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
                    }
                }
            }
        }
        
        return row == 1;
    }
    
    /**
     *@Author: DKS
     *@Description: 导入账单信息
     *@Param: excel:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/9/7 11:25
     **/
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveFinanceOrder(List<PropertyFinanceOrderEntity> propertyFinanceOrderEntityList, Long communityId, String uid) {
        // 需要添加的账单实体
        List<PropertyFinanceOrderEntity> addPropertyFinanceOrderEntityList = new ArrayList<>();
        
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntityList) {
            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(propertyFinanceOrderEntity.getFeeRuleId());
            // 批量新增
            PropertyFinanceOrderEntity entity = new PropertyFinanceOrderEntity();
            entity.setId(SnowFlake.nextId());
            entity.setCommunityId(communityId);
            entity.setBeginTime(propertyFinanceOrderEntity.getBeginTime());
            entity.setOverTime(propertyFinanceOrderEntity.getOverTime());
            entity.setOrderTime(LocalDate.now());
            entity.setAssociatedType(propertyFinanceOrderEntity.getAssociatedType());
            entity.setUid(propertyFinanceOrderEntity.getUid());
            entity.setTargetId(propertyFinanceOrderEntity.getTargetId());
            entity.setPropertyFee(propertyFinanceOrderEntity.getPropertyFee());
            entity.setTotalMoney(propertyFinanceOrderEntity.getPropertyFee());
            entity.setOrderStatus(0);
            entity.setBuildType(3);
            entity.setHide(1);
            entity.setType(propertyFeeRuleEntity.getType());
            entity.setFeeRuleId(propertyFeeRuleEntity.getId());
            entity.setOrderNum(FinanceBillServiceImpl.getOrderNum(String.valueOf(propertyFeeRuleEntity.getCommunityId()),propertyFeeRuleEntity.getSerialNumber()));
            entity.setDeleted(0);
            entity.setCreateTime(LocalDateTime.now());
            addPropertyFinanceOrderEntityList.add(entity);
        }
        // 批量新增账单
        Integer saveFinanceOrderRow = 0;
        if (addPropertyFinanceOrderEntityList.size() > 0) {
            saveFinanceOrderRow = propertyFinanceOrderMapper.saveFinanceOrder(addPropertyFinanceOrderEntityList);
        }
        return saveFinanceOrderRow;
    }
    
    /**
     *@Author: DKS
     *@Description: 导出账单信息
     *@Param: excel:
     *@Return: com.jsy.community.vo.CommonResult
     *@Date: 2021/9/8 10:40
     **/
    @Override
    public List<PropertyFinanceOrderEntity> queryExportFinanceExcel(PropertyFinanceOrderEntity qo) {
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities;
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        //是否查关联类型
        if (qo.getAssociatedType() != null) {
            queryWrapper.eq("associated_type", qo.getAssociatedType());
        }
        //是否查关联目标
        if (qo.getTargetId() != null) {
            queryWrapper.eq("target_id", qo.getTargetId());
        }
        //是否查收费项目
        if (qo.getFeeRuleName() != null) {
            List<Long> communityIds = new ArrayList<>();
            communityIds.add(qo.getCommunityId());
            List<Long> feeRuleIdList = propertyFeeRuleMapper.selectFeeRuleIdList(communityIds, qo.getFeeRuleName());
            queryWrapper.in("fee_rule_id", feeRuleIdList);
        }
        //是否查交易单号
        if (qo.getOrderNum() != null) {
            queryWrapper.eq("order_num", qo.getOrderNum());
        }
        //是否查生成时间
        if (qo.getCreateTime() != null) {
            queryWrapper.eq("create_time", qo.getCreateTime());
        }
        //是否查开始时间和结束时间
        if (qo.getBeginTime() != null && qo.getOverTime() != null) {
            queryWrapper.eq("begin_time", qo.getBeginTime());
            queryWrapper.eq("over_time", qo.getOverTime());
        }
        //是否查状态
        if (qo.getHide() != null) {
            queryWrapper.eq("hide", qo.getHide());
        }
	    //是否查账单状态
	    if (qo.getOrderStatus() != null) {
		    queryWrapper.eq("order_status", qo.getOrderStatus());
	    }
        queryWrapper.orderByDesc("create_time");
        propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(propertyFinanceOrderEntities)) {
            return propertyFinanceOrderEntities;
        }
        //补充返回数据
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            // 补充关联目标名称
            if (propertyFinanceOrderEntity.getAssociatedType() == 1) {
                HouseEntity houseEntity = houseMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                if (houseEntity != null) {
                    propertyFinanceOrderEntity.setAddress(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
                }
            } else if (propertyFinanceOrderEntity.getAssociatedType() == 2) {
                CarPositionEntity carPositionEntity = carPositionMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                propertyFinanceOrderEntity.setAddress(carPositionEntity.getCarPosition());
            }
            // 补充收费项目
            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(propertyFinanceOrderEntity.getFeeRuleId());
            if (propertyFeeRuleEntity != null) {
                propertyFinanceOrderEntity.setFeeRuleName(propertyFeeRuleEntity.getName());
            }
        }
        
        return propertyFinanceOrderEntities;
    }
}

