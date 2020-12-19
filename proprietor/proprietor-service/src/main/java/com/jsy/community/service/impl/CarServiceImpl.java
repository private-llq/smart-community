package com.jsy.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.mapper.CarMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 车辆 服务实现类
 * @author YuLF
 * @since 2020-11-10
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group)
public class CarServiceImpl extends ServiceImpl<CarMapper, CarEntity> implements ICarService {

    @Resource
    private CarMapper carMapper;

    /**
     * 根据提供的参数对车辆进行分页查询
     * @param param  车辆分页条件查询参数
     * @return       返回当前页数据
     */
    @Override
    public Page<CarEntity> queryProprietorCar(BaseQO<CarEntity> param) {
        QueryWrapper<CarEntity> wrapper = new QueryWrapper<>();
        CarEntity query = param.getQuery();
        Page<CarEntity> pageCondition = new Page<>( param.getPage(), param.getSize() );
        wrapper.eq("uid", query.getUid());
        wrapper.eq("community_id",query.getCommunityId());
        Page<CarEntity> resultData = carMapper.selectPage(pageCondition, wrapper);
        //按条件查询
        //...
        //wrapper.eq("check_status", 0);
        log.info("查询所属人车辆满足条件行数："+resultData.getTotal() + "每页显示条数："+resultData.getSize());
        return resultData;
    }

    /**
     * 根据实体类字段 进行更新
     * @param carQO      车辆修改参数实体对象
     * @param uid        用户id
     * @return           返回修改影响行数
     */
    @Override
    public Integer updateProprietorCar(CarQO carQO, String uid) {
        CarEntity carEntity = new CarEntity();
        BeanUtil.copyProperties(carQO, carEntity);
        return carMapper.update(carEntity, new UpdateWrapper<CarEntity>().eq("id",carEntity.getId()).eq("uid", uid).eq("deleted", 0));
    }

    /**
     * 根据车辆id 进行逻辑删除
     * @param params 条件参数列表
     * @return       返回逻辑删除行数
     */
    @Override
    public Integer deleteProprietorCar(Map<String, Object> params) {
        //逻辑删除
        return carMapper.deleteByMap(params);
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



    /**
     * 列表添加车辆信息方式
     * @param carEntityList 业主车辆信息 列表
     * @return              返回影响行数
     */
    @Override
    public Integer addProprietorCar(List<CarEntity> carEntityList) {
        //对所有车辆信息 设置默认的id
        for(CarEntity carEntity : carEntityList ){
            carEntity.setId(SnowFlake.nextId());
        }
        return carMapper.addProprietorCar(carEntityList);
    }

    /**
     * 按用户id查出用户所有车辆信息
     * @param userId        用户id
     * @return              返回业主车辆信息
     */
    @Override
    public List<CarEntity> queryUserCarById(String userId) {
        return carMapper.queryUserCarById(userId);
    }

    /**
     * 批量更新车辆信息
     * @param carEntityList   更新参数对象，只会取需要的字段 
     */
    @Override
    public void updateProprietorCarBatch(List<CarEntity> carEntityList, String uid) {
        carMapper.updateProprietorCarBatch(carEntityList, uid);
    }


}
