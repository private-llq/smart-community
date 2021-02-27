package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.ILivingPaymentOperationService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.livingpayment.LivingPaymentQO;
import com.jsy.community.qo.livingpayment.RemarkQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @program: com.jsy.community
 * @description: 生活缴费service实现类
 * @author: Hu
 * @create: 2020-12-11 09:31
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class LivingPaymentOperationServiceImpl implements ILivingPaymentOperationService {
    @Autowired
    private LivingPaymentOperationMapper livingPaymentOperationMapper;
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
    private PayFamilyMapper payFamilyMapper;
    //户主详情
    @Autowired
    private PayUserDetailsMapper payUserDetailsMapper;
    //缴费类型
    @Autowired
    private PayTypeMapper payTypeMapper;
    //缴费类型
    @Autowired
    private PayCompanyMapper payCompanyMapper;

    /**
     * @Description: 交费记录
     * @author: Hu
     * @since: 2020/12/12 10:14
     * @Param:
     * @return:
     */
    @Transactional
    public void add(LivingPaymentQO livingPaymentQO){
        PayCompanyEntity payCompanyEntity = payCompanyMapper.selectOne(new QueryWrapper<PayCompanyEntity>().eq("id", livingPaymentQO.getCompanyId()).eq("type_id", livingPaymentQO.getTypeId()));
        if (StringUtils.isEmpty(payCompanyEntity)){
            throw new ProprietorException(JSYError.REQUEST_PARAM);
        }
        //如果组名为空   默认我家
        Long group_id=0l;
        if(livingPaymentQO.getGroupName()!=null&&!"".equals(livingPaymentQO.getGroupName())) {
            //先查没有就新增
            PayGroupEntity payGroupEntity = payGroupMapper.selectOne(new QueryWrapper<PayGroupEntity>().eq("uid",livingPaymentQO.getUserID()).eq("name",livingPaymentQO.getGroupName()));
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
        }else {
            //先查没有就新增
            PayGroupEntity payGroupEntity = payGroupMapper.selectOne(new QueryWrapper<PayGroupEntity>().eq("uid", livingPaymentQO.getUserID()).eq("name", "我家"));
            if (!StringUtils.isEmpty(payGroupEntity)) {
                group_id = payGroupEntity.getId();
            } else {
                PayGroupEntity groupEntity = new PayGroupEntity();
                groupEntity.setId(SnowFlake.nextId());
                groupEntity.setUid(livingPaymentQO.getUserID());
                groupEntity.setName("我家");
                groupEntity.setType(1);
                payGroupMapper.insert(groupEntity);
                group_id = groupEntity.getId();
            }
        }
        PayCompanyEntity entity = payCompanyMapper.selectOne(new QueryWrapper<PayCompanyEntity>()
                .eq("id", livingPaymentQO.getCompanyId())
        );

        //添加订单
        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setId(SnowFlake.nextId());

        //生成订单号
        payOrderEntity.setOrderNum(order());


        payOrderEntity.setPayType(livingPaymentQO.getPayTpye());
        payOrderEntity.setFamilyId(livingPaymentQO.getFamilyId());
        payOrderEntity.setStatus(3);
        payOrderEntity.setOrderTime(LocalDateTime.now());
        payOrderEntity.setCompanyName(entity.getName());
        payOrderEntity.setPaymentBalance(livingPaymentQO.getPaymentBalance());
        payOrderEntity.setPayYear(LocalDateTime.now().getYear());
        payOrderEntity.setPayMonth(LocalDateTime.now().getMonthValue());
        payOrderEntity.setGroupId(group_id);
        payOrderEntity.setPayTypeName(livingPaymentQO.getPayTypeName());
        payOrderEntity.setFamilyName(livingPaymentQO.getFamilyName());
        payOrderEntity.setTypeId(livingPaymentQO.getTypeId());
        payOrderEntity.setAddress(livingPaymentQO.getAddress());
        payOrderEntity.setCompanyId(livingPaymentQO.getCompanyId());
        payOrderEntity.setAccountBalance(livingPaymentQO.getAccountBalance());
        payOrderEntity.setBillClassification(1);
        payOrderEntity.setBillClassificationName("充值缴费");


        //到账时间
        payOrderEntity.setArriveTime(LocalDateTime.now());

        payOrderMapper.insert(payOrderEntity);
        PayFamilyEntity familyEntity = payFamilyMapper.selectOne(new QueryWrapper<PayFamilyEntity>()
                .eq("uid", livingPaymentQO.getUserID())
                .eq("family_id", livingPaymentQO.getFamilyId())
                .eq("company_id", livingPaymentQO.getCompanyId())
        );
        if (StringUtils.isEmpty(familyEntity)){
            //添加缴费户号
            PayFamilyEntity payFamilyEntity = new PayFamilyEntity();
            payFamilyEntity.setId(SnowFlake.nextId());
            payFamilyEntity.setGroupId(group_id);
            payFamilyEntity.setCompanyId(livingPaymentQO.getCompanyId());
            payFamilyEntity.setFamilyId(livingPaymentQO.getFamilyId());
            payFamilyEntity.setFamilyName(livingPaymentQO.getFamilyName());
            payFamilyEntity.setTypeId(livingPaymentQO.getTypeId());
            payFamilyEntity.setUid(livingPaymentQO.getUserID());
            payFamilyMapper.insert(payFamilyEntity);
        }
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
     * @Description: 生成订单流水号
     * @author: Hu
     * @since: 2020/12/28 16:22
     * @Param:
     * @return:
     */
    public String order() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=sdfTime.format(new Date().getTime()).replaceAll("[[\\s-:punct:]]", "");
        int s1=(int) (Math.random() * 999999);
        return s+s1;
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
        remarkQO.setBillClassificationName("充值缴费");
        livingPaymentOperationMapper.addRemark(remarkQO);
    }
}
