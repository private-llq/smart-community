package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarBasicsEntity;
import com.jsy.community.qo.property.CarBasicsMonthQO;
import com.jsy.community.qo.property.CarBasicsRuleQO;


public interface ICarBasicsService extends IService<CarBasicsEntity> {

    /**
     * @Description: 添加或修改临时车规则
     * @Param: [carBasicsRuleQO]
     * @Return: void
     * @Author: Tian
     * @Date: 2021/8/3-15:59
     **/
    boolean addBasics(CarBasicsRuleQO carBasicsRuleQO, String uid, Long communityId);
    //创建社区默认新增一条数据
    boolean addBasics2(String uid, Long communityId);
    /**
     * @Description: 查找当前社区的车禁设置
     * @Param: [communityId]
     * @Return: com.jsy.community.entity.property.CarBasicsEntity
     * @Author: Tian
     * @Date: 2021/8/4-15:39
     **/
    CarBasicsEntity findOne(Long communityId);

    /**
     * @Description: 添加或修改特殊车辆收费设置
     * @Param: [exceptionCar]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/4-16:05
     **/
    boolean addExceptionCar(Integer exceptionCar, String uid,Long communityId);

    /**
     * @Description: 添加或者修改包月选项
     * @Param: [carBasicsMonthQO, userId, adminCommunityId]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/5-14:38
     **/
    boolean addMonthlyPayment(CarBasicsMonthQO carBasicsMonthQO, String userId, Long adminCommunityId);
}
