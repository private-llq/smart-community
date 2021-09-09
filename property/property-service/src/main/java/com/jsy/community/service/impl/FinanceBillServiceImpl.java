package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IFinanceBillService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @program: com.jsy.community
 * @description:  财务账单每天更新实现类
 * @author: Hu
 * @create: 2021-04-24 14:16
 **/
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class FinanceBillServiceImpl implements IFinanceBillService {

    @Autowired
    private PropertyFinanceOrderMapper propertyFinanceOrderMapper;

    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;

    @Autowired
    private PropertyFeeRuleRelevanceMapper propertyFeeRuleRelevanceMapper;

    @Autowired
    private UserHouseMapper userHouseMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private CarPositionMapper carPositionMapper;




    /**
     * @Description: 更新所有按月生成的周期账单
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */

    @Transactional(rollbackFor = Exception.class)
    public void updateMonth() {
        //上月個的天数
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, LocalDate.now().getYear());
        cal.set(Calendar.MONTH, LocalDate.now().minusMonths(1).getMonthValue() - 1);
        int dateOfMonth = cal.getActualMaximum(Calendar.DATE);

        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity = null;

        //查询所有小区收费类型为周期  收费周期为按月的收费项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("bill_day", LocalDateTime.now()
                        .getDayOfMonth())
                .eq("disposable", 2)
                .eq("period", 1));
        if (feeRuleEntities.size() != 0) {
            for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
                //生成上月账单
                LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue() - 1);
                //获取当前缴费项目关联的房间或者车位id集合
                List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
                //relevanceType等于1表示关联的是房屋，2表示关联的是车位
                if (feeRuleEntity.getRelevanceType() == 1) {
                    //查询所有缴费项目关联的房间
                    List<HouseEntity> house = houseMapper.selectInIds(ruleList);
                    for (HouseEntity houseEntity : house) {
                        entity = new PropertyFinanceOrderEntity();
                        entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                        entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                        entity.setType(feeRuleEntity.getType());
                        entity.setFeeRuleId(feeRuleEntity.getId());
                        entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                        entity.setCommunityId(feeRuleEntity.getCommunityId());
                        entity.setOrderTime(LocalDate.now());
                        entity.setAssociatedType(1);
                        entity.setUid(houseEntity.getUid());
                        entity.setTargetId(houseEntity.getHouseId());
                        //单价乘建筑面积乘周期
                        entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())).multiply(new BigDecimal(dateOfMonth)));
                        entity.setId(SnowFlake.nextId());
                        orderList.add(entity);
                    }
                } else {
                    //查询当前收费项目关联的车位
                    List<CarPositionEntity> entityList = carPositionMapper.selectBatchIds(ruleList);
                    for (CarPositionEntity positionEntity : entityList) {
                        if (LocalDateTime.now().isAfter(positionEntity.getEndTime())) {
                            entity = new PropertyFinanceOrderEntity();
                            entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                            entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                            entity.setType(feeRuleEntity.getType());
                            entity.setFeeRuleId(feeRuleEntity.getId());
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setAssociatedType(2);
                            entity.setUid(positionEntity.getUid());
                            entity.setTargetId(positionEntity.getHouseId());
                            //单价乘周期
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(dateOfMonth)));
                            entity.setId(SnowFlake.nextId());
                            orderList.add(entity);
                        }
                    }
                }

            }
        }
        if (orderList!=null&&orderList.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(orderList);
        }

    }

    /**
     * @Description: 更新所有按年生成的周期账单
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    public void updateAnnual() {
        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity = null;
        //查询今天所有需要年度收费的项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("bill_month", LocalDate.now().getMonthValue())
                .eq("bill_day", LocalDateTime.now().getDayOfMonth())
                .eq("period", 4));
        if (feeRuleEntities.size() != 0) {
            for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
                //获取当前缴费项目关联的房间或者车位id集合
                List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
                //查询所有未空置的房间生成账单
                List<HouseEntity> list = houseMapper.selectInIds(ruleList);
                for (HouseEntity houseEntity : list) {
                    entity = new PropertyFinanceOrderEntity();
                    //去年第一天
                    entity.setBeginTime(LocalDateTime.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear()).withHour(0).withMinute(0).withSecond(0).toLocalDate());
                    //去年最后一天
                    entity.setOverTime(LocalDateTime.now().minusYears(1).with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59).withSecond(59).toLocalDate());
                    entity.setType(feeRuleEntity.getType());
                    entity.setFeeRuleId(feeRuleEntity.getId());
                    entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                    entity.setCommunityId(feeRuleEntity.getCommunityId());
                    entity.setOrderTime(LocalDate.now());
                    entity.setAssociatedType(1);
                    entity.setUid(houseEntity.getUid());
                    entity.setTargetId(houseEntity.getHouseId());
                    entity.setPropertyFee(feeRuleEntity.getMonetaryUnit());
                    entity.setId(SnowFlake.nextId());
                    orderList.add(entity);
                }
            }
        }
        if (orderList!=null&&orderList.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(orderList);
        }

    }


    /**
     * @Description: 更新所有临时的账单   临时账单只更新一次  更新完成过后就把收费项目的状态改为未启动或者删除临时项目
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTemporary(){
        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity=null;
        //查询当前天所要生成订单的缴费项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("disposable",1));
        if (feeRuleEntities.size()!=0){
            for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
                LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue()-1);
                //获取当前缴费项目关联的房间或者车位id集合
                List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
                //查询收费项目关联的所有房屋
                List<HouseEntity> list=houseMapper.selectInIds(ruleList);
                //装修管理费
                if (feeRuleEntity.getType()==1){
                    for (HouseEntity positionEntity : list) {

                    entity = new PropertyFinanceOrderEntity();
                    entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                    entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                    entity.setType(feeRuleEntity.getType());
                    entity.setFeeRuleId(feeRuleEntity.getId());
                    entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                    entity.setCommunityId(feeRuleEntity.getCommunityId());
                    entity.setOrderTime(LocalDate.now());
                    entity.setAssociatedType(2);
                    entity.setUid(positionEntity.getUid());
                    entity.setTargetId(positionEntity.getHouseId());
                    entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(positionEntity.getBuildArea())));
                    entity.setId(SnowFlake.nextId());
                    orderList.add(entity);
                }
                } else {
                    if (feeRuleEntity.getType()==9||feeRuleEntity.getType()==10){
                        for (HouseEntity houseEntity : list) {
                            entity = new PropertyFinanceOrderEntity();
                            entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                            entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                            entity.setType(feeRuleEntity.getType());
                            entity.setFeeRuleId(feeRuleEntity.getId());
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setAssociatedType(2);
                            entity.setUid(houseEntity.getUid());
                            entity.setTargetId(houseEntity.getHouseId());
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit());
                            entity.setId(SnowFlake.nextId());
                            orderList.add(entity);
                        }

                    }
                }
                //修改收费项目启用状态
                feeRuleEntity.setStatus(0);
                propertyFeeRuleMapper.updateById(feeRuleEntity);
            }
        }

        if (orderList!=null&&orderList.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(orderList);
        }

    }




    /**
     * @Description: 更新小区账单的违约金
     * @author: Hu
     * @since: 2021/5/21 11:03
     * @Param: []
     * @return: void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePenalSum(){
        Map<Long, PropertyFeeRuleEntity> map = new HashMap<>();
        //查询所有缴费项目封装到map里面
        List<PropertyFeeRuleEntity> ruleEntities = propertyFeeRuleMapper.selectList(null);
        for (PropertyFeeRuleEntity ruleEntity : ruleEntities) {
            map.put(ruleEntity.getId(),ruleEntity);
        }
        //查詢所有未缴费的订单
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("order_status", 0));
        if (list.size()!=0){
            for (PropertyFinanceOrderEntity entity : list) {
                //如果超过违约天数还未缴就生成违约金
                if (entity.getOrderTime().plusDays(map.get(entity.getFeeRuleId()).getPenalDays()).isBefore(LocalDate.now())) {
                    entity.setPenalSum(entity.getPenalSum().add(entity.getPropertyFee().multiply(map.get(entity.getFeeRuleId()).getPenalSum())));
                    entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum()));
                    propertyFinanceOrderMapper.updateById(entity);
                }
            }
        }


    }

    /**
     * @Description: 生成账单号
     * @author: Hu
     * @since: 2021/5/21 11:03
     * @Param:
     * @return:
     */
    public static String getOrderNum(String communityId){
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
        long millis = System.currentTimeMillis();
        str.append(millis);
        int s1=(int) (Math.random() * 99);
        str.append(s1);
        return str.toString();
    }


}
