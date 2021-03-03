package com.jsy.community.service.impl;

import com.jsy.community.api.ILivingpaymentQueryService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.entity.PayGroupEntity;
import com.jsy.community.entity.PayOrderEntity;
import com.jsy.community.entity.PayTypeEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.livingpayment.PaymentRecordsQO;
import com.jsy.community.vo.livingpayment.*;
import com.jsy.community.vo.shop.PaymentRecordsMapVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-02-26 14:01
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class LivingpaymentQueryServiceImpl implements ILivingpaymentQueryService {
    @Autowired
    private LivingpaymentQueryMapper livingpaymentQueryMapper;
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

    @Override
    public Map getPayDetails(String number, Long id) {
        PayCompanyEntity entity = payCompanyMapper.selectById(id);
        Map map = new HashMap();
        map.put("familyName","纵横世纪");
        map.put("familyId",number);
        map.put("companyId",entity.getId());
        map.put("companyName",entity.getName());
        map.put("typeId",entity.getTypeId());
        map.put("accountBalance",-0.01);
        map.put("address","天王星b座1810");
        return map;
    }

    /**
     * @Description: 查询组下面已经缴过费的户号
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    @Override
    public List<GroupVO> selectGroup(String groupName, String userId) {
        return livingpaymentQueryMapper.selectGroup(groupName,userId);
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
        List<PaymentRecordsVO> recordList = livingpaymentQueryMapper.selectOrder(paymentRecordsQO);
        Map<String, List<PaymentRecordsVO>> returnMap = new LinkedHashMap<>();
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
        List<DefaultHouseOwnerVO> list=livingpaymentQueryMapper.selectList(userId,page,size);
        return list;
    }


    /**
     * @Description: 查询全部户号
     * @author: Hu
     * @since: 2021/2/27 16:55
     * @Param:
     * @return:
     */
    @Override
    public List<FamilyIdVO> selectFamilyId(String uid) {
        return livingpaymentQueryMapper.selectFamilyId(uid);
    }

    @Override
    public TheBillingDetailsVO selectOrderId(Long id,String uid) {
        return livingpaymentQueryMapper.selectOrderId(id,uid);
    }

    /**
     * @Description: 查询一条缴费详情
     * @author: Hu
     * @since: 2021/1/15 15:39
     * @Param:
     * @return:
     */
    @Override
    public PaymentDetailsVO selectPaymentDetailsVO(Long id, String userId) {
        return livingpaymentQueryMapper.selectPaymentDetailsVO(id,userId);
    }

    @Override
    public PaymentRecordsMapVO selectGroupAll(String userId) {
        List<PayGroupEntity> list1=livingpaymentQueryMapper.findGroup(userId);
        List<GroupVO> list = livingpaymentQueryMapper.selectGroupAll(userId);
        Map<String,List<GroupVO>> returnMap = new LinkedHashMap<>();
        returnMap.put("我家",new ArrayList<GroupVO>());
        returnMap.put("父母",new ArrayList<GroupVO>());
        returnMap.put("房东",new ArrayList<GroupVO>());
        returnMap.put("朋友",new ArrayList<GroupVO>());
        if (list1!=null){
            for (PayGroupEntity entity : list1) {
                if (entity.getType()==5){
                    returnMap.put(entity.getName(),new ArrayList<GroupVO>());
                }
            }
        }
        if (list!=null){
            for (GroupVO vo : list) {
                if(returnMap.get(vo.getGroupName()) == null){
                    returnMap.put(vo.getGroupName(),new ArrayList<GroupVO>());
                }
                returnMap.get(vo.getGroupName()).add(vo);

            }
        }
        PaymentRecordsMapVO mapVO = new PaymentRecordsMapVO();
        mapVO.setMap(returnMap);
        return mapVO;
    }



    /**
     * @Description: 缴费凭证
     * @author: Hu
     * @since: 2020/12/28 15:53
     * @Param:
     * @return:
     */
    @Override
    public PayVoucherVO getOrderID(Long id,String uid) {
        PayOrderEntity entity = payOrderMapper.selectById(id);
        if (entity==null){
            throw new ProprietorException("该订单不存在！");
        }
        PayTypeEntity typeEntity = payTypeMapper.selectById(entity.getTypeId());
        PayVoucherVO payVoucherVO = new PayVoucherVO();
        payVoucherVO.setOrderId(entity.getId());
        payVoucherVO.setCompanyName(entity.getCompanyName());
        payVoucherVO.setOrderNum(entity.getOrderNum());
        payVoucherVO.setPayTypeName(entity.getPayTypeName());
        payVoucherVO.setTypeName(typeEntity.getName());
        payVoucherVO.setStatus(entity.getStatus());
        payVoucherVO.setFamilyId(entity.getFamilyId());
        payVoucherVO.setPaymentBalance(entity.getPaymentBalance());
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

}
