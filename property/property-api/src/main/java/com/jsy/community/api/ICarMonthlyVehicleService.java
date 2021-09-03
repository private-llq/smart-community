package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarMonthlyVehicle;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.utils.PageInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface ICarMonthlyVehicleService extends IService<CarMonthlyVehicle> {


    Integer SaveMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle, Long communityId);

    Integer UpdateMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle);

    Integer DelMonthlyVehicle(String uid);

    PageInfo FindByMultiConditionPage(CarMonthlyVehicleQO carMonthlyVehicleQO, Long communityId);

    /**
     * 延期 0 按天  1 按月
     */
    void delay(String uid, Integer type, Integer dayNum, BigDecimal fee);


    void monthlyChange(String uid, Integer type);

    Map<String, Object> addLinkByExcel(List<String[]> strings, Long communityId);

    List<CarMonthlyVehicle> selectList(Long communityId);

    Map<String, Object> addLinkByExcel2(List<String[]> strings, Long communityId);

    void issue(String uid, Long adminCommunityId);

    Map selectByStatus(String carNumber, String carColor, Long community_id);

}