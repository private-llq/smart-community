package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.CarEntity;
import com.baomidou.mybatisplus.extension.service.IService;

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
    Page<CarEntity> queryProprietorCar(Map<String,Object> param);

    /**
     * 通过车辆信息更新车辆方法
     * @param carEntity 车辆对象实体
     * @return          返回修改影响行数
     */
    Integer updateProprietorCar(CarEntity carEntity);

    /**
     * 通过车辆id逻辑删除车辆方法
     * @param id    车辆id
     * @return      返回修改影响行数
     */
    Integer deleteProprietorCar(long id);


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
     * 车辆图片文件上传服务提供接口
     * @param carImage 图片文件流
     * @param fileName 文件名称
     * @return         上传成功将放回图片URL，否则返回Null
     */
    String carImageUpload(byte[] carImage, String fileName);
}
