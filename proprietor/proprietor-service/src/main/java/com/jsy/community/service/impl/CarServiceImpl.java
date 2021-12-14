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
import com.jsy.community.qo.property.CarChargeQO;
import com.jsy.community.qo.proprietor.CarQO;
import com.jsy.community.utils.OrderCochainUtil;
import com.jsy.community.utils.PushInfoUtil;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
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
    private UserIMMapper userIMMapper;

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private AppCarOrderMapper appCarOrderMapper;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private IPropertyCompanyService propertyCompanyService;

    @Autowired
    private CarOrderRecordMapper carOrderRecordMapper;


    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionService carPositionService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IBindingPositionService bindingPositionService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private ICarChargeService carChargeService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private ICarBasicsService carBasicsService;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private ICarMonthlyVehicleService carMonthlyVehicleService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService userInfoRpcService;

    @DubboReference(version = com.zhsj.im.chat.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.im.chat.api.constant.RpcConst.Rpc.Group.GROUP_IM_CHAT, check=false)
    private IImChatPublicPushRpcService iImChatPublicPushRpcService;

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
     * @Description: 查询所有临时车记录
     * @author: Hu
     * @since: 2021/10/26 15:33
     * @Param: [communityId, userId]
     * @return: java.util.List<com.jsy.community.entity.CarOrderEntity>
     */
    @Override
    public List<CarOrderEntity> getTemporaryOrder(Long communityId, String userId) {
        List<CarOrderEntity> entities = carMapper.getTemporaryOrder(communityId,userId);
        CommunityEntity communityEntity = communityMapper.selectById(communityId);
        if (entities.size()!=0) {
            for (CarOrderEntity entity : entities) {
//                entity.setMinute(ChronoUnit.MINUTES.between(entity.getBeginTime(), LocalDateTime.now()));
                entity.setCarTypeText("临时车");
                entity.setCarPositionText(communityEntity.getName());
            }
        }
        return entities;
    }



    /**
     * @Description: 修改临时车辆订单状态
     * @author: Hu
     * @since: 2021/10/26 16:46
     * @Param: [s]
     * @return: void
     */
    @Override
    @TxcTransaction
    public void updateByOrder(String id,BigDecimal total,String outTradeNo,Integer payType) {
        CarOrderEntity carOrderEntity = appCarOrderMapper.selectById(id);
        String orderNum = getOrderNum(carOrderEntity.getCommunityId().toString());
        if (carOrderEntity != null) {
            carOrderEntity.setPayType(1);
            carOrderEntity.setOrderStatus(1);
            carOrderEntity.setIsPayAnother(1);
            carOrderEntity.setMoney(total);
            carOrderEntity.setOrderTime(LocalDateTime.now());
            carOrderEntity.setOverTime(LocalDateTime.now());
            carOrderEntity.setBillNum(orderNum);
            appCarOrderMapper.updateById(carOrderEntity);

            //向账单表添加数据
            CommunityEntity communityEntity = communityMapper.selectById(carOrderEntity.getCommunityId());
            CarChargeEntity carChargeEntity = carChargeService.selectOne(carOrderEntity.getCommunityId());
            PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
            orderEntity.setAssociatedType(2);
            orderEntity.setBuildType(4);
            orderEntity.setRise(communityEntity.getName()+"-"+carChargeEntity.getName());
            orderEntity.setCommunityId(carOrderEntity.getCommunityId());
            orderEntity.setOrderTime(LocalDate.now());
            orderEntity.setUid(carOrderEntity.getUid());
            orderEntity.setPropertyFee(total);
            orderEntity.setOrderNum(orderNum);
            orderEntity.setOrderStatus(1);
            orderEntity.setPayType(2);
            orderEntity.setTripartiteOrder(outTradeNo);
            orderEntity.setPayTime(LocalDateTime.now());
            orderEntity.setBeginTime(carOrderEntity.getBeginTime().toLocalDate());
            orderEntity.setOverTime(carOrderEntity.getOverTime().toLocalDate());
            orderEntity.setId(SnowFlake.nextId());
            propertyFinanceOrderService.insert(orderEntity);


            LocalDateTime fromDateTime = carOrderEntity.getBeginTime();
            LocalDateTime toDateTime = carOrderEntity.getOverTime();
            LocalDateTime tempDateTime = LocalDateTime.from( fromDateTime );

            long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS);
            tempDateTime = tempDateTime.plusHours( hours );
            long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES);
            PropertyCompanyEntity companyEntity = propertyCompanyService.selectCompany(communityEntity.getPropertyId());
            //支付上链
            OrderCochainUtil.orderCochain("停车费",
                    1,
                    payType,
                    total,
                    outTradeNo,
                    carOrderEntity.getUid(),
                    companyEntity.getUnifiedSocialCreditCode(),
                    hours+"小时"+minutes+"分钟"+"停车位",
                    null);

            //推送消息
            UserImVo userIm = userInfoRpcService.getEHomeUserIm(carOrderEntity.getUid());
            HashMap<Object, Object> map = new HashMap<>();
            map.put("type",1);
            map.put("dataId",carOrderEntity.getId());
            map.put("orderNum",outTradeNo);
            PushInfoUtil.pushPayAppMsg(
                    iImChatPublicPushRpcService,
                    userIm.getImId(),
                    payType,
                    total.toString(),
                    null,
                    "临时缴费",
                    map,
                    BusinessEnum.PushInfromEnum.MONTHLYRENTPAYMENT.getName());
        }
    }

    /**
     * @Description: 查询一条临时账单详情
     * @author: Hu
     * @since: 2021/10/26 15:34
     * @Param: [id, userId]
     * @return: com.jsy.community.entity.CarOrderEntity
     */
    @Override
    public CarOrderEntity getTemporaryOrderById(Long id, String userId) {
        CarOrderEntity carOrderEntity = appCarOrderMapper.selectById(id);

        //设置金额
        CarChargeQO chargeQO = new CarChargeQO();
        chargeQO.setCarColor(carOrderEntity.getPlateColor());
        chargeQO.setCommunityId(String.valueOf(carOrderEntity.getCommunityId()));
        chargeQO.setInTime(carOrderEntity.getBeginTime());
        chargeQO.setReTime(LocalDateTime.now());
        carOrderEntity.setMoney(carChargeService.charge(chargeQO));

        CommunityEntity entity = communityMapper.selectById(carOrderEntity.getCommunityId());
        carOrderEntity.setCarPositionText(entity.getName());
        Integer integer = carOrderEntity.getPlateColor()=="黄色"?0:1;
        CarChargeEntity carChargeEntity = carChargeService.selectTemporary(carOrderEntity.getCommunityId(),integer);
        if (carChargeEntity != null) {
            carOrderEntity.setExpenseRule(carChargeEntity.getChargePrice());
        }
        CarBasicsEntity basicsEntity = carBasicsService.findOne(carOrderEntity.getCommunityId());
        if (basicsEntity != null) {
            carOrderEntity.setRetentionMinute(basicsEntity.getDwellTime());
        }
        carOrderEntity.setMinute(ChronoUnit.MINUTES.between(carOrderEntity.getBeginTime(), LocalDateTime.now()));
        carOrderEntity.setRetentionHour(24);
        carOrderEntity.setCarTypeText("临时车");
        return carOrderEntity;
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
        CommunityEntity communityEntity = communityMapper.selectById(baseQO.getQuery().getCommunityId());
        QueryWrapper<CarOrderEntity> wrapper = new QueryWrapper<CarOrderEntity>()
                .select("id,order_num,month,order_time,begin_time,over_time,money,car_plate,car_position_id,type,renewal_in")
                .eq("community_id", baseQO.getQuery().getCommunityId())
                .eq("uid", userId)
                .eq("order_status",1);
        if (!"".equals(baseQO.getQuery().getMonth())&&baseQO.getQuery().getMonth()!=null&&baseQO.getQuery().getMonth()!=0){
            wrapper.eq("month",baseQO.getQuery().getMonth());
        }
        Page<CarOrderEntity> page = appCarOrderMapper.selectPage(new Page<CarOrderEntity>(baseQO.getPage(), baseQO.getSize()), wrapper);
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
                record.setCarPositionId(communityEntity.getId());
//                record.setCarPositionText(positionMap.get(record.getCarPositionId()));
                record.setCarPositionText(communityEntity.getName());
                if (record.getType()==1){
                    record.setTypeText("临时车");
                }else {
                    record.setTypeText("月租车");
                    record.setMonth(record.getRenewalIn());
                }
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
     * @Description: 查询小区名称
     * @author: Hu
     * @since: 2021/10/26 14:40
     * @Param:
     * @return:
     */
    @Override
    public CommunityEntity selectCommunityName(Long communityId) {
        return communityMapper.selectById(communityId);
    }

    @Override
    public CarOrderEntity getOrder(Long id) {
        CarOrderEntity entity = appCarOrderMapper.selectById(id);
        CommunityEntity communityEntity = communityMapper.selectById(entity.getCommunityId());
        entity.setMonth(entity.getRenewalIn());
        entity.setCarPositionText(communityEntity.getName());
        return entity;
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
            //查询数据库是否正在包月中
            CarEntity plate = carMapper.selectOne(new QueryWrapper<CarEntity>().eq("car_plate", carEntity.getCarPlate()).eq("type",2));
            if (Objects.nonNull(plate)){
                throw new ProprietorException("当前车辆已进行包月，如果需要延期，请查询记录执行时间延期操作！");
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
//                entity.setPayType(1);
//                entity.setOrderNum("4654132516843135");
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

            //向账单表添加数据
            CommunityEntity communityEntity = communityMapper.selectById(entity.getCommunityId());
            CarChargeEntity carChargeEntity = carChargeService.selectOne(carEntity.getCommunityId());
            PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
            orderEntity.setAssociatedType(2);
            orderEntity.setBuildType(4);
            orderEntity.setType(12);
            orderEntity.setRise(communityEntity.getName()+"-"+carChargeEntity.getName());
            orderEntity.setCommunityId(entity.getCommunityId());
            orderEntity.setOrderTime(LocalDate.now());
            orderEntity.setUid(entity.getUid());
            orderEntity.setTargetId(entity.getCarId());
            orderEntity.setPropertyFee(entity.getMoney());
            orderEntity.setOrderNum(getOrderNum(entity.getCommunityId().toString()));
            orderEntity.setOrderStatus(1);
            orderEntity.setPayType(2);
            orderEntity.setPayTime(LocalDateTime.now());
            orderEntity.setBeginTime(carEntity.getBeginTime().toLocalDate());
            orderEntity.setOverTime(carEntity.getOverTime().toLocalDate());
            orderEntity.setId(SnowFlake.nextId());
            propertyFinanceOrderService.insert(orderEntity);

            //添加缴费记录
            CarOrderEntity carOrderEntity = new CarOrderEntity();
            carOrderEntity.setCarId(carEntity.getId());
            carOrderEntity.setCommunityId(carEntity.getCommunityId());
            carOrderEntity.setCarPositionId(carEntity.getCarPositionId());
            carOrderEntity.setType(2);
            carOrderEntity.setUid(carEntity.getUid());
            carOrderEntity.setPayType(1);
            carOrderEntity.setRenewalIn(entity.getMonth());
            carOrderEntity.setBillNum(orderEntity.getOrderNum());
            carOrderEntity.setRise("月租车-"+carEntity.getCarPlate());
            carOrderEntity.setOrderTime(LocalDateTime.now());
            carOrderEntity.setBeginTime(carEntity.getBeginTime());
            carOrderEntity.setOverTime(carEntity.getOverTime());
            carOrderEntity.setMoney(entity.getMoney());
            carOrderEntity.setMonth(LocalDateTime.now().getMonthValue());
            carOrderEntity.setOrderNum(entity.getOrderNum());
            carOrderEntity.setOrderStatus(1);
            carOrderEntity.setCarPlate(carEntity.getCarPlate());
            carOrderEntity.setId(SnowFlake.nextId());
            appCarOrderMapper.insert(carOrderEntity);

            //修改月租车辆表
            carMonthlyVehicleService.updateMonth(entity.getCarPlate(),carEntity.getOverTime(),entity.getMoney());

            //停车临时收费表更新
            entity.setStatus(1);
            carOrderRecordMapper.updateById(entity);

            PropertyCompanyEntity companyEntity = propertyCompanyService.selectCompany(communityEntity.getPropertyId());
            //支付上链
            OrderCochainUtil.orderCochain("停车费",
                    1,
                    entity.getPayType(),
                    entity.getMoney(),
                    entity.getOrderNum(),
                    entity.getUid(),
                    companyEntity.getUnifiedSocialCreditCode(),
                    entity.getMonth()+"月车位租金费",
                    null);
            //推送消息
            UserImVo userIm = userInfoRpcService.getEHomeUserIm(entity.getUid());
            HashMap<Object, Object> map = new HashMap<>();
            map.put("type",2);
            map.put("dataId",carOrderEntity.getId());
            map.put("orderNum",entity.getOrderNum());
            PushInfoUtil.pushPayAppMsg(
                    iImChatPublicPushRpcService,
                    userIm.getImId(),
                    entity.getPayType(),
                    entity.getMoney().toString(),
                    null,
                    "月租缴费",
                    map,
                    BusinessEnum.PushInfromEnum.MONTHLYRENTPAYMENT.getName());
        } else {
            throw new ProprietorException("当前月租车辆不存在！");
        }
    }

    /**
     * @Description: 生成账单号
     * @author: Hu
     * @since: 2021/5/21 11:03
     * @Param:
     * @return:
     */
    public static String getOrderNum(String communityId){
        StringBuilder str=new StringBuilder();
        if (communityId.length()>=4){
            String s = communityId.substring(communityId.length() - 4, communityId.length());
            str.append(s);
        }else {
            if (communityId.length()==3){
                str.append("0"+communityId);
            } else{
                if (communityId.length()==2){
                    str.append("00"+communityId);
                } else {
                    str.append("000"+communityId);
                }
            }
        }
        long millis = System.currentTimeMillis();
        str.append(millis);
        int s1=(int) (Math.random() * 99);
        str.append(s1);
        return str.toString();
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

                //向账单表添加数据
                CommunityEntity communityEntity = communityMapper.selectById(entity.getCommunityId());
                CarChargeEntity carChargeEntity = carChargeService.selectOne(carEntity.getCommunityId());
                PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
                orderEntity.setAssociatedType(2);
                orderEntity.setBuildType(4);
                orderEntity.setType(12);
                orderEntity.setOrderNum(getOrderNum(entity.getCommunityId().toString()));
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


                //存入车辆管理 默认是已绑定
                BindingPositionEntity bindingPositionEntity = new BindingPositionEntity();
                bindingPositionEntity.setUid(UserUtils.randomUUID());//uuid主键
                bindingPositionEntity.setBindingStatus(1);//默认绑定
                bindingPositionEntity.setCommunityId(entity.getCommunityId());//社区id
                bindingPositionEntity.setCarNumber(entity.getCarPlate());//车牌号
                bindingPositionService.saveBindingPosition(bindingPositionEntity);

                //新增订单
                CarOrderEntity carOrderEntity = new CarOrderEntity();
                carOrderEntity.setCarId(carEntity.getId());
                carOrderEntity.setCommunityId(carEntity.getCommunityId());
                carOrderEntity.setCarPositionId(carEntity.getCarPositionId());
                carOrderEntity.setType(2);
                carOrderEntity.setBillNum(orderEntity.getOrderNum());
                carOrderEntity.setRise("月租车-"+carEntity.getCarPlate());
                carOrderEntity.setUid(carEntity.getUid());
                carOrderEntity.setPayType(1);
                carOrderEntity.setRenewalIn(entity.getMonth());
                carOrderEntity.setOrderTime(LocalDateTime.now());
                carOrderEntity.setBeginTime(carEntity.getBeginTime());
                carOrderEntity.setOverTime(carEntity.getOverTime());
                carOrderEntity.setMoney(carEntity.getMoney());
                carOrderEntity.setMonth(LocalDateTime.now().getMonthValue());
                carOrderEntity.setOrderNum(carEntity.getOrderNum());
                carOrderEntity.setOrderStatus(1);
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
                vehicle.setDistributionStatus(1);
                vehicle.setStartTime(carEntity.getBeginTime());
                vehicle.setEndTime(carEntity.getOverTime());
                vehicle.setMonthlyFee(entity.getMoney());
                vehicle.setMonthlyMethodId(chargeEntity.getUid());
                carMonthlyVehicleService.appMonth(vehicle);

                //停车临时收费表更新
                entity.setStatus(1);
                carOrderRecordMapper.updateById(entity);

                PropertyCompanyEntity companyEntity = propertyCompanyService.selectCompany(communityEntity.getPropertyId());
                //支付上链
                OrderCochainUtil.orderCochain("停车费",
                        1,
                        entity.getPayType(),
                        entity.getMoney(),
                        entity.getOrderNum(),
                        entity.getUid(),
                        companyEntity.getUnifiedSocialCreditCode(),
                        entity.getMonth()+"月车位租金费",
                        null);
                //推送消息
                UserImVo userIm = userInfoRpcService.getEHomeUserIm(entity.getUid());
                HashMap<Object, Object> map = new HashMap<>();
                map.put("type",2);
                map.put("dataId",carOrderEntity.getId());
                map.put("orderNum",entity.getOrderNum());
                PushInfoUtil.pushPayAppMsg(
                        iImChatPublicPushRpcService,
                        userIm.getImId(),
                        entity.getPayType(),
                        entity.getMoney().toString(),
                        null,
                        "月租缴费",
                        map,
                        BusinessEnum.PushInfromEnum.MONTHLYRENTPAYMENT.getName());
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
        CarEntity carEntity = carMapper.selectById(id);
        if (carEntity != null) {
            CarEntity entity = carMapper.selectOne(new QueryWrapper<CarEntity>().eq("car_plate", carEntity.getCarPlate()).eq("type", 2));
            if (entity != null) {
                if (entity.getOverTime().isBefore(LocalDateTime.now())){
                    throw new ProprietorException("该车辆存在逾期未付款，请先缴清费用！");
                }
                throw new ProprietorException("该车辆为月租产权车辆请先解除该车辆！");
            }
        }
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
        CommunityEntity communityEntity = communityMapper.selectById(carEntity.getCommunityId());
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
                        entity.setCarPositionText(communityEntity.getName());
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
