package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.qo.proprietor.CarQO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 车辆 Mapper 接口
 * @author YuLF
 * @since 2020-11-10
 */
public interface CarMapper extends BaseMapper<CarEntity> {


    /**
     * 列表添加车辆信息方式
     * @param cars 业主车辆信息 列表
     * @param uid  业主id
     */
    void addProprietorCarForList(List<CarQO> cars, String uid);


    /**
     * 通过用户id查询用户车辆
     * @param userId        用户id
     * @return              返回车辆信息列表
     */
    @Select("select id,car_plate,car_type,driving_license_url from t_car where uid = #{userId} and deleted = 0")
    List<CarEntity> queryUserCarById(@Param("userId") String userId);


    /**
     * 手动通过uid和 车辆信息进行更新车辆
     * @param c             车辆信息
     * @param uid           用户id
     */
    void update(CarQO c, String uid);

    /**
     * @Description: 查询临时账单
     * @author: Hu
     * @since: 2021/10/26 15:53
     * @Param:
     * @return:
     */
    List<CarOrderEntity> getTemporaryOrder(@Param("communityId") Long communityId, @Param("uid") String userId);
}
