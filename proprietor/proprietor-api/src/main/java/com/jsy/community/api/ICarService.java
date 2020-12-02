package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CarQO;

import java.util.List;
import java.util.Map;

/**
 * 车辆 服务提供类
 * @author YuLF
 * @since 2020-11-10
 */
public interface ICarService extends IService<CarEntity> {


    /**
     * 查询所属人车辆分页方法
     * @param param   车辆分页条件查询对象
     * @return        返回当前页数据
     */
    Page<CarEntity> queryProprietorCar(BaseQO<CarEntity> param);

    /**
     * 通过车辆信息更新车辆方法
     * @param carEntity 车辆对象实体
     * @return          返回修改影响行数
     */
    Integer updateProprietorCar(CarQO carEntity,Long uid);

    /**
     * 通过车辆id逻辑删除车辆方法
     * @param params    参数列表
     * @return          返回修改影响行数
     */
    Integer deleteProprietorCar(Map<String, Object> params);


    /**
     * 新增车辆方法
     * @param carEntity 车辆参数对象
     * @return          返回插入影响行数
     */
    Integer addProprietorCar(CarEntity carEntity);


    /**
     * 根据车牌条件查询某一记录数
     * @param carPlate  车牌
     * @return          返回车辆是否存在布尔值
     */
    Boolean carIsExist(String carPlate);


    /**
     * 业主登记时，调用车辆登记接口登记业主的车辆
     * @param carEntityList   业主车辆信息 列表
     * @return                返回sql插入影响行数
     */
    Integer addProprietorCar(List<CarEntity> carEntityList);
}
