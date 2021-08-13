package com.jsy.community.service.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarMonthlyVehicleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.CarMonthlyVehicle;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.CarMonthlyVehicleMapper;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@DubboService(version = Const.version, group = Const.group)
public class CarMonthlyVehicleServiceImpl extends ServiceImpl<CarMonthlyVehicleMapper, CarMonthlyVehicle> implements ICarMonthlyVehicleService {

    @Autowired
    private CarMonthlyVehicleMapper carMonthlyVehicleMapper;


    /**
     * 新增
     * @param carMonthlyVehicle
     * @return
     */
    @Override
    public Integer SaveMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle) {
        carMonthlyVehicle.setUid(UserUtils.randomUUID());
        int insert = carMonthlyVehicleMapper.insert(carMonthlyVehicle);
        return insert;
    }

    /**
     * 只能修改 车牌号，车主姓名，电话，备注
     * @param carMonthlyVehicle
     * @return
     */
    @Override
    @Transactional
    public Integer UpdateMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle) {
        int update = carMonthlyVehicleMapper.update(carMonthlyVehicle,
                new UpdateWrapper<CarMonthlyVehicle>().eq("uid", carMonthlyVehicle)
                        .set("car_number",carMonthlyVehicle.getCarNumber())
                        .set("owner_name",carMonthlyVehicle.getOwnerName())
                        .set("phone",carMonthlyVehicle.getPhone())
                        .set("remarks",carMonthlyVehicle.getRemarks()));
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
        carMonthlyVehicleMapper.delete(new QueryWrapper<CarMonthlyVehicle>().eq("uid",uid));
        return null;
    }

    /**
     * 多条件查询+分页
     * @param carMonthlyVehicleQO
     * @return
     */
    @Override
    public PageInfo FindByMultiConditionPage(CarMonthlyVehicleQO carMonthlyVehicleQO) {
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
     * @param fee
     */
    @Override
    @Transactional
    public void delay(String uid,Integer type,Integer dayNum, BigDecimal fee) {

        if (Objects.isNull(type)){
            throw new JSYException(-1,"请先选择按天还是按月!");
        }

        if (Objects.isNull(fee)){
            throw new JSYException(-1,"费用必填项!");
        }

        if (BigDecimal.ZERO.compareTo(fee)==0 || BigDecimal.ZERO.compareTo(fee)==1){
            throw new JSYException(-1,"费用不能小于0或者等于0!");
        }

        CarMonthlyVehicle carMonthlyVehicle = carMonthlyVehicleMapper.selectOne(new QueryWrapper<CarMonthlyVehicle>().eq("uid", uid));
        if (Objects.isNull(carMonthlyVehicle)){
            throw new JSYException(-1,"uid错误,这条数据不存在！");
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
    }

    /**
     * 包月变更 0:地上 1：地下
     * @param uid
     * @param type
     */
    @Override
    @Transactional
    public void monthlyChange(String uid, Integer type) {
        CarMonthlyVehicle carMonthlyVehicle = new CarMonthlyVehicle();
        carMonthlyVehicle.setMonthlyMethod(type);
        carMonthlyVehicleMapper.update(carMonthlyVehicle,new QueryWrapper<CarMonthlyVehicle>().eq("uid",uid));
    }

    /**
     * 数据导入
     * @param strings
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> addLinkByExcel(List<String[]> strings) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 成功数
        int success = 0;
        // 失败数
        int fail = 0;

        // 失败明细数据
        List<Map<String, String>> failStaffList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        for (String[] string : strings) {
            CarMonthlyVehicle vehicle = new CarMonthlyVehicle();
            vehicle.setUid(UserUtils.randomUUID());
            //车牌号
            String carNumber=string[0];
            vehicle.setCarNumber(carNumber);
            //车主姓名
            String ownerName= string[1];
            vehicle.setOwnerName(ownerName);
            //联系电话
            String phone=string[2];
            if (StringUtils.isEmpty(phone)){
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("车牌号",string[0]);
                hashMap.put("车主姓名",string[1]);
                hashMap.put("联系电话",string[2]);
                hashMap.put("包月方式",string[3]);
                hashMap.put("开始时间",string[4]);
                hashMap.put("结束时间",string[5]);
                hashMap.put("包月费用",string[6]);
                hashMap.put("下发状态",string[7]);
                hashMap.put("备注",string[8]);
                hashMap.put("车位编号",string[9]);
                failStaffList.add(hashMap);
                fail += 1;
                continue;
            }
            vehicle.setPhone(phone);
            //包月方式 0:地上 1：地下
            String monthlyMethod =string[3];

            if ("地上".equals(monthlyMethod)){
                vehicle.setMonthlyMethod(0);
            }else if ("地下".equals(monthlyMethod)){
                vehicle.setMonthlyMethod(1);
            }else {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("车牌号",string[0]);
                hashMap.put("车主姓名",string[1]);
                hashMap.put("联系电话",string[2]);
                hashMap.put("包月方式",string[3]);
                hashMap.put("开始时间",string[4]);
                hashMap.put("结束时间",string[5]);
                hashMap.put("包月费用",string[6]);
                hashMap.put("下发状态",string[7]);
                hashMap.put("备注",string[8]);
                hashMap.put("车位编号",string[9]);
                failStaffList.add(hashMap);
                fail += 1;
                continue;
            }
            //开始时间
            LocalDateTime startTime=LocalDateTime.parse(string[4],df);
            vehicle.setStartTime(startTime);
            //结束时间
            LocalDateTime endTime=LocalDateTime.parse(string[5],df);
            vehicle.setEndTime(endTime);
            //包月费用
            BigDecimal monthlyFee=new BigDecimal(string[6]);
            vehicle.setMonthlyFee(monthlyFee);
            //下发状态 0：未下发 1：已下发
            String distributionStatus=(string[7]);
            if("未下发".equals(distributionStatus) || StringUtils.isEmpty(distributionStatus)){
                vehicle.setDistributionStatus(0);
            }else if ("已下发".equals(distributionStatus)){
                vehicle.setDistributionStatus(1);
            }else {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("车牌号",string[0]);
                hashMap.put("车主姓名",string[1]);
                hashMap.put("联系电话",string[2]);
                hashMap.put("包月方式",string[3]);
                hashMap.put("开始时间",string[4]);
                hashMap.put("结束时间",string[5]);
                hashMap.put("包月费用",string[6]);
                hashMap.put("下发状态",string[7]);
                hashMap.put("备注",string[8]);
                hashMap.put("车位编号",string[9]);
                failStaffList.add(hashMap);
                fail += 1;
                continue;
            }
            //备注
            String remarks=string[8];
            vehicle.setRemarks(remarks);
            //车位编号
            String carPosition=string[9];
            vehicle.setCarPosition(carPosition);

            success += 1;
            carMonthlyVehicleMapper.insert(vehicle);
        }
        resultMap.put("success", "成功" + success + "条");
        resultMap.put("fail", "失败" + fail + "条");
        resultMap.put("failData", failStaffList);
        return resultMap;
    }

    /**
     * 查询所有数据，返回list
     */
    public List<CarMonthlyVehicle> selectList() {
        List<CarMonthlyVehicle> list = carMonthlyVehicleMapper.selectList(null);
        return list;
    }



}
