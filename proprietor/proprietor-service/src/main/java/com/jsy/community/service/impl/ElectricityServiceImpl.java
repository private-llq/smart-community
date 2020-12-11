package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IElectricityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.proprietor.ElectricityQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 电费service实现类
 * @author: Hu
 * @create: 2020-12-11 09:31
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class ElectricityServiceImpl implements IElectricityService {
    @Autowired
    private ElectricityMapper electricityMapper;
    //户组
    @Autowired
    private PayGroupMapper payGroupMapper;
    @Autowired
    private UserMapper userMapper;
    //订单
    @Autowired
    private PayOrderMapper payOrderMapper;
    //缴费户号
    @Autowired
    private PayHouseOwnerMapper payHouseOwnerMapper;
    //户主详情
    @Autowired
    private PayUserDetailsMapper payUserDetailsMapper;

    //交电费
    @Transactional
    public void add(ElectricityQO electricityQO){
        //设置组号
        PayGroupEntity payGroupEntity = payGroupMapper.selectById(electricityQO.getGroup());
        payGroupEntity.setUid(electricityQO.getUserID());
        payGroupMapper.updateById(payGroupEntity);
        //添加订单
        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderNum(electricityQO.getOrderNum());
        payOrderEntity.setPayType(1);
        payOrderEntity.setFamilyId(electricityQO.getDoorNo());
        payOrderEntity.setStatus(1);
        payOrderEntity.setOrderTime(LocalDateTime.now());
        payOrderEntity.setUnit(electricityQO.getPayCostUnit());
        payOrderEntity.setPaymentAmount(electricityQO.getPayNum());
        payOrderMapper.insert(payOrderEntity);
        //添加缴费户号
        PayHouseOwnerEntity payHouseOwnerEntity = new PayHouseOwnerEntity();
        payHouseOwnerEntity.setGroupId(electricityQO.getGroup());
        payHouseOwnerEntity.setPayBalance(new BigDecimal(10000));
        payHouseOwnerEntity.setPayCompany(electricityQO.getPayCostUnit());
        payHouseOwnerEntity.setPayExpen(electricityQO.getPayNum());
        payHouseOwnerEntity.setPayNumber(electricityQO.getDoorNo());
        payHouseOwnerEntity.setPayTime(LocalDateTime.now());
        payHouseOwnerEntity.setType(1);
        payHouseOwnerMapper.insert(payHouseOwnerEntity);
        //添加户主详情
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid",electricityQO.getUserID()));
        PayUserDetailsEntity payUserDetailsEntity=new PayUserDetailsEntity();
        payUserDetailsEntity.setAddress(null);
        payUserDetailsEntity.setAge(null);
        payUserDetailsEntity.setIdCard(userEntity.getIdCard());
        payUserDetailsEntity.setName(userEntity.getRealName());
        payUserDetailsEntity.setSex(userEntity.getSex());
        payUserDetailsMapper.insert(payUserDetailsEntity);


    }


}
