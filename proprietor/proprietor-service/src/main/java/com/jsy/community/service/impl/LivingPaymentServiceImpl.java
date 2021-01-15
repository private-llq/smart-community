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
import com.jsy.community.vo.*;
import com.jsy.community.vo.shop.PaymentRecordsMapVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

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
    public PaymentDetailsVO add(LivingPaymentQO livingPaymentQO){
        //如果组名为空   默认我家
        Long group_id=0l;
        if("".equals(livingPaymentQO.getGroupName())&&livingPaymentQO.getGroupName()!=null){
            PayGroupEntity payGroupEntity = payGroupMapper.selectOne(new QueryWrapper<PayGroupEntity>().eq("uid",livingPaymentQO.getUserID()).eq("name","我家"));
            if (!StringUtils.isEmpty(payGroupEntity)){
                group_id=payGroupEntity.getId();
            }else {
                PayGroupEntity groupEntity = new PayGroupEntity();
                groupEntity.setId(SnowFlake.nextId());
                groupEntity.setUid(livingPaymentQO.getUserID());
                groupEntity.setName("我家");
                groupEntity.setType(1);
                payGroupMapper.insert(groupEntity);
                group_id=groupEntity.getId();
            }
        }else {
            //如果不为空就获取id为空就新增一条
            //设置组号
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
        }
        PayCompanyEntity entity = payCompanyMapper.selectOne(new QueryWrapper<PayCompanyEntity>()
                .eq("id", livingPaymentQO.getPayCostUnitId())
        );

        //添加订单
        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setId(SnowFlake.nextId());

        //生成订单号
        payOrderEntity.setOrderNum(order());


        payOrderEntity.setPayType(livingPaymentQO.getPayTpye());
        payOrderEntity.setFamilyId(livingPaymentQO.getDoorNo());
        payOrderEntity.setStatus(1);
        payOrderEntity.setOrderTime(LocalDateTime.now());
        payOrderEntity.setUnit(entity.getName());
        payOrderEntity.setPaymentAmount(livingPaymentQO.getPayNum());
        payOrderEntity.setPayYear(LocalDateTime.now().getYear());
        payOrderEntity.setPayMonth(LocalDateTime.now().getMonthValue());
        payOrderEntity.setGroupId(group_id);
        payOrderEntity.setPaymentType(livingPaymentQO.getType());
        payOrderEntity.setAddress(livingPaymentQO.getAddress());
        payOrderEntity.setUnitId(livingPaymentQO.getPayCostUnitId());
        payOrderEntity.setPayBalance(livingPaymentQO.getPayBalance());
        payOrderMapper.insert(payOrderEntity);
        PayHouseOwnerEntity ownerEntity = payHouseOwnerMapper.selectOne(new QueryWrapper<PayHouseOwnerEntity>()
                .eq("uid", livingPaymentQO.getUserID())
                .eq("family_id", livingPaymentQO.getDoorNo())
                .eq("pay_company", livingPaymentQO.getPayCostUnitId())
        );
        if (StringUtils.isEmpty(ownerEntity)){
            //添加缴费户号
            PayHouseOwnerEntity payHouseOwnerEntity = new PayHouseOwnerEntity();
            payHouseOwnerEntity.setId(SnowFlake.nextId());
            payHouseOwnerEntity.setGroupId(group_id);
            payHouseOwnerEntity.setPayCompany(livingPaymentQO.getPayCostUnitId());
            payHouseOwnerEntity.setFamilyId(livingPaymentQO.getDoorNo());
            payHouseOwnerEntity.setType(livingPaymentQO.getType());
            payHouseOwnerEntity.setUid(livingPaymentQO.getUserID());
            payHouseOwnerMapper.insert(payHouseOwnerEntity);
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

        //返回缴费详情
        PaymentDetailsVO paymentDetailsVO = new PaymentDetailsVO();
        paymentDetailsVO.setId(payOrderEntity.getId());
        paymentDetailsVO.setUnitName(entity.getName());
        paymentDetailsVO.setPayBalance(livingPaymentQO.getPayBalance());
        paymentDetailsVO.setDoorNo(livingPaymentQO.getDoorNo());
        paymentDetailsVO.setOrderTime(LocalDateTime.now());
        paymentDetailsVO.setFamilyName(livingPaymentQO.getFamilyName());
        paymentDetailsVO.setAccountingTime(LocalDateTime.now());
        paymentDetailsVO.setPaySum(livingPaymentQO.getPayNum());
        paymentDetailsVO.setAddress(livingPaymentQO.getAddress());

        //假数据默认返回已到账
        paymentDetailsVO.setStatus(2);
        return paymentDetailsVO;
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
    public PaymentRecordsMapVO selectOrder(PaymentRecordsQO paymentRecordsQO) {
        List<PaymentRecordsVO> recordList = livingPaymentMapper.selectOrder(paymentRecordsQO);
        Map<String, List<PaymentRecordsVO>> returnMap = new HashMap<>();
        for(PaymentRecordsVO paymentRecordsVO : recordList){
//            if(!returnMap.keySet().contains(paymentRecordsVO.getTimeGroup())){
            if(returnMap.get(paymentRecordsVO.getTimeGroup()) == null){
                returnMap.put(paymentRecordsVO.getTimeGroup(),new ArrayList<>());
            }
            returnMap.get(paymentRecordsVO.getTimeGroup()).add(paymentRecordsVO);
        }
        PaymentRecordsMapVO mapVO = new PaymentRecordsMapVO();
        mapVO.setMap(returnMap);
        return mapVO;
    }
    /**
     * @Description: 默认查询所有缴费信息
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    @Override
    public List<DefaultHouseOwnerVO> selectList(String userId) {
        int page=0;
        int size=3;
        List<DefaultHouseOwnerVO> list=livingPaymentMapper.selectList(userId,page,size);
        return list;
    }

    @Override
    public List<GroupVO> selectGroupAll(String userId) {
        return livingPaymentMapper.selectGroupAll(userId);
    }

    /**
     * @Description: 缴费凭证
     * @author: Hu
     * @since: 2020/12/28 15:53
     * @Param: 
     * @return: 
     */
    @Override
    public PayVoucherVO getOrderID(Long id) {
        PayOrderEntity entity = payOrderMapper.selectById(id);
        PayTypeEntity typeEntity = payTypeMapper.selectById(entity.getPaymentType());
        PayVoucherVO payVoucherVO = new PayVoucherVO();
        payVoucherVO.setId(entity.getId());
        payVoucherVO.setUnitName(entity.getUnit());
        payVoucherVO.setOrderNum(entity.getOrderNum());
        payVoucherVO.setPayNum("");
        payVoucherVO.setPayType(typeEntity.getName());
        payVoucherVO.setStatus(entity.getStatus());
        payVoucherVO.setDoorNo(entity.getFamilyId());
        payVoucherVO.setPaySum(entity.getPaymentAmount());
        payVoucherVO.setAddress(entity.getAddress());
        payVoucherVO.setOrderTime(entity.getOrderTime());
        return payVoucherVO;
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
        payOrderEntity.setTally(remarkQO.getTally());
        payOrderEntity.setRemark(remarkQO.getRemark());
        payOrderEntity.setRemarkImg(remarkQO.getRemarkImg());
        payOrderMapper.updateById(payOrderEntity);
    }
}
