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
        List<PropertyFinanceOrderEntity> list=new LinkedList();
        PropertyFinanceOrderEntity orderEntity=null;
        QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT community_id");
//        wrapper.eq("check_status",1);
        //查出所有认证过的小区ID
        List<UserHouseEntity> entityList = userHouseMapper.selectList(wrapper);
        for (UserHouseEntity userHouseEntity : entityList) {
            //查出当前小区的收费规则
            PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("status", 1).eq("community_id",userHouseEntity.getCommunityId()));
            if (entity != null) {
                //查出当前小区所有已认证过的房屋
                List<HouseEntity> entities = houseMapper.selectHouseAll(userHouseEntity.getCommunityId());
                //根据收费规则循环封装当前小区所有房屋账单
                for (HouseEntity houseEntity : entities) {
                    //根据period计费周期封装账单数据   1日，2周，3月，4季，5年  测试阶段只有按天计费的和按月计费的
                    if (entity.getPeriod() == 1) {
                        orderEntity = new PropertyFinanceOrderEntity();
                        orderEntity.setOrderNum(getOrderNum(userHouseEntity.getCommunityId()+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(userHouseEntity.getCommunityId());
                        orderEntity.setUid(houseEntity.getUid());
                        orderEntity.setHouseId(houseEntity.getId());
                        orderEntity.setPropertyFee(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setTotalMoney(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setId(SnowFlake.nextId());
                        list.add(orderEntity);
                    }
                    //如果不是按天计费就获取当前日期如果是一号就新增账单
                    else if (LocalDate.now().getDayOfMonth() == 1) {
                        orderEntity = new PropertyFinanceOrderEntity();
                        orderEntity.setOrderNum(getOrderNum(userHouseEntity.getCommunityId()+"",entity.getSerialNumber()));
                        orderEntity.setOrderTime(LocalDate.now());
                        orderEntity.setCommunityId(userHouseEntity.getCommunityId());
                        orderEntity.setUid(houseEntity.getUid());
                        orderEntity.setHouseId(houseEntity.getId());
                        orderEntity.setPropertyFee(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setTotalMoney(new BigDecimal(houseEntity.getBuildArea()).multiply(entity.getMonetaryUnit()));
                        orderEntity.setId(SnowFlake.nextId());
                        list.add(orderEntity);
                    }
                }
            }
        }
        if (list!=null&&list.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(list);
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
