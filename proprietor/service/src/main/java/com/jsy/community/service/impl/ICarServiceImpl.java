package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.mapper.CarMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;
import java.util.Map;

/**
 * 车辆 服务实现类
 * @author YuLF
 * @since 2020-11-10
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group)
public class ICarServiceImpl extends ServiceImpl<CarMapper, CarEntity> implements ICarService {

    @Resource
    private CarMapper carMapper;

    /**
     * 根据提供的参数对车辆进行分页查询
     * @param param  车辆分页条件查询参数
     * @return       返回当前页数据
     */
    @Override
    public Page<CarEntity> queryProprietorCar(Map<String,Object> param) {
        QueryWrapper<CarEntity> wrapper = new QueryWrapper<>();
        Page<CarEntity> page = new Page<>();
        page.setCurrent((long) param.get("page"));
        page.setSize((long) param.get("pageSize"));
        wrapper.eq("uid", param.get("uid"));
        wrapper.eq("check_status", param.get("checkStatus"));
        wrapper.eq("deleted", 0);
        Page<CarEntity> page1 = page(page, wrapper);
        log.info("查询所属人车辆满足条件行数："+page1.getTotal() + "每页显示条数："+page1.getSize());
        return page1;
    }

    /**
     * 根据实体类字段 进行更新
     * @param carEntity 车辆修改参数实体对象
     * @return           返回修改影响行数
     */
    @Override
    public Integer updateProprietorCar(CarEntity carEntity) {
        return carMapper.update(carEntity, new UpdateWrapper<CarEntity>().eq("id",carEntity.getId()));
    }

    /**
     * 根据车辆id 进行逻辑删除
     * @param id   车辆id
     * @return     返回逻辑删除行数
     */
    @Override
    public Integer deleteProprietorCar(long id) {
        //removeById(id); 物理删除
        //逻辑删除
        return carMapper.deleteById(id);
    }

    /**
     * 新增车辆操作方法
     * @param carEntity 车辆实体对象
     * @return 返回插入结果
     */
    @Override
    public Integer addProprietorCar(CarEntity carEntity) {
        return carMapper.insert(carEntity);
    }

    /**
     * 根据车牌查询车辆是否存在
     * @param carPlate 车牌
     * @return 返回车辆是否存在
     */
    @Override
    public Boolean carIsExist(String carPlate) {
        return carMapper.selectCount(new QueryWrapper<CarEntity>().eq("car_plate", carPlate).eq("deleted",0)) > 0;
    }


}
