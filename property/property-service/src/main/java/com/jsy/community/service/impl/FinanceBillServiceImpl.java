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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public void updateMonth(){
        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity=null;

        //查询所有小区收费类型为周期  收费周期为按月的收费项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("bill_day", LocalDateTime.now()
                        .getDayOfMonth())
                .eq("disposable",2)
                .eq("period",1));
        for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
            //生成上月账单
            LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue()-1);
            //获取当前缴费项目关联的房间或者车位id集合
            List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
            //relevanceType等于1表示关联的是房屋，2表示关联的是车位
            if (feeRuleEntity.getRelevanceType()==1){
                //leisure等于一表示要生成空置房间的账单  相反则不
                if (feeRuleEntity.getLeisure()==1){
                    //查询所有房间账单包括没有业主认证的房间
                    List<HouseEntity> house = houseMapper.getHouseAll(feeRuleEntity.getCommunityId());
                    for (HouseEntity houseEntity : house) {
                        entity = new PropertyFinanceOrderEntity();
                        entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                        entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                        entity.setType(feeRuleEntity.getType());
                        entity.setFeeRuleId(feeRuleEntity.getId());
                        entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                        entity.setCommunityId(feeRuleEntity.getCommunityId());
                        entity.setOrderTime(LocalDate.now());
                        entity.setAssociatedType(1);
                        entity.setUid(houseEntity.getUid());
                        entity.setTargetId(houseEntity.getHouseId());
                        entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                        entity.setId(SnowFlake.nextId());
                        orderList.add(entity);
                    }
                } else {
                    //查询所有业主认证过的房间
                    List<HouseEntity> houseAll = houseMapper.selectHouseAll(feeRuleEntity.getCommunityId());
                    for (HouseEntity houseEntity : houseAll) {
                        entity = new PropertyFinanceOrderEntity();
                        entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                        entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                        entity.setType(feeRuleEntity.getType());
                        entity.setFeeRuleId(feeRuleEntity.getId());
                        entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                        entity.setCommunityId(feeRuleEntity.getCommunityId());
                        entity.setOrderTime(LocalDate.now());
                        entity.setAssociatedType(1);
                        entity.setUid(houseEntity.getUid());
                        entity.setTargetId(houseEntity.getHouseId());
                        entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                        entity.setId(SnowFlake.nextId());
                        orderList.add(entity);
                    }
                }

            }else{
                    //Type等于11表示车位管理费  只查所有业主自己产权的车位
                    if (feeRuleEntity.getType()==11){
                        //查询当前小区业主自用的车位
                        List<CarPositionEntity> entityList = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().eq("community_id", feeRuleEntity.getCommunityId()).eq("car_pos_status",1));
                        for (CarPositionEntity positionEntity : entityList) {
                            entity = new PropertyFinanceOrderEntity();
                            entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                            entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                            entity.setType(feeRuleEntity.getType());
                            entity.setFeeRuleId(feeRuleEntity.getId());
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setAssociatedType(2);
                            entity.setUid(positionEntity.getUid());
                            entity.setTargetId(positionEntity.getHouseId());
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit());
                            entity.setId(SnowFlake.nextId());
                            orderList.add(entity);
                        }
                    }else {
                        //Type等于12表示车位租金  查询有的月租车
                        if (feeRuleEntity.getType()==12){
                            //查询所有月租车车位
                            List<CarPositionEntity> entityList = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().eq("community_id", feeRuleEntity.getCommunityId()).eq("car_pos_status",2));
                            for (CarPositionEntity positionEntity : entityList) {
                                entity = new PropertyFinanceOrderEntity();
                                entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                                entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                                entity.setType(feeRuleEntity.getType());
                                entity.setFeeRuleId(feeRuleEntity.getId());
                                entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                                entity.setCommunityId(feeRuleEntity.getCommunityId());
                                entity.setOrderTime(LocalDate.now());
                                entity.setAssociatedType(2);
                                entity.setUid(positionEntity.getUid());
                                entity.setTargetId(positionEntity.getHouseId());
                                entity.setPropertyFee(feeRuleEntity.getMonetaryUnit());
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
    public void updateAnnual(){
        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity=null;
        //查询当前天所要生成订单的缴费项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("bill_month",LocalDate.now().getMonthValue()).eq("bill_day", LocalDateTime.now().getDayOfMonth()));
        for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
            //获取当前缴费项目关联的房间或者车位id集合
            List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
            //查询所有未空置的房间生成账单
            List<HouseEntity> list=houseMapper.selectUserHouseAuth(ruleList);

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
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("bill_day", LocalDateTime.now().getDayOfMonth()));
        for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
            LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue()-1);
            //获取当前缴费项目关联的房间或者车位id集合
            List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
            //查询所有未空置的房间生成账单
            List<HouseEntity> list=houseMapper.selectUserHouseAuth(ruleList);

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
        for (PropertyFinanceOrderEntity entity : list) {
            //如果超过违约天数还未缴就生成违约金
            if (entity.getOrderTime().plusDays(map.get(entity.getFeeRuleId()).getPenalDays()).isBefore(LocalDate.now())) {
                entity.setPenalSum(entity.getPenalSum().add(entity.getPropertyFee().multiply(map.get(entity.getFeeRuleId()).getPenalSum())));
                entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum()));
                propertyFinanceOrderMapper.updateById(entity);
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
    public static String getOrderNum(String communityId,String serialNumber){
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
        long millis = System.currentTimeMillis();
        String time = String.valueOf(millis).substring(String.valueOf(millis).length() - 10, String.valueOf(millis).length());
        str.append(time);
        int s1=(int) (Math.random() * 99);
        str.append(s1);
        return str.toString();
    }


}
