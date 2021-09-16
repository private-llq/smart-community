package com.jsy.community.service.impl;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.entity.property.*;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private CommunityMapper communityMapper;

    @Autowired
    private AppCarOrderMapper appCarOrderMapper;

    @Autowired
    private CarOrderRecordMapper carOrderRecordMapper;


    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionService carPositionService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private ICarChargeService carChargeService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private ICarBasicsService carBasicsService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private ICarMonthlyVehicleService carMonthlyVehicleService;

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
     * @Description: 查询月租缴费订单
     * @author: Hu
     * @since: 2021/8/26 17:32
     * @Param: [communityId, userId]
     * @return: java.util.List<com.jsy.community.entity.CarOrderEntity>
     */
    @Override
    public Map<String, Object> MonthOrder(BaseQO<CarEntity> baseQO, String userId) {
        LinkedList<Long> ids = new LinkedList<>();
        Map<Long,String> positionMap = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        Page<CarOrderEntity> page = appCarOrderMapper.selectPage(new Page<CarOrderEntity>(baseQO.getPage(), baseQO.getSize()), new QueryWrapper<CarOrderEntity>()
                .select("id,order_num,month,order_time,over_time,money,car_plate,car_position_id,type")
                .eq("community_id", baseQO.getQuery().getCommunityId())
                .eq("uid", userId)
                .eq("type", 2));
        List<CarOrderEntity> records = page.getRecords();
        if (records.size()!=0){
            for (CarOrderEntity record : records) {
                ids.add(record.getCarPositionId());
            }
//            List<CarPositionEntity> service = carPositionService.getByIds(ids);
//            for (CarPositionEntity entity : service) {
//                positionMap.put(entity.getId(),entity.getCarPosition());
//            }
            for (CarOrderEntity record : records) {
                record.setCarPositionId(999999999999999L);
//                record.setCarPositionText(positionMap.get(record.getCarPositionId()));
                record.setCarPositionText("地上");
                record.setTypeText("月租车");
            }
            map.put("total",page.getTotal());
            map.put("list",records);
            return map;
        }
        return null;
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
        return carChargeEntity.getMoney().multiply(new BigDecimal(carEntity.getMonth()));
    }

    /**
     * @Description: 解除月租车辆绑定关系
     * @author: Hu
     * @since: 2021/8/27 16:33
     * @Param:
     * @return:
     */
    @Override
    @TxcTransaction
    public void deleteMonthCar(Long id) {
        CarEntity carEntity = carMapper.selectById(id);
        carMapper.deleteById(id);
//        carPositionService.updateByPosition(carEntity.getCarPositionId());
    }

    /**
     * @Description: 查询一条停车缴费临时订单
     * @author: Hu
     * @since: 2021/8/27 13:57
     * @Param: [id]
     * @return: com.jsy.community.entity.CarOrderRecordEntity
     */
    @Override
    public CarOrderRecordEntity findOne(Long id) {
        return carOrderRecordMapper.selectById(id);
    }

    /**
     * @Description: 绑定月租临时订单记录表
     * @author: Hu
     * @since: 2021/8/26 14:42
     * @Param: [carEntity]
     * @return: java.math.BigDecimal
     */
    @Override
    @TxcTransaction
    public Long bindingRecord(CarEntity carEntity) {
        CarBasicsEntity basicsEntity = carBasicsService.findOne(carEntity.getCommunityId());
        if (basicsEntity!=null){
            if (basicsEntity.getMonthlyPayment()==0){
                throw new ProprietorException("当前小区不允许车辆包月！");
            }
            if (basicsEntity.getWhetherAllowMonth()==0){
                //查询车主在当前小区是否存在未交的物业费账单
                List<PropertyFinanceOrderEntity> list = propertyFinanceOrderService.FeeOrderList(carEntity.getCommunityId(),carEntity.getUid());
                if (list.size()!=0){
                    throw new ProprietorException("您在当前小区存在未缴的物业费账单，请先缴清物业费！");
                }
            }
            if (carEntity.getMonth()<=basicsEntity.getMonthMaxTime()){
                CarOrderRecordEntity entity = new CarOrderRecordEntity();
                entity.setId(SnowFlake.nextId());
                entity.setType(1);
                entity.setCarPositionId(carEntity.getCarPositionId());
                entity.setCarId(carEntity.getCarPositionId());
                entity.setCommunityId(carEntity.getCommunityId());
                entity.setCarPlate(carEntity.getCarPlate());
                entity.setMonth(carEntity.getMonth());
                entity.setMoney(carEntity.getMoney());
                entity.setUid(carEntity.getUid());
                carOrderRecordMapper.insert(entity);

                //支付成功后回调     测试阶段直接回调
//                bindingMonthCar(entity);
                return entity.getId();
            }
            throw new ProprietorException("当前小区最大允许包月时长："+basicsEntity.getMonthMaxTime());

        }
            throw new ProprietorException("当前小区不允许车辆包月！");
    }

    /**
     * @Description: 续费月租临时订单记录表
     * @author: Hu
     * @since: 2021/8/26 14:42
     * @Param: [carEntity]
     * @return: java.math.BigDecimal
     */
    @Override
    @TxcTransaction
    public Long renewRecord(CarEntity carEntity) {
        CarEntity entity = carMapper.selectById(carEntity.getId());
        CarBasicsEntity basicsEntity = carBasicsService.findOne(entity.getCommunityId());
        if (basicsEntity!=null) {
            if (basicsEntity.getMonthlyPayment() == 0) {
                throw new ProprietorException("当前小区不允许车辆包月！");
            }
            if (basicsEntity.getWhetherAllowMonth() == 0) {
                //查询车主在当前小区是否存在未交的物业费账单
                List<PropertyFinanceOrderEntity> list = propertyFinanceOrderService.FeeOrderList(entity.getCommunityId(), entity.getUid());
                if (list.size() != 0) {
                    throw new ProprietorException("您在当前小区存在未缴的物业费账单，请先缴清物业费！");
                }
            }
            if (carEntity.getMonth()<=basicsEntity.getMonthMaxTime()){
                CarOrderRecordEntity orderRecordEntity = new CarOrderRecordEntity();
                orderRecordEntity.setId(SnowFlake.nextId());
                orderRecordEntity.setCarPlate(entity.getCarPlate());
                orderRecordEntity.setCommunityId(entity.getCommunityId());
                orderRecordEntity.setCarId(carEntity.getId());
                orderRecordEntity.setType(2);
                orderRecordEntity.setCarPositionId(entity.getCarPositionId());
                orderRecordEntity.setMonth(carEntity.getMonth());
                orderRecordEntity.setMoney(carEntity.getMoney());
                orderRecordEntity.setUid(carEntity.getUid());
                carOrderRecordMapper.insert(orderRecordEntity);

                //支付成功后回调     测试阶段直接回调
//                renewMonthCar(orderRecordEntity);
                return orderRecordEntity.getId();
            }
            throw new ProprietorException("当前小区最大允许包月时长："+basicsEntity.getMonthMaxTime());
        }
        throw new ProprietorException("当前小区不允许车辆包月！");
    }

    /**
     * @Description: 续费月租车辆
     * @author: Hu
     * @since: 2021/8/26 11:51
     * @Param: [carEntity]
     * @return: void
     */
    @Override
    @TxcTransaction
    public void renewMonthCar(CarOrderRecordEntity entity) {

        CarEntity carEntity = carMapper.selectById(entity.getCarId());
        if (carEntity!=null){
            carEntity.setOverTime(carEntity.getOverTime().plusMonths(entity.getMonth()));
            carMapper.updateById(carEntity);


            //业主绑定车辆后修改车位状态
//            carPositionService.bindingMonthCar(carEntity);

            //添加缴费记录
            CarOrderEntity carOrderEntity = new CarOrderEntity();
            carOrderEntity.setCarId(carEntity.getId());
            carOrderEntity.setCommunityId(carEntity.getCommunityId());
            carOrderEntity.setCarPositionId(carEntity.getCarPositionId());
            carOrderEntity.setType(2);
            carOrderEntity.setUid(carEntity.getUid());
            carOrderEntity.setPayType(1);
            carOrderEntity.setRise("月租车-"+carEntity.getCarPlate());
            carOrderEntity.setOrderTime(LocalDateTime.now());
            carOrderEntity.setBeginTime(carEntity.getBeginTime());
            carOrderEntity.setOverTime(carEntity.getOverTime());
            carOrderEntity.setMoney(entity.getMoney());
            carOrderEntity.setMonth(entity.getMonth());
            carOrderEntity.setOrderNum(carEntity.getOrderNum());
            carOrderEntity.setOrderStatus(0);
            carOrderEntity.setCarPlate(carEntity.getCarPlate());
            carOrderEntity.setId(SnowFlake.nextId());
            appCarOrderMapper.insert(carOrderEntity);

            //修改月租车辆表
            carMonthlyVehicleService.updateMonth(entity.getCarPlate(),carEntity.getOverTime(),entity.getMoney());

            //停车临时收费表更新
            entity.setStatus(1);
            carOrderRecordMapper.updateById(entity);

            //向账单表添加数据
            CommunityEntity communityEntity = communityMapper.selectById(entity.getCommunityId());
            CarChargeEntity carChargeEntity = carChargeService.selectOne(carEntity.getCommunityId());
            PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
            orderEntity.setAssociatedType(2);
            orderEntity.setBuildType(4);
            orderEntity.setRise(communityEntity.getName()+"-"+carChargeEntity.getName());
            orderEntity.setCommunityId(entity.getCommunityId());
            orderEntity.setOrderTime(LocalDate.now());
            orderEntity.setUid(entity.getUid());
            orderEntity.setTargetId(entity.getCarId());
            orderEntity.setPropertyFee(entity.getMoney());
            orderEntity.setOrderStatus(1);
            orderEntity.setPayType(2);
            orderEntity.setPayTime(LocalDateTime.now());
            orderEntity.setBeginTime(carEntity.getBeginTime().toLocalDate());
            orderEntity.setOverTime(carEntity.getOverTime().toLocalDate());
            orderEntity.setId(SnowFlake.nextId());
            propertyFinanceOrderService.insert(orderEntity);
        } else {
            throw new ProprietorException("当前月租车辆不存在！");
        }
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
    public void bindingMonthCar(CarOrderRecordEntity entity) {
            UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("uid", entity.getUid()));
            CarEntity carEntity = new CarEntity();
            if (userEntity != null) {
                BeanUtils.copyProperties(entity,carEntity);
                carEntity.setId(SnowFlake.nextId());
                carEntity.setBeginTime(LocalDateTime.now());
                carEntity.setOverTime(LocalDateTime.now().plusMonths(carEntity.getMonth()));
                carEntity.setType(2);
                carEntity.setCarPositionId(null);
                carEntity.setOwner(userEntity.getRealName());
                carEntity.setContact(userEntity.getMobile());
                carMapper.insert(carEntity);

                //业主绑定车辆后修改车位状态
//                carPositionService.bindingMonthCar(carEntity);

                //新增订单
                CarOrderEntity carOrderEntity = new CarOrderEntity();
                carOrderEntity.setCarId(carEntity.getId());
                carOrderEntity.setCommunityId(carEntity.getCommunityId());
                carOrderEntity.setCarPositionId(carEntity.getCarPositionId());
                carOrderEntity.setType(2);
                carOrderEntity.setRise("月租车-"+carEntity.getCarPlate());
                carOrderEntity.setUid(carEntity.getUid());
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

                //添加月租车辆表
                CarChargeEntity chargeEntity = carChargeService.selectOne(entity.getCommunityId());
                CarMonthlyVehicle vehicle = new CarMonthlyVehicle();
                vehicle.setId(SnowFlake.nextId());
                vehicle.setCommunityId(entity.getCommunityId());
                vehicle.setCarNumber(entity.getCarPlate());
                vehicle.setOwnerName(carEntity.getOwner());
                vehicle.setPhone(carEntity.getContact());
                vehicle.setStartTime(carEntity.getBeginTime());
                vehicle.setEndTime(carEntity.getOverTime());
                vehicle.setMonthlyFee(entity.getMoney());
                vehicle.setMonthlyMethodId(chargeEntity.getUid());
                carMonthlyVehicleService.appMonth(vehicle);

                //停车临时收费表更新
                entity.setStatus(1);
                carOrderRecordMapper.updateById(entity);

                //向账单表添加数据
                CommunityEntity communityEntity = communityMapper.selectById(entity.getCommunityId());
                CarChargeEntity carChargeEntity = carChargeService.selectOne(carEntity.getCommunityId());
                PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
                orderEntity.setAssociatedType(2);
                orderEntity.setBuildType(4);
                orderEntity.setCommunityId(entity.getCommunityId());
                orderEntity.setOrderTime(LocalDate.now());
                orderEntity.setUid(entity.getUid());
                orderEntity.setRise(communityEntity.getName()+"-"+carChargeEntity.getName());
                orderEntity.setTargetId(entity.getCarId());
                orderEntity.setPropertyFee(entity.getMoney());
                orderEntity.setOrderStatus(1);
                orderEntity.setPayType(2);
                orderEntity.setPayTime(LocalDateTime.now());
                orderEntity.setBeginTime(carEntity.getBeginTime().toLocalDate());
                orderEntity.setOverTime(carEntity.getOverTime().toLocalDate());
                orderEntity.setId(SnowFlake.nextId());
                propertyFinanceOrderService.insert(orderEntity);
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
//                        entity.setCarPositionText(map.get(entity.getCarPositionId()));
                        entity.setCarPositionId(999999999999999L);
                        entity.setCarPositionText("地上");
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
