package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.ILivingPaymentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.LivingPaymentQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.qo.proprietor.RemarkQO;
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
        PayGroupEntity payGroupEntity = payGroupMapper.selectOne(new QueryWrapper<PayGroupEntity>().eq("uid",livingPaymentQO.getUserID()).eq("type",livingPaymentQO.getTypeGroup()).eq("name",livingPaymentQO.getGroupName()));

        Long group_id=0l;
        if (!StringUtils.isEmpty(payGroupEntity)){
            group_id=payGroupEntity.getId();
        }else {
            PayGroupEntity groupEntity = new PayGroupEntity();
            groupEntity.setId(SnowFlake.nextId());
            groupEntity.setUid(livingPaymentQO.getUserID());
            groupEntity.setName(livingPaymentQO.getGroupName());
            groupEntity.setType(livingPaymentQO.getTypeGroup());
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
    /**
     * @Description: 查询每月订单记录
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    @Override
    public Map<String, Object> selectOrder(PaymentRecordsQO paymentRecordsQO) {
        String userID = paymentRecordsQO.getUserID();
//        userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid",userID));
        ArrayList<Object> January = new ArrayList<>();
        ArrayList<Object> February = new ArrayList<>();
        ArrayList<Object> March = new ArrayList<>();
        ArrayList<Object> April = new ArrayList<>();
        ArrayList<Object> May = new ArrayList<>();
        ArrayList<Object> June = new ArrayList<>();
        ArrayList<Object> July = new ArrayList<>();
        ArrayList<Object> August = new ArrayList<>();
        ArrayList<Object> September = new ArrayList<>();
        ArrayList<Object> October = new ArrayList<>();
        ArrayList<Object> November = new ArrayList<>();
        ArrayList<Object> December = new ArrayList<>();
        Map<String, Object> Map = new HashMap<>();
        List<PaymentRecordsVO> list = livingPaymentMapper.selectOrder(paymentRecordsQO);
        for (PaymentRecordsVO paymentRecordsVO : list) {
            switch (paymentRecordsVO.getPayMonth()){
                case 1 : January.add(paymentRecordsVO); break;
                case 2 : February.add(paymentRecordsVO); break;
                case 3 : March.add(paymentRecordsVO); break;
                case 4 : April.add(paymentRecordsVO); break;
                case 5 : May.add(paymentRecordsVO); break;
                case 6 : June.add(paymentRecordsVO); break;
                case 7 : July.add(paymentRecordsVO); break;
                case 8 : August.add(paymentRecordsVO); break;
                case 9 : September.add(paymentRecordsVO); break;
                case 10 : October.add(paymentRecordsVO); break;
                case 11 : November.add(paymentRecordsVO); break;
                case 12 : December.add(paymentRecordsVO); break;
                default:break;
            }
        }
        Map.put("January",January);
        Map.put("February",February);
        Map.put("March",March);
        Map.put("April",April);
        Map.put("May",May);
        Map.put("June",June);
        Map.put("July",July);
        Map.put("August",August);
        Map.put("September",September);
        Map.put("October",October);
        Map.put("November",November);
        Map.put("December",December);
        return Map;
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
