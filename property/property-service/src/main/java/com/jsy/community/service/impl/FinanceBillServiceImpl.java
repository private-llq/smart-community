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
    @Transactional
    public void updateDays(){
        QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT community_id");
        wrapper.eq("check_status",1);
        List<UserHouseEntity> entityList = userHouseMapper.selectList(wrapper);
        for (UserHouseEntity userHouseEntity : entityList) {
            PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("community_id",userHouseEntity.getCommunityId()));
            if (entity != null) {
                List<HouseEntity> entities = houseMapper.selectHouseAll(userHouseEntity.getCommunityId());
                for (HouseEntity houseEntity : entities) {
                    if (entity.getPeriod() == 1) {
                        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
                        orderEntity.setOrderNum(getOrderNum(userHouseEntity.getCommunityId()+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(userHouseEntity.getCommunityId());
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
                        orderEntity.setOrderNum(getOrderNum(userHouseEntity.getCommunityId()+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(userHouseEntity.getCommunityId());
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





    /**
     * @Description: 更新所有小区账单
     * @author: Hu
     * @since: 2021/5/21 11:03
     * @Param: []
     * @return: void
     */
    @Override
    @Transactional
    public void updatePenalSum(){
        QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT no,community_id");
        wrapper.eq("check_status",1);
        List<UserHouseEntity> entityList = userHouseMapper.selectList(wrapper);
        for (UserHouseEntity houseEntity : entityList) {
            PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("community_id",houseEntity.getCommunityId()));
            if (entity != null) {
                List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("community_id", houseEntity.getCommunityId()).eq("order_status",0));
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
