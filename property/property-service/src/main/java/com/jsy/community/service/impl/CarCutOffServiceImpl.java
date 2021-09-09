package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarCutOffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.mapper.CarCutOffMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.util.TimeUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarCutOffServiceImpl extends ServiceImpl<CarCutOffMapper,CarCutOffEntity> implements ICarCutOffService {
   @Autowired
   private CarCutOffMapper carCutOffMapper;

   /**
    * @Description: 根据社区id查询 临时车在场数量
    * @Param: [carCutOffQO]
    * @Return: java.lang.Long
    * @Author: Tian
    * @Date: 2021/9/9-14:43
    **/
   @Override
    public Long selectPage(CarCutOffQO carCutOffQO) {

        QueryWrapper<CarCutOffEntity> queryWrapper = new QueryWrapper<>();
        //通过社区id   查询 临时车 在场的数量
        queryWrapper.eq("community_id",carCutOffQO.getCommunityId()).eq("state",0).eq("belong",1);
        Long total = Long.valueOf(carCutOffMapper.selectCount(queryWrapper));
        return total;
    }

    @Override
    public boolean addCutOff(CarCutOffEntity carCutOffEntity) {
        carCutOffEntity.setId(SnowFlake.nextId());
        return carCutOffMapper.insert(carCutOffEntity) == 1;
    }

    @Override
    public List<CarCutOffEntity> selectAccess(String carNumber, Integer state) {

        if (state==0){
            QueryWrapper<CarCutOffEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("car_number",carNumber).eq("state",state);
            List<CarCutOffEntity> carCutOffEntityList = carCutOffMapper.selectList(queryWrapper);
            return carCutOffEntityList;
        }else {
            QueryWrapper<CarCutOffEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("car_number",carNumber).eq("state",state);
            List<CarCutOffEntity> carCutOffEntityList = carCutOffMapper.selectList(queryWrapper);
            return carCutOffEntityList;
        }
    }

    @Override
    public boolean updateCutOff(CarCutOffEntity carCutOffEntity) {
        return  carCutOffMapper.updateById(carCutOffEntity) == 1;
    }

    @Override
    public Page<CarCutOffEntity> selectCarPage(BaseQO<CarCutOffQO> baseQO,Long communityId) {
        QueryWrapper<CarCutOffEntity> queryWrapper = new QueryWrapper<>();

        CarCutOffQO query = baseQO.getQuery();
        if (!StringUtils.isEmpty(query.getCarNumber())){
            queryWrapper.like("car_number",query.getCarNumber());
        }
        //车辆所属类型
        if (query.getBelong()!=null){
            queryWrapper.eq("belong",query.getBelong());
        }

        queryWrapper.eq("community_id",communityId);//状态
        Page<CarCutOffEntity> page = new Page<CarCutOffEntity>(baseQO.getPage(),baseQO.getSize());

        if (query.getState()!=null){
            queryWrapper.eq("state",query.getState());
        }
        Page<CarCutOffEntity> selectPage = carCutOffMapper.selectPage(page,queryWrapper);
        List<CarCutOffEntity> records = selectPage.getRecords();
        for (CarCutOffEntity i: records) {
            if (i.getOpenTime()!=null  && i.getStopTime()!=null){
                HashMap<String, Long> datePoor = TimeUtils.getDatePoor(i.getOpenTime(), i.getStopTime());
                String s = datePoor.get("day")+"天："+datePoor.get("hour")+" 小时："+datePoor.get("min")+" 分钟";
                i.setStopCarTime(s);
            }
        }
        return selectPage;
    }
}
