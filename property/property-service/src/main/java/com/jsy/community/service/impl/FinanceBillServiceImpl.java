package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IFinanceBillService;
import com.jsy.community.api.IUserImService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.utils.PushInfoUtil;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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
    private PropertyFeeRuleRelevanceMapper propertyFeeRuleRelevanceMapper;

    @Autowired
    private PropertyUserHouseMapper propertyUserHouseMapper;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserImService userImService;

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private CarPositionMapper carPositionMapper;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService userInfoRpcService;

    @DubboReference(version = com.zhsj.im.chat.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.im.chat.api.constant.RpcConst.Rpc.Group.GROUP_IM_CHAT, check=false)
    private IImChatPublicPushRpcService iImChatPublicPushRpcService;




    /**
     * @Description: 更新所有按月生成的周期账单
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void updateMonth() {
        //上月個的天数
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, LocalDate.now().getYear());
        cal.set(Calendar.MONTH, LocalDate.now().minusMonths(1).getMonthValue() - 1);
        int dateOfMonth = cal.getActualMaximum(Calendar.DATE);

        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity = null;
        //查询所有小区收费类型为周期  收费周期为按月的收费项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("bill_day", LocalDateTime.now()
                        .getDayOfMonth())
                .eq("disposable", 2)
                .eq("period", 1));
        if (feeRuleEntities.size() != 0) {
            for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
                //生成上月账单
                LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue() - 1);
                //获取当前缴费项目关联的房间或者车位id集合
                List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
                if (ruleList.size()!=0){
                    CommunityEntity communityEntity = communityMapper.selectById(feeRuleEntity.getCommunityId());
                    //relevanceType等于1表示关联的是房屋，2表示关联的是车位
                    if (feeRuleEntity.getRelevanceType() == 1) {
                        //查询所有缴费项目关联的房间
                        List<HouseEntity> house = houseMapper.selectInIds(ruleList);
                        for (HouseEntity houseEntity : house) {
                            Thread.sleep(1);
                            entity = new PropertyFinanceOrderEntity();
                            entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                            entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                            entity.setType(feeRuleEntity.getType());
                            entity.setRise(communityEntity.getName()+"-"+feeRuleEntity.getName());
                            entity.setFeeRuleId(feeRuleEntity.getId());
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setAssociatedType(1);
                            entity.setUid(houseEntity.getUid());
                            entity.setTargetId(houseEntity.getId());
                            //单价乘建筑面积乘周期
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(houseEntity.getBuildArea())).multiply(new BigDecimal(dateOfMonth)));
                            entity.setId(SnowFlake.nextId());
                            orderList.add(entity);
                        }
                    } else {
                        //查询当前收费项目关联的车位
                        List<CarPositionEntity> entityList = carPositionMapper.selectBatchIds(ruleList);
                        for (CarPositionEntity positionEntity : entityList) {
                            if (LocalDateTime.now().isAfter(positionEntity.getEndTime())) {
                                Thread.sleep(1);
                                entity = new PropertyFinanceOrderEntity();
                                entity.setRise(communityEntity.getName()+"-"+feeRuleEntity.getName());
                                entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                                entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                                entity.setType(feeRuleEntity.getType());
                                entity.setFeeRuleId(feeRuleEntity.getId());
                                entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                                entity.setCommunityId(feeRuleEntity.getCommunityId());
                                entity.setOrderTime(LocalDate.now());
                                entity.setAssociatedType(2);
                                entity.setUid(positionEntity.getUid());
                                entity.setTargetId(positionEntity.getId());
                                //单价乘周期
                                entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(dateOfMonth)));
                                entity.setId(SnowFlake.nextId());
                                orderList.add(entity);
                            }
                        }
                    }
                }

            }
        }
        if (orderList!=null&&orderList.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(orderList);
        }

    }

    /**
     * @Description: 更新所有按年生成的周期账单
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    @SneakyThrows
    public void updateAnnual() {
        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity = null;
        //查询今天所有需要年度收费的项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("bill_month", LocalDate.now().getMonthValue())
                .eq("bill_day", LocalDateTime.now().getDayOfMonth())
                .eq("period", 4));
        if (feeRuleEntities.size() != 0) {
            for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
                //获取当前缴费项目关联的房间或者车位id集合
                List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
                if (ruleList.size()!=0){
                    //查询所有未空置的房间生成账单
                    List<HouseEntity> list = houseMapper.selectInIds(ruleList);
                    //查询小区
                    CommunityEntity communityEntity = communityMapper.selectById(feeRuleEntity.getCommunityId());
                    for (HouseEntity houseEntity : list) {
                        Thread.sleep(1);
                        entity = new PropertyFinanceOrderEntity();
                        //去年第一天
                        entity.setBeginTime(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
                        //去年最后一天
                        entity.setOverTime(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()));
                        entity.setType(feeRuleEntity.getType());
                        entity.setFeeRuleId(feeRuleEntity.getId());
                        entity.setRise(communityEntity.getName()+"-"+feeRuleEntity.getName());
                        entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                        entity.setCommunityId(feeRuleEntity.getCommunityId());
                        entity.setOrderTime(LocalDate.now());
                        entity.setAssociatedType(1);
                        entity.setUid(houseEntity.getUid());
                        entity.setTargetId(houseEntity.getId());
                        entity.setPropertyFee(feeRuleEntity.getMonetaryUnit());
                        entity.setId(SnowFlake.nextId());
                        orderList.add(entity);
                    }
                }
            }
        }
        if (orderList!=null&&orderList.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(orderList);
        }

    }

    /**
     * @Description: 推送按月缴费信息
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    public void pushMonth() {
        LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue() - 1);
        Set<String> uid = new HashSet<>();
        //查询今天所有需要推送消息的项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("push_day", LocalDateTime.now().getDayOfMonth())
                .eq("period", 1)
                .eq("push_status",1));
        for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
            List<PropertyFinanceOrderEntity> list = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>()
                    .eq("fee_rule_id", feeRuleEntity.getId())
                    .ge("begin_time", LocalDate.of(date.getYear(), date.getMonthValue(), 1))
                    .lt("over_time", LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1)));

            for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
                uid.add(propertyFinanceOrderEntity.getUid());
            }
        }
        //消息推送
        if (uid.size()!=0){
            List<UserImVo> userImVos = userInfoRpcService.batchGetEHomeUserIm(uid);
            for (UserImVo userIMEntity : userImVos) {
                PushInfoUtil.PushPublicTextMsg(iImChatPublicPushRpcService,userIMEntity.getImId(),
                        "账单通知",
                        "上月账单出来了，及时缴费哦！",
                        null,
                        "尊敬的用户您好，您的"+LocalDate.now().minusMonths(1L).getDayOfMonth()+"已出炉，请前往生活缴费进行处理。",
                        null,
                        BusinessEnum.PushInfromEnum.BILLINGNOTICE.getName());
            }
        }
    }

    /**
     * @Description: 推送按年缴费信息
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    public void pushAnnual() {
        LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue() - 1);
        Set<String> uid = new HashSet<>();
        //查询今天所有需要推送消息的项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("bill_month", LocalDate.now().getMonthValue())
                .eq("push_day", LocalDateTime.now().getDayOfMonth())
                .eq("period", 4)
                .eq("push_status",1));
        for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
            List<PropertyFinanceOrderEntity> list = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>()
                    .eq("fee_rule_id", feeRuleEntity.getId())
                    .ge("begin_time", LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))
                    .lt("over_time", LocalDate.of(LocalDate.now().plusYears(1).getYear(),1, 1)));

            for (PropertyFinanceOrderEntity propertyFinanceOrderEntity : list) {
                uid.add(propertyFinanceOrderEntity.getUid());
            }
        }
        //消息推送
        if (uid.size()!=0){
            List<UserImVo> userImVos = userInfoRpcService.batchGetEHomeUserIm(uid);
            for (UserImVo userIMEntity : userImVos) {
                PushInfoUtil.PushPublicTextMsg(iImChatPublicPushRpcService,userIMEntity.getImId(),
                        "账单通知",
                        "上月账单出来了，及时缴费哦！",
                        null,
                        "尊敬的用户您好，您的"+LocalDate.now().minusMonths(1L).getDayOfMonth()+"已出炉，请前往生活缴费进行处理。",
                        null,
                        BusinessEnum.PushInfromEnum.BILLINGNOTICE.getName());
            }
        }
    }


    /**
     * @Description: 更新所有临时的账单   临时账单只更新一次  更新完成过后就把收费项目的状态改为未启动或者删除临时项目
     * @author: Hu
     * @since: 2021/5/21 11:05
     * @Param: []
     * @return: void
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void updateTemporary(){
        List<PropertyFinanceOrderEntity> orderList = new LinkedList<>();
        PropertyFinanceOrderEntity entity=null;
        Set<String> uidAll = new HashSet<>();
        //查询当前天所要生成订单的缴费项目
        List<PropertyFeeRuleEntity> feeRuleEntities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("disposable",1));
        if (feeRuleEntities.size()!=0){
            for (PropertyFeeRuleEntity feeRuleEntity : feeRuleEntities) {
                LocalDate date = LocalDate.now().withMonth(LocalDate.now().getMonthValue()-1);
                //获取当前缴费项目关联的房间或者车位id集合
                List<String> ruleList = propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleEntity.getId());
                if (ruleList.size()!=0){
                    //查询收费项目关联的所有房屋
                    List<HouseEntity> list=houseMapper.selectInIds(ruleList);
                    CommunityEntity communityEntity = communityMapper.selectById(feeRuleEntity.getCommunityId());
                    //装修管理费
                    if (feeRuleEntity.getType()==1){
                        for (HouseEntity positionEntity : list) {
                            Thread.sleep(1);
                            entity = new PropertyFinanceOrderEntity();
                            entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                            entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                            entity.setType(feeRuleEntity.getType());
                            entity.setRise(communityEntity.getName()+"-"+feeRuleEntity.getName());
                            entity.setFeeRuleId(feeRuleEntity.getId());
                            entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                            entity.setCommunityId(feeRuleEntity.getCommunityId());
                            entity.setOrderTime(LocalDate.now());
                            entity.setAssociatedType(2);
                            entity.setUid(positionEntity.getUid());
                            entity.setTargetId(positionEntity.getId());
                            entity.setPropertyFee(feeRuleEntity.getMonetaryUnit().multiply(new BigDecimal(positionEntity.getBuildArea())));
                            entity.setId(SnowFlake.nextId());
                            if (feeRuleEntity.getPushStatus()==1){
                                uidAll.add(positionEntity.getUid());
                            }
                            orderList.add(entity);
                        }
                    } else {
                        if (feeRuleEntity.getType()==9||feeRuleEntity.getType()==10){
                            for (HouseEntity houseEntity : list) {
                                Thread.sleep(1);
                                entity = new PropertyFinanceOrderEntity();
                                entity.setBeginTime(LocalDate.of(date.getYear(), date.getMonthValue(), 1));
                                entity.setOverTime(date.with(TemporalAdjusters.lastDayOfMonth()));
                                entity.setType(feeRuleEntity.getType());
                                entity.setFeeRuleId(feeRuleEntity.getId());
                                entity.setOrderNum(getOrderNum(String.valueOf(feeRuleEntity.getCommunityId())));
                                entity.setCommunityId(feeRuleEntity.getCommunityId());
                                entity.setOrderTime(LocalDate.now());
                                entity.setAssociatedType(2);
                                entity.setUid(houseEntity.getUid());
                                entity.setRise(communityEntity.getName()+"-"+feeRuleEntity.getName());
                                entity.setTargetId(houseEntity.getId());
                                entity.setPropertyFee(feeRuleEntity.getMonetaryUnit());
                                entity.setId(SnowFlake.nextId());
                                if (feeRuleEntity.getPushStatus()==1){
                                    uidAll.add(houseEntity.getUid());
                                }
                                orderList.add(entity);
                            }

                        }
                    }
                }
                //修改收费项目启用状态
                feeRuleEntity.setStatus(0);
                propertyFeeRuleMapper.updateById(feeRuleEntity);
            }
        }

        if (orderList!=null&&orderList.size()!=0){
            //把封装好的list批量新增到数据库订单表
            propertyFinanceOrderMapper.saveList(orderList);
        }
        //消息推送
        if (uidAll.size()!=0){
            List<UserImVo> userImVos = userInfoRpcService.batchGetEHomeUserIm(uidAll);
            for (UserImVo userIMEntity : userImVos) {
                PushInfoUtil.PushPublicTextMsg(iImChatPublicPushRpcService,userIMEntity.getImId(),
                        "账单通知",
                        "上月账单出来了，及时缴费哦！",
                        null,
                        "尊敬的用户您好，您的"+LocalDate.now().minusMonths(1L).getDayOfMonth()+"已出炉，请前往生活缴费进行处理。",
                        null,
                        BusinessEnum.PushInfromEnum.BILLINGNOTICE.getName());
            }
        }

    }




    /**
     * @Description: 更新小区账单的违约金
     * @author: Hu
     * @since: 2021/5/21 11:03
     * @Param: []
     * @return: void
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePenalSum(){
        Map<Long, PropertyFeeRuleEntity> map = new HashMap<>();
        //查询所有缴费项目封装到map里面
        List<PropertyFeeRuleEntity> ruleEntities = propertyFeeRuleMapper.selectList(null);
        for (PropertyFeeRuleEntity ruleEntity : ruleEntities) {
            map.put(ruleEntity.getId(),ruleEntity);
        }
        //查詢所有未缴费的订单
        List<PropertyFinanceOrderEntity> list = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>().eq("order_status", 0));
        if (list.size()!=0){
            for (PropertyFinanceOrderEntity entity : list) {
                //如果超过违约天数还未缴就生成违约金
                if (entity.getOrderTime().plusDays(map.get(entity.getFeeRuleId()).getPenalDays()).isBefore(LocalDate.now())) {
                    entity.setPenalSum(entity.getPenalSum().add(entity.getPropertyFee().multiply(map.get(entity.getFeeRuleId()).getPenalSum())));
                    entity.setTotalMoney(entity.getPropertyFee().add(entity.getPenalSum()));
                    propertyFinanceOrderMapper.updateById(entity);
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
    public static String getOrderNum(String communityId){
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
        long millis = System.currentTimeMillis();
        str.append(millis);
        int s1=(int) (Math.random() * 99);
        str.append(s1);
        return str.toString();
    }


}
