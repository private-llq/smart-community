package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarTemporaryOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.mapper.CarOrderMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarOrderQO;
import com.jsy.community.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService(version = Const.version, group = Const.group_property)
public class ICarTemporaryOrderServiceImpl extends ServiceImpl<CarOrderMapper, CarOrderEntity> implements ICarTemporaryOrderService {
    @Autowired
    private CarOrderMapper carOrderMapper;

    @Override
    public Page<CarOrderEntity> selectCarOrder(BaseQO<CarOrderQO> baseQO) {
        Page<CarOrderEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        if (baseQO.getQuery()==null){
            baseQO.setQuery(new CarOrderQO());
        }
        QueryWrapper<CarOrderEntity> queryWrapper = new QueryWrapper<CarOrderEntity>();
        CarOrderQO query = baseQO.getQuery();
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

        //时间段
        if (query.getBeginTime()!=null  && query.getOverTime()!=null){
            queryWrapper.ge("order_time",query.getBeginTime())
                        .le("order_time",query.getOverTime());
        }

        Page<CarOrderEntity> selectPage = carOrderMapper.selectPage(page, queryWrapper);
        List<CarOrderEntity> records = selectPage.getRecords();
        for (CarOrderEntity i: records) {
            if (i.getBeginTime()!=null  && i.getOverTime()!=null){
                HashMap<String, Long> datePoor = TimeUtils.getDatePoor(i.getBeginTime(), i.getOverTime());
                String s = datePoor.get("day")+"天："+datePoor.get("hour")+" 小时："+datePoor.get("min")+" 分钟";
                i.setStopCarTime(s);
            }
        }
        return selectPage;
    }

    @Override
    public Map<String, Object> selectMoney() {
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<CarOrderEntity> queryWrapper = new QueryWrapper<>();
        List<CarOrderEntity> selectList = carOrderMapper.selectList(queryWrapper.ge("create_time", LocalDate.now())
                .le("create_time", LocalDate.now().plusDays(1)));
        System.out.println(selectList);
        //今日订单数
        int size = selectList.size();
        System.out.println(size);

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


}
