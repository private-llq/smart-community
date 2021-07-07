package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.mapper.PropertyFinanceOrderMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import com.jsy.community.vo.property.UserPropertyFinanceOrderVO;
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




    /**
     * @Description: 查询房间所有未缴账单
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [userInfo, houseId]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object> houseCost(AdminInfoVo userInfo, Long houseId) {
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("house_id",houseId).eq("order_status",0));
        if (list!=null){
            List<Object> objects = new ArrayList<>();
            HouseEntity entity = houseService.getById(houseId);
            for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
                PropertyFinanceOrderVO orderVO = new PropertyFinanceOrderVO();
                BeanUtils.copyProperties(propertyFinanceOrderEntity,orderVO);
                BeanUtils.copyProperties(entity,orderVO);
                orderVO.setId(propertyFinanceOrderEntity.getId());
                orderVO.setOrderNum(propertyFinanceOrderEntity.getOrderNum());
                orderVO.setHouseTypeText(entity.getHouseType()==1?"商铺":"住宅");
                objects.add(orderVO);
            }
            UserEntity userEntity = userService.queryUserDetailByUid(list.get(0).getUid());
            UserPropertyFinanceOrderVO vo = new UserPropertyFinanceOrderVO();
            vo.setRealName(userEntity.getRealName());
            vo.setNumber(entity.getNumber());
            Map<String, Object> map = new HashMap<>();
            map.put("user",vo);
            map.put("bill",objects);
            return map;
        }
        return null;
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
    public PropertyFinanceOrderVO getOrderNum(AdminInfoVo userInfo, String orderNum) {
        PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderMapper.selectOne(new QueryWrapper<PropertyFinanceOrderEntity>().eq("order_num", orderNum));
        HouseEntity houseEntity = houseService.getOne(new QueryWrapper<HouseEntity>().eq("id", propertyFinanceOrderEntity.getHouseId()));
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
        if(query.getHouseId() != null){
            queryWrapper.eq("house_id",query.getHouseId());
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
            houseIds.add(entity.getHouseId());
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
            entity.setAddress(houseMap.get(entity.getHouseId()) == null ? null : houseMap.get(entity.getHouseId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setUid(null);
            entity.setHouseId(null);
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
        if(query.getHouseId() != null){
            queryWrapper.eq("house_id",query.getHouseId());
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
            houseIds.add(entity.getHouseId());
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
            entity.setAddress(houseMap.get(entity.getHouseId()) == null ? null : houseMap.get(entity.getHouseId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setStatementEntity(statementEntityMap.get(entity.getStatementNum()) == null ? null : statementEntityMap.get(entity.getStatementNum()));
	        entity.setUid(null);
	        entity.setHouseId(null);
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
        if(query.getHouseId() != null){
            queryWrapper.eq("house_id",query.getHouseId());
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
            houseIds.add(entity.getHouseId());
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
            entity.setAddress(houseMap.get(entity.getHouseId()) == null ? null : houseMap.get(entity.getHouseId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setStatementEntity(statementEntityMap.get(entity.getStatementNum()) == null ? null : statementEntityMap.get(entity.getStatementNum()));
        }
        return orderEntities;
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
     * @Description: 修改已经支付了的物业账单状态
     * @author: Hu
     * @since: 2021/7/5 17:33
     * @Param:
     * @return:
     */
    @Override
    public void UpdateOrderStatus(Map<String, String> map, String[] ids) {
        //根据传来的id集合查询账单
        List<PropertyFinanceOrderEntity> list=propertyFinanceOrderMapper.selectByIdsList(ids);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
            propertyFinanceOrderEntity.setOrderStatus(1);
            propertyFinanceOrderEntity.setPayType(1);
            propertyFinanceOrderEntity.setPayTime(LocalDateTime.now());
            propertyFinanceOrderEntity.setTripartiteOrder(map.get("out_trade_no"));
            propertyFinanceOrderMapper.updateById(propertyFinanceOrderEntity);
        }
    }

    @Override
    /**
     * @Description: 查询物业账单总金额
     * @author: Hu
     * @since: 2021/7/5 16:12
     * @Param: [orderIds]
     * @return: java.math.BigDecimal
     */
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


}
