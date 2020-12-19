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
     * @return              返回影响行数
     */
    Integer addProprietorCar(List<CarEntity> carEntityList);



    @Select("select id,car_plate,car_type,car_image_url from t_car where uid = #{userId}")
    List<CarEntity> queryUserCarById(@Param("userId") String userId);


    /**
     * 批量更新车辆信息
     * @param carEntityList   更新参数对象，只会取需要的字段
     */
    void updateProprietorCarBatch(@Param("carList") List<CarEntity> carEntityList,@Param("uid") String uid);
}
