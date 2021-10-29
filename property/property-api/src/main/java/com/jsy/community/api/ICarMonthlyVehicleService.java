package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarMonthlyVehicle;
import com.jsy.community.qo.CarMonthlyDelayQO;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.property.OverdueVo;

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
    void delay(CarMonthlyDelayQO carMonthlyDelayQO);

    /**
     * 车辆
     * @param carMonthlyVehicleQO
     * @return
     */
    List<CarMonthlyVehicle> selectListCar(CarMonthlyVehicleQO carMonthlyVehicleQO);

    /**
     * 车位
     * @param carMonthlyVehicleQO
     * @return
     */
    List<CarMonthlyVehicle> selectListPostion (CarMonthlyVehicleQO carMonthlyVehicleQO);



    Map<String, Object> addLinkByExcel2(List<String[]> strings, Long communityId);

    void issue(String uid, Long adminCommunityId);

    OverdueVo MonthlyOverdue(String carNumber, Long adminCommunityId);

    Map selectByStatus(String carNumber, String carColor, Long community_id);

    /**
     * @Description: app绑定月租车辆
     * @author: Hu
     * @since: 2021/9/3 10:37
     * @Param:
     * @return:
     */
    void appMonth(CarMonthlyVehicle vehicle);

    /**
     * @Description: app修改月租车辆到期时间
     * @author: Hu
     * @since: 2021/9/3 11:26
     * @Param:
     * @return:
     */
    void updateMonth(String carPlate, LocalDateTime orveTime,BigDecimal money);

    PageInfo findByMultiConditionPage2Position(CarMonthlyVehicleQO carMonthlyVehicleQO,Long communityId);

    Integer SaveMonthlyVehicle2Position(CarMonthlyVehicle carMonthlyVehicle, Long adminCommunityId);

    Map<String, Object> addLinkByExcel2Position(List<String[]> strings, Long communityId);

}