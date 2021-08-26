package com.jsy.community.service.impl;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.jsy.community.api.ICarChargeService;
import com.jsy.community.api.ICarPositionService;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.property.CarChargeEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.mapper.AppCarOrderMapper;
import com.jsy.community.mapper.CarMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AppCarOrderMapper appCarOrderMapper;


    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionService carPositionService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarChargeService carChargeService;

    @Autowired
    private HouseMemberMapper houseMemberMapper;

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
        log.info("查询所属人车辆满足条件行数："+resultData.getTotal() + "每页显示条数："+resultData.getSize());
        return resultData;
    }

    /**
     * 根据实体类字段 进行更新
     * @param carQo      车辆修改参数实体对象
     * @param uid        用户id
     * @return           返回修改影响行数
     */
    @Override
    public Integer updateProprietorCar(CarQO carQo, String uid) {
        CarEntity carEntity = new CarEntity();
        BeanUtil.copyProperties(carQo, carEntity);
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
        carEntity.setId(SnowFlake.nextId());
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
     * @param cars 业主车辆信息 列表
     * @param uid  业主id
     */
    @Override
    public void addProprietorCarForList(List<CarQO> cars, String uid) {
        carMapper.addProprietorCarForList(cars, uid);
    }

    /**
     * 按用户id查出用户所有车辆信息
     * @param userId        用户id
     * @return              返回业主车辆信息
     */
    @Override
    public List<CarEntity> queryUserCarById(String userId) {
        List<CarEntity> cars = carMapper.queryUserCarById(userId);
        cars.forEach( car -> car.setCarTypeText(BusinessEnum.CarTypeEnum.getCode(car.getCarType())));
        return cars;
    }

    @Override
    public List<CarEntity> getAllCarById(Long communityId, String userId) {
        Map<String, Object> columnMap = new HashMap<>(2);
        columnMap.put("uid", userId);
        columnMap.put("community_id", communityId);
        return carMapper.selectByMap(columnMap);
    }



    /**
     * @Description: 获取车位费
     * @author: Hu
     * @since: 2021/8/26 14:42
     * @Param: [carEntity]
     * @return: java.math.BigDecimal
     */
    @Override
    public BigDecimal payPositionFees(CarEntity carEntity) {
        CarChargeEntity carChargeEntity = carChargeService.selectOne(carEntity.getCommunityId());
        return carChargeEntity.getMoney().multiply(carEntity.getMoney());
    }

    /**
     * @Description: 续费月租车辆
     * @author: Hu
     * @since: 2021/8/26 11:51
     * @Param: [carEntity]
     * @return: void
     */
    @Override
    @Transactional
    public void renewMonthCar(CarEntity carEntity) {
        CarEntity entity = carMapper.selectById(carEntity.getId());
        entity.setOverTime(entity.getOverTime().plusMonths(carEntity.getMonth()));
        carMapper.updateById(entity);


        //业主绑定车辆后修改车位状态
        carPositionService.bindingMonthCar(carEntity);

        CarOrderEntity carOrderEntity = new CarOrderEntity();
        carOrderEntity.setCarId(entity.getId());
        carOrderEntity.setCommunityId(entity.getCommunityId());
        carOrderEntity.setCarPositionId(entity.getCarPositionId());
        carOrderEntity.setType(2);
        carOrderEntity.setOrderTime(LocalDateTime.now());
        carOrderEntity.setBeginTime(entity.getBeginTime());
        carOrderEntity.setOverTime(entity.getOverTime());
        carOrderEntity.setMoney(carEntity.getMoney());
        carOrderEntity.setOrderNum(carEntity.getOrderNum());
        carOrderEntity.setOrderStatus(0);
        carOrderEntity.setCarPlate(carEntity.getCarPlate());
        carOrderEntity.setId(SnowFlake.nextId());

        appCarOrderMapper.insert(carOrderEntity);
    }

    /**
     * @Description: 绑定月租车辆
     * @author: Hu
     * @since: 2021/8/26 11:13
     * @Param: [carEntity]
     * @return: void
     */
    @Override
    @TxcTransaction
    public void bindingMonthCar(CarEntity carEntity) {
            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", carEntity.getUid()));
            if (userEntity != null) {
                carEntity.setId(SnowFlake.nextId());
                carEntity.setBeginTime(LocalDate.now());
                carEntity.setOverTime(LocalDate.now().plusMonths(carEntity.getMonth()));
                carEntity.setType(2);
                carEntity.setOwner(userEntity.getRealName());
                carEntity.setContact(userEntity.getMobile());
                carMapper.insert(carEntity);

                //业主绑定车辆后修改车位状态
                carPositionService.bindingMonthCar(carEntity);

                //新增订单
                CarOrderEntity carOrderEntity = new CarOrderEntity();
                carOrderEntity.setCarId(carEntity.getId());
                carOrderEntity.setCommunityId(carEntity.getCommunityId());
                carOrderEntity.setCarPositionId(carEntity.getCarPositionId());
                carOrderEntity.setType(2);
                carOrderEntity.setPayType(1);
                carOrderEntity.setOrderTime(LocalDateTime.now());
                carOrderEntity.setBeginTime(carEntity.getBeginTime());
                carOrderEntity.setOverTime(carEntity.getOverTime());
                carOrderEntity.setMoney(carEntity.getMoney());
                carOrderEntity.setOrderNum(carEntity.getOrderNum());
                carOrderEntity.setOrderStatus(0);
                carOrderEntity.setCarPlate(carEntity.getCarPlate());
                carOrderEntity.setId(SnowFlake.nextId());
                appCarOrderMapper.insert(carOrderEntity);
            }

    }

    /**
     * @Description: 获取当前小区空置车位
     * @author: Hu
     * @since: 2021/8/25 15:45
     * @Param: [communityId]
     * @return: java.util.List<com.jsy.community.entity.property.CarPositionEntity>
     */
    @Override
    public List<CarPositionEntity> getPosition(Long communityId) {
        return carPositionService.getPosition(communityId);
    }

    /**
     * @Description: 新app删除车辆
     * @author: Hu
     * @since: 2021/8/21 10:40
     * @Param: [id, userId]
     * @return: void
     */
    @Override
    public void delete(Long id, String userId) {
        carMapper.delete(new QueryWrapper<CarEntity>().eq("uid",userId).eq("id",id));
    }


    /**
     * @Description: 新app查询车辆
     * @author: Hu
     * @since: 2021/8/21 10:40
     * @Param: [communityId, uid]
     * @return: java.util.List<com.jsy.community.entity.CarEntity>
     */
    @Override
    public List<CarEntity> getCars(CarEntity carEntity,String uid) {
        Map<Long,String> map = null;
        QueryWrapper<CarEntity> wrapper = null;
        //type = 1则把所有车辆全部查出来
        if (carEntity.getType()==3){
            wrapper = new QueryWrapper<CarEntity>()
                    .select("id,car_plate,community_id")
                    .eq("uid", uid)
                    .eq("type",carEntity.getType());
        }else {
            //type = 2则当前小区的月租车辆
            if (carEntity.getType()==2){
                wrapper = new QueryWrapper<CarEntity>()
                        .select("id,car_plate,community_id,begin_time,over_time,type,car_position_id")
                        .eq("uid", uid)
                        .eq("community_id",carEntity.getCommunityId())
                        .eq("type",carEntity.getType());
            }
        }
        //如果查询的是月租车则要查询车位编号
        List<CarEntity> list = carMapper.selectList(wrapper);
        if (carEntity.getType()==2){
            if (list.size()!=0){
                //封装车位id
                LinkedList<Long> positionIds = new LinkedList<>();
                for (CarEntity entity : list) {
                    positionIds.add(entity.getCarPositionId());
                }
                List<CarPositionEntity> carPositionEntities = carPositionService.getByIds(positionIds);
                if (carPositionEntities!=null){
                    map=new HashMap<>();
                    //把车位信息封装到map  key未车位id  value为车位编号
                    for (CarPositionEntity carPositionEntity : carPositionEntities) {
                        map.put(carPositionEntity.getId(),carPositionEntity.getCarPosition());
                    }
                    for (CarEntity entity : list) {
                        entity.setCarPositionText(map.get(entity.getCarPositionId()));
                        entity.setTypeText("月租车");
                        //计算当前月租车所剩天数
                        long until = LocalDate.now().until(entity.getOverTime(), ChronoUnit.DAYS);
                        if (until>0){
                            entity.setRemainingDays(until);
                        }else {
                            entity.setRemainingDays(0L);
                        }
                    }
                }
            }

        }

        return list;
    }


    /**
     * @Description: 新app修改车辆
     * @author: Hu
     * @since: 2021/8/21 10:08
     * @Param: [carEntity]
     * @return: void
     */
    @Override
    public void updateRelationCar(CarEntity carEntity) {
            carMapper.updateById(carEntity);

    }

    /**
     * @Description: 新app添加车辆
     * @author: Hu
     * @since: 2021/8/21 10:08
     * @Param: [carEntity]
     * @return: void
     */
    @Override
    public void addRelationCar(CarEntity carEntity) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", carEntity.getUid()));
            carEntity.setId(SnowFlake.nextId());
            carEntity.setContact(userEntity.getMobile());
            carEntity.setOwner(userEntity.getRealName());
            carEntity.setType(3);
            carMapper.insert(carEntity);
    }

    @Override
    public void update(CarQO c, String uid) {
        carMapper.update(c, uid);
    }


}
