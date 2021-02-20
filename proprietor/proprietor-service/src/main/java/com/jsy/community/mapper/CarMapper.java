package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CarEntity;
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
     * @param carEntityList 业主车辆信息 列表
     */
    void addProprietorCarForList(List<CarEntity> carEntityList);


    /**
     * 通过用户id查询用户车辆
     * @param userId        用户id
     * @return              返回车辆信息列表
     */
    @Select("select id,car_plate,car_type,car_image_url from t_car where uid = #{userId} and deleted = 0")
    List<CarEntity> queryUserCarById(@Param("userId") String userId);


}
