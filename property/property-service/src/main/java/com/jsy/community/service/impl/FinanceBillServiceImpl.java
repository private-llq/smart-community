package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IFinanceBillService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.mapper.PropertyFinanceOrderMapper;
import com.jsy.community.mapper.UserHouseMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

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
    private UserHouseMapper userHouseMapper;

    @Autowired
    private HouseMapper houseMapper;




    /**
     * @Description: 更新所有小区账单
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDays(){
        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity=null;
        //查询当前天所要生成订单的缴费项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("bill_day", LocalDateTime.now().getDayOfMonth()));
        for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
            //获取当前缴费项目关联的房间或者车位id集合
            String[] split = feeRuleEntity.getRelevance().split(",");
            //如果chargeMode=1表示按面积计算
            if (feeRuleEntity.getChargeMode()==1) {
                //disposable=1表示临时
                if (feeRuleEntity.getDisposable()==1){
                        //查询所有未空置的房间生成账单
                        List<HouseEntity> list=houseMapper.selectUserHouseAuth(split);
                        for (HouseEntity houseEntity : list) {
                            entity = new PropertyFinanceOrderEntity();
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setUid("");
                            entity.setHouseId(houseEntity.getHouseId());
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                            entity.setPenalSum(new BigDecimal("0"));
                            entity.setTotalMoney(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                            entity.setOrderStatus(0);
                            entity.setId(SnowFlake.nextId());
                            entity.setCreateTime(LocalDateTime.now());
                            orderList.add(entity);
                    }
                }
                //disposable!=1表示周期
                else {
                    //leisure=1表示要生成空置房间的账单
                    if (feeRuleEntity.getLeisure()==1){
                        //查询缴费项目关联的所有房间（不管房间是否已经有业主认证，只要关联的都生成订单）
                        List<HouseEntity> list=houseMapper.selectInIds(split);
                        for (HouseEntity houseEntity : list) {
                            entity = new PropertyFinanceOrderEntity();
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setUid("");
                            entity.setHouseId(houseEntity.getHouseId());
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                            entity.setPenalSum(new BigDecimal("0"));
                            entity.setTotalMoney(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                            entity.setOrderStatus(0);
                            entity.setId(SnowFlake.nextId());
                            entity.setCreateTime(LocalDateTime.now());
                            orderList.add(entity);
                        }
                    }
                    //leisure！=1表示不生成空置房间的账单
                    else {
                        //查询缴费项目关联的所有房间中未空置的房间（房屋已有业主认证表示未空置）
                        List<HouseEntity> list=houseMapper.selectUserHouseAuth(split);
                        for (HouseEntity houseEntity : list) {
                            entity = new PropertyFinanceOrderEntity();
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setUid("");
                            entity.setHouseId(houseEntity.getHouseId());
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                            entity.setPenalSum(new BigDecimal("0"));
                            entity.setTotalMoney(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())));
                            entity.setOrderStatus(0);
                            entity.setId(SnowFlake.nextId());
                            entity.setCreateTime(LocalDateTime.now());
                            orderList.add(entity);
                        }
                    }
                }
            }
            //如果chargeMode！=1表示按户计算
            else {
                //disposable=1表示临时
                if (feeRuleEntity.getDisposable()==1){
                    //查询所有未空置的房间生成账单(临时是根据缴费项目固定金额生成账单)
                    List<HouseEntity> list=houseMapper.selectUserHouseAuth(split);
                    for (HouseEntity houseEntity : list) {
                        entity = new PropertyFinanceOrderEntity();
                        entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId()),feeRuleEntity.getSerialNumber()));
                        entity.setCommunityId(feeRuleEntity.getCommunityId());
                        entity.setOrderTime(LocalDate.now());
                        entity.setUid("");
                        entity.setHouseId(houseEntity.getHouseId());
                        entity.setPropertyFee(feeRuleEntity.getMonetaryUnit());
                        entity.setPenalSum(new BigDecimal("0"));
                        entity.setTotalMoney(feeRuleEntity.getMonetaryUnit());
                        entity.setOrderStatus(0);
                        entity.setId(SnowFlake.nextId());
                        entity.setCreateTime(LocalDateTime.now());
                        orderList.add(entity);
                    }

                } else {
                    //车位费暂时空置

                }
            }
        }

        if (orderList!=null&&orderList.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(orderList);
        }
    }

    

    /**
     * @Description: 更新所有小区账单
     * @author: Hu
     * @since: 2021/5/21 11:03
     * @Param: []
     * @return: void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePenalSum(){
        QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT community_id");
//        wrapper.eq("check_status",1);
        //查出所有认证过的小区ID
        List<UserHouseEntity> entityList = userHouseMapper.selectList(wrapper);
        for (UserHouseEntity houseEntity : entityList) {
            //查询当前小区的收费规则
            PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("community_id",houseEntity.getCommunityId()));
            if (entity != null) {
                //查询当前小区所有未结算的账单
                List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("community_id", houseEntity.getCommunityId()).eq("order_status",0));
                for (PropertyFinanceOrderEntity orderEntity : entities) {
                    //根据小区缴费规则生成违约金
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
     * @since: 2021/5/21 11:03
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
        long millis = System.currentTimeMillis();
        String time = String.valueOf(millis).substring(String.valueOf(millis).length() - 10, String.valueOf(millis).length());
        str.append(time);
        int s1=(int) (Math.random() * 99);
        str.append(s1);
        return str.toString();
    }


}
