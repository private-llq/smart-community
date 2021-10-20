package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarTemporaryOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.mapper.CarOrderMapper;
import com.jsy.community.mapper.CarPositionMapper;
import com.jsy.community.mapper.PropertyFinanceOrderMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.OrderQO;
import com.jsy.community.qo.property.CarOrderQO;
import com.jsy.community.qo.property.CarTemporaryOrderQO;
import com.jsy.community.qo.property.CarTemporaryQO;
import com.jsy.community.util.TimeUtils;
import com.jsy.community.vo.SelectMoney3Vo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@DubboService(version = Const.version, group = Const.group_property)
public class ICarTemporaryOrderServiceImpl extends ServiceImpl<CarOrderMapper, CarOrderEntity> implements ICarTemporaryOrderService {
    @Autowired
    private CarOrderMapper carOrderMapper;
    @Autowired
    private CarPositionMapper positionMapper;

    @Autowired
    private PropertyFinanceOrderMapper propertyFinanceOrderMapper;


   /**
    * @Description: 订单管理查询
    * @Param: [baseQO, communityId]
    * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CarOrderEntity>
    * @Author: Tian
    * @Date: 2021/9/13-16:46
    **/
   @Override
    public Page<CarOrderEntity>   selectCarOrder(BaseQO<CarOrderQO> baseQO, Long communityId) {
        Page<CarOrderEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        if (baseQO.getQuery()==null){
            baseQO.setQuery(new CarOrderQO());
        }
        QueryWrapper<CarOrderEntity> queryWrapper = new QueryWrapper<CarOrderEntity>();
        CarOrderQO query = baseQO.getQuery();

        //临时车  1和 2  包月车
        if (query.getType()!=null){
            queryWrapper.eq("type", query.getType());
        }

       if (StringUtils.isNotBlank(query.getCarPlate())){
           if (query.getType()==1){
               //临时车根据车牌号
               queryWrapper.like("car_plate", query.getCarPlate());
           }else {
               //包月车根据车位id
               CarPositionEntity car_position = positionMapper.selectOne(new QueryWrapper<CarPositionEntity>().like("car_position", query.getCarPlate()));
               if (car_position!=null){
                   queryWrapper.like("car_position_id", car_position.getId());
               }
           }
       }

        //支付类型 1已支付  2未支付
        if (query.getOrderStatus()!=null){
            queryWrapper.eq("order_status",query.getOrderStatus());
        }

        //时间段
        if (query.getBeginTime()!=null  && query.getOverTime()!=null){
            queryWrapper.ge("order_time",query.getBeginTime())
                        .le("order_time",query.getOverTime());
        }
        queryWrapper.eq("community_id",communityId);
        Page<CarOrderEntity> selectPage = carOrderMapper.selectPage(page, queryWrapper);

        List<CarOrderEntity> records = selectPage.getRecords();
        for (CarOrderEntity i: records) {
            String rise = i.getRise();
            int indexOf = rise.indexOf("车");
            i.setStopCarTime(rise.substring(indexOf+1));
//            System.out.println("停车时长\n\n"+i.getStopCarTime());
//            if (i.getBeginTime()!=null  && i.getOverTime()!=null){
//                HashMap<String, Long> datePoor = TimeUtils.getDatePoor(i.getBeginTime(), i.getOverTime());
//                String s = datePoor.get("day")+"天："+datePoor.get("hour")+" 小时："+datePoor.get("min")+" 分钟";
//                i.setStopCarTime(s);
//            }
//            System.out.println(i.getCarPositionId());
            if (i.getCarPositionId()!=null){
                CarPositionEntity entity = positionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("id", i.getCarPositionId()));
                i.setCarPositionText(entity.getCarPosition());
            }

        }
        return selectPage;
    }

  /**
   * @Description: 查询今日订单数
   * @Param: [communityId]
   * @Return: java.util.Map<java.lang.String,java.lang.Object>
   * @Author: Tian
   * @Date: 2021/9/13-16:47
   **/
  @Override
    public Map<String, Object> selectMoney(Long communityId) {
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<CarOrderEntity> queryWrapper = new QueryWrapper<>();
        List<CarOrderEntity> selectList = carOrderMapper.selectList(queryWrapper.ge("create_time", LocalDate.now())
                .le("create_time", LocalDate.now().plusDays(1)));
        System.out.println(selectList);
        //今日订单数
        int size = selectList.size();
        System.out.println(size);
        queryWrapper.eq("community_id",communityId);
        BigDecimal money = new BigDecimal(0);
        List<CarOrderEntity> list = carOrderMapper.selectList(queryWrapper.ge("order_time", LocalDate.now())
                .le("order_time", LocalDate.now().plusDays(1))
                .eq("order_status",1)//已支付
                .eq("pay_type",1)//线上支付
           );//

        for (CarOrderEntity i:list) {
            BigDecimal decimal = i.getMoney();
            System.out.println(decimal);
           money = money.add(decimal);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("total",size);
        map.put("money",money);

        return map;
    }

    /**
     * @Description:导出月租订单
     * @Param: [query, communityId]
     * @Return: java.util.List<com.jsy.community.qo.property.CarTemporaryOrderQO>
     * @Author: Tian
     * @Date: 2021/9/10-9:46
     **/
    @Override
    public List<CarTemporaryOrderQO> selectCarOrderList(CarOrderQO query, Long communityId) {
        QueryWrapper<CarOrderEntity> queryWrapper = new QueryWrapper<CarOrderEntity>();
        if (StringUtils.isNotBlank(query.getCarPlate())){
            if (query.getType()==1){
                //临时车根据车牌号
                queryWrapper.like("car_plate", query.getCarPlate());
            }else {
                //包月车根据车位id
                CarPositionEntity car_position = positionMapper.selectOne(new QueryWrapper<CarPositionEntity>().like("car_position", query.getCarPlate()));
                if (car_position!=null){
                    queryWrapper.like("car_position_id", car_position.getId());
                }
            }
        }
        //临时车  1和 2  包月车
        if (query.getType()!=null){
            queryWrapper.eq("type", query.getType());
        }
        //支付类型 1已支付  2未支付
        if (query.getOrderStatus()!=null){
            queryWrapper.eq("order_status",query.getOrderStatus());
        }
        queryWrapper.eq("community_id",communityId);

        //时间段
        if (query.getBeginTime()!=null  && query.getOverTime()!=null){
            queryWrapper.ge("order_time",query.getBeginTime())
                    .le("order_time",query.getOverTime());
        }
        List<CarTemporaryOrderQO> orderQOS = new ArrayList<>();
        List<CarOrderEntity> list = carOrderMapper.selectList(queryWrapper);
        Date date;
        for (CarOrderEntity i: list) {
            CarTemporaryOrderQO carTemporaryOrderQO = new CarTemporaryOrderQO();
            BeanUtils.copyProperties(i,carTemporaryOrderQO);
            if (i.getBeginTime()!=null){
                date = Date.from(i.getBeginTime().atZone(ZoneId.systemDefault()).toInstant());
                carTemporaryOrderQO.setBeginTime(date);
            }

            if (i.getOverTime()!=null){
                date = Date.from(i.getOverTime().atZone(ZoneId.systemDefault()).toInstant());
                carTemporaryOrderQO.setOverTime(date);
            }

            if (i.getOrderTime()!=null){

                date = Date.from(i.getOrderTime().atZone(ZoneId.systemDefault()).toInstant());
                carTemporaryOrderQO.setOrderTime(date);
            }
            if (i.getCarPositionId()!=null){
                CarPositionEntity entity = positionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("id", i.getCarPositionId()));
                carTemporaryOrderQO.setCarPosition(entity.getCarPosition());
            }
            orderQOS.add(carTemporaryOrderQO);
        }
        return orderQOS;
    }

   /**
    * @Description: 导出临时车
    * @Param: [query, communityId]
    * @Return: java.util.List<com.jsy.community.qo.property.CarTemporaryQO>
    * @Author: Tian
    * @Date: 2021/9/10-9:46
    **/ @Override
    public List<CarTemporaryQO> selectTemporaryQOList(CarOrderQO query, Long communityId) {
        QueryWrapper<CarOrderEntity> queryWrapper = new QueryWrapper<CarOrderEntity>();
        if (StringUtils.isNotBlank(query.getCarPlate())){
            //车牌
            queryWrapper.like("car_plate", query.getCarPlate());
        }
        //临时车  1和 2  包月车
        if (query.getType()!=null){
            queryWrapper.eq("type", query.getType());
        }
        //支付类型 1已支付  2未支付
        if (query.getOrderStatus()!=null){
            queryWrapper.eq("order_status",query.getOrderStatus());
        }
        queryWrapper.eq("community_id",communityId);

        //时间段
        if (query.getBeginTime()!=null  && query.getOverTime()!=null){
            queryWrapper.ge("order_time",query.getBeginTime())
                    .le("order_time",query.getOverTime());
        }
        List<CarTemporaryQO> orderQOS = new ArrayList<>();
        List<CarOrderEntity> list = carOrderMapper.selectList(queryWrapper);
        Date date;
        for (CarOrderEntity i: list) {
            CarTemporaryQO carTemporaryQO = new CarTemporaryQO();
            BeanUtils.copyProperties(i,carTemporaryQO);
            if (i.getBeginTime()!=null){
                date = Date.from(i.getBeginTime().atZone(ZoneId.systemDefault()).toInstant());
                carTemporaryQO.setBeginTime(date);
            }

            if (i.getOverTime()!=null){
                date = Date.from(i.getOverTime().atZone(ZoneId.systemDefault()).toInstant());
                carTemporaryQO.setOverTime(date);
            }

            if (i.getOrderTime()!=null){
                date = Date.from(i.getOrderTime().atZone(ZoneId.systemDefault()).toInstant());
                carTemporaryQO.setOrderTime(date);
            }
            if (i.getOverTime()!=null  && i.getBeginTime()!=null){
                HashMap<String, Long> datePoor = TimeUtils.getDatePoor(i.getBeginTime(), i.getOverTime());
                String s = datePoor.get("day")+"天："+datePoor.get("hour")+" 小时："+datePoor.get("min")+" 分钟";
                carTemporaryQO.setStopCarTime(s);
            }
            orderQOS.add(carTemporaryQO);
        }
        return orderQOS;
    }


    /**
     * 简单模式
     * 1：今日 2：本月
     * @return
     */
    @Override
    public List<Map<String, BigDecimal>> selectMoney2(Long adminCommunityId) {


            ArrayList<Map<String, BigDecimal>> list = new ArrayList<>();

            LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime today_end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            OrderQO orderQO1 = new OrderQO();
            orderQO1.setStartTime(today_start);
            orderQO1.setEndTime(today_end);
            orderQO1.setCommunityId(adminCommunityId);
            orderQO1.setType(1);
            list.add(this.sum(orderQO1)) ;

            OrderQO orderQO2 = new OrderQO();
            LocalDateTime month_start = LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN);
            LocalDateTime month_end = LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX);
            orderQO2.setStartTime(month_start);
            orderQO2.setEndTime(month_end);
            orderQO2.setCommunityId(adminCommunityId);
            orderQO2.setType(2);

            list.add(this.sum(orderQO2));
        return list;
    }

    /**
     * 图表模式 1：最近5天  2：最近10天  3：本月
     * @param orderQO
     * @return
     */
    @Override
    public List<SelectMoney3Vo> selectMoney3(OrderQO orderQO) {
        if (orderQO.getType()==1){//最近5天
            ArrayList<SelectMoney3Vo> list = new ArrayList<>();
            for (int i = 0; i >-5; i--) {
                LocalDateTime today_start = LocalDateTime.of(LocalDate.now().plusDays(i), LocalTime.MIN);//每一天的开始时间
                LocalDateTime today_end = LocalDateTime.of(LocalDate.now().plusDays(i), LocalTime.MAX);//每一天的结束时间
                LocalDateTime day = LocalDateTime.now().plusDays(i);//每一天的 当天日期
                String localDay= day.getMonthValue()+"-"+day.getDayOfMonth();

                //计算临时和包月金额
                orderQO.setStartTime(today_start);
                orderQO.setEndTime(today_end);
                Map<String, BigDecimal> map = this.sum(orderQO);


                //赋值
                SelectMoney3Vo selectMoney3Vo = new SelectMoney3Vo();
                selectMoney3Vo.setDay(localDay);
                selectMoney3Vo.setMoney1(map.get("1"));
                selectMoney3Vo.setMoney2(map.get("2"));

                list.add(selectMoney3Vo);
            }
            Collections.reverse(list);
            return list;

        }
        if (orderQO.getType()==2){//最近10天
            ArrayList<SelectMoney3Vo> list = new ArrayList<>();
            for (int i = 0; i >-10; i--) {
                LocalDateTime today_start = LocalDateTime.of(LocalDate.now().plusDays(i), LocalTime.MIN);//每一天的开始时间
                LocalDateTime today_end = LocalDateTime.of(LocalDate.now().plusDays(i), LocalTime.MAX);//每一天的结束时间
                LocalDateTime day = LocalDateTime.now().plusDays(i);//每一天的 当天日期
                String localDay= day.getMonthValue()+"-"+day.getDayOfMonth();

                //计算临时和包月金额
                orderQO.setStartTime(today_start);
                orderQO.setEndTime(today_end);
                Map<String, BigDecimal> map = this.sum(orderQO);


                //赋值
                SelectMoney3Vo selectMoney3Vo = new SelectMoney3Vo();
                selectMoney3Vo.setDay(localDay);
                selectMoney3Vo.setMoney1(map.get("1"));
                selectMoney3Vo.setMoney2(map.get("2"));

                list.add(selectMoney3Vo);
            }
            Collections.reverse(list);
            return list;

        }
        if (orderQO.getType()==3){//本月
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//本月最大天数

            ArrayList<SelectMoney3Vo> list = new ArrayList<>();
            for (int i = 0; i <=maxDays-1; i++) {
                LocalDateTime today_start = LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())).plusDays(i), LocalTime.MIN);//本月第一天开始时间
                LocalDateTime today_end = LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())).plusDays(i), LocalTime.MAX);//本月第一天结束时间


                LocalDateTime day = LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())).plusDays(i), LocalTime.NOON);//从每月第一天开始计算日期
                String localDay= day.getMonthValue()+"-"+day.getDayOfMonth();

                //计算临时和包月金额
                orderQO.setStartTime(today_start);
                orderQO.setEndTime(today_end);
                Map<String, BigDecimal> map = this.sum(orderQO);


                //赋值
                SelectMoney3Vo selectMoney3Vo = new SelectMoney3Vo();
                selectMoney3Vo.setDay(localDay);
                selectMoney3Vo.setMoney1(map.get("1"));
                selectMoney3Vo.setMoney2(map.get("2"));

                list.add(selectMoney3Vo);
            }
            return list;

        }

        return null;
    }

    private Map<String,BigDecimal> sum(OrderQO orderQO){
        //临时查订单
        List<CarOrderEntity> carOrderEntities = carOrderMapper.selectList(new QueryWrapper<CarOrderEntity>()
                .eq("deleted", 0)//未删除的订单
                .eq("community_id", orderQO.getCommunityId())//社区id
                .eq("type", 1)//临时停车
                .eq("order_status", 1)//已支付
                .ge("order_time",orderQO.getStartTime())
                .le("order_time",orderQO.getEndTime())
        );
        BigDecimal sumMoney = carOrderEntities.stream().map(CarOrderEntity::getMoney).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        //包月查账单
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = propertyFinanceOrderMapper.selectList(new QueryWrapper<PropertyFinanceOrderEntity>()
                .eq("community_id", orderQO.getCommunityId())
                .eq("type", 12)
                .eq("order_status", 1)
                .eq("deleted", 0)
                .ge("pay_time", orderQO.getStartTime())
                .le("pay_time", orderQO.getEndTime())

        );
        BigDecimal sumFee = propertyFinanceOrderEntities.stream().map(PropertyFinanceOrderEntity::getPropertyFee).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        HashMap<String, BigDecimal> hashMap = new HashMap<>();
        hashMap.put("1",sumMoney);//临时
        hashMap.put("2",sumFee);//包月
        return hashMap;
    }



}
