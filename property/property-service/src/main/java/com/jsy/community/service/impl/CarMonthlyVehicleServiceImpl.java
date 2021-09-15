package com.jsy.community.service.impl;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.TxTransaction;
import com.jsy.community.api.ICarMonthlyVehicleService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.*;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.CarMonthlyDelayQO;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@DubboService(version = Const.version, group = Const.group_property)
public class CarMonthlyVehicleServiceImpl extends ServiceImpl<CarMonthlyVehicleMapper, CarMonthlyVehicle> implements ICarMonthlyVehicleService {

    @Autowired
    private CarMonthlyVehicleMapper carMonthlyVehicleMapper;
    @Autowired
    private CarPositionMapper carPositionMapper;
    @Autowired
    private CarChargeMapper CarChargeMapper;
    @Autowired
    private CarBlackListMapper carBlackListMapper;
    @Autowired
    private CarProprietorMapper carProprietorMapper;
    @Autowired
    private CarBasicsMapper carBasicsMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private BindingPositionMapper bindingPositionMapper;




    /**
     * @Description: app修改月租车辆到期时间
     * @author: Hu
     * @since: 2021/9/3 11:39
     * @Param: [carPlate, overTime]
     * @return: void
     */
    @Override
    @TxTransaction
    public void updateMonth(String carPlate, LocalDateTime overTime,BigDecimal money) {
        CarMonthlyVehicle vehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("car_number", carPlate));
        if (vehicle!=null){
            vehicle.setMonthlyFee(vehicle.getMonthlyFee().add(money));
            vehicle.setEndTime(overTime);
            carMonthlyVehicleMapper.updateById(vehicle);
        }else {
            throw new JSYException("当前月租车辆不存在！");
        }
    }

    /**
     * @Description: app绑定月租车辆
     * @author: Hu
     * @since: 2021/9/3 10:37
     * @Param: [vehicle]
     * @return: void
     */
    @Override
    @Transactional
    public void appMonth(CarMonthlyVehicle vehicle) {
        //查询黑名单中是否存在该车辆
        CarBlackListEntity car_number = carBlackListMapper.selectOne(new QueryWrapper<CarBlackListEntity>().eq("car_number", vehicle.getCarNumber()));
        if (Objects.nonNull(car_number)){
            throw new PropertyException("该车辆已进入黑名单，无法进场或离场!");
        }
        //查询收费设置数据
        CarChargeEntity carChargeEntity = CarChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", vehicle.getMonthlyMethodId()));
        vehicle.setUid(UserUtils.randomUUID());
        vehicle.setMonthlyMethodId(carChargeEntity.getUid());//存收费设置里面的id
        vehicle.setMonthlyMethodName(carChargeEntity.getName());//存收费设置里面的名字
        carMonthlyVehicleMapper.insert(vehicle);

    }

    /**
     * 新增
     * @param carMonthlyVehicle
     * @return
     */
    @Override
    @Transactional
    public Integer SaveMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle,Long communityId) {

        if (Objects.isNull(carMonthlyVehicle.getStartTime())){
            throw new PropertyException("开始时间不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getEndTime())){
            throw new PropertyException("结束时间不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getMonthlyFee())){
            throw new PropertyException("包月费用不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getPhone())){
            throw new PropertyException("手机号码不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getCarNumber())){
            throw new PropertyException("车牌号不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getOwnerName())){
            throw new PropertyException("车主姓名不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getCarPosition())){
            throw new PropertyException("车位号不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getMonthlyMethodId())){
            throw new PropertyException("包月方式不能为空！");
        }
        //查询黑名单中是否存在该车辆
        CarBlackListEntity car_number = carBlackListMapper.selectOne(new QueryWrapper<CarBlackListEntity>().eq("car_number", carMonthlyVehicle.getCarNumber()));
        if (Objects.nonNull(car_number)){
            throw new PropertyException("该车辆已进入黑名单，无法进场或离场!");
        }


        //查询该车位是否已经出租
        CarPositionEntity positionEntity = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("binding_status", 1).eq("car_pos_status", 2).eq("community_id", communityId).eq("car_position", carMonthlyVehicle.getCarPosition()));
        if (Objects.nonNull(positionEntity)){
            throw new PropertyException("该车位已被出租，如果需要延期，请查询记录执行时间延期操作！");
        }

        //查询数据库是否正在包月中
        CarMonthlyVehicle vehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("car_number", carMonthlyVehicle.getCarNumber()).ge("end_time",carMonthlyVehicle.getEndTime()));
        if (Objects.nonNull(vehicle)){
            throw new PropertyException("已进行包月，如果需要延期，请查询记录执行时间延期操作！");
        }

        //查询基础设置里面的最大续费月数
        CarBasicsEntity carBasicsEntity = carBasicsMapper.selectOne(new QueryWrapper<CarBasicsEntity>().eq("community_id", communityId));
        Integer monthMaxTime = carBasicsEntity.getMonthMaxTime();//最大续费月数
        LocalDateTime startTime = carMonthlyVehicle.getStartTime();
        LocalDateTime endTime = carMonthlyVehicle.getEndTime();
        long tempTime = Duration.between(startTime, endTime).toMinutes();//相差分钟
        if (tempTime>monthMaxTime*30*24*60){
            throw new PropertyException("当前包月时间已超过你设置的最大包月数，"+monthMaxTime+"个月！");
        }





        //查询收费设置数据
        String monthlyMethodId = carMonthlyVehicle.getMonthlyMethodId();
        CarChargeEntity carChargeEntity = CarChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", monthlyMethodId));
        carMonthlyVehicle.setUid(UserUtils.randomUUID());
        carMonthlyVehicle.setCommunityId(communityId);
        carMonthlyVehicle.setDistributionStatus(0);//新增默认是未下发
        carMonthlyVehicle.setMonthlyMethodId(carChargeEntity.getUid());//存收费设置里面的id
        carMonthlyVehicle.setMonthlyMethodName(carChargeEntity.getName());//存收费设置里面的名字

        int insert = carMonthlyVehicleMapper.insert(carMonthlyVehicle);



        //存入车辆管理 默认是已绑定
        BindingPositionEntity entity = new BindingPositionEntity();
        entity.setUid(UserUtils.randomUUID());//uuid主键
        entity.setBindingStatus(1);//默认绑定
        entity.setCommunityId(communityId);//社区id
        entity.setCarNumber(carMonthlyVehicle.getCarNumber());//车牌号
        entity.setPositionId(carMonthlyVehicle.getCarPosition());//车位号
        bindingPositionMapper.insert(entity);




        //修改车位的信息：为已绑定 开始结束时间变更
        CarPositionEntity carPositionEntity = new CarPositionEntity();
        carPositionEntity.setBindingStatus(1);//已绑定
        carPositionEntity.setCarPosStatus(2);//租赁状态
        carPositionEntity.setOwnerPhone(carMonthlyVehicle.getPhone());//联系电话
        carPositionEntity.setRemark(carMonthlyVehicle.getRemarks());//备注
        carPositionEntity.setBeginTime(carMonthlyVehicle.getStartTime());//开始时间
        carPositionEntity.setEndTime(carMonthlyVehicle.getEndTime());//结束时间
        carPositionEntity.setUserName(carMonthlyVehicle.getOwnerName());//租户姓名
        carPositionMapper.update(carPositionEntity,new QueryWrapper<CarPositionEntity>().eq("car_position",carMonthlyVehicle.getCarPosition()).eq("community_id",carMonthlyVehicle.getCommunityId()));


        //保存车辆数据到基础车辆表t_car中
        CarPositionEntity car_position = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("car_position", carMonthlyVehicle.getCarPosition()));
        CarEntity carEntity = new CarEntity();
        carEntity.setId(SnowFlake.nextId());//雪花算法生成ID
        carEntity.setCommunityId(communityId);//社区id
        carEntity.setCarPositionId(car_position.getId());//车位id
        carEntity.setCarPlate(carMonthlyVehicle.getCarNumber());//车辆牌照
        carEntity.setContact(carMonthlyVehicle.getPhone());//联系方式
        carEntity.setOwner(carMonthlyVehicle.getOwnerName());//车辆所属人
        carEntity.setType(2);//月租
        carEntity.setCreateTime(LocalDateTime.now());//创建时间
        carMapper.insert(carEntity);



        //生成月租账单
        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
        orderEntity.setCommunityId(communityId);//社区id
        orderEntity.setPropertyFee(carMonthlyVehicle.getMonthlyFee());//包月费用
        orderEntity.setOrderStatus(1);//已收款
        orderEntity.setCreateTime(LocalDateTime.now());//创建时间
        orderEntity.setPayType(2);//线下物业支付
        orderEntity.setPayTime(LocalDateTime.now());//支付时间
        orderEntity.setBeginTime(carMonthlyVehicle.getStartTime().toLocalDate());//账单开始时间
        orderEntity.setOverTime(carMonthlyVehicle.getEndTime().toLocalDate());//账单结束时间
        orderEntity.setBuildType(1);//系统生成

        PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("deleted", 0)
                .eq("community_id", communityId)
                .eq("type", 12)
                .eq("disposable", 2)
        );

        orderEntity.setFeeRuleId(propertyFeeRuleEntity.getId());//缴费项目id
        orderEntity.setType(propertyFeeRuleEntity.getType());//账单类型
        orderEntity.setAssociatedType(2);//关联类型车位
        orderEntity.setOrderTime(LocalDate.now());//账单月份
        String orderNum = FinanceBillServiceImpl.getOrderNum(String.valueOf(communityId));
        orderEntity.setOrderNum(orderNum);//账单号
        orderEntity.setTargetId(car_position.getId());//车位id

        return insert;
    }

    /**
     * 修改
     * @param carMonthlyVehicle
     * @return
     */
    @Override
    @Transactional
    public Integer UpdateMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle) {
        String monthlyMethodId = carMonthlyVehicle.getMonthlyMethodId();
        CarChargeEntity carChargeEntity = CarChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", monthlyMethodId));


        carMonthlyVehicle.setMonthlyMethodName(carChargeEntity.getName());
        int update = carMonthlyVehicleMapper.update(carMonthlyVehicle, new QueryWrapper<CarMonthlyVehicle>().eq("uid", carMonthlyVehicle.getUid()));

        return update;
    }

    /**
     * 根据uuid删除
     * @param uid
     * @return
     */
    @Override
    @Transactional
    public Integer DelMonthlyVehicle(String uid) {

        CarMonthlyVehicle vehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("uid", uid));

        //包月未到期的数据不能删除
        long endTime = vehicle.getEndTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long localTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if (localTime<=endTime){
            throw new PropertyException("包月未到期的数据不能删除！");

        }

        //修改车位的信息：为未绑定 其他信息全部真置空
        carPositionMapper.update(new CarPositionEntity(),new UpdateWrapper<CarPositionEntity>()
                .eq("car_position",vehicle.getCarPosition())
                .eq("community_id",vehicle.getCommunityId())
                .set("car_pos_status",0)
                .set("binding_status",0)
                .set("owner_phone",null)
                .set("user_name",null)
                .set("begin_time",null)
                .set("end_time",null)
                .set("remark",null)
        );
        int delete = carMonthlyVehicleMapper.delete(new QueryWrapper<CarMonthlyVehicle>().eq("uid", uid));
        return delete;
    }

    /**
     * 多条件查询+分页
     * @param carMonthlyVehicleQO
     * @return
     */
    @Override
    public PageInfo FindByMultiConditionPage(CarMonthlyVehicleQO carMonthlyVehicleQO,Long communityId) {
        carMonthlyVehicleQO.setCommunityId(communityId);
        Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        Page<CarMonthlyVehicle> page = new Page<>(carMonthlyVehicleQO.getPage(),carMonthlyVehicleQO.getSize());
        IPage<CarMonthlyVehicle> iPage = carMonthlyVehicleMapper.FindByMultiConditionPage(page, carMonthlyVehicleQO);

        List<CarMonthlyVehicle> collect = iPage.getRecords().stream().map(x -> {
            if (now > x.getEndTime().toEpochSecond(ZoneOffset.of("+8"))) {
                //到期
                x.setExpirationStatus("0");
            }else {
                //未到期
                x.setExpirationStatus("1");
            }
            return x;
        }).collect(Collectors.toList());
        PageInfo<CarMonthlyVehicle> pageInfo = new PageInfo<>();
        pageInfo.setRecords(collect);
        pageInfo.setSize(iPage.getSize());
        pageInfo.setCurrent(iPage.getCurrent());
        pageInfo.setTotal(iPage.getTotal());
        return pageInfo;
    }
    /**
     * 包月延期 0 按天 1 按月
     * @param carMonthlyDelayQO
     */
    @Override
    @Transactional
    public void delay(CarMonthlyDelayQO carMonthlyDelayQO) {

        Integer type = carMonthlyDelayQO.getType();
        Long communityId = carMonthlyDelayQO.getCommunityId();
        Integer dayNum = carMonthlyDelayQO.getDayNum();
        BigDecimal fee = carMonthlyDelayQO.getFee();
        String uid = carMonthlyDelayQO.getUid();

        if (Objects.isNull(type)){
            throw new PropertyException(-1,"请先选择按天还是按月!");
        }

        if (Objects.isNull(fee)){
            throw new PropertyException(-1,"费用必填项!");
        }

        if (BigDecimal.ZERO.compareTo(fee)==0 || BigDecimal.ZERO.compareTo(fee)==1){
            throw new PropertyException(-1,"费用不能小于0或者等于0!");
        }

        CarMonthlyVehicle carMonthlyVehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("uid", uid));
        if (Objects.isNull(carMonthlyVehicle)){
            throw new PropertyException(-1,"uid错误,这条数据不存在！");
        }
        BigDecimal monthlyFee = carMonthlyVehicle.getMonthlyFee();
        LocalDateTime endTime = carMonthlyVehicle.getEndTime();

        if (type==1){
            LocalDateTime time = endTime.plusMonths(dayNum);
            carMonthlyVehicleMapper.update(carMonthlyVehicle,new UpdateWrapper<CarMonthlyVehicle>().eq("uid",uid).set("end_time",time).set("monthly_fee",monthlyFee.add(fee)));

        }
        if (type==0){
            LocalDateTime time = endTime.plusDays(dayNum);
            carMonthlyVehicleMapper.update(carMonthlyVehicle,new UpdateWrapper<CarMonthlyVehicle>().eq("uid",uid).set("end_time",time).set("monthly_fee",monthlyFee.add(fee)));
        }
        //再查出延期后开始 结束时间
        CarMonthlyVehicle reCarMonthlyVehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("uid", uid));

        //修改车位的信息：为已绑定 结束时间变更
        CarPositionEntity carPositionEntity = new CarPositionEntity();
        carPositionEntity.setBindingStatus(1);//已绑定
        carPositionEntity.setCarPosStatus(2);//租赁状态
        carPositionEntity.setOwnerPhone(reCarMonthlyVehicle.getPhone());//联系电话
        carPositionEntity.setRemark(reCarMonthlyVehicle.getRemarks());//备注
        carPositionEntity.setBeginTime(reCarMonthlyVehicle.getStartTime());//开始时间
        carPositionEntity.setEndTime(reCarMonthlyVehicle.getEndTime());//延期后的结束时间
        carPositionEntity.setUserName(reCarMonthlyVehicle.getOwnerName());//租户姓名
        carPositionMapper.update(carPositionEntity,new QueryWrapper<CarPositionEntity>().eq("car_position",carMonthlyVehicle.getCarPosition()).eq("community_id",carMonthlyVehicle.getCommunityId()));



        CarPositionEntity car_position = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("car_position", carMonthlyVehicle.getCarPosition()));
        //生成月租账单
        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
        orderEntity.setCommunityId(communityId);//社区id
        orderEntity.setPropertyFee(carMonthlyVehicle.getMonthlyFee());//包月费用
        orderEntity.setOrderStatus(1);//已收款
        orderEntity.setCreateTime(LocalDateTime.now());//创建时间
        orderEntity.setPayType(2);//线下物业支付
        orderEntity.setPayTime(LocalDateTime.now());//支付时间
        orderEntity.setBeginTime(carMonthlyVehicle.getStartTime().toLocalDate());//账单开始时间
        orderEntity.setOverTime(carMonthlyVehicle.getEndTime().toLocalDate());//账单结束时间
        orderEntity.setBuildType(1);//系统生成

        PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("deleted", 0)
                .eq("community_id", communityId)
                .eq("type", 12)
                .eq("disposable", 2)
        );
        orderEntity.setFeeRuleId(propertyFeeRuleEntity.getId());//缴费项目id
        orderEntity.setType(propertyFeeRuleEntity.getType());//账单类型
        orderEntity.setAssociatedType(2);//关联类型车位
        orderEntity.setOrderTime(LocalDate.now());//账单月份
        String orderNum = FinanceBillServiceImpl.getOrderNum(String.valueOf(communityId));
        orderEntity.setOrderNum(orderNum);//账单号
        orderEntity.setTargetId(car_position.getId());//车位id
    }


    /**
     * 车辆
     * @param carMonthlyVehicleQO
     * @return
     */
    @Override
    public List<CarMonthlyVehicle> selectListCar(CarMonthlyVehicleQO carMonthlyVehicleQO) {
        List<CarMonthlyVehicle> list = carMonthlyVehicleMapper.selectListQueryCar(carMonthlyVehicleQO);
        return list;
    }


    /**
     * 车位
     */
    public List<CarMonthlyVehicle> selectListPostion(CarMonthlyVehicleQO carMonthlyVehicleQO) {
        List<CarMonthlyVehicle> list = carMonthlyVehicleMapper.selectListQueryPostion(carMonthlyVehicleQO);
        return list;
    }


    /**
     * 数据导入 2.0
     * @param strings
     * @param communityId
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> addLinkByExcel2(List<String[]> strings, Long communityId) {

        // 成功数
        int success = 0;
        // 失败数
        int fail = 0;
        // 失败明细数据
        List<Map<String, String>> failStaffList = new ArrayList<>();
        //时间格式化模板
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //返回MAP
        Map<String, Object> resultMap = new HashMap<>();

        labe:for (String[] string : strings) {
            for (int i = 0; i <string.length; i++) {
                if (StringUtils.isBlank(string[i])){
                    throw new PropertyException("数据表格中不能存在空值");
                }
            }
            CarMonthlyVehicle vehicle = new CarMonthlyVehicle();
            //uuid
            vehicle.setUid(UserUtils.randomUUID());
            //车牌号
            String carNumber=string[0];
            if (StringUtils.isBlank(carNumber)){
                throw new PropertyException("车牌号不能存在空值");
            }
            vehicle.setCarNumber(carNumber);
            //车主姓名
            String ownerName= string[1];
            vehicle.setOwnerName(ownerName);
            //联系电话
            String phone=string[2];
            vehicle.setPhone(phone);
            //包月方式 用户随意输入 只和最终的金额有关
            String monthlyMethodName =string[3];
            vehicle.setMonthlyMethodName(monthlyMethodName);
            //开始时间
            LocalDateTime startTime=LocalDateTime.parse(string[4],df);
            vehicle.setStartTime(startTime);
            //结束时间
            LocalDateTime endTime=LocalDateTime.parse(string[5],df);
            if ( LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()>=endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()){
                throw new PropertyException("请检查你的开始和结束时间，请勿导入过期数据！");
            }
            vehicle.setEndTime(endTime);
            //包月费用
            BigDecimal monthlyFee=new BigDecimal(string[6]);
            vehicle.setMonthlyFee(monthlyFee);
            //备注
            String remarks=string[7];
            vehicle.setRemarks(remarks);
            //车位编号
            String carPosition=string[8];
            vehicle.setCarPosition(carPosition);
            //社区ID
            Long getCommunityId=communityId;
            vehicle.setCommunityId(getCommunityId);

            //收费设置id
            /*CarChargeEntity carChargeEntity = CarChargeMapper.selectList(new QueryWrapper<CarChargeEntity>().eq("name", monthlyMethodName)).get(0);
            if (Objects.isNull(carChargeEntity)){
                throw new PropertyException("你填写的模板名称有错误！请按收费设置里面的模板名称填写！");
            }*/
            /*String monthlyMethodId = carChargeEntity.getUid();
            vehicle.setMonthlyMethodId(monthlyMethodId);*/

            // 如果Excel中有与数据库中 有相同社区下的同一条数据则不能添加成功 （用停车的开始时间和结束时间来区分）
            List<CarMonthlyVehicle> recarMonthlyVehicles = carMonthlyVehicleMapper.selectList(new QueryWrapper<CarMonthlyVehicle>().eq("car_number", carNumber).eq("community_id", communityId));
            for (CarMonthlyVehicle carMonthlyVehicle : recarMonthlyVehicles) {
                if (!Objects.isNull(carMonthlyVehicle)){
                    if (vehicle.getStartTime().equals(carMonthlyVehicle.getStartTime())){
                        if (vehicle.getEndTime().equals(carMonthlyVehicle.getEndTime())){
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("车牌号",string[0]);
                            hashMap.put("车主姓名",string[1]);
                            hashMap.put("联系电话",string[2]);
                            hashMap.put("包月方式",string[3]);
                            hashMap.put("开始时间",string[4]);
                            hashMap.put("结束时间",string[5]);
                            hashMap.put("包月费用",string[6]);
                            //hashMap.put("下发状态",string[7]);
                            hashMap.put("备注",string[7]);
                            hashMap.put("车位编号",string[8]);
                            failStaffList.add(hashMap);
                            fail += 1;//失败数据累加
                            continue labe;
                        }
                    }
             }

            }
            //成功数累加
            success += 1;
            carMonthlyVehicleMapper.insert(vehicle);


            //存入车辆管理 默认是已绑定
            BindingPositionEntity entity = new BindingPositionEntity();
            entity.setUid(UserUtils.randomUUID());//uuid主键
            entity.setBindingStatus(1);//默认绑定
            entity.setCommunityId(communityId);//社区id
            entity.setCarNumber(carNumber);//车牌号
            entity.setPositionId(carPosition);//车位号
            bindingPositionMapper.insert(entity);

            //修改车位的信息：为已绑定 结束时间变更
            CarPositionEntity carPositionEntity = new CarPositionEntity();
            carPositionEntity.setBindingStatus(1);//已绑定
            carPositionEntity.setCarPosStatus(2);//租赁状态
            carPositionEntity.setOwnerPhone(phone);//联系电话
            carPositionEntity.setRemark(remarks);//备注
            carPositionEntity.setBeginTime(startTime);//开始时间
            carPositionEntity.setEndTime(endTime);//延期后的结束时间
            carPositionEntity.setUserName(ownerName);//租户姓名
            carPositionMapper.update(carPositionEntity,new QueryWrapper<CarPositionEntity>().eq("car_position",carPosition).eq("community_id",communityId));


            //保存车辆数据到基础车辆表t_car中
            CarPositionEntity car_position = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("car_position", carPosition));
            CarEntity carEntity = new CarEntity();
            carEntity.setId(SnowFlake.nextId());//雪花算法生成ID
            carEntity.setCommunityId(communityId);//社区id
            carEntity.setCarPositionId(car_position.getId());//车位id
            carEntity.setCarPlate(carNumber);//车辆牌照
            carEntity.setContact(phone);//联系方式
            carEntity.setOwner(ownerName);//车辆所属人
            carEntity.setType(2);//月租
            carEntity.setCreateTime(LocalDateTime.now());//创建时间
            carMapper.insert(carEntity);



            //生成月租账单
            PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
            orderEntity.setCommunityId(communityId);//社区id
            orderEntity.setTotalMoney(monthlyFee);//包月费用
            orderEntity.setOrderStatus(1);//已收款
            orderEntity.setCreateTime(LocalDateTime.now());//创建时间
            orderEntity.setPayType(2);//线下物业支付
            orderEntity.setPayTime(LocalDateTime.now());//支付时间
            orderEntity.setBeginTime(startTime.toLocalDate());//账单开始时间
            orderEntity.setOverTime(endTime.toLocalDate());//账单结束时间
            orderEntity.setBuildType(1);//系统生成

            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>()
                    .eq("status", 1)
                    .eq("deleted", 0)
                    .eq("community_id", communityId)
                    .eq("type", 12)
                    .eq("disposable", 2)
            );
            orderEntity.setFeeRuleId(propertyFeeRuleEntity.getId());//缴费项目id
            orderEntity.setType(propertyFeeRuleEntity.getType());//账单类型
            orderEntity.setAssociatedType(2);//关联类型车位
            orderEntity.setOrderTime(LocalDate.now());//账单月份
            String orderNum = FinanceBillServiceImpl.getOrderNum(String.valueOf(communityId));
            orderEntity.setOrderNum(orderNum);//账单号
            orderEntity.setTargetId(car_position.getId());//车位id


        }
        resultMap.put("success", "成功" + success + "条");
        resultMap.put("fail", "失败" + fail + "条");
        resultMap.put("failData", failStaffList);
        return resultMap;
    }

    /**
     * 下发（修改）
     * @param uid
     * @param adminCommunityId
     */
    @Override
    public void issue(String uid, Long adminCommunityId) {
        CarMonthlyVehicle carMonthlyVehicle = new CarMonthlyVehicle();
        carMonthlyVehicle.setDistributionStatus(1);
        carMonthlyVehicleMapper.update(carMonthlyVehicle,new QueryWrapper<CarMonthlyVehicle>().eq("uid",uid).eq("community_id",adminCommunityId));
    }


    /**
     * 1临时，2月租，3业主
     * @param carNumber 车牌号
     * @param carColor 车牌颜色
     * @param community_id 社区id
     * @return map
     */
    @Override
    public Map selectByStatus(String carNumber, String carColor, Long community_id) {

        //查询未到期的、已下发的包月车辆
        CarMonthlyVehicle carMonthlyVehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("car_number", carNumber).eq("community_id", community_id).eq("distribution_status",1).ge("end_time", LocalDateTime.now()));
        if (Objects.nonNull(carMonthlyVehicle)){
            HashMap<Integer, CarMonthlyVehicle> hashMap = new HashMap<>();
            CarMonthlyVehicle vehicle = new CarMonthlyVehicle();
            BeanUtil.copyProperties(carMonthlyVehicle,vehicle);
            hashMap.put(2,vehicle);
            return hashMap;//月租车辆
        }

        CarProprietorEntity carProprietorEntity = carProprietorMapper.selectOne(new QueryWrapper<CarProprietorEntity>().eq("car_number", carNumber).eq("community_id", community_id).eq("deleted", 0));
        if (Objects.nonNull(carProprietorEntity)){
            HashMap<Integer, CarProprietorEntity> hashMap = new HashMap<>();
            CarProprietorEntity proprietorEntity = new CarProprietorEntity();
            BeanUtil.copyProperties(carProprietorEntity,proprietorEntity);
            hashMap.put(3,proprietorEntity);
            return hashMap;//业主车辆
        }


        //临时车 黄牌

        if ( StringUtils.containsAny(carColor,"黄色","黄牌","黄")){
            CarChargeEntity carChargeEntity = CarChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("community_id", community_id).eq("type", 1).eq(StringUtils.isNotBlank(carColor),"plate_type", 0));
            HashMap<Integer, CarChargeEntity> HashMap = new HashMap<>();
            CarChargeEntity chargeEntity = new CarChargeEntity();
            BeanUtil.copyProperties(carChargeEntity,chargeEntity);
            HashMap.put(1,chargeEntity);
            return HashMap;
        }
        //临时车 其他拍照
        CarChargeEntity carChargeEntity = CarChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("community_id", community_id).eq("type", 1).eq(StringUtils.isNotBlank(carColor),"plate_type", 1));
        HashMap<Integer, CarChargeEntity> HashMap = new HashMap<>();
        CarChargeEntity chargeEntity = new CarChargeEntity();
        BeanUtil.copyProperties(carChargeEntity,chargeEntity);
        HashMap.put(1,chargeEntity);
        return HashMap;
    }

    /***********************************************************************包月车位***********************************************************************************************/
    /**
     * 多条件+分页查询包月车位
     */
    @Override
    public PageInfo findByMultiConditionPage2Position(CarMonthlyVehicleQO carMonthlyVehicleQO,Long communityId) {
        carMonthlyVehicleQO.setCommunityId(communityId);
        Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        Page<CarMonthlyVehicle> page = new Page<>(carMonthlyVehicleQO.getPage(),carMonthlyVehicleQO.getSize());
        IPage<CarMonthlyVehicle> iPage = carMonthlyVehicleMapper.FindByMultiConditionPage2Position(page, carMonthlyVehicleQO);
        List<CarMonthlyVehicle> collect = iPage.getRecords().stream().map(x -> {
            HouseEntity houseEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>().eq("type", 4).eq("community_id", communityId).eq("id", x.getHouseId()).eq("deleted",0));
            if (Objects.nonNull(houseEntity)){
                String building = houseEntity.getBuilding();//楼栋
                String unit = houseEntity.getUnit();//单元
                Integer floor = houseEntity.getFloor();//楼层
                String door = houseEntity.getDoor();//门牌
                String belongHouse=building+unit+floor+door;
                x.setBelongHouse(belongHouse);
            }else {
                x.setBelongHouse("未绑定楼栋");
            }
            if (now > x.getEndTime().toEpochSecond(ZoneOffset.of("+8"))) {
                //到期
                x.setExpirationStatus("0");
            }else {
                //未到期
                x.setExpirationStatus("1");
            }
            return x;
        }).collect(Collectors.toList());
        PageInfo<CarMonthlyVehicle> pageInfo = new PageInfo<>();
        pageInfo.setRecords(collect);
        pageInfo.setSize(iPage.getSize());
        pageInfo.setCurrent(iPage.getCurrent());
        pageInfo.setTotal(iPage.getTotal());
        return pageInfo;
    }

    /**
     * 包月车位新增
     * @param carMonthlyVehicle
     * @param communityId 社区id
     */
    @Override
    public Integer SaveMonthlyVehicle2Position(CarMonthlyVehicle carMonthlyVehicle, Long communityId) {
        if (Objects.isNull(carMonthlyVehicle.getStartTime())){
            throw new PropertyException("开始时间不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getEndTime())){
            throw new PropertyException("结束时间不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getMonthlyFee())){
            throw new PropertyException("包月费用不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getPhone())){
            throw new PropertyException("手机号码不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getCarNumber())){
            throw new PropertyException("车牌号不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getOwnerName())){
            throw new PropertyException("车主姓名不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getCarPosition())){
            throw new PropertyException("车位号不能为空！");
        }
        /*if (Objects.isNull(carMonthlyVehicle.getMonthlyMethodId())){
            throw new PropertyException("包月方式不能为空！");
        }*/

       /* if (Objects.isNull(carMonthlyVehicle.getMonthlyMethodId())){
            throw new PropertyException("楼宇/单元不能为空！");
        }
        if (Objects.isNull(carMonthlyVehicle.getMonthlyMethodId())){
            throw new PropertyException("所属房屋不能为空！");
        }*/

        //查询黑名单中是否存在该车辆
        CarBlackListEntity car_number = carBlackListMapper.selectOne(new QueryWrapper<CarBlackListEntity>().eq("car_number", carMonthlyVehicle.getCarNumber()));
        if (Objects.nonNull(car_number)){
            throw new PropertyException("该车辆已进入黑名单，无法包月!");
        }

        //查询该车位是否已经出租
        CarPositionEntity positionEntity = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("binding_status", 1).eq("car_pos_status", 2).eq("community_id", communityId).eq("car_position", carMonthlyVehicle.getCarPosition()));
        if (Objects.nonNull(positionEntity)){
            throw new PropertyException("该车位已被出租，如果需要延期，请查询记录执行时间延期操作！");
        }
        //查询数据库是否正在包月中
        CarMonthlyVehicle vehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("car_number", carMonthlyVehicle.getCarNumber()).ge("end_time",carMonthlyVehicle.getEndTime()));
        if (Objects.nonNull(vehicle)){
            throw new PropertyException("已进行包月，如果需要延期，请查询记录执行时间延期操作！");
        }

        //查询基础设置里面的最大续费月数
        CarBasicsEntity carBasicsEntity = carBasicsMapper.selectOne(new QueryWrapper<CarBasicsEntity>().eq("community_id", communityId));
        Integer monthMaxTime = carBasicsEntity.getMonthMaxTime();//最大续费月数
        LocalDateTime startTime = carMonthlyVehicle.getStartTime();
        LocalDateTime endTime = carMonthlyVehicle.getEndTime();
        long tempTime = Duration.between(startTime, endTime).toMinutes();//相差分钟
        if (tempTime>monthMaxTime*30*24*60){
            throw new PropertyException("当前包月时间已超过你设置的最大包月数，"+monthMaxTime+"个月！");
        }

        //查询收费设置数据
        /*String monthlyMethodId = carMonthlyVehicle.getMonthlyMethodId();
        CarChargeEntity carChargeEntity = CarChargeMapper.selectOne(new QueryWrapper<CarChargeEntity>().eq("uid", monthlyMethodId));*/

        carMonthlyVehicle.setUid(UserUtils.randomUUID());
        carMonthlyVehicle.setCommunityId(communityId);
        carMonthlyVehicle.setDistributionStatus(0);//新增默认是未下发

        /*carMonthlyVehicle.setMonthlyMethodId(carChargeEntity.getUid());//存收费设置里面的id
        carMonthlyVehicle.setMonthlyMethodName(carChargeEntity.getName());//存收费设置里面的名字*/
        int insert = carMonthlyVehicleMapper.insert(carMonthlyVehicle);


        //存入车辆管理 默认是已绑定
        BindingPositionEntity entity = new BindingPositionEntity();
        entity.setUid(UserUtils.randomUUID());//uuid主键
        entity.setBindingStatus(1);//默认绑定
        entity.setCommunityId(communityId);//社区id
        entity.setCarNumber(carMonthlyVehicle.getCarNumber());//车牌号
        entity.setPositionId(carMonthlyVehicle.getCarPosition());//车位号
        bindingPositionMapper.insert(entity);


        //修改车位的信息：为已绑定 开始结束时间变更
        CarPositionEntity carPositionEntity = new CarPositionEntity();
        carPositionEntity.setBindingStatus(1);//已绑定
        carPositionEntity.setCarPosStatus(2);//租赁状态
        carPositionEntity.setOwnerPhone(carMonthlyVehicle.getPhone());//联系电话
        carPositionEntity.setRemark(carMonthlyVehicle.getRemarks());//备注
        carPositionEntity.setBeginTime(carMonthlyVehicle.getStartTime());//开始时间
        carPositionEntity.setEndTime(carMonthlyVehicle.getEndTime());//结束时间
        carPositionEntity.setUserName(carMonthlyVehicle.getOwnerName());//租户姓名
        carPositionMapper.update(carPositionEntity,new QueryWrapper<CarPositionEntity>().eq("car_position",carMonthlyVehicle.getCarPosition()).eq("community_id",carMonthlyVehicle.getCommunityId()));


        //保存车辆数据到基础车辆表t_car中
        CarPositionEntity car_position = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("car_position", carMonthlyVehicle.getCarPosition()));
        CarEntity carEntity = new CarEntity();
        carEntity.setId(SnowFlake.nextId());//雪花算法生成ID
        carEntity.setCommunityId(communityId);//社区id
        carEntity.setCarPositionId(car_position.getId());//车位id
        carEntity.setCarPlate(carMonthlyVehicle.getCarNumber());//车辆牌照
        carEntity.setContact(carMonthlyVehicle.getPhone());//联系方式
        carEntity.setOwner(carMonthlyVehicle.getOwnerName());//车辆所属人
        carEntity.setType(2);//月租
        carEntity.setCreateTime(LocalDateTime.now());//创建时间
        carMapper.insert(carEntity);



        //生成月租账单
        PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
        orderEntity.setCommunityId(communityId);//社区id
        orderEntity.setPropertyFee(carMonthlyVehicle.getMonthlyFee());//包月费用
        orderEntity.setOrderStatus(1);//已收款
        orderEntity.setCreateTime(LocalDateTime.now());//创建时间
        orderEntity.setPayType(2);//线下物业支付
        orderEntity.setPayTime(LocalDateTime.now());//支付时间
        orderEntity.setBeginTime(carMonthlyVehicle.getStartTime().toLocalDate());//账单开始时间
        orderEntity.setOverTime(carMonthlyVehicle.getEndTime().toLocalDate());//账单结束时间
        orderEntity.setBuildType(1);//系统生成

        PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>()
                .eq("status", 1)
                .eq("deleted", 0)
                .eq("community_id", communityId)
                .eq("type", 12)
                .eq("disposable", 2)
        );
        orderEntity.setFeeRuleId(propertyFeeRuleEntity.getId());//缴费项目id
        orderEntity.setType(propertyFeeRuleEntity.getType());//账单类型
        orderEntity.setAssociatedType(2);//关联类型车位
        orderEntity.setOrderTime(LocalDate.now());//账单月份
        String orderNum = FinanceBillServiceImpl.getOrderNum(String.valueOf(communityId));
        orderEntity.setOrderNum(orderNum);//账单号
        orderEntity.setTargetId(car_position.getId());//车位id


        return insert;
    }


    /**
     * 包月车位数据导入
     * @param strings
     * @param communityId
     * @return
     */
    @Override
    public Map<String, Object> addLinkByExcel2Position(List<String[]> strings, Long communityId) {
        // 成功数
        int success = 0;
        // 失败数
        int fail = 0;
        // 失败明细数据
        List<Map<String, String>> failStaffList = new ArrayList<>();
        //时间格式化模板
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //返回MAP
        Map<String, Object> resultMap = new HashMap<>();

        labe:for (String[] string : strings) {
            for (int i = 0; i <string.length; i++) {
                if (StringUtils.isBlank(string[i])){
                    throw new PropertyException("数据表格中不能存在空值");
                }
            }
            CarMonthlyVehicle vehicle = new CarMonthlyVehicle();
            //下发状态
            vehicle.setDistributionStatus(0);//未下发
            //uuid
            vehicle.setUid(UserUtils.randomUUID());
            //车位号
            String carPosition=string[0];
            if (StringUtils.isBlank(carPosition)){
                throw new PropertyException("车位号不能存在空值");
            }
            vehicle.setCarPosition(carPosition);
            //车主姓名
            String ownerName= string[1];
            vehicle.setOwnerName(ownerName);
            //联系电话
            String phone=string[2];
            vehicle.setPhone(phone);
            //车牌号
            String carNumber = string[3];
            vehicle.setCarNumber(carNumber);
            //开始时间
            LocalDateTime startTime=LocalDateTime.parse(string[4],df);
            vehicle.setStartTime(startTime);
            //结束时间
            LocalDateTime endTime=LocalDateTime.parse(string[5],df);
            if ( LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()>=endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()){
                throw new PropertyException("请检查你的开始和结束时间，请勿导入过期数据！");
            }
            vehicle.setEndTime(endTime);
            //包月费用
            BigDecimal monthlyFee=new BigDecimal(string[6]);
            vehicle.setMonthlyFee(monthlyFee);
            //备注
            String remarks=string[7];
            vehicle.setRemarks(remarks);

            //社区ID
            Long getCommunityId=communityId;
            vehicle.setCommunityId(getCommunityId);


            //查询该车位是否被租用
            CarPositionEntity positionEntity = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("car_position", carPosition));
            if (Objects.isNull(positionEntity)){
                throw new PropertyException("你导入的数据中车位号错误！找不到对应的车位！");
            }
            if (positionEntity.getBindingStatus()==1
                    &&positionEntity.getCarPosStatus()==2
            ){
                throw new PropertyException("你导入的数据中有车位正在被租用！或者存在车位已过期的包月数据！");
            }


            // 如果Excel中有与数据库中 有相同社区下的同一条数据则不能添加成功 （用停车的开始时间和结束时间来区分）
            List<CarMonthlyVehicle> recarMonthlyVehicles = carMonthlyVehicleMapper.selectList(new QueryWrapper<CarMonthlyVehicle>().eq("car_number", carNumber).eq("community_id", communityId));
            for (CarMonthlyVehicle carMonthlyVehicle : recarMonthlyVehicles) {
                if (!Objects.isNull(carMonthlyVehicle)){
                    if (vehicle.getStartTime().equals(carMonthlyVehicle.getStartTime())){
                        if (vehicle.getEndTime().equals(carMonthlyVehicle.getEndTime())){
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("车位号",string[0]);
                            hashMap.put("车主姓名",string[1]);
                            hashMap.put("联系电话",string[2]);
                            hashMap.put("车牌号",string[3]);
                            hashMap.put("开始时间",string[4]);
                            hashMap.put("结束时间",string[5]);
                            hashMap.put("包月费用",string[6]);
                            hashMap.put("备注",string[7]);
                            failStaffList.add(hashMap);
                            fail += 1;//失败数据累加
                            continue labe;
                        }
                    }
                }

            }
            //成功数累加
            success += 1;


            carMonthlyVehicleMapper.insert(vehicle);

            //存入车辆管理 默认是已绑定
            BindingPositionEntity entity = new BindingPositionEntity();
            entity.setUid(UserUtils.randomUUID());//uuid主键
            entity.setBindingStatus(1);//默认绑定
            entity.setCommunityId(communityId);//社区id
            entity.setCarNumber(carNumber);//车牌号
            entity.setPositionId(carPosition);//车位号
            bindingPositionMapper.insert(entity);

            //修改车位的信息：为已绑定 结束时间变更
            CarPositionEntity carPositionEntity = new CarPositionEntity();
            carPositionEntity.setBindingStatus(1);//已绑定
            carPositionEntity.setCarPosStatus(2);//租赁状态
            carPositionEntity.setOwnerPhone(phone);//联系电话
            carPositionEntity.setRemark(remarks);//备注
            carPositionEntity.setBeginTime(startTime);//开始时间
            carPositionEntity.setEndTime(endTime);//延期后的结束时间
            carPositionEntity.setUserName(ownerName);//租户姓名

            carPositionMapper.update(carPositionEntity,new QueryWrapper<CarPositionEntity>().eq("car_position",carPosition).eq("community_id",communityId));


            //保存车辆数据到基础车辆表t_car中
            CarPositionEntity car_position = carPositionMapper.selectOne(new QueryWrapper<CarPositionEntity>().eq("car_position", carPosition));
            CarEntity carEntity = new CarEntity();
            carEntity.setId(SnowFlake.nextId());//雪花算法生成ID
            carEntity.setCommunityId(communityId);//社区id
            carEntity.setCarPositionId(car_position.getId());//车位id
            carEntity.setCarPlate(carNumber);//车辆牌照
            carEntity.setContact(phone);//联系方式
            carEntity.setOwner(ownerName);//车辆所属人
            carEntity.setType(2);//月租
            carEntity.setCreateTime(LocalDateTime.now());//创建时间
            carMapper.insert(carEntity);

            UserUtils.randomUUID();


            //生成月租账单
            PropertyFinanceOrderEntity orderEntity = new PropertyFinanceOrderEntity();
            orderEntity.setCommunityId(communityId);//社区id
            orderEntity.setTotalMoney(monthlyFee);//包月费用
            orderEntity.setOrderStatus(1);//已收款
            orderEntity.setCreateTime(LocalDateTime.now());//创建时间
            orderEntity.setPayType(2);//线下物业支付
            orderEntity.setPayTime(LocalDateTime.now());//支付时间
            orderEntity.setBeginTime(startTime.toLocalDate());//账单开始时间
            orderEntity.setOverTime(endTime.toLocalDate());//账单结束时间
            orderEntity.setBuildType(1);//系统生成

            PropertyFeeRuleEntity propertyFeeRuleEntity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>()
                    .eq("status", 1)
                    .eq("deleted", 0)
                    .eq("community_id", communityId)
                    .eq("type", 12)
                    .eq("disposable", 2)
            );
            orderEntity.setFeeRuleId(propertyFeeRuleEntity.getId());//缴费项目id
            orderEntity.setType(propertyFeeRuleEntity.getType());//账单类型
            orderEntity.setAssociatedType(2);//关联类型车位
            orderEntity.setOrderTime(LocalDate.now());//账单月份
            String orderNum = FinanceBillServiceImpl.getOrderNum(String.valueOf(communityId));
            orderEntity.setOrderNum(orderNum);//账单号
            orderEntity.setTargetId(car_position.getId());//车位id

        }
        resultMap.put("success", "成功" + success + "条");
        resultMap.put("fail", "失败" + fail + "条");
        resultMap.put("failData", failStaffList);
        return resultMap;
    }



}
