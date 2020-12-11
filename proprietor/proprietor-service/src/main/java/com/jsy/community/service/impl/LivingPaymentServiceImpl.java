package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.ILivingPaymentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.LivingPaymentQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.vo.DefaultHouseOwnerVO;
import com.jsy.community.vo.GroupVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 生活缴费service实现类
 * @author: Hu
 * @create: 2020-12-11 09:31
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class LivingPaymentServiceImpl implements ILivingPaymentService {
    @Autowired
    private LivingPaymentMapper livingPaymentMapper;
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

    //交费记录
    @Transactional
    public void add(LivingPaymentQO livingPaymentQO){
        //设置组号
        PayGroupEntity payGroupEntity = payGroupMapper.selectById(livingPaymentQO.getGroup());
        payGroupEntity.setUid(livingPaymentQO.getUserID());
        payGroupMapper.updateById(payGroupEntity);
        //添加订单
        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderNum(livingPaymentQO.getOrderNum());
        payOrderEntity.setPayType(1);
        payOrderEntity.setFamilyId(livingPaymentQO.getDoorNo());
        payOrderEntity.setStatus(1);
        payOrderEntity.setOrderTime(LocalDateTime.now());
        payOrderEntity.setUnit(livingPaymentQO.getPayCostUnit());
        payOrderEntity.setPaymentAmount(livingPaymentQO.getPayNum());
        payOrderMapper.insert(payOrderEntity);
        //添加缴费户号
        PayHouseOwnerEntity payHouseOwnerEntity = new PayHouseOwnerEntity();
        payHouseOwnerEntity.setGroupId(livingPaymentQO.getGroup());
        payHouseOwnerEntity.setPayBalance(livingPaymentQO.getPayBalance());
        payHouseOwnerEntity.setPayCompany(livingPaymentQO.getPayCostUnit());
        payHouseOwnerEntity.setPayExpen(livingPaymentQO.getPayNum());
        payHouseOwnerEntity.setPayNumber(livingPaymentQO.getDoorNo());
        payHouseOwnerEntity.setPayTime(LocalDateTime.now());
        payHouseOwnerEntity.setType(1);
        payHouseOwnerMapper.insert(payHouseOwnerEntity);
        //添加户主详情
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid",livingPaymentQO.getUserID()));
        PayUserDetailsEntity payUserDetailsEntity=new PayUserDetailsEntity();
        payUserDetailsEntity.setAddress(null);
        payUserDetailsEntity.setAge(null);
        payUserDetailsEntity.setIdCard(userEntity.getIdCard());
        payUserDetailsEntity.setName(userEntity.getRealName());
        payUserDetailsEntity.setSex(userEntity.getSex());
        payUserDetailsMapper.insert(payUserDetailsEntity);

    }

    //查询组下面已经缴过费的户号
    @Override
    public List<GroupVO> selectGroup(GroupQO groupQO) {
        List<PayHouseOwnerEntity> payHouseOwnerEntities = payHouseOwnerMapper.selectList(new QueryWrapper<PayHouseOwnerEntity>().eq("group_id", groupQO.getGroup()).eq("pay_company", groupQO.getPayCostUnit()));
        List<GroupVO> list=new ArrayList<>();
        GroupVO groupVO=null;
        for (PayHouseOwnerEntity payHouseOwnerEntity : payHouseOwnerEntities) {
            groupVO=new GroupVO();
            groupVO.setType(payHouseOwnerEntity.getType());
            groupVO.setPayCostUnit(payHouseOwnerEntity.getPayCompany());
            groupVO.setDoorNo(payHouseOwnerEntity.getPayNumber());
            list.add(groupVO);
        }
        return list;
    }
    //查询订单记录
    @Override
    public void selectOrder(PaymentRecordsQO paymentRecordsQO) {

    }

    @Override
    public List selectList(String userId) {
        List<PayGroupEntity> list = payGroupMapper.selectList(new QueryWrapper<PayGroupEntity>().eq("uid", userId));
        long[] id=null;
        int i=0;
        for (PayGroupEntity payGroupEntity : list) {
            id[i]=payGroupEntity.getId();
            ++i;
        }
        List<DefaultHouseOwnerVO> defaultHouseOwnerVOS=new ArrayList<>();
        DefaultHouseOwnerVO defaultHouseOwnerVO=null;
        List<PayHouseOwnerEntity> entities = payHouseOwnerMapper.selectList(new QueryWrapper<PayHouseOwnerEntity>().in("group_id", id));
        for (PayHouseOwnerEntity entity : entities) {
            defaultHouseOwnerVO=new DefaultHouseOwnerVO();
            defaultHouseOwnerVO.setGroupId(entity.getGroupId());

            //defaultHouseOwnerVO.setGroupName(entity.get);
        }

        return null;
    }


}
