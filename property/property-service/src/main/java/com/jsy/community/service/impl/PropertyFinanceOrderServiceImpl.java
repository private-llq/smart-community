package com.jsy.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConstsEnum;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.property.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FinanceOrderOperationQO;
import com.jsy.community.qo.property.FinanceOrderQO;
import com.jsy.community.qo.property.StatementNumQO;
import com.jsy.community.utils.*;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.FinanceOrderAndCarOrHouseInfoVO;
import com.jsy.community.vo.property.PropertyFinanceOrderVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.basecommon.constant.BaseConstant;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
import com.zhsj.sign.api.rpc.IContractRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @program: com.jsy.community
 * @description: ??????????????????
 * @author: Hu
 * @create: 2021-04-20 16:31
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PropertyFinanceOrderServiceImpl extends ServiceImpl<PropertyFinanceOrderMapper, PropertyFinanceOrderEntity> implements IPropertyFinanceOrderService {
    @Autowired
    private PropertyFinanceOrderMapper propertyFinanceOrderMapper;

    @Autowired
    private PropertyCompanyMapper propertyCompanyMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseService houseService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ProprietorUserService userService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserImService userImService;

    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceReceiptService propertyFinanceReceiptService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceStatementService propertyFinanceStatementService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyAdvanceDepositRecordService PropertyAdvanceDepositRecordService;

    @Autowired
    private PropertyDepositMapper propertyDepositMapper;

    @Autowired
    private PropertyAdvanceDepositRecordMapper propertyAdvanceDepositRecordMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private PropertyAdvanceDepositMapper propertyAdvanceDepositMapper;

    @Autowired
    private CarPositionMapper carPositionMapper;

    @Autowired
    private IPropertyFinanceTicketTemplateFieldService ticketTemplateFieldService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService userInfoRpcService;

    @DubboReference(version = com.zhsj.im.chat.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.im.chat.api.constant.RpcConst.Rpc.Group.GROUP_IM_CHAT, check=false)
    private IImChatPublicPushRpcService iImChatPublicPushRpcService;

    @DubboReference(version = BaseConstant.Rpc.VERSION, group = BaseConstant.Rpc.Group.GROUP_CONTRACT)
    private IContractRpcService contractRpcService;

    /**
     * @Description: ??????????????????????????????
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [userInfo, houseId]
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     */
    @Override
    public Map<String, Object> findList(AdminInfoVo userInfo, BaseQO<FinanceOrderQO> baseQO) {
        //????????????????????????
        Map<Long, String> houseMap = new HashMap<>();
        //????????????????????????
        Map<Long, String> carPositionMap = new HashMap<>();

        //????????????????????????
        List<HouseEntity> houseEntities = houseService.selectAll(baseQO.getQuery().getCommunityId());
        //??????????????????????????????
        List<CarPositionEntity> carPositionEntities = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().eq("community_id", baseQO.getQuery().getCommunityId()));
        //????????????map
        for (HouseEntity houseEntity : houseEntities) {
            houseMap.put(houseEntity.getId(), houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
        }
        //????????????map
        for (CarPositionEntity positionEntity : carPositionEntities) {
            carPositionMap.put(positionEntity.getId(), positionEntity.getCarPosition());
        }
        if (baseQO.getPage() == null || baseQO.getPage() == 0) {
            baseQO.setPage(1L);
        }
        //????????????
        List<PropertyFinanceOrderEntity> orderEntities = propertyFinanceOrderMapper.findList((baseQO.getPage() - 1) * baseQO.getSize(), baseQO.getSize(), baseQO.getQuery());
        //?????????????????????????????????????????????
        for (PropertyFinanceOrderEntity entity : orderEntities) {
            //??????associatedType??????1??????????????????   ????????????????????????
            if (entity.getAssociatedType() != null) {
                if (entity.getAssociatedType() == 1) {
                    entity.setAddress(houseMap.get(entity.getTargetId()));
                } else {
                    entity.setAddress(carPositionMap.get(entity.getTargetId()));
                }
            }
            //???????????????  ?????????=(propertyFee+penalSum)-coupon-deduction
            entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum()).subtract(entity.getCoupon()).subtract(entity.getDeduction()));
            if (entity.getType() != null) {
                entity.setFeeRuleName(BusinessEnum.FeeRuleNameEnum.getName(entity.getType()));
            } else {
                entity.setFeeRuleName(entity.getRise().substring(entity.getRise().indexOf("-") + 1));
            }
        }
        //???????????????
        Integer total = propertyFinanceOrderMapper.getTotal(baseQO.getQuery());
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("list", orderEntities);
        returnMap.put("total", total);
        return returnMap;
    }


    /**
     * @Description: ????????????????????????????????????
     * @Param: [receiptNums, query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
     **/
    @Override
    public List<PropertyFinanceOrderEntity> queryByReceiptNums(Collection<String> receiptNums, PropertyFinanceOrderEntity query) {
        return propertyFinanceOrderMapper.queryByReceiptNums(receiptNums, query);
    }


    /**
     * @Description: ???????????????????????????
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [userInfo, orderNum]
     * @return: com.jsy.community.vo.property.PropertyFinanceOrderVO
     */
    @Override
    public PropertyFinanceOrderVO getOrderNum(AdminInfoVo userInfo, Long id) {
        PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderMapper.selectById(id);
        HouseEntity houseEntity = houseService.getOne(new QueryWrapper<HouseEntity>().eq("id", propertyFinanceOrderEntity.getTargetId()));
        PropertyFinanceOrderVO financeOrderVO = new PropertyFinanceOrderVO();
        BeanUtils.copyProperties(propertyFinanceOrderEntity, financeOrderVO);
        BeanUtils.copyProperties(houseEntity, financeOrderVO);
        financeOrderVO.setHouseTypeText(houseEntity.getHouseType() == 1 ? "??????" : "??????");
        financeOrderVO.setId(propertyFinanceOrderEntity.getId());
        return financeOrderVO;
    }

    /**
     * @Description: ???????????????????????????????????????
     * @Param: [orderNum]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/22
     **/
    @Override
    public List<String> queryReceiptNumsListByOrderNumLike(String orderNum) {
        return propertyFinanceOrderMapper.queryReceiptNumsListByOrderNumLike(orderNum);
    }

    /**
     * @Description: ????????????????????? (????????????)
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/24
     **/
    @Override
    public PageInfo<PropertyFinanceOrderEntity> queryPaid(BaseQO<PropertyFinanceOrderEntity> baseQO) {
        PropertyFinanceOrderEntity query = baseQO.getQuery();
        Page<PropertyFinanceOrderEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq("community_id", query.getCommunityId());
        queryWrapper.eq("order_status", PropertyConstsEnum.OrderStatusEnum.ORDER_STATUS_PAID.getCode());
        queryWrapper.orderByDesc("create_time");
        if (query.getTargetId() != null) {
            queryWrapper.eq("house_id", query.getTargetId());
        }
        if (query.getOrderStartDate() != null) {
            queryWrapper.ge("order_time", query.getOrderStartDate());
        }
        if (query.getOrderEndDate() != null) {
            queryWrapper.le("order_time", query.getOrderEndDate());
        }
        if (query.getReceiptStartDate() != null || query.getReceiptEndDate() != null) {
            PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
            receiptEntity.setStartDate(query.getReceiptStartDate());
            receiptEntity.setEndDate(query.getReceiptEndDate());
            List<String> receiptNums = propertyFinanceReceiptService.queryReceiptNumsByCondition(receiptEntity);
            if (CollectionUtils.isEmpty(receiptNums)) {
                return new PageInfo<>();
            }
            queryWrapper.in("receipt_num", receiptNums);
        }
        if (!StringUtils.isEmpty(query.getRealName())) {
            //????????????????????????????????????????????????uid
            Set<String> allUidSet = propertyFinanceOrderMapper.queryUidSetByCommunityId(query.getCommunityId());
            LinkedList<String> allUidSetList = new LinkedList<>(allUidSet);
            //???????????????in????????????999???????????????
            int size = 999;
            if (!CollectionUtils.isEmpty(allUidSet)) {
                //??????????????????
                int times = allUidSet.size() % size == 0 ? allUidSet.size() / size : allUidSet.size() / size + 1;
                //???????????????uid
                List<String> uids = new LinkedList<>();
                int remain = allUidSet.size(); //??????????????????
                for (int i = 0; i < times; i++) {
                    List<String> targetUid = userService.queryUidOfNameLike(allUidSetList.subList(i * size, (i * size) + remain), query.getRealName());
                    if (!CollectionUtils.isEmpty(targetUid)) {
                        uids.addAll(targetUid);
                    }
                    remain = remain > size ? remain : remain - size;
                }
                //??????????????????
                if (CollectionUtils.isEmpty(uids)) {
                    return new PageInfo<>();
                }
                queryWrapper.in("uid", uids);
            }
        }
        //????????????
        Page<PropertyFinanceOrderEntity> pageData = propertyFinanceOrderMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        //??????????????????
        Set<Long> houseIds = new HashSet<>();
        Set<String> uids = new HashSet<>();
        Set<String> receiptNums = new HashSet<>();
        for (PropertyFinanceOrderEntity entity : pageData.getRecords()) {
            houseIds.add(entity.getTargetId());
            uids.add(entity.getUid());
            receiptNums.add(entity.getReceiptNum());
        }
        //????????????????????? (houseService)
        Map<Long, HouseEntity> houseMap = houseService.queryIdAndHouseMap(houseIds);
        //????????????????????? (houseService)
        Map<String, Map<String, String>> realNameMap = userService.queryNameByUidBatch(uids);
        //???????????????????????? (propertyFinanceReceiptService)
        Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
        //????????????
        for (PropertyFinanceOrderEntity entity : pageData.getRecords()) {
            entity.setAddress(houseMap.get(entity.getTargetId()) == null ? null : houseMap.get(entity.getTargetId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setUid(null);
            entity.setTargetId(null);
        }
        PageInfo<PropertyFinanceOrderEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        return pageInfo;
    }

    /**
     * @Description: ???????????? (????????????)
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    @Override
    public PageInfo<PropertyFinanceOrderEntity> queryPage(BaseQO<PropertyFinanceOrderEntity> baseQO) {
        PropertyFinanceOrderEntity query = baseQO.getQuery();
        Page<PropertyFinanceOrderEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq("community_id", query.getCommunityId());
        queryWrapper.orderByDesc("create_time");
        //??????????????????
        if (!StringUtils.isEmpty(query.getOrderNum())) {
            queryWrapper.like("order_num", query.getOrderNum());
        }
        if (!StringUtils.isEmpty(query.getReceiptNum())) {
            queryWrapper.like("receipt_num", query.getReceiptNum());
        }
        if (!StringUtils.isEmpty(query.getStatementNum())) {
            queryWrapper.like("statement_num", query.getStatementNum());
        }
        if (query.getOrderStatus() != null) {
            queryWrapper.eq("order_status", query.getOrderStatus());
        }
        if (query.getStatementStatus() != null) {
            queryWrapper.eq("statement_status", query.getStatementStatus());
        }
        if (query.getTargetId() != null) {
            queryWrapper.eq("house_id", query.getTargetId());
        }
        if (query.getOrderStartDate() != null) {
            queryWrapper.ge("order_time", query.getOrderStartDate());
        }
        if (query.getOrderEndDate() != null) {
            queryWrapper.le("order_time", query.getOrderEndDate());
        }
        //?????????????????????
        if (!StringUtils.isEmpty(query.getRealName())) {
            //????????????????????????????????????????????????uid
            Set<String> allUidSet = propertyFinanceOrderMapper.queryUidSetByCommunityId(query.getCommunityId());
            LinkedList<String> allUidSetList = new LinkedList<>(allUidSet);
            //???????????????in????????????999???????????????
            int size = 999;
            if (!CollectionUtils.isEmpty(allUidSet)) {
                //??????????????????
                int times = allUidSet.size() % size == 0 ? allUidSet.size() / size : allUidSet.size() / size + 1;
                //???????????????uid
                List<String> uids = new LinkedList<>();
                int remain = allUidSet.size(); //??????????????????
                for (int i = 0; i < times; i++) {
                    List<String> targetUid = userService.queryUidOfNameLike(allUidSetList.subList(i * size, (i * size) + remain), query.getRealName());
                    if (!CollectionUtils.isEmpty(targetUid)) {
                        uids.addAll(targetUid);
                    }
                    remain = remain > size ? remain : remain - size;
                }
                //??????????????????
                if (CollectionUtils.isEmpty(uids)) {
                    return new PageInfo<>();
                }
                queryWrapper.in("uid", uids);
            }
        }
        if (query.getReceiptStartDate() != null || query.getReceiptEndDate() != null) {
            PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
            receiptEntity.setStartDate(query.getReceiptStartDate());
            receiptEntity.setEndDate(query.getReceiptEndDate());
            List<String> receiptNums = propertyFinanceReceiptService.queryReceiptNumsByCondition(receiptEntity);
            if (CollectionUtils.isEmpty(receiptNums)) {
                return new PageInfo<>();
            }
            queryWrapper.in("receipt_num", receiptNums);
        }
        if (query.getStatementStartDate() != null || query.getStatementEndDate() != null) {
            PropertyFinanceStatementEntity statementEntity = new PropertyFinanceStatementEntity();
            statementEntity.setCreateStartDate(query.getStatementStartDate());
            statementEntity.setCreateEndDate(query.getStatementEndDate());
            List<String> statementNums = propertyFinanceStatementService.queryStatementNumsByCondition(statementEntity);
            if (CollectionUtils.isEmpty(statementNums)) {
                return new PageInfo<>();
            }
            queryWrapper.in("statement_num", statementNums);
        }
        //????????????
        Page<PropertyFinanceOrderEntity> pageData = propertyFinanceOrderMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isEmpty(pageData.getRecords())) {
            return new PageInfo<>();
        }
        //??????????????????
        Set<Long> houseIds = new HashSet<>();
        Set<String> uids = new HashSet<>();
        Set<String> receiptNums = new HashSet<>();
        Set<String> statementNums = new HashSet<>();
        for (PropertyFinanceOrderEntity entity : pageData.getRecords()) {
            houseIds.add(entity.getTargetId());
            uids.add(entity.getUid());
            receiptNums.add(entity.getReceiptNum());
            statementNums.add(entity.getStatementNum());
        }
        //????????????????????? (houseService)
        Map<Long, HouseEntity> houseMap = houseService.queryIdAndHouseMap(houseIds);
        //????????????????????? (houseService)
        Map<String, Map<String, String>> realNameMap = userService.queryNameByUidBatch(uids);
        //???????????????????????? (propertyFinanceReceiptService)
        Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
        //???????????????????????? (propertyFinanceStatementService)
        Map<String, PropertyFinanceStatementEntity> statementEntityMap = propertyFinanceStatementService.queryByStatementNumBatch(statementNums);
        //????????????
        for (PropertyFinanceOrderEntity entity : pageData.getRecords()) {
            entity.setAddress(houseMap.get(entity.getTargetId()) == null ? null : houseMap.get(entity.getTargetId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setStatementEntity(statementEntityMap.get(entity.getStatementNum()) == null ? null : statementEntityMap.get(entity.getStatementNum()));
            entity.setUid(null);
            entity.setTargetId(null);
        }
        //??????????????????(??????)
        BigDecimal totalOrder = new BigDecimal(0);//????????????
        BigDecimal notReceipt = new BigDecimal(0);//0.?????????
        BigDecimal receipted = new BigDecimal(0);//1.?????????
        //??????????????????(?????????)
        BigDecimal notStatement = new BigDecimal(0);//1.?????????
        BigDecimal statementing = new BigDecimal(0);//2.?????????
        BigDecimal statemented = new BigDecimal(0);//3.?????????
        BigDecimal statementReject = new BigDecimal(0);//4.??????
        //??????????????????
        //??????????????????(?????????)
        queryWrapper.select("sum(total_money) as totalOrder");
        List<Map<String, Object>> totalOrderMoneyListMap = propertyFinanceOrderMapper.selectMaps(queryWrapper);
        if (totalOrderMoneyListMap.get(0) != null) {
            totalOrder = totalOrder.add(new BigDecimal(String.valueOf(totalOrderMoneyListMap.get(0).get("totalOrder"))));
        }
        //??????????????????(???????????????)
        queryWrapper.select("order_status, sum(total_money) as total_money, now() as create_time");
        queryWrapper.groupBy("order_status");
        List<PropertyFinanceOrderEntity> receiptData = propertyFinanceOrderMapper.selectList(queryWrapper);
        for (PropertyFinanceOrderEntity entity : receiptData) {
            switch (entity.getOrderStatus()) {
                case 0:
                    notReceipt = notReceipt.add(entity.getTotalMoney());
                    break;
                case 1:
                    receipted = receipted.add(entity.getTotalMoney());
                    break;
                default:
            }
        }
        //??????????????????
        queryWrapper.select("statement_status, sum(total_money) as total_money, now() as create_time");
        queryWrapper.groupBy("statement_status");
        List<PropertyFinanceOrderEntity> statementData = propertyFinanceOrderMapper.selectList(queryWrapper);
        for (PropertyFinanceOrderEntity entity : statementData) {
            if (entity.getStatementStatus() != null) {
                switch (entity.getStatementStatus()) {
                    case 1:
                        notStatement = notStatement.add(entity.getTotalMoney());
                        break;
                    case 2:
                        statementing = statementing.add(entity.getTotalMoney());
                        break;
                    case 3:
                        statemented = statemented.add(entity.getTotalMoney());
                        break;
                    case 4:
                        statementReject = statementReject.add(entity.getTotalMoney());
                        break;
                    default:
                }
            }
        }
        PageInfo<PropertyFinanceOrderEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageData, pageInfo);
        Map<String, Object> extra = new HashMap<>();
        extra.put("totalOrder", totalOrder);
        extra.put("notReceipt", notReceipt);
        extra.put("receipted", receipted);
        extra.put("notStatement", notStatement);
        extra.put("statementing", statementing);
        extra.put("statemented", statemented);
        extra.put("statementReject", statementReject);
        pageInfo.setExtra(extra);
        return pageInfo;
    }

    /**
     * @Author: Pipi
     * @Description: ???????????????????????????????????????
     * @Param: propertyFinanceOrderEntity:
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Date: 2021/4/25 15:52
     **/
    @Override
    public List<PropertyFinanceOrderEntity> queryExportExcelList(PropertyFinanceOrderEntity query) {
        List<PropertyFinanceOrderEntity> orderEntities = new ArrayList<>();
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq("community_id", query.getCommunityId());
        queryWrapper.orderByDesc("create_time");
        //??????????????????
        if (!StringUtils.isEmpty(query.getOrderNum())) {
            queryWrapper.like("order_num", query.getOrderNum());
        }
        if (!StringUtils.isEmpty(query.getReceiptNum())) {
            queryWrapper.like("receipt_num", query.getReceiptNum());
        }
        if (!StringUtils.isEmpty(query.getStatementNum())) {
            queryWrapper.like("statement_num", query.getStatementNum());
        }
        if (query.getOrderStatus() != null) {
            queryWrapper.eq("order_status", query.getOrderStatus());
        }
        if (query.getStatementStatus() != null) {
            queryWrapper.eq("statement_status", query.getStatementStatus());
        }
        if (query.getTargetId() != null) {
            queryWrapper.eq("house_id", query.getTargetId());
        }
        if (query.getOrderStartDate() != null) {
            queryWrapper.ge("order_time", query.getOrderStartDate());
        }
        if (query.getOrderEndDate() != null) {
            queryWrapper.le("order_time", query.getOrderEndDate());
        }
        //?????????????????????
        if (!StringUtils.isEmpty(query.getRealName())) {
            //????????????????????????????????????????????????uid
            Set<String> allUidSet = propertyFinanceOrderMapper.queryUidSetByCommunityId(query.getCommunityId());
            LinkedList<String> allUidSetList = new LinkedList<>(allUidSet);
            //???????????????in????????????999???????????????
            int size = 999;
            if (!CollectionUtils.isEmpty(allUidSet)) {
                //??????????????????
                int times = allUidSet.size() % size == 0 ? allUidSet.size() / size : allUidSet.size() / size + 1;
                //???????????????uid
                List<String> uids = new LinkedList<>();
                int remain = allUidSet.size(); //??????????????????
                for (int i = 0; i < times; i++) {
                    uids.addAll(userService.queryUidOfNameLike(allUidSetList.subList(i * size, (i * size) + remain), query.getRealName()));
                    remain = remain > size ? remain : remain - size;
                }
                //??????????????????
                if (CollectionUtils.isEmpty(uids)) {
                    uids.add("0");
                }
                queryWrapper.in("uid", uids);
            }
        }
        if (query.getReceiptStartDate() != null || query.getReceiptEndDate() != null) {
            PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
            receiptEntity.setStartDate(query.getReceiptStartDate());
            receiptEntity.setEndDate(query.getReceiptEndDate());
            List<String> receiptNums = propertyFinanceReceiptService.queryReceiptNumsByCondition(receiptEntity);
            if (CollectionUtils.isEmpty(receiptNums)) {
                return orderEntities;
            }
            queryWrapper.in("receipt_num", receiptNums);
        }
        if (query.getStatementStartDate() != null || query.getStatementEndDate() != null) {
            PropertyFinanceStatementEntity statementEntity = new PropertyFinanceStatementEntity();
            statementEntity.setCreateStartDate(query.getStatementStartDate());
            statementEntity.setCreateEndDate(query.getStatementEndDate());
            List<String> statementNums = propertyFinanceStatementService.queryStatementNumsByCondition(statementEntity);
            if (CollectionUtils.isEmpty(statementNums)) {
                return orderEntities;
            }
            queryWrapper.in("statement_num", statementNums);
        }
        orderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(orderEntities)) {
            return orderEntities;
        }
        //??????????????????
        Set<Long> houseIds = new HashSet<>();
        Set<String> uids = new HashSet<>();
        Set<String> receiptNums = new HashSet<>();
        Set<String> statementNums = new HashSet<>();
        for (PropertyFinanceOrderEntity entity : orderEntities) {
            houseIds.add(entity.getTargetId());
            uids.add(entity.getUid());
            receiptNums.add(entity.getReceiptNum());
            statementNums.add(entity.getStatementNum());
        }
        //????????????????????? (houseService)
        Map<Long, HouseEntity> houseMap = houseService.queryIdAndHouseMap(houseIds);
        //????????????????????? (houseService)
        Map<String, Map<String, String>> realNameMap = userService.queryNameByUidBatch(uids);
        //???????????????????????? (propertyFinanceReceiptService)
        Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
        //???????????????????????? (propertyFinanceStatementService)
        Map<String, PropertyFinanceStatementEntity> statementEntityMap = propertyFinanceStatementService.queryByStatementNumBatch(statementNums);
        //????????????
        for (PropertyFinanceOrderEntity entity : orderEntities) {
            entity.setAddress(houseMap.get(entity.getTargetId()) == null ? null : houseMap.get(entity.getTargetId()).getAddress());
            entity.setRealName(realNameMap.get(entity.getUid()) == null ? null : realNameMap.get(entity.getUid()).get("name"));
            entity.setReceiptEntity(receiptEntityMap.get(entity.getReceiptNum()) == null ? null : receiptEntityMap.get(entity.getReceiptNum()));
            entity.setStatementEntity(statementEntityMap.get(entity.getStatementNum()) == null ? null : statementEntityMap.get(entity.getStatementNum()));
        }
        return orderEntities;
    }


    @Override
    public void update(Long id) {
        propertyFinanceOrderMapper.updateStatus(id);
    }

    @Override
    public void updates(FinanceOrderOperationQO financeOrderOperationQO) {
        if (financeOrderOperationQO.getOrderTimeOver() != null) {
            financeOrderOperationQO.setOrderTimeOver(financeOrderOperationQO.getOrderTimeOver().plusDays(1));
        }
        if (financeOrderOperationQO.getOverTime() != null) {
            financeOrderOperationQO.setOverTime(financeOrderOperationQO.getOverTime().plusMonths(1));
        }
        propertyFinanceOrderMapper.updates(financeOrderOperationQO);
    }

    /**
     * @Description: ??????????????????
     * @author: Hu
     * @since: 2021/8/7 14:37
     * @Param: [ids]
     * @return: void
     */
    @Override
    public void deletes(FinanceOrderOperationQO financeOrderOperationQO) {
        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        if (financeOrderOperationQO.getOrderTimeBegin() != null) {
            wrapper.ge("order_time", financeOrderOperationQO.getOrderTimeBegin());
        }
        if (financeOrderOperationQO.getOrderTimeOver() != null) {
            financeOrderOperationQO.setOrderTimeOver(financeOrderOperationQO.getOrderTimeOver().plusDays(1));
            wrapper.le("order_time", financeOrderOperationQO.getOrderTimeOver());
        }
        if (financeOrderOperationQO.getType() != null) {
            wrapper.eq("type", financeOrderOperationQO.getType());
        }
        propertyFinanceOrderMapper.delete(wrapper);
    }

    /**
     * @Description: ????????????????????????
     * @author: Hu
     * @since: 2021/8/7 14:35
     * @Param: [id]
     * @return: void
     */
    @Override
    public void delete(Long id) {
        propertyFinanceOrderMapper.deleteById(id);
    }

    /**
     * @Description: ????????????????????????
     * @author: Hu
     * @since: 2021/8/7 14:24
     * @Param: [id, coupon]
     * @return: void
     */
    @Override
    public void updateOrder(Long id, BigDecimal coupon) {
        PropertyFinanceOrderEntity orderEntity = propertyFinanceOrderMapper.selectById(id);
        if (orderEntity != null) {
            if (orderEntity.getPropertyFee().subtract(coupon).compareTo(BigDecimal.ZERO) >= 0) {
                orderEntity.setCoupon(coupon);
                propertyFinanceOrderMapper.updateById(orderEntity);
            } else {
                throw new PropertyException(JSYError.COUPON_BEYOND.getCode(), "????????????????????????????????????!");
            }
        } else {
            throw new PropertyException(JSYError.DATA_LOST.getCode(), "???????????????!");
        }
    }

    /**
     * @Description: ??????????????????????????????
     * @author: Hu
     * @since: 2021/7/6 11:14
     * @Param: [userId, orderId]
     * @return: java.lang.Object
     */
    @Override
    public PropertyFinanceOrderEntity findOne(Long orderId) {
        return propertyFinanceOrderMapper.selectById(orderId);
    }


    /**
     * @Description: ???????????????-????????????????????????
     * @Param: [payType, tripartiteOrder, ids]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2021/7/7
     **/
    @Override
    public void updateOrderStatusBatch(Integer payType, String tripartiteOrder, String[] ids, BigDecimal total) {
        log.info("???????????????????????????");
        int rows = propertyFinanceOrderMapper.updateOrderBatch(payType, tripartiteOrder, ids);
        if (rows != ids.length) {
            log.info("?????????????????????????????????????????????" + tripartiteOrder + " ??????ID???" + Arrays.toString(ids));
        }
        StringBuilder detailedList = new StringBuilder();
        PropertyFinanceOrderEntity orderEntity = propertyFinanceOrderMapper.selectById(ids[0]);
        UserImVo userIm = userInfoRpcService.getEHomeUserIm(orderEntity.getUid());
        CommunityEntity communityEntity = communityMapper.selectById(orderEntity.getCommunityId());
        PropertyCompanyEntity companyEntity = propertyCompanyMapper.selectById(communityEntity.getPropertyId());

        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderMapper.selectByIdsList(ids);
        for (int i = 0; i < list.size(); i++) {
            detailedList.append(list.get(i).getBeginTime().getMonthValue()+"???");
            detailedList.append(BusinessEnum.FeeRuleNameEnum.getName(list.get(i).getType()));
            if (i!=list.size()-1){
                detailedList.append("\n");
            }
        }
        RealInfoDto idCardRealInfo = userInfoRpcService.getIdCardRealInfo(orderEntity.getUid());
        if (ObjectUtil.isNull(idCardRealInfo)) {
            throw new ProprietorException(JSYError.ACCOUNT_NOT_EXISTS);
        }
        //????????????
        contractRpcService.communityOrderUpLink("?????????",
                1,
                payType,
                total,
                tripartiteOrder,
                idCardRealInfo.getIdCardNumber(),
                companyEntity.getUnifiedSocialCreditCode(),
                detailedList.toString(),
                null);
        /*CochainResponseEntity responseEntity = OrderCochainUtil.orderCochain("?????????",
                1,
                payType,
                total,
                tripartiteOrder,
                orderEntity.getUid(),
                companyEntity.getUnifiedSocialCreditCode(),
                detailedList.toString(),
                null);
        log.info("???????????????"+responseEntity);*/
        Map<Object, Object> map = new HashMap<>();
        map.put("type", 3);
        map.put("dataId", tripartiteOrder);
        map.put("orderNum", tripartiteOrder);
        PushInfoUtil.pushPayAppMsg(
                iImChatPublicPushRpcService,
                userIm.getImId(),
                payType,
                total.toString(),
                null,
                "????????????",
                map,
                BusinessEnum.PushInfromEnum.PROPERTYPAYMENT.getName());
    }

    /**
     * @Description: ???????????????????????????
     * @author: Hu
     * @since: 2021/7/5 16:12
     * @Param: [orderIds]
     * @return: java.math.BigDecimal
     */
    @Override
    public BigDecimal getTotalMoney(String ids) {
        BigDecimal totalMoney = propertyFinanceOrderMapper.getTotalMoney(ids.split(","));
        return totalMoney == null ? new BigDecimal("0.00") : totalMoney;
    }

    /**
     * @Description: ??????????????????????????????
     * @author: Hu
     * @since: 2021/7/5 11:19
     * @Param: [userId]
     * @return: void
     */
    @Override
    public List<PropertyFinanceOrderEntity> selectByUserList(PropertyFinanceOrderEntity qo) {
        QueryWrapper queryWrapper = new QueryWrapper<PropertyFinanceOrderEntity>()
                .eq("uid", qo.getUid())
                .eq("community_id", qo.getCommunityId());
        if (qo.getOrderStatus() != null && (qo.getOrderStatus() == 0 || qo.getOrderStatus() == 1)) {
            queryWrapper.eq("order_status", qo.getOrderStatus());
        }
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderMapper.selectList(queryWrapper);
        for (PropertyFinanceOrderEntity orderEntity : list) {
            if (orderEntity.getAssociatedType() == 2) {
                CarPositionEntity entity = carPositionMapper.selectById(orderEntity.getTargetId());
                if (entity != null) {
                    orderEntity.setTargetId(entity.getHouseId());
                }
            }
        }
        return list;
    }

    /**
     * @Author: Pipi
     * @Description: ????????????????????????????????????
     * @Param: baseQO:
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Date: 2021/4/24 11:44
     **/
    @Override
    public Page<PropertyFinanceOrderEntity> queryPageByStatemenNum(BaseQO<StatementNumQO> baseQO) {
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        Page<PropertyFinanceOrderEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        StatementNumQO query = baseQO.getQuery();
        queryWrapper.select("id, order_num, order_time, '?????????' as orderType, total_money, receipt_num");
        queryWrapper.eq("statement_num", query.getStatementNum());
        queryWrapper.orderByDesc("create_time");
        Page<PropertyFinanceOrderEntity> pageData = propertyFinanceOrderMapper.selectPage(page, queryWrapper);
        if (!CollectionUtils.isEmpty(pageData.getRecords())) {
            Set<String> receiptNums = new HashSet<>();
            pageData.getRecords().forEach(orderEntity -> {
                receiptNums.add(orderEntity.getReceiptNum());
            });
            //???????????????????????? (propertyFinanceReceiptService)
            Map<String, PropertyFinanceReceiptEntity> receiptEntityMap = propertyFinanceReceiptService.queryByReceiptNumBatch(receiptNums);
            pageData.getRecords().forEach(orderEntity -> {
                orderEntity.setReceiptEntity(receiptEntityMap.get(orderEntity.getReceiptNum()) == null ? null : receiptEntityMap.get(orderEntity.getReceiptNum()));
            });
        }
        return pageData;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/8/17 16:00
     **/
    @Override
    public List<PropertyFinanceFormEntity> getFinanceFormCommunityIncome(PropertyFinanceFormEntity qo, List<String> communityIdList) {
        // ?????????????????????
        List<PropertyFinanceFormEntity> propertyFinanceFormEntityList = new LinkedList<>();
        // ????????????
        QueryWrapper<PropertyDepositEntity> DepositWrapper = new QueryWrapper<>();
        DepositWrapper.ne("status", 1);
        if (qo.getStartTime() != null) {
            DepositWrapper.ge("create_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            DepositWrapper.le("create_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            DepositWrapper.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                DepositWrapper.in("community_id", communityIdList);
            }
        }
        DepositWrapper.eq("deleted", 0);
        // ?????????????????????????????????
        List<PropertyDepositEntity> propertyDepositEntities = propertyDepositMapper.selectList(DepositWrapper);
        // ????????????????????????
        BigDecimal depositSum = new BigDecimal("0.00");
        // ????????????
        BigDecimal depositRefund = new BigDecimal("0.00");
        for (PropertyDepositEntity propertyDepositEntity : propertyDepositEntities) {
            depositSum = depositSum.add(propertyDepositEntity.getBillMoney());
            if (propertyDepositEntity.getStatus() == 3) {
                depositRefund = depositRefund.add(propertyDepositEntity.getBillMoney());
            }
        }
        PropertyFinanceFormEntity propertyFinanceFormEntity = new PropertyFinanceFormEntity();
        // ??????
        propertyFinanceFormEntity.setTypeName("????????????");
        // ??????????????????
        propertyFinanceFormEntity.setOnlineCharging(depositSum);
        // ??????????????????
        propertyFinanceFormEntity.setOfflineCharging(new BigDecimal("0.00"));
        // ????????????
        propertyFinanceFormEntity.setRefundOrWithdrawal(depositRefund);
        // ????????????
        propertyFinanceFormEntity.setTotal(depositSum);
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity);

        // ???????????????
        QueryWrapper<PropertyAdvanceDepositRecordEntity> advanceDepositRecordWrapper = new QueryWrapper<>();
        if (qo.getStartTime() != null) {
            advanceDepositRecordWrapper.ge("create_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            advanceDepositRecordWrapper.le("create_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            advanceDepositRecordWrapper.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                advanceDepositRecordWrapper.in("community_id", communityIdList);
            }
        }
        advanceDepositRecordWrapper.eq("deleted", 0);
        // ????????????????????????????????????
        List<PropertyAdvanceDepositRecordEntity> propertyAdvanceDepositRecordEntities = propertyAdvanceDepositRecordMapper.selectList(advanceDepositRecordWrapper);
        // ???????????????????????????
        BigDecimal advanceDepositSum = new BigDecimal("0.00");
        // ???????????????
        BigDecimal advanceDepositWithdrawal = new BigDecimal("0.00");
        for (PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity : propertyAdvanceDepositRecordEntities) {
            if (propertyAdvanceDepositRecordEntity.getDepositAmount() != null) {
                advanceDepositSum = advanceDepositSum.add(propertyAdvanceDepositRecordEntity.getDepositAmount());
            }
            if (propertyAdvanceDepositRecordEntity.getPayAmount() != null) {
                advanceDepositWithdrawal = advanceDepositWithdrawal.add(propertyAdvanceDepositRecordEntity.getPayAmount());
            }
        }
        PropertyFinanceFormEntity propertyFinanceFormEntity1 = new PropertyFinanceFormEntity();
        // ??????
        propertyFinanceFormEntity1.setTypeName("???????????????");
        // ?????????????????????
        propertyFinanceFormEntity1.setOnlineCharging(advanceDepositSum);
        // ?????????????????????
        propertyFinanceFormEntity1.setOfflineCharging(new BigDecimal("0.00"));
        // ???????????????
        propertyFinanceFormEntity1.setRefundOrWithdrawal(advanceDepositWithdrawal);
        // ???????????????
        propertyFinanceFormEntity1.setTotal(advanceDepositSum);
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity1);

        // ??????????????????
        QueryWrapper<PropertyFinanceOrderEntity> financeOrderWrapper = new QueryWrapper<>();
        if (qo.getStartTime() != null) {
            financeOrderWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            financeOrderWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderWrapper.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                financeOrderWrapper.in("community_id", communityIdList);
            }
        }
        financeOrderWrapper.eq("deleted", 0);
        // ???????????????????????????????????????
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(financeOrderWrapper);
        // ??????????????????????????????
        BigDecimal communitySum = new BigDecimal("0.00");
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            if (propertyFinanceOrderEntity.getTotalMoney() != null) {
                communitySum = communitySum.add(propertyFinanceOrderEntity.getTotalMoney());
            }
        }
        PropertyFinanceFormEntity propertyFinanceFormEntity2 = new PropertyFinanceFormEntity();
        // ??????
        propertyFinanceFormEntity2.setTypeName("????????????");
        // ????????????????????????
        propertyFinanceFormEntity2.setOnlineCharging(communitySum);
        // ????????????????????????
        propertyFinanceFormEntity2.setOfflineCharging(new BigDecimal("0.00"));
        // ??????????????????????????????
        propertyFinanceFormEntity2.setRefundOrWithdrawal(new BigDecimal("0.00"));
        // ??????????????????
        propertyFinanceFormEntity2.setTotal(communitySum);
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity2);

        // ??????????????????
        PropertyFinanceFormEntity propertyFinanceFormEntity3 = new PropertyFinanceFormEntity();
        // ??????
        propertyFinanceFormEntity3.setTypeName("??????");
        // ??????????????????
        propertyFinanceFormEntity3.setOnlineCharging(propertyFinanceFormEntity.getOnlineCharging().add(propertyFinanceFormEntity1.getOnlineCharging()).add(propertyFinanceFormEntity2.getOnlineCharging()));
        // ??????????????????
        propertyFinanceFormEntity3.setOfflineCharging(new BigDecimal("0.00"));
        // ????????????????????????
        propertyFinanceFormEntity3.setRefundOrWithdrawal(propertyFinanceFormEntity.getRefundOrWithdrawal().add(propertyFinanceFormEntity1.getRefundOrWithdrawal().add(propertyFinanceFormEntity2.getRefundOrWithdrawal())));
        // ?????????
        propertyFinanceFormEntity3.setTotal(propertyFinanceFormEntity.getTotal().add(propertyFinanceFormEntity1.getTotal()).add(propertyFinanceFormEntity2.getTotal()));
        propertyFinanceFormEntityList.add(propertyFinanceFormEntity3);

        return propertyFinanceFormEntityList;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-??????????????????-??????????????????
     * @Param:
     * @Return: PropertyFinanceFormChargeEntity
     * @Date: 2021/8/18 11:08
     **/
    @Override
    public List<PropertyFinanceFormChargeEntity> getFinanceFormCommunityChargeByOrderGenerateTime(PropertyFinanceFormChargeEntity qo, List<String> communityIdList) {
        // ?????????????????????
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = new LinkedList<>();

        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as totalMoney,SUM( penal_sum ) as receivablePenalMoney,SUM( coupon ) as couponMoney,SUM( deduction ) as deductionMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            queryWrapper.in("community_id", communityIdList);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.groupBy("fee_rule_id");
        // ??????????????????
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity;
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            propertyFinanceFormChargeEntity = new PropertyFinanceFormChargeEntity();
            propertyFinanceFormChargeEntity.setFeeRuleId(propertyFinanceOrderEntity.getFeeRuleId());
            propertyFinanceFormChargeEntity.setTotalMoney(propertyFinanceOrderEntity.getTotalMoney());
            propertyFinanceFormChargeEntity.setCouponMoney(propertyFinanceOrderEntity.getCouponMoney());
            propertyFinanceFormChargeEntity.setReceivablePenalMoney(propertyFinanceOrderEntity.getReceivablePenalMoney());
            propertyFinanceFormChargeEntity.setDeductionMoney(propertyFinanceOrderEntity.getDeductionMoney());
            propertyFinanceFormChargeEntity.setArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setThisMonthArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCollectPenalMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOnlineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOfflineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntityList.add(propertyFinanceFormChargeEntity);
        }

        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("fee_rule_id as feeRuleId,SUM( penal_sum ) as collectPenalMoney,SUM( total_money ) as communityOnlineCharging");
        if (qo.getStartTime() != null) {
            wrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper.eq("community_id", qo.getCommunityId());
        } else {
            wrapper.in("community_id", communityIdList);
        }
        wrapper.eq("order_status", 1);
        wrapper.eq("deleted", 0);
        wrapper.groupBy("fee_rule_id");
        // ???????????????????????????
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(wrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            if (entity.getFeeRuleId() != null) {
                for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                    if (financeFormChargeEntity.getFeeRuleId() != null) {
                        if (financeFormChargeEntity.getFeeRuleId().equals(entity.getFeeRuleId())) {
                            financeFormChargeEntity.setCollectPenalMoney(entity.getCollectPenalMoney());
                            financeFormChargeEntity.setCommunityOnlineCharging(entity.getCommunityOnlineCharging());
                        }
                    }
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> financeOrderQueryWrapper = new QueryWrapper<>();
        financeOrderQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as arrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderQueryWrapper.lt("order_time", qo.getStartTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderQueryWrapper.eq("order_status", 0);
        financeOrderQueryWrapper.eq("deleted", 0);
        financeOrderQueryWrapper.groupBy("fee_rule_id");
        // ?????????????????????????????????
        List<PropertyFinanceOrderEntity> lastMonthEntities = propertyFinanceOrderMapper.selectList(financeOrderQueryWrapper);
        for (PropertyFinanceOrderEntity lastMonthEntity : lastMonthEntities) {
            if (lastMonthEntity.getFeeRuleId() != null) {
                for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                    if (financeFormChargeEntity.getFeeRuleId() != null) {
                        if (financeFormChargeEntity.getFeeRuleId().equals(lastMonthEntity.getFeeRuleId())) {
                            financeFormChargeEntity.setArrearsMoney(lastMonthEntity.getArrearsMoney());
                        }
                    }
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> financeOrderEntityQueryWrapper = new QueryWrapper<>();
        financeOrderEntityQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as thisMonthArrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderEntityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            financeOrderEntityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderEntityQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderEntityQueryWrapper.eq("order_status", 0);
        financeOrderEntityQueryWrapper.eq("deleted", 0);
        financeOrderEntityQueryWrapper.groupBy("fee_rule_id");
        // ?????????????????????????????????
        List<PropertyFinanceOrderEntity> entityList = propertyFinanceOrderMapper.selectList(financeOrderEntityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entityList) {
            if (propertyFinanceOrderEntity.getFeeRuleId() != null) {
                for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                    if (financeFormChargeEntity.getFeeRuleId() != null) {
                        if (financeFormChargeEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                            financeFormChargeEntity.setThisMonthArrearsMoney(propertyFinanceOrderEntity.getThisMonthArrearsMoney());
                        }
                    }
                }
            }
        }
        //????????????
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            if (financeFormChargeEntity.getArrearsMoney() == null && financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            } else if (financeFormChargeEntity.getArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getThisMonthArrearsMoney());
            } else if (financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney());
            } else {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney().add(financeFormChargeEntity.getThisMonthArrearsMoney()));
            }
        }
        // ??????????????????
        List<Long> paramList = new ArrayList<>();
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            paramList.add(financeFormChargeEntity.getFeeRuleId());
        }
        if (paramList.size() > 0) {
            Map<String, Map<String, Object>> longMapMap = propertyFeeRuleMapper.selectFeeRuleIdName(paramList);
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                Map<String, Object> countMap = longMapMap.get(financeFormChargeEntity.getFeeRuleId());
                financeFormChargeEntity.setFeeRuleName(countMap != null ? String.valueOf(countMap.get("name")) : "");
            }
        }

        return propertyFinanceFormChargeEntityList;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-??????????????????-??????????????????
     * @Param:
     * @Return: PropertyFinanceFormChargeEntity
     * @Date: 2021/8/18 11:08
     **/
    @Override
    public List<PropertyFinanceFormChargeEntity> getFinanceFormCommunityChargeByOrderPeriodTime(PropertyFinanceFormChargeEntity qo, List<String> communityIdList) {
        // ?????????????????????
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = new LinkedList<>();

        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as totalMoney,SUM( penal_sum ) as receivablePenalMoney,SUM( coupon ) as couponMoney,SUM( deduction ) as deductionMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            queryWrapper.in("community_id", communityIdList);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.ne("build_type", 2);
        queryWrapper.groupBy("fee_rule_id");
        // ??????????????????
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity;
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            propertyFinanceFormChargeEntity = new PropertyFinanceFormChargeEntity();
            propertyFinanceFormChargeEntity.setFeeRuleId(propertyFinanceOrderEntity.getFeeRuleId());
            propertyFinanceFormChargeEntity.setTotalMoney(propertyFinanceOrderEntity.getTotalMoney());
            propertyFinanceFormChargeEntity.setCouponMoney(propertyFinanceOrderEntity.getCouponMoney());
            propertyFinanceFormChargeEntity.setReceivablePenalMoney(propertyFinanceOrderEntity.getReceivablePenalMoney());
            propertyFinanceFormChargeEntity.setDeductionMoney(propertyFinanceOrderEntity.getDeductionMoney());
            propertyFinanceFormChargeEntity.setArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setThisMonthArrearsMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCollectPenalMoney(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOnlineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntity.setCommunityOfflineCharging(new BigDecimal("0.00"));
            propertyFinanceFormChargeEntityList.add(propertyFinanceFormChargeEntity);
        }

        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("fee_rule_id as feeRuleId,SUM( penal_sum ) as collectPenalMoney,SUM( total_money ) as communityOnlineCharging");
        if (qo.getStartTime() != null) {
            wrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper.eq("community_id", qo.getCommunityId());
        } else {
            wrapper.in("community_id", communityIdList);
        }
        wrapper.eq("order_status", 1);
        wrapper.eq("deleted", 0);
        wrapper.ne("build_type", 2);
        wrapper.groupBy("fee_rule_id");
        // ???????????????????????????
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(wrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            if (entity.getFeeRuleId() != null) {
                for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                    if (financeFormChargeEntity.getFeeRuleId() != null) {
                        if (financeFormChargeEntity.getFeeRuleId().equals(entity.getFeeRuleId())) {
                            financeFormChargeEntity.setCollectPenalMoney(entity.getCollectPenalMoney());
                            financeFormChargeEntity.setCommunityOnlineCharging(entity.getCommunityOnlineCharging());
                        }
                    }
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> financeOrderQueryWrapper = new QueryWrapper<>();
        financeOrderQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as arrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderQueryWrapper.lt("order_time", qo.getStartTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderQueryWrapper.eq("order_status", 0);
        financeOrderQueryWrapper.eq("deleted", 0);
        financeOrderQueryWrapper.ne("build_type", 2);
        financeOrderQueryWrapper.groupBy("fee_rule_id");
        // ?????????????????????????????????
        List<PropertyFinanceOrderEntity> lastMonthEntities = propertyFinanceOrderMapper.selectList(financeOrderQueryWrapper);
        for (PropertyFinanceOrderEntity lastMonthEntity : lastMonthEntities) {
            if (lastMonthEntity.getFeeRuleId() != null) {
                for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                    if (financeFormChargeEntity.getFeeRuleId() != null) {
                        if (financeFormChargeEntity.getFeeRuleId().equals(lastMonthEntity.getFeeRuleId())) {
                            financeFormChargeEntity.setArrearsMoney(lastMonthEntity.getArrearsMoney());
                        }
                    }
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> financeOrderEntityQueryWrapper = new QueryWrapper<>();
        financeOrderEntityQueryWrapper.select("fee_rule_id as feeRuleId,SUM( total_money ) as thisMonthArrearsMoney");
        if (qo.getStartTime() != null) {
            financeOrderEntityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            financeOrderEntityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            financeOrderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            financeOrderEntityQueryWrapper.in("community_id", communityIdList);
        }
        financeOrderEntityQueryWrapper.eq("order_status", 0);
        financeOrderEntityQueryWrapper.eq("deleted", 0);
        financeOrderEntityQueryWrapper.ne("build_type", 2);
        financeOrderEntityQueryWrapper.groupBy("fee_rule_id");
        // ?????????????????????????????????
        List<PropertyFinanceOrderEntity> entityList = propertyFinanceOrderMapper.selectList(financeOrderEntityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entityList) {
            if (propertyFinanceOrderEntity.getFeeRuleId() != null) {
                for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                    if (financeFormChargeEntity.getFeeRuleId() != null) {
                        if (financeFormChargeEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                            financeFormChargeEntity.setThisMonthArrearsMoney(propertyFinanceOrderEntity.getThisMonthArrearsMoney());
                        }
                    }
                }
            }
        }
        //????????????
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            if (financeFormChargeEntity.getArrearsMoney() == null && financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(new BigDecimal("0.00"));
            } else if (financeFormChargeEntity.getArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getThisMonthArrearsMoney());
            } else if (financeFormChargeEntity.getThisMonthArrearsMoney() == null) {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney());
            } else {
                financeFormChargeEntity.setArrearsMoneySum(financeFormChargeEntity.getArrearsMoney().add(financeFormChargeEntity.getThisMonthArrearsMoney()));
            }
        }
        // ??????????????????
        List<Long> paramList = new ArrayList<>();
        for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
            paramList.add(financeFormChargeEntity.getFeeRuleId());
        }
        if (paramList.size() > 0) {
            Map<String, Map<String, Object>> longMapMap = propertyFeeRuleMapper.selectFeeRuleIdName(paramList);
            for (PropertyFinanceFormChargeEntity financeFormChargeEntity : propertyFinanceFormChargeEntityList) {
                Map<String, Object> countMap = longMapMap.get(financeFormChargeEntity.getFeeRuleId());
                financeFormChargeEntity.setFeeRuleName(countMap != null ? String.valueOf(countMap.get("name")) : "");
            }
        }

        return propertyFinanceFormChargeEntityList;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/8/19 9:31
     **/
    @Override
    public List<PropertyCollectionFormEntity> getCollectionFormCollection(PropertyCollectionFormEntity qo, List<String> communityIdList) {
        // ??????????????????
        List<PropertyCollectionFormEntity> propertyCollectionFormEntityList = new LinkedList<>();
        // ??????????????????????????????????????????????????????id
        List<Long> FeeRuleIdList = new ArrayList<>();
        if (qo.getFeeRuleName() != null) {
            if (qo.getCommunityId() != null) {
                List<String> communityIds = new ArrayList<>();
                communityIds.add(String.valueOf(qo.getCommunityId()));
                FeeRuleIdList = propertyFeeRuleMapper.selectFeeRuleIdList(communityIds, qo.getFeeRuleName());
            } else {
                if (!CollectionUtils.isEmpty(communityIdList)) {
                    FeeRuleIdList = propertyFeeRuleMapper.selectFeeRuleIdList(communityIdList, qo.getFeeRuleName());
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS totalSum");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                queryWrapper.in("community_id", communityIdList);
            }
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            queryWrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null) {
            queryWrapper.eq("fee_rule_id", 0);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.groupBy("fee_rule_id");
        // ??????????????????
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        PropertyCollectionFormEntity propertyCollectionFormEntity;
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            propertyCollectionFormEntity = new PropertyCollectionFormEntity();
            propertyCollectionFormEntity.setFeeRuleId(propertyFinanceOrderEntity.getFeeRuleId());
            propertyCollectionFormEntity.setTotalSum(propertyFinanceOrderEntity.getTotalSum());
            propertyCollectionFormEntity.setWeChatPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setAliPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setBalancePaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setCashPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setUnionPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntity.setBankPaySum(new BigDecimal("0.00"));
            propertyCollectionFormEntityList.add(propertyCollectionFormEntity);
        }

        QueryWrapper<PropertyFinanceOrderEntity> query = new QueryWrapper<>();
        query.select("fee_rule_id as feeRuleId,SUM(total_money) AS weChatPaySum");
        if (qo.getStartTime() != null) {
            query.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            query.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            query.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                query.in("community_id", communityIdList);
            }
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            query.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null) {
            query.eq("fee_rule_id", 0);
        }
        query.eq("deleted", 0);
        query.eq("pay_type", 1);
        query.groupBy("fee_rule_id");
        // ??????????????????
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(query);
        for (PropertyFinanceOrderEntity entity : entities) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(entity.getFeeRuleId())) {
                    collectionFormEntity.setWeChatPaySum(entity.getWeChatPaySum());
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS aliPaySum");
        if (qo.getStartTime() != null) {
            wrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                wrapper.in("community_id", communityIdList);
            }
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            wrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null) {
            wrapper.eq("fee_rule_id", 0);
        }
        wrapper.eq("deleted", 0);
        wrapper.eq("pay_type", 2);
        wrapper.groupBy("fee_rule_id");
        // ?????????????????????
        List<PropertyFinanceOrderEntity> entities1 = propertyFinanceOrderMapper.selectList(wrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities1) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setAliPaySum(propertyFinanceOrderEntity.getAliPaySum());
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> entityQueryWrapper = new QueryWrapper<>();
        entityQueryWrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS balancePaySum");
        if (qo.getStartTime() != null) {
            entityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            entityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            entityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                entityQueryWrapper.in("community_id", communityIdList);
            }
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            entityQueryWrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null) {
            entityQueryWrapper.eq("fee_rule_id", 0);
        }
        entityQueryWrapper.eq("deleted", 0);
        entityQueryWrapper.eq("pay_type", 3);
        entityQueryWrapper.groupBy("fee_rule_id");
        // ??????????????????
        List<PropertyFinanceOrderEntity> entities2 = propertyFinanceOrderMapper.selectList(entityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities2) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setBalancePaySum(propertyFinanceOrderEntity.getBalancePaySum());
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> orderEntityQueryWrapper = new QueryWrapper<>();
        orderEntityQueryWrapper.select("fee_rule_id as feeRuleId,SUM(total_money) AS cashPaySum");
        if (qo.getStartTime() != null) {
            orderEntityQueryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            orderEntityQueryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            orderEntityQueryWrapper.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                orderEntityQueryWrapper.in("community_id", communityIdList);
            }
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            orderEntityQueryWrapper.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null) {
            orderEntityQueryWrapper.eq("fee_rule_id", 0);
        }
        orderEntityQueryWrapper.eq("deleted", 0);
        orderEntityQueryWrapper.eq("pay_type", 4);
        orderEntityQueryWrapper.groupBy("fee_rule_id");
        // ??????????????????
        List<PropertyFinanceOrderEntity> entities3 = propertyFinanceOrderMapper.selectList(orderEntityQueryWrapper);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities3) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setCashPaySum(propertyFinanceOrderEntity.getCashPaySum());
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> wrapper1 = new QueryWrapper<>();
        wrapper1.select("fee_rule_id as feeRuleId,SUM(total_money) AS UnionPaySum");
        if (qo.getStartTime() != null) {
            wrapper1.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            wrapper1.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            wrapper1.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                wrapper1.in("community_id", communityIdList);
            }
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            wrapper1.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null) {
            wrapper1.eq("fee_rule_id", 0);
        }
        wrapper1.eq("deleted", 0);
        wrapper1.eq("pay_type", 5);
        wrapper1.groupBy("fee_rule_id");
        // ????????????????????????
        List<PropertyFinanceOrderEntity> entities4 = propertyFinanceOrderMapper.selectList(wrapper1);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities4) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setUnionPaySum(propertyFinanceOrderEntity.getUnionPaySum());
                }
            }
        }

        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("fee_rule_id as feeRuleId,SUM(total_money) AS bankPaySum");
        if (qo.getStartTime() != null) {
            queryWrapper1.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper1.le("order_time", qo.getEndTime());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper1.eq("community_id", qo.getCommunityId());
        } else {
            if (!CollectionUtils.isEmpty(communityIdList)) {
                queryWrapper1.in("community_id", communityIdList);
            }
        }
        if (qo.getFeeRuleName() != null && FeeRuleIdList.size() > 0) {
            queryWrapper1.in("fee_rule_id", FeeRuleIdList);
        } else if (qo.getFeeRuleName() != null) {
            queryWrapper1.eq("fee_rule_id", 0);
        }
        queryWrapper1.eq("deleted", 0);
        queryWrapper1.eq("pay_type", 6);
        queryWrapper1.groupBy("fee_rule_id");
        // ????????????????????????
        List<PropertyFinanceOrderEntity> entities5 = propertyFinanceOrderMapper.selectList(queryWrapper1);
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : entities5) {
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                if (collectionFormEntity.getFeeRuleId().equals(propertyFinanceOrderEntity.getFeeRuleId())) {
                    collectionFormEntity.setBankPaySum(propertyFinanceOrderEntity.getBankPaySum());
                }
            }
        }
        // ??????????????????
        List<Long> paramList = new ArrayList<>();
        for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
            paramList.add(collectionFormEntity.getFeeRuleId());
        }
        if (paramList.size() > 0 && paramList != null) {
            Map<String, Map<String, Object>> longMapMap = propertyFeeRuleMapper.selectFeeRuleIdName(paramList);
            for (PropertyCollectionFormEntity collectionFormEntity : propertyCollectionFormEntityList) {
                Map<String, Object> countMap = longMapMap.get(collectionFormEntity.getFeeRuleId());
                collectionFormEntity.setFeeRuleName(countMap != null ? String.valueOf(countMap.get("name")) : "");
            }
        }

        // ??????????????????
        for (PropertyCollectionFormEntity entity : propertyCollectionFormEntityList) {
            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(entity.getFeeRuleId());
            CommunityEntity communityEntity = communityMapper.selectById(propertyFeeRuleEntity.getCommunityId());
            entity.setCommunityName(communityEntity.getName());
        }

        return propertyCollectionFormEntityList;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????-??????????????????
     * @Param:
     * @Return: PropertyFinanceFormChargeEntity
     * @Date: 2021/8/19 11:08
     **/
    @Override
    public PropertyCollectionFormEntity getCollectionFormOrderByOrderGenerateTime(PropertyCollectionFormEntity qo) {
        // ?????????????????????
        PropertyCollectionFormEntity propertyCollectionFormEntity = new PropertyCollectionFormEntity();
        propertyCollectionFormEntity.setStatementReceivableMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementArrearsMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementCollectMoney(new BigDecimal("0.00"));

        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("order_status AS orderStatus,SUM(total_money) AS totalMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getTargetId() != null) {
            queryWrapper.eq("target_id", qo.getTargetId());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.groupBy("order_status");
        // ??????????????????
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(queryWrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            // ?????????-??????
            if (entity.getOrderStatus() == 1) {
                propertyCollectionFormEntity.setStatementCollectMoney(entity.getTotalMoney());
            } else if (entity.getOrderStatus() == 0) {
                // ?????????-??????
                propertyCollectionFormEntity.setStatementArrearsMoney(entity.getTotalMoney());
            }
        }
        // ??????-??????
        if (propertyCollectionFormEntity.getStatementCollectMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementArrearsMoney());
        } else if (propertyCollectionFormEntity.getStatementArrearsMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney());
        } else {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney().add(propertyCollectionFormEntity.getStatementArrearsMoney()));
        }
        // ??????????????????
        HouseEntity houseEntity = houseMapper.selectById(propertyCollectionFormEntity.getTargetId());
        if (houseEntity != null) {
            propertyCollectionFormEntity.setTargetIdName(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
        }

        return propertyCollectionFormEntity;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????-??????????????????
     * @Param:
     * @Return: PropertyFinanceFormChargeEntity
     * @Date: 2021/8/19 11:08
     **/
    @Override
    public PropertyCollectionFormEntity getCollectionFormOrderByOrderPeriodTime(PropertyCollectionFormEntity qo) {
        // ?????????????????????
        PropertyCollectionFormEntity propertyCollectionFormEntity = new PropertyCollectionFormEntity();
        propertyCollectionFormEntity.setStatementReceivableMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementArrearsMoney(new BigDecimal("0.00"));
        propertyCollectionFormEntity.setStatementCollectMoney(new BigDecimal("0.00"));

        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("order_status AS orderStatus,SUM(total_money) AS totalMoney");
        if (qo.getStartTime() != null) {
            queryWrapper.ge("order_time", qo.getStartTime());
        }
        if (qo.getEndTime() != null) {
            queryWrapper.le("order_time", qo.getEndTime());
        }
        if (qo.getTargetId() != null) {
            queryWrapper.eq("target_id", qo.getTargetId());
        }
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.ne("build_type", 2);
        queryWrapper.groupBy("order_status");
        // ??????????????????
        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(queryWrapper);
        for (PropertyFinanceOrderEntity entity : entities) {
            // ?????????-??????
            if (entity.getOrderStatus() == 1) {
                propertyCollectionFormEntity.setStatementCollectMoney(entity.getTotalMoney());
            } else if (entity.getOrderStatus() == 0) {
                // ?????????-??????
                propertyCollectionFormEntity.setStatementArrearsMoney(entity.getTotalMoney());
            }
        }
        // ??????-??????
        if (propertyCollectionFormEntity.getStatementCollectMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementArrearsMoney());
        } else if (propertyCollectionFormEntity.getStatementArrearsMoney() == null) {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney());
        } else {
            propertyCollectionFormEntity.setStatementReceivableMoney(propertyCollectionFormEntity.getStatementCollectMoney().add(propertyCollectionFormEntity.getStatementArrearsMoney()));
        }
        // ??????????????????
        HouseEntity houseEntity = houseMapper.selectById(propertyCollectionFormEntity.getTargetId());
        if (houseEntity != null) {
            propertyCollectionFormEntity.setTargetIdName(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
        }

        return propertyCollectionFormEntity;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-??????????????????
     * @Param: propertyFinanceFormEntity:
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceFormEntity>
     * @Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyFinanceFormEntity> queryExportExcelFinanceFormList(PropertyFinanceFormEntity propertyFinanceFormEntity, List<String> communityIdList) {
        List<PropertyFinanceFormEntity> propertyFinanceFormEntityList = new LinkedList<>();
        try {
            if (propertyFinanceFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyFinanceFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyFinanceFormEntity.getYear());
                propertyFinanceFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyFinanceFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyFinanceFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyFinanceFormEntity.getMonth());
                propertyFinanceFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        propertyFinanceFormEntityList = getFinanceFormCommunityIncome(propertyFinanceFormEntity, communityIdList);
        if (propertyFinanceFormEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(), "????????????");
        }
        return propertyFinanceFormEntityList;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-??????????????????
     * @Param: propertyFinanceFormChargeEntity:
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceFormChargeEntity>
     * @Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyFinanceFormChargeEntity> queryExportExcelChargeList(PropertyFinanceFormChargeEntity propertyFinanceFormChargeEntity, List<String> communityIdList) {
        List<PropertyFinanceFormChargeEntity> propertyFinanceFormChargeEntityList = new LinkedList<>();
        try {
            if (propertyFinanceFormChargeEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyFinanceFormChargeEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyFinanceFormChargeEntity.getYear());
                propertyFinanceFormChargeEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormChargeEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyFinanceFormChargeEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyFinanceFormChargeEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyFinanceFormChargeEntity.getMonth());
                propertyFinanceFormChargeEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyFinanceFormChargeEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (propertyFinanceFormChargeEntity.getType()) {
            case 1:
                // ?????????????????????
                propertyFinanceFormChargeEntityList = getFinanceFormCommunityChargeByOrderGenerateTime(propertyFinanceFormChargeEntity, communityIdList);
                break;
            case 2:
                // ?????????????????????
                propertyFinanceFormChargeEntityList = getFinanceFormCommunityChargeByOrderPeriodTime(propertyFinanceFormChargeEntity, communityIdList);
                break;
            default:
                break;
        }
        if (propertyFinanceFormChargeEntityList == null || propertyFinanceFormChargeEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(), "????????????");
        }
        return propertyFinanceFormChargeEntityList;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param: propertyCollectionFormEntity:
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyCollectionFormEntity>
     * @Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyCollectionFormEntity> queryExportExcelCollectionFormList(PropertyCollectionFormEntity propertyCollectionFormEntity, List<String> communityIdList) {
        try {
            if (propertyCollectionFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyCollectionFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyCollectionFormEntity.getYear());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getDateTime() != null) {
                ZoneId zone = ZoneId.systemDefault();
                Instant instant = propertyCollectionFormEntity.getDateTime().atStartOfDay().atZone(zone).toInstant();
                String firstDate = DateCalculateUtil.getFirstDate(Date.from(instant));
                String lastDate = DateCalculateUtil.getLastDate(Date.from(instant));
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<PropertyCollectionFormEntity> propertyFinanceFormEntityList = getCollectionFormCollection(propertyCollectionFormEntity, communityIdList);
        if (propertyFinanceFormEntityList == null || propertyFinanceFormEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(), "????????????");
        }
        return propertyFinanceFormEntityList;
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????-????????????
     * @Param: propertyCollectionFormEntity:
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyCollectionFormEntity>
     * @Date: 2021/8/19 15:52
     **/
    @Override
    public List<PropertyCollectionFormEntity> queryExportExcelCollectionFormOrderList(PropertyCollectionFormEntity propertyCollectionFormEntity) {
        List<PropertyCollectionFormEntity> propertyCollectionFormEntityList = new LinkedList<>();
        try {
            if (propertyCollectionFormEntity.getYear() != null) {
                String firstYearDateOfAmount = DateCalculateUtil.getFirstYearDateOfAmount(propertyCollectionFormEntity.getYear());
                String lastYearDateOfAmount = DateCalculateUtil.getLastYearDateOfAmount(propertyCollectionFormEntity.getYear());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastYearDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (propertyCollectionFormEntity.getMonth() != null) {
                String firstMouthDateOfAmount = DateCalculateUtil.getFirstMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                String lastMouthDateOfAmount = DateCalculateUtil.getLastMouthDateOfAmount(propertyCollectionFormEntity.getMonth());
                propertyCollectionFormEntity.setStartTime(LocalDate.parse(firstMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                propertyCollectionFormEntity.setEndTime(LocalDate.parse(lastMouthDateOfAmount, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PropertyCollectionFormEntity entity = new PropertyCollectionFormEntity();
        switch (propertyCollectionFormEntity.getType()) {
            case 1:
                // ?????????????????????
                entity = getCollectionFormOrderByOrderGenerateTime(propertyCollectionFormEntity);
                break;
            case 2:
                // ?????????????????????
                entity = getCollectionFormOrderByOrderPeriodTime(propertyCollectionFormEntity);
                break;
            default:
                break;
        }
        propertyCollectionFormEntityList.add(entity);
        if (propertyCollectionFormEntityList.size() <= 0) {
            throw new JSYException(JSYError.NOT_FOUND.getCode(), "????????????");
        }

        return propertyCollectionFormEntityList;
    }

    /**
     * @Description: ??????????????????????????????
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/08/26 09:35
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PropertyFinanceOrderEntity addTemporaryCharges(PropertyFinanceOrderEntity propertyFinanceOrderEntity) {
        // ??????id
        propertyFinanceOrderEntity.setId(SnowFlake.nextId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = sdf.format(new Date());
        // ??????????????????
        propertyFinanceOrderEntity.setOrderTime(LocalDate.parse(dateNow, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // ??????????????????
        CommunityEntity communityEntity = communityMapper.selectById(propertyFinanceOrderEntity.getCommunityId());
        propertyFinanceOrderEntity.setRise(communityEntity.getName() + "-" + propertyFinanceOrderEntity.getFeeRuleName());
        // ???????????????
        propertyFinanceOrderEntity.setOrderNum(FinanceBillServiceImpl.getOrderNum(String.valueOf(propertyFinanceOrderEntity.getCommunityId())));
        // ???????????????
        propertyFinanceOrderEntity.setTotalMoney(propertyFinanceOrderEntity.getPropertyFee());
        // ????????????????????????
        propertyFinanceOrderEntity.setBuildType(2);
        propertyFinanceOrderMapper.insert(propertyFinanceOrderEntity);
	    return propertyFinanceOrderEntity;
    }

    /**
     * @Description: ????????????????????????
     * @author: Hu
     * @since: 2021/8/31 14:43
     * @Param: [ids]
     * @return: void
     */
    @Override
    public void updateStatusIds(String ids, Integer hide) {
        propertyFinanceOrderMapper.updateStatusIds(ids.split(","), hide);
    }


    /**
     * @Description: ???????????????????????????????????????????????????????????????
     * @author: Hu
     * @since: 2021/9/3 9:51
     * @Param: [communityId]
     * @return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     */
    @Override
    public List<PropertyFinanceOrderEntity> FeeOrderList(Long communityId, String uid) {
        return propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("uid", uid).eq("community_id", communityId).eq("order_status",0));
    }

    /**
     * @Description: ??????????????????????????????
     * @author: Hu
     * @since: 2021/8/31 15:01
     * @Param: [adminCommunityId]
     * @return: java.util.List<com.jsy.community.entity.property.PropertyFeeRuleEntity>
     */
    @Override
    public List<PropertyFeeRuleEntity> getFeeList(Long adminCommunityId) {
        return propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>().select("id,name").eq("community_id", adminCommunityId));
    }

    /**
     * @Description: ??????????????????
     * @author: Hu
     * @since: 2021/8/31 14:50
     * @Param: [ids]
     * @return: void
     */
    @Override
    public void deleteIds(String ids) {
        propertyFinanceOrderMapper.delete(new QueryWrapper<PropertyFinanceOrderEntity>().in("id", (Object) ids.split(",")));
    }

    /**
     * @Description: ??????
     * @Param: [propertyFinanceOrderEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: DKS
     * @Date: 2021/09/06 10:46
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean collection(List<Long> ids, Long communityId, Integer payType) {
        int row;

        for (Long id : ids) {
            PropertyFinanceOrderEntity propertyFinanceOrderEntity = propertyFinanceOrderMapper.selectById(id);
            if (propertyFinanceOrderEntity.getOrderStatus() == 1) {
                throw new PropertyException(JSYError.DUPLICATE_KEY.getCode(), "?????????,?????????????????????");
            }
            // ??????????????????????????????,???????????????????????????????????????
            if (payType == 7) {
                PropertyFinanceOrderEntity propertyFinanceOrderEntity1 = propertyFinanceOrderMapper.selectById(id);
                propertyFinanceOrderEntity1.setDeduction(propertyFinanceOrderEntity.getTotalMoney());
                propertyFinanceOrderMapper.updateById(propertyFinanceOrderEntity1);

                // ????????????????????????????????????????????????????????????
                if (propertyFinanceOrderEntity.getAssociatedType() == 2) {
                    CarPositionEntity carPositionEntity = carPositionMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                    PropertyAdvanceDepositEntity propertyAdvanceDepositEntity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(carPositionEntity.getHouseId(), communityId);
                    if (propertyAdvanceDepositEntity != null) {
                        if (propertyAdvanceDepositEntity.getBalance().subtract(propertyFinanceOrderEntity.getTotalMoney()).compareTo(BigDecimal.ZERO) == -1) {
                            HouseEntity houseEntity = houseMapper.selectById(carPositionEntity.getHouseId());
                            throw new PropertyException(JSYError.NOT_ENOUGH.getCode(), houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getFloor() + "-" + houseEntity.getDoor() + "???????????????");
                        }
                    } else {
                        throw new PropertyException(JSYError.NOT_ENOUGH.getCode(), "?????????????????????????????????");
                    }

                    // ???????????????????????????
                    propertyAdvanceDepositEntity.setBalanceRecord(propertyFinanceOrderEntity.getTotalMoney());
                    propertyAdvanceDepositEntity.setBalance(propertyAdvanceDepositEntity.getBalance().add(propertyFinanceOrderEntity.getTotalMoney().negate()));
                    propertyAdvanceDepositEntity.setUpdateTime(LocalDateTime.now());
                    // ?????????????????????
                    propertyAdvanceDepositMapper.updateById(propertyAdvanceDepositEntity);
                    // ?????????????????????????????????????????????????????????
                    PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
                    propertyAdvanceDepositRecordEntity.setCommunityId(propertyAdvanceDepositEntity.getCommunityId());
                    propertyAdvanceDepositRecordEntity.setType(1);
                    propertyAdvanceDepositRecordEntity.setOrderId(id);
                    // ??????????????????????????????????????????
                    PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity1 = propertyAdvanceDepositRecordMapper.queryMaxCreateTimeRecord(
                            propertyAdvanceDepositEntity.getId(), propertyAdvanceDepositEntity.getCommunityId());
                    propertyAdvanceDepositRecordEntity.setPayAmount(propertyAdvanceDepositEntity.getBalanceRecord());
                    propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositRecordEntity1.getBalanceRecord().add(propertyAdvanceDepositEntity.getBalanceRecord().negate()));
                    propertyAdvanceDepositRecordEntity.setAdvanceDepositId(propertyAdvanceDepositEntity.getId());
                    propertyAdvanceDepositRecordEntity.setComment(propertyAdvanceDepositEntity.getComment());
                    propertyAdvanceDepositRecordEntity.setUpdateBy(propertyAdvanceDepositEntity.getUpdateBy());
                    PropertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
                } else if (propertyFinanceOrderEntity.getAssociatedType() == 1) {
                    // ????????????????????????????????????id??????????????????????????????
                    PropertyAdvanceDepositEntity propertyAdvanceDepositEntity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(propertyFinanceOrderEntity.getTargetId(), communityId);
                    if (propertyAdvanceDepositEntity != null) {
                        if (propertyAdvanceDepositEntity.getBalance().subtract(propertyFinanceOrderEntity.getTotalMoney()).compareTo(BigDecimal.ZERO) == -1) {
                            HouseEntity houseEntity = houseMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                            throw new PropertyException(JSYError.NOT_ENOUGH.getCode(), houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getFloor() + "-" + houseEntity.getDoor() + "???????????????");
                        }
                    } else {
                        throw new PropertyException(JSYError.NOT_ENOUGH.getCode(), "?????????????????????????????????");
                    }

                    // ???????????????????????????
                    propertyAdvanceDepositEntity.setBalanceRecord(propertyFinanceOrderEntity.getTotalMoney());
                    propertyAdvanceDepositEntity.setBalance(propertyAdvanceDepositEntity.getBalance().add(propertyFinanceOrderEntity.getTotalMoney().negate()));
                    propertyAdvanceDepositEntity.setUpdateTime(LocalDateTime.now());
                    // ?????????????????????
                    propertyAdvanceDepositMapper.updateById(propertyAdvanceDepositEntity);
                    // ?????????????????????????????????????????????????????????
                    PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity = new PropertyAdvanceDepositRecordEntity();
                    propertyAdvanceDepositRecordEntity.setCommunityId(propertyAdvanceDepositEntity.getCommunityId());
                    propertyAdvanceDepositRecordEntity.setType(1);
                    propertyAdvanceDepositRecordEntity.setOrderId(id);
                    // ??????????????????????????????????????????
                    PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity1 = propertyAdvanceDepositRecordMapper.queryMaxCreateTimeRecord(
                            propertyAdvanceDepositEntity.getId(), propertyAdvanceDepositEntity.getCommunityId());
                    propertyAdvanceDepositRecordEntity.setPayAmount(propertyAdvanceDepositEntity.getBalanceRecord());
                    propertyAdvanceDepositRecordEntity.setBalanceRecord(propertyAdvanceDepositRecordEntity1.getBalanceRecord().add(propertyAdvanceDepositEntity.getBalanceRecord().negate()));
                    propertyAdvanceDepositRecordEntity.setAdvanceDepositId(propertyAdvanceDepositEntity.getId());
                    propertyAdvanceDepositRecordEntity.setComment(propertyAdvanceDepositEntity.getComment());
                    propertyAdvanceDepositRecordEntity.setUpdateBy(propertyAdvanceDepositEntity.getUpdateBy());
                    PropertyAdvanceDepositRecordService.addPropertyAdvanceDepositRecord(propertyAdvanceDepositRecordEntity);
                }
            }
        }

        //??????????????????????????????
        row = propertyFinanceOrderMapper.collection(ids, 2);
        return row >= 1;
    }

    @Override
    public List<CarPositionEntity> carList(Long adminCommunityId) {
        return carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().select("id,car_position").eq("community_id", adminCommunityId));
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????
     * @Param: excel:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/9/7 11:25
     **/
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveFinanceOrder(List<PropertyFinanceOrderEntity> propertyFinanceOrderEntityList, Long communityId, String uid) {
        // ???????????????????????????
        List<PropertyFinanceOrderEntity> addPropertyFinanceOrderEntityList = new ArrayList<>();

        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntityList) {
            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(propertyFinanceOrderEntity.getFeeRuleId());
            // ????????????
            PropertyFinanceOrderEntity entity = new PropertyFinanceOrderEntity();
            entity.setId(SnowFlake.nextId());
            entity.setCommunityId(communityId);
            entity.setBeginTime(propertyFinanceOrderEntity.getBeginTime());
            entity.setOverTime(propertyFinanceOrderEntity.getOverTime());
            entity.setOrderTime(LocalDate.now());
            entity.setAssociatedType(propertyFinanceOrderEntity.getAssociatedType());
            entity.setUid(propertyFinanceOrderEntity.getUid());
            entity.setTargetId(propertyFinanceOrderEntity.getTargetId());
            entity.setPropertyFee(propertyFinanceOrderEntity.getPropertyFee());
            entity.setTotalMoney(propertyFinanceOrderEntity.getPropertyFee());
            entity.setOrderStatus(propertyFinanceOrderEntity.getOrderStatus());
            entity.setBuildType(3);
            entity.setHide(1);
            entity.setType(propertyFeeRuleEntity.getType());
            entity.setFeeRuleId(propertyFeeRuleEntity.getId());
            entity.setOrderNum(FinanceBillServiceImpl.getOrderNum(String.valueOf(propertyFeeRuleEntity.getCommunityId())));
            entity.setDeleted(0L);
            entity.setCreateTime(LocalDateTime.now());
            addPropertyFinanceOrderEntityList.add(entity);
        }
        // ??????????????????
        Integer saveFinanceOrderRow = 0;
        if (addPropertyFinanceOrderEntityList.size() > 0) {
            saveFinanceOrderRow = propertyFinanceOrderMapper.saveFinanceOrder(addPropertyFinanceOrderEntityList);
        }
        return saveFinanceOrderRow;
    }


    @Override
    public List<PropertyFinanceOrderEntity> findOrder(String orderId) {
        return propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("tripartite_order", orderId));
    }

    /**
     * @Description: ??????????????????
     * @author: Hu
     * @since: 2021/9/17 9:22
     * @Param: [ids, adminCommunityId]
     * @return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     */
    @Override
    public List<PropertyFinanceOrderEntity> getIds(String ids, Long adminCommunityId) {
        List<String> list = Arrays.asList(ids.split(","));
        Map<Long, String> carPositionMap = new HashMap<>();
        Map<Long, String> houseMap = new HashMap<>();

        //??????
        List<CarPositionEntity> entityList = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().select("id,car_position,house_id").eq("community_id", adminCommunityId));
        for (CarPositionEntity carPositionEntity : entityList) {
            carPositionMap.put(carPositionEntity.getId(), carPositionEntity.getCarPosition() + "," + carPositionEntity.getHouseId());
        }
        //??????
        List<HouseEntity> houseEntities = houseMapper.selectList(new QueryWrapper<HouseEntity>().eq("community_id", adminCommunityId).eq("type", 4));
        for (HouseEntity houseEntity : houseEntities) {
            houseMap.put(houseEntity.getId(), houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
        }

        List<PropertyFinanceOrderEntity> entities = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>()
                .select("id,associated_type,target_id,type,begin_time,over_time,property_fee,penal_sum,coupon,total_money,rise")
                .in("id", list));
        for (PropertyFinanceOrderEntity entity : entities) {

            entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum().subtract(entity.getCoupon())));
            if (entity.getType() != null) {
                entity.setFeeRuleName(BusinessEnum.FeeRuleNameEnum.getName(entity.getType()));
            } else {
                entity.setFeeRuleName(entity.getRise().substring(entity.getRise().indexOf("-") + 1));
            }
            
            if (entity.getAssociatedType() == 1) {
                PropertyAdvanceDepositEntity depositEntity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(entity.getTargetId(), adminCommunityId);
                if (depositEntity != null) {
                    entity.setDeduction(depositEntity.getBalance());
                } else {
                    entity.setDeduction(new BigDecimal(0));
                }
                entity.setAddress(houseMap.get(entity.getTargetId()));
            } else {
                String[] split = carPositionMap.get(entity.getTargetId()).split(",");
                if (split.length > 0) {
                    entity.setAddress(split[0]);
                    PropertyAdvanceDepositEntity depositEntity = propertyAdvanceDepositMapper.queryAdvanceDepositByHouseId(Long.parseLong(split[1]), adminCommunityId);
                    if (depositEntity != null) {
                        entity.setDeduction(depositEntity.getBalance());
                    } else {
                        entity.setDeduction(new BigDecimal(0));
                    }
                }
            }
        }
        return entities;
    }

    /**
     * @Description: app????????????????????????????????????????????????
     * @author: Hu
     * @since: 2021/9/9 14:19
     * @Param: [entity]
     * @return: void
     */
    @Override
    public void insert(PropertyFinanceOrderEntity orderEntity) {
        PropertyFeeRuleEntity ruleEntity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("disposable", 2)
                .eq("community_id", orderEntity.getCommunityId())
                .eq("status", 1)
                .eq("relevance_type", 2)
                .eq("type", 12));

        propertyFinanceOrderMapper.insert(orderEntity);
    }

    /**
     * @Author: DKS
     * @Description: ??????????????????
     * @Param: excel:
     * @Return: com.jsy.community.vo.CommonResult
     * @Date: 2021/9/8 10:40
     **/
    @Override
    public List<PropertyFinanceOrderEntity> queryExportFinanceExcel(PropertyFinanceOrderEntity qo) {
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities;
        QueryWrapper<PropertyFinanceOrderEntity> queryWrapper = new QueryWrapper<>();
        //?????????????????????
        if (qo.getAssociatedType() != null) {
            queryWrapper.eq("associated_type", qo.getAssociatedType());
        }
        //?????????????????????
        if (qo.getTargetId() != null) {
            queryWrapper.eq("target_id", qo.getTargetId());
        }
        //?????????????????????
        if (StringUtils.isNotBlank(qo.getFeeRuleName())) {
            List<String> communityIds = new ArrayList<>();
            communityIds.add(String.valueOf(qo.getCommunityId()));
            List<Long> feeRuleIdList = propertyFeeRuleMapper.selectFeeRuleIdList(communityIds, qo.getFeeRuleName());
            queryWrapper.in("fee_rule_id", feeRuleIdList);
        }
        //?????????????????????
        if (StringUtils.isNotBlank(qo.getOrderNum())) {
            queryWrapper.eq("order_num", qo.getOrderNum());
        }
        //?????????????????????
        if (qo.getOrderStartDate() != null && qo.getOrderEndDate() != null) {
            queryWrapper.gt("create_time", qo.getOrderStartDate());
            queryWrapper.lt("create_time", qo.getOrderEndDate());
        }
        //?????????????????????
        if (qo.getOrderStartDate() != null && qo.getOrderEndDate() != null) {
            queryWrapper.gt("pay_time", qo.getPayTimeStartDate());
            queryWrapper.lt("pay_time", qo.getPayTimeEndDate());
        }
        //????????????????????????????????????
        if (qo.getBeginTime() != null && qo.getOverTime() != null) {
            queryWrapper.eq("begin_time", qo.getBeginTime());
            queryWrapper.eq("over_time", qo.getOverTime());
        }
        //???????????????
        if (qo.getHide() != null) {
            queryWrapper.eq("hide", qo.getHide());
        }
        //?????????????????????
        if (qo.getOrderStatus() != null) {
            queryWrapper.eq("order_status", qo.getOrderStatus());
        }
        //???????????????
        if (qo.getCommunityId() != null) {
            queryWrapper.eq("community_id", qo.getCommunityId());
        }
        //?????????????????????
        if (qo.getPayType() != null) {
            queryWrapper.eq("pay_type", qo.getPayType());
        }
        queryWrapper.orderByDesc("create_time");
        propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(propertyFinanceOrderEntities)) {
            return propertyFinanceOrderEntities;
        }
        //??????????????????
        for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : propertyFinanceOrderEntities) {
            // ????????????????????????
            if (propertyFinanceOrderEntity.getAssociatedType() == 1) {
                HouseEntity houseEntity = houseMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                if (houseEntity != null) {
                    propertyFinanceOrderEntity.setAddress(houseEntity.getBuilding() + houseEntity.getUnit() + houseEntity.getDoor());
                }
            } else if (propertyFinanceOrderEntity.getAssociatedType() == 2) {
                CarPositionEntity carPositionEntity = carPositionMapper.selectById(propertyFinanceOrderEntity.getTargetId());
                propertyFinanceOrderEntity.setAddress(carPositionEntity.getCarPosition());
            }
            // ??????????????????
            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(propertyFinanceOrderEntity.getFeeRuleId());
            if (propertyFeeRuleEntity != null) {
                propertyFinanceOrderEntity.setFeeRuleName(propertyFeeRuleEntity.getName());
            }
        }

        return propertyFinanceOrderEntities;
    }

    /**
     * ????????????ID???????????????????????????/????????????
     *
     * @param id ??????ID
     * @return FinanceOrderAndCarOrHouseInfoVO
     */
    @Override
    public FinanceOrderAndCarOrHouseInfoVO queryTemplateAndFinanceOrder(Long id) {
        //????????????
        FinanceOrderAndCarOrHouseInfoVO templateAndFinanceOrderVO = new FinanceOrderAndCarOrHouseInfoVO();
        PropertyFinanceOrderEntity propertyFinanceOrder = propertyFinanceOrderMapper.selectById(id);
        if (propertyFinanceOrder == null) {
            return templateAndFinanceOrderVO;
        }
        templateAndFinanceOrderVO.setFinanceOrder(propertyFinanceOrder);
        //????????????????????????????????????
        Long targetId = propertyFinanceOrder.getTargetId();
        Integer associatedType = propertyFinanceOrder.getAssociatedType();
        if (associatedType == null || targetId == null) {
            return templateAndFinanceOrderVO;
        }
        if (associatedType.equals(1)) {
            //??????????????????
            HouseEntity houseEntity = houseMapper.selectById(targetId);
            templateAndFinanceOrderVO.setHouseInfo(houseEntity);
        } else {
            //??????????????????
            CarPositionEntity carPositionEntity = carPositionMapper.selectById(targetId);
            templateAndFinanceOrderVO.setCarInfo(carPositionEntity);
        }
        //??????????????????
        if (propertyFinanceOrder.getFeeRuleId() != null) {
            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectById(propertyFinanceOrder.getFeeRuleId());
            if (propertyFeeRuleEntity != null) {
                templateAndFinanceOrderVO.setFeeName(propertyFeeRuleEntity.getName());
                templateAndFinanceOrderVO.setMonetaryUnit(propertyFeeRuleEntity.getMonetaryUnit());
            }
        }
        return templateAndFinanceOrderVO;
    }
}

