package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.ILivingPaymentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.LivingPaymentQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.qo.proprietor.RemarkQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.DefaultHouseOwnerVO;
import com.jsy.community.vo.GroupVO;
import com.jsy.community.vo.PaymentRecordsVO;
import com.jsy.community.vo.UserGroupVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * @Description: 交费记录
     * @author: Hu
     * @since: 2020/12/12 10:14
     * @Param:
     * @return:
     */
    @Transactional
    public void add(LivingPaymentQO livingPaymentQO){
        System.out.println(livingPaymentQO);
        //设置组号
        PayGroupEntity payGroupEntity = payGroupMapper.selectOne(new QueryWrapper<PayGroupEntity>().eq("uid",livingPaymentQO.getUserID()).eq("name",livingPaymentQO.getGroupName()));

        Long group_id=0l;
        if (!StringUtils.isEmpty(payGroupEntity)){
            group_id=payGroupEntity.getId();
        }else {
            PayGroupEntity groupEntity = new PayGroupEntity();
            groupEntity.setId(SnowFlake.nextId());
            groupEntity.setUid(livingPaymentQO.getUserID());
            groupEntity.setName(livingPaymentQO.getGroupName());
            groupEntity.setType(livingPaymentQO.getGroupName()=="我家"?1:livingPaymentQO.getGroupName()=="父母"?2:livingPaymentQO.getGroupName()=="房东"?3:livingPaymentQO.getGroupName()=="朋友"?4:5);
            payGroupMapper.insert(groupEntity);
            group_id=groupEntity.getId();
        }
        //添加订单
        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setId(SnowFlake.nextId());
        payOrderEntity.setOrderNum(livingPaymentQO.getOrderNum());
        payOrderEntity.setPayType(livingPaymentQO.getPayTpye());
        payOrderEntity.setFamilyId(livingPaymentQO.getDoorNo());
        payOrderEntity.setStatus(1);
        payOrderEntity.setOrderTime(LocalDateTime.now());
        payOrderEntity.setUnit(livingPaymentQO.getPayCostUnit());
        payOrderEntity.setPaymentAmount(livingPaymentQO.getPayNum());
        payOrderEntity.setPayYear(LocalDateTime.now().getYear());
        payOrderEntity.setPayMonth(LocalDateTime.now().getMonthValue());
        payOrderEntity.setGroupId(group_id);
        payOrderEntity.setPaymentType(livingPaymentQO.getType());
        payOrderMapper.insert(payOrderEntity);
        //添加缴费户号
        PayHouseOwnerEntity payHouseOwnerEntity = new PayHouseOwnerEntity();
        payHouseOwnerEntity.setId(SnowFlake.nextId());
        payHouseOwnerEntity.setGroupId(group_id);
        payHouseOwnerEntity.setPayBalance(livingPaymentQO.getPayBalance());
        payHouseOwnerEntity.setPayCompany(livingPaymentQO.getPayCostUnit());
        payHouseOwnerEntity.setPayExpen(livingPaymentQO.getPayNum());
        payHouseOwnerEntity.setPayNumber(livingPaymentQO.getDoorNo());
        payHouseOwnerEntity.setPayTime(LocalDateTime.now());
        payHouseOwnerEntity.setType(1);
        payHouseOwnerMapper.insert(payHouseOwnerEntity);
        //添加户主详情
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid",livingPaymentQO.getUserID()));
        userEntity.setId(SnowFlake.nextId());
        PayUserDetailsEntity payUserDetailsEntity=new PayUserDetailsEntity();
        payUserDetailsEntity.setAddress(null);
        payUserDetailsEntity.setAge(null);
        payUserDetailsEntity.setIdCard(userEntity.getIdCard());
        payUserDetailsEntity.setName(userEntity.getRealName());
        payUserDetailsEntity.setSex(userEntity.getSex());
        payUserDetailsMapper.insert(payUserDetailsEntity);

    }


    /**
     * @Description: 查询组下面已经缴过费的户号
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    @Override
    public List<GroupVO> selectGroup(GroupQO groupQO) {
        return livingPaymentMapper.selectGroup(groupQO);
    }
    /**
     * @Description: 查询每月订单记录
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    @Override
    public Map<String, List<PaymentRecordsVO>> selectOrder(BaseQO<PaymentRecordsQO> baseQO) {
        Page<PaymentRecordsVO> page = new Page<>();
        MyPageUtils.setPageAndSize(page,baseQO);
        List<PaymentRecordsVO> recordList = livingPaymentMapper.selectOrder(page,baseQO.getQuery()).getRecords();
        Map<String, List<PaymentRecordsVO>> returnMap = new HashMap<>();
        for(PaymentRecordsVO paymentRecordsVO : recordList){
//            if(!returnMap.keySet().contains(paymentRecordsVO.getTimeGroup())){
            if(returnMap.get(paymentRecordsVO.getTimeGroup()) == null){
                returnMap.put(paymentRecordsVO.getTimeGroup(),new ArrayList<>());
            }
            returnMap.get(paymentRecordsVO.getTimeGroup()).add(paymentRecordsVO);
        }
        return returnMap;
    }
    /**
     * @Description: 默认查询所有缴费信息
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    @Override
    public List selectList(String userId) {
        List<DefaultHouseOwnerVO> list=livingPaymentMapper.selectList(userId);
        return list;
    }

    /**
     * 查询当前登录人员自定义的分组
     * @param
     * @return
     */
    @Override
    public List<UserGroupVO> selectUserGroup(String userId) {
        return payGroupMapper.selectUserGroup(userId);
    }

    /**
     * @Description: 添加订单备注
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public void addRemark(RemarkQO remarkQO) {
        PayOrderEntity payOrderEntity = payOrderMapper.selectById(remarkQO.getId());
        payOrderEntity.setBillClassification(remarkQO.getBillClassification());
        payOrderEntity.setLabel(remarkQO.getLabel());
        payOrderEntity.setRemark(remarkQO.getRemark());
        payOrderEntity.setRemarkImg(remarkQO.getRemarkImg());
        payOrderMapper.updateById(payOrderEntity);
    }


}
