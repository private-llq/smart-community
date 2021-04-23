package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.mapper.PropertyFinanceOrderMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import com.jsy.community.vo.property.UserPropertyFinanceOrderVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseService houseService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IUserService userService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceReceiptService propertyFinanceReceiptService;
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceStatementService propertyFinanceStatementService;


    /**
     * @Description: 更新所有小区账单
     * @author: Hu
     * @since: 2021/4/22 9:28
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public void updateDays(){
        List<Long> list=propertyFinanceOrderMapper.communityIdList();
        for (Long id : list) {
            PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("community_id",id));
            if (entity != null) {
            List<HouseEntity> entities = propertyFinanceOrderMapper.selectHouseAll(id);
            for (HouseEntity houseEntity : entities) {
                    if (entity.getPeriod() == 1) {
                        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
                        orderEntity.setOrderNum(getOrderNum(id+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(id);
                        orderEntity.setUid(houseEntity.getUid());
                        orderEntity.setHouseId(houseEntity.getId());
                        orderEntity.setPropertyFee(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setPenalSum(new BigDecimal(0));
                        orderEntity.setTotalMoney(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setOrderStatus(0);
                        orderEntity.setId(SnowFlake.nextId());
                        propertyFinanceOrderMapper.insert(orderEntity);
                    }else if (LocalDate.now().getDayOfMonth() == 1) {
                        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
                        orderEntity.setOrderNum(getOrderNum(id+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(id);
                        orderEntity.setUid(houseEntity.getUid());
                        orderEntity.setHouseId(houseEntity.getId());
                        orderEntity.setPropertyFee(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setPenalSum(new BigDecimal(0));
                        orderEntity.setTotalMoney(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setOrderStatus(0);
                        orderEntity.setId(SnowFlake.nextId());
                        propertyFinanceOrderMapper.insert(orderEntity);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> houseCost(AdminInfoVo userInfo, Long houseId) {
        List<PropertyFinanceOrderVO> list = propertyFinanceOrderMapper.houseCost(houseId);
        for (PropertyFinanceOrderVO propertyFinanceOrderVO : list) {
            propertyFinanceOrderVO.setHouseTypeText(propertyFinanceOrderVO.getHouseType()==1?"商铺":"住宅");
        }
        UserPropertyFinanceOrderVO userPropertyFinanceOrderVO=propertyFinanceOrderMapper.findUser(houseId);
        Map<String, Object> map = new HashMap<>();
        map.put("user",userPropertyFinanceOrderVO);
        map.put("bill",list);
        return map;
    }

    /**
     * @Description: 更新所有小区账单
     * @author: Hu
     * @since: 2021/4/22 9:28
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public void updatePenalSum(){
        List<Long> list=propertyFinanceOrderMapper.communityIdList();
        for (Long id : list) {
            PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("community_id",id));
            if (entity != null) {
                List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("community_id", id).eq("order_status",0));
                for (PropertyFinanceOrderEntity orderEntity : entities) {
                        //在缴费规则的条件下把账单加上违约天数和当前时间比较
                        if (orderEntity.getOrderTime().plusDays(entity.getPenalDays()).isBefore(LocalDate.now())) {
                            orderEntity.setPenalSum(orderEntity.getPenalSum().add(orderEntity.getPropertyFee().multiply(entity.getPenalSum())));
                            orderEntity.setTotalMoney(orderEntity.getPropertyFee().multiply(entity.getPenalSum()).add(orderEntity.getTotalMoney()));
                            propertyFinanceOrderMapper.updateById(orderEntity);
                        }
                }
            }
        }
    }

    /**
     * @Description: 生成账单号
     * @author: Hu
     * @since: 2021/4/22 9:28
     * @Param:
     * @return:
     */
    public String getOrderNum(String communityId,String serialNumber){
        StringBuilder str=new StringBuilder();
        if (communityId.length()>=4){
            String s = communityId.substring(communityId.length() - 4, communityId.length());
            str.append(s);
        }else {
            if (communityId.length()==3){
                str.append("0"+communityId);
            } else{
                if (communityId.length()==2){
                    str.append("00"+communityId);
                } else {
                    str.append("000"+communityId);
                }
            }
        }
        String substring = serialNumber.substring(serialNumber.length() - 2, serialNumber.length());
        str.append(substring);
        long time = new Date().getTime();
        String s = String.valueOf(time).substring(String.valueOf(time).length() - 10, String.valueOf(time).length());
        str.append(time);
        int s1=(int) (Math.random() * 99);
        str.append(s1);
        return str.toString();
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
    * @Description: 分页查询
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
            queryWrapper.ge("order_time",query.getOrderEndDate());
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
                if(!CollectionUtils.isEmpty(uids)){
                    queryWrapper.in("uid",uids);
                }
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
        //金额统计数据(账单)
        BigDecimal totalOrder = new BigDecimal(0);//应收合计
        BigDecimal notReceipt = new BigDecimal(0);//0.待收款
        BigDecimal receipted = new BigDecimal(0);//1.已收款
        //金额统计数据(结算单)
        BigDecimal notStatement = new BigDecimal(0);//1.待结算
        BigDecimal statementing = new BigDecimal(0);//2.结算中
        BigDecimal statemented = new BigDecimal(0);//3.已结算
        BigDecimal statementReject = new BigDecimal(0);//4.驳回
        //设置数据
        for(PropertyFinanceOrderEntity entity : pageData.getRecords()){
            entity.setAddress(houseMap.get(entity.getHouseId()) == null ? null : houseMap.get(entity.getHouseId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setStatementEntity(statementEntityMap.get(entity.getStatementNum()) == null ? null : statementEntityMap.get(entity.getStatementNum()));
            //金额统计
            totalOrder = totalOrder.add(entity.getTotalMoney());
            switch (entity.getOrderStatus()){
                case 0:
                    notReceipt = notReceipt.add(entity.getTotalMoney());
                    break;
                case 1:
                    receipted = receipted.add(entity.getTotalMoney());
                    break;
            }
            if(entity.getStatementEntity() != null){
                switch (entity.getStatementStatus()){
                    case 1:
                        notStatement = notStatement.add(entity.getStatementEntity().getTotalMoney());
                        break;
                    case 2:
                        statementing = statementing.add(entity.getStatementEntity().getTotalMoney());
                        break;
                    case 3:
                        statemented = statemented.add(entity.getStatementEntity().getTotalMoney());
                        break;
                    case 4:
                        statementReject = statementReject.add(entity.getStatementEntity().getTotalMoney());
                        break;
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

}
