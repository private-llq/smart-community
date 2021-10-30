package com.jsy.community.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
//import com.alibaba.nacos.common.utils.UuidUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.config.ExcelListener;
import com.jsy.community.config.ExcelUtils;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.property.*;
import com.jsy.community.qo.property.*;
import com.jsy.community.util.CarOperation;
import com.jsy.community.util.Crc16Util;
import com.jsy.community.util.OrderNoUtil;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.car.CarVO;
import com.jsy.community.vo.car.GpioData;
import com.jsy.community.vo.car.Rs485Data;
import com.jsy.community.vo.property.OverdueVo;
import com.jsy.community.vo.property.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.entity.ContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * <p>
 * 车位 前端控制器
 * </p>
 *
 * @author Arli
 * @since 2021-08-03
 */

@ApiJSYController
@RequestMapping("/car-position")
@RestController
@Api(tags = "车位模块")
public class CarPositionController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionService iCarPositionService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private PropertyUserService iUserService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionTypeService iCarPositionTypeService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarCutOffService carCutOffService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarEquipmentManageService equipmentManageService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarMonthlyVehicleService iCarMonthlyVehicleService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarBlackListService iCarBlackListService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarBasicsService iCarBasicsService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPatternService iCarPatternService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarOrderService iCarOrderService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarLaneService iCarLaneService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IVisitorService visitorService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarChargeService carChargeService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarProprietorService iCarProprietorService;

    @ApiOperation("分页查询车位信息")
    @Login
    @RequestMapping(value = "/selectCarPositionPaging", method = RequestMethod.POST)
    public CommonResult<PageVO> selectCarPositionPaging(@RequestBody SelectCarPositionPagingQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id

        Page<CarPositionEntity> ss = iCarPositionService.selectCarPositionPaging(qo, adminCommunityId);
        PageVO<CarPositionEntity> pageVO = new PageVO();
        pageVO.setPages(ss.getPages());

        List<CarPositionEntity> records = ss.getRecords();//原来数据
        List<CarPositionTypeEntity> list = iCarPositionTypeService.getAllType();//所有的类型
        for (CarPositionEntity record : records) {
            for (CarPositionTypeEntity entity : list) {
                if (record.getTypeId() == entity.getId()) {
                    record.setTypeCarPosition(entity.getDescription());
                }
            }
        }


        pageVO.setRecords(records);
        pageVO.setCurrent(ss.getCurrent());
        pageVO.setPages(ss.getPages());
        pageVO.setTotal(ss.getTotal());
        return CommonResult.ok(pageVO, "查询成功");
    }
   /* @ApiOperation("条件查询车位信息")
    @Login
    @RequestMapping(value = "/selectCarPosition", method = RequestMethod.POST)
    public CommonResult<List<CarPositionEntity>> selectCarPositionPaging(@RequestBody CarPositionEntity qo) {
        List<CarPositionEntity> carPositionEntity= iCarPositionService.selectCarPosition(qo);
        return CommonResult.ok(carPositionEntity,"查询成功") ;
    }*/


    @ApiOperation("导入车位信息")
    @PostMapping("/carPositionUpload")
    @ResponseBody
    @Login
    @CarOperation(operation = "导入车位信息")
    public String upload(MultipartFile file) throws IOException {
        System.out.println("wenjian" + file);
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        EasyExcel.read(file.getInputStream(), //文件流
                CarPositionEntity.class, //实体类class
                new ExcelListener(iCarPositionService, adminCommunityId))//将调用的server用构造方法传进来
                .sheet(0)//工作表下标从0开始
                .doRead();
        return "success";

    }

    @ApiOperation("导出模板")
    @GetMapping("/carPositionExport")
    @ResponseBody
    @CarOperation(operation = "导出模板")
    public void downLoadFile(HttpServletResponse response) throws IOException {

        ExcelUtils.exportModule("车位信息表", response, CarPositionEntity.class, null, 2);

    }


    @ApiOperation("查询所有可用车位信息")
    @Login
    @RequestMapping(value = "/selectAllCarPosition", method = RequestMethod.POST)
    public CommonResult<List<CarPositionEntity>> selectCarPositionPaging() {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        List<CarPositionEntity> list = iCarPositionService.getAll(adminCommunityId);

        return CommonResult.ok(list, "查询成功");
    }

    @ApiOperation("新增车位信息")
    @Login
    @CarOperation(operation = "新增车位信息")
    @RequestMapping(value = "/insterCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> insterCarPosition(@RequestBody InsterCarPositionQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        Boolean boo = iCarPositionService.insterCarPosition(qo, adminCommunityId);
        return CommonResult.ok(boo, "新增成功");
    }

    @ApiOperation("批量新增车位信息")
    @Login
    @CarOperation(operation = "批量新增车位信息")
    @RequestMapping(value = "/moreInsterCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> moreInsterCarPosition(@RequestBody MoreInsterCarPositionQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id

        Boolean boo = iCarPositionService.moreInsterCarPosition(qo, adminCommunityId);


        return CommonResult.ok(boo, "新增成功");
    }

    @ApiOperation("绑定用户")
    @Login
    @CarOperation(operation = "绑定用户")
    @RequestMapping(value = "/customerBinding", method = RequestMethod.POST)
    public CommonResult<Boolean> customerBinding(@RequestBody CustomerBindingQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        CarPositionEntity carPositionEntity = new CarPositionEntity();
        BeanUtils.copyProperties(qo, carPositionEntity);
        carPositionEntity.setBindingStatus(1);
        LocalDateTime now = LocalDateTime.now();
        carPositionEntity.setBeginTime(now);
        System.out.println(qo.getNumber());

        if (qo.getNumber() != null) {
//            long ALL =  qo.getNumber() * 30 * 24 * 60 * 60 * 1000+System.currentTimeMillis() ;
//            LocalDateTime dateTime = new Date(ALL).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
            LocalDateTime minus = now.minus(-30 * qo.getNumber(), ChronoUnit.DAYS);
            carPositionEntity.setEndTime(minus);
        }


        //查询用户的id
        String uid = iUserService.selectUserUID(qo.getOwnerPhone(), qo.getUserName());
        carPositionEntity.setUid(uid);

        if (qo.getCarPosStatus() == 1) {//绑定为业主的时候
            long carProprietorId = SnowFlake.nextId();//外键业主车辆表
            carPositionEntity.setCarProprietorId(carProprietorId);//外键
            carPositionEntity.setCarNumber(qo.getCarNumber());//车牌号
            CarProprietorEntity entity = new CarProprietorEntity();
            entity.setCommunityId(adminCommunityId);//社区id
            entity.setCarNumber(qo.getCarNumber());//车牌号
            entity.setProprietorName(qo.getUserName());//用户名
            entity.setId(carProprietorId);//主键id
            entity.setPhone(Long.parseLong(qo.getOwnerPhone()));//电话
            iCarProprietorService.addProprietor(entity, adminCommunityId);
        }


        boolean b = iCarPositionService.updateById(carPositionEntity);
        if (b) {


            return CommonResult.ok(b, "绑定成功");
        }
        return CommonResult.ok(b, "绑定失败");
    }

    @ApiOperation("解除绑定")
    @Login
    @CarOperation(operation = "解除绑定")
    @RequestMapping(value = "/relieve", method = RequestMethod.POST)
    public CommonResult<Boolean> relieve(Long id) {

        //查询业主车辆
        CarPositionEntity byId = iCarPositionService.getById(id);
        if (byId.getCarPosStatus() == 1) {
            System.out.println(byId.getCarProprietorId());
            boolean b1 = iCarProprietorService.deleteProprietor(byId.getCarProprietorId());//删除业主车辆
        }
        Boolean b = iCarPositionService.relieve(id);
        if (b) {
            return CommonResult.ok(b, "解绑成功");
        }
        return CommonResult.ok(b, "解绑失败");
    }

    @ApiOperation("删除车位")
    @Login
    @CarOperation(operation = "删除车位")
    @RequestMapping(value = "/deletedCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> deletedCarPosition(Long id) {
        Boolean b = iCarPositionService.deletedCarPosition(id);
        if (b) {
            return CommonResult.ok(b, "删除成功");
        }
        return CommonResult.ok(b, "删除失败");
    }

    @CarOperation(operation = "编辑车位")
    @ApiOperation("编辑车位")
    @Login
    @RequestMapping(value = "/updateCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> updateCarPosition(@RequestBody UpdateCarPositionQO qo) {


        //日志记录
        //RecordCarLogUtils.recordLog(this.getClass(),"updateCarPosition","操作日志",RoleId,UpdateCarPositionQO.class);


        Boolean b = iCarPositionService.updateCarPosition(qo);
        if (b) {
            return CommonResult.ok(b, "更新成功");
        }
        return CommonResult.ok(b, "更新失败");
    }





    @ApiOperation("过车记录")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void carBeforeRecord(@RequestParam Map<String, Object> body,
                                HttpServletResponse response) throws IOException {


        CarIdentifyEntity entity = JSON.parseObject(JSON.toJSONString(body), CarIdentifyEntity.class);//返回实体类

        CarVO carVO = new CarVO();//整体返回對象
        carVO.setError_num(0);
        carVO.setError_str("响应");
        carVO.setPasswd("123456");//密码
        carVO.setGpio_data(new ArrayList<>());//开闸数据
        carVO.setRs485_data(new ArrayList<>());//语音和led
        carVO.setWhitelist_data(new ArrayList<>());//注册数据
        //查询社区代写
        Long communityId = equipmentManageService.equipmentOne(entity.getCamId()).getCommunityId();
//        System.out.println("社区id" + communityId);
//        System.out.println("相机id" + entity.getCamId());
//        System.out.println("进出" + entity.getVdcType());
//        System.out.println("车牌号" + entity.getPlateNum());


        //区分心跳和正常相应
        if (entity.getType().equals("heartbeat")) {
            System.out.println("心跳" + entity.getType());
            Long aLong = selectResidueCarPositionCount(communityId);//查询临时车余位
            String standard = Crc16Util.getStandard(aLong.intValue());//换算显示格式
            //led显示余位
            Rs485Data e2 = new Rs485Data();
            e2.setEncodetype("hex2string");
            e2.setData(Crc16Util.getUltimatelyValue2("余位" + standard, "00", entity.getType()));
            carVO.getRs485_data().add(e2);
            ////黑白名单的注册
//        WhitelistData whitelistData = new WhitelistData();
//        whitelistData.setAction("delete");
//        whitelistData.setPlateNumber("沪A99999");
//        whitelistData.setType("W");
//        whitelistData.setStart("2021/08/31 11:00:00");
//        whitelistData.setEnd("2022/12/31 23:59:59");
//        carVO.getWhitelist_data().add(whitelistData);

        } else {
            System.out.println("正常" + entity.getType());
            //全景图
            String carInAndOutPicture = base64GetString(entity.getPlateNum(), entity.getStartTime(), entity.getPicture(), "全景");
            //车位号
            String carInAndOutPicture1 = base64GetString(entity.getPlateNum(), entity.getStartTime(), entity.getCloseupPic(), "車牌");
            System.out.println("全景图" + carInAndOutPicture);
            System.out.println("车位号图" + carInAndOutPicture1);


            //在小区是否是黑名单车辆
            CarBlackListEntity carBlackListEntity = iCarBlackListService.carBlackListOne(entity.getPlateNum(), communityId);
            if (carBlackListEntity == null) {//不是黑名单
                //根据车牌号查询车辆的3种状态
                Integer next = getInteger(entity, communityId);

                if (next != null && next == 3) {//业主

                    if (entity.getVdcType().equals("in")) {//进口
                        //是否开闸
                        GpioData gpioData = new GpioData();
                        gpioData.setIonum("io1");
                        gpioData.setAction("on");
                        carVO.getGpio_data().add(gpioData);
                        //语音播报内容
                        Rs485Data e2 = new Rs485Data();
                        e2.setEncodetype("hex2string");
                        e2.setData(Crc16Util.getUltimatelyVoice("欢迎回家", entity.getType()));//欢迎回家
                        carVO.getRs485_data().add(e2);
                        //led
                        Rs485Data e3 = new Rs485Data();
                        e3.setEncodetype("hex2string");
                        e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                        carVO.getRs485_data().add(e3);

                    } else if (entity.getVdcType().equals("out")) {//出口

                        //是否开闸
                        GpioData gpioData = new GpioData();
                        gpioData.setIonum("io1");
                        gpioData.setAction("on");
                        carVO.getGpio_data().add(gpioData);
                        //语音播报内容
                        Rs485Data e2 = new Rs485Data();
                        e2.setEncodetype("hex2string");
                        e2.setData(Crc16Util.getUltimatelyVoice("一路平安", entity.getType()));//一路平安
                        carVO.getRs485_data().add(e2);
                        //led
                        Rs485Data e3 = new Rs485Data();
                        e3.setEncodetype("hex2string");
                        e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                        carVO.getRs485_data().add(e3);

                    }
                    //开闸记录
                    extracted(entity.getPlateNum(), entity.getVehicleType(), entity.getStartTime(), entity.getCamId(), entity.getVdcType(), entity.getTrigerType(), carInAndOutPicture, carInAndOutPicture1, entity.getCarSubLogo(), entity.getPlateColor());

                } else if (next != null && next == 2) {//月租车

                    if (entity.getVdcType().equals("in")) {//进口
                        //是否开闸
                        GpioData gpioData = new GpioData();
                        gpioData.setIonum("io1");
                        gpioData.setAction("on");
                        carVO.getGpio_data().add(gpioData);
                        //查询出包月车辆的剩余天数
                        Long extracted = extracted(entity.getPlateNum(), entity.getPlateColor(), communityId);
                        String standard = Crc16Util.getStandard(extracted.intValue());//显示格式
                        String showValue = "剩余天数" + standard;
                        System.out.println(showValue);

                        //语音播报内容
                        Rs485Data e2 = new Rs485Data();
                        e2.setEncodetype("hex2string");
                        e2.setData(Crc16Util.getUltimatelyVoice("欢迎回家", entity.getType()));//欢迎回家
                        carVO.getRs485_data().add(e2);
                        //led
                        Rs485Data e3 = new Rs485Data();
                        e3.setEncodetype("hex2string");
                        e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                        carVO.getRs485_data().add(e3);
                        //led
                        Rs485Data e4 = new Rs485Data();
                        e4.setEncodetype("hex2string");
                        e4.setData(Crc16Util.getUltimatelyValue2(showValue, "02", entity.getType()));//剩余天数(待解决)
                        carVO.getRs485_data().add(e4);


                    } else if (entity.getVdcType().equals("out")) {//出口
                        //是否开闸
                        GpioData gpioData = new GpioData();
                        gpioData.setIonum("io1");
                        gpioData.setAction("on");
                        carVO.getGpio_data().add(gpioData);

                        //查询出包月车辆的剩余天数
                        Long extracted = extracted(entity.getPlateNum(), entity.getPlateColor(), communityId);
                        String standard = Crc16Util.getStandard(extracted.intValue());//显示格式
                        String showValue = "剩余天数" + standard;
                        System.out.println(showValue);

                        //语音播报内容
                        Rs485Data e2 = new Rs485Data();
                        e2.setEncodetype("hex2string");
                        e2.setData(Crc16Util.getUltimatelyVoice("一路平安", entity.getType()));//一路平安
                        carVO.getRs485_data().add(e2);
                        //led
                        Rs485Data e3 = new Rs485Data();
                        e3.setEncodetype("hex2string");
                        e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                        carVO.getRs485_data().add(e3);
                        //led
                        Rs485Data e4 = new Rs485Data();
                        e4.setEncodetype("hex2string");
                        e4.setData(Crc16Util.getUltimatelyValue2(showValue, "02", entity.getType()));//剩余天数
                        carVO.getRs485_data().add(e4);

                    }
                    //开闸记录
                    extracted(entity.getPlateNum(), entity.getVehicleType(), entity.getStartTime(), entity.getCamId(), entity.getVdcType(), entity.getTrigerType(), carInAndOutPicture, carInAndOutPicture1, entity.getCarSubLogo(), entity.getPlateColor());

                } else {//临时车
                    // 查询特殊车辆是否收费
                    CarBasicsEntity one2 = iCarBasicsService.findOne(communityId);
                    if (one2 == null) {
                        throw new PropertyException(510, "请设置特殊车辆是否收费");
                    }
                    Integer exceptionCar = one2.getExceptionCar();//0：不收费  1：收费
                    System.out.println("特殊车辆是否收费" + exceptionCar);
                    if (entity.getPlateColor().equals("白色") && exceptionCar == 0) {//特殊车辆且不收费

                        if (entity.getVdcType().equals("in")) {//进口
                            //是否开闸
                            GpioData gpioData = new GpioData();
                            gpioData.setIonum("io1");
                            gpioData.setAction("on");
                            carVO.getGpio_data().add(gpioData);
                            //语音播报内容
                            Rs485Data e2 = new Rs485Data();
                            e2.setEncodetype("hex2string");
                            e2.setData(Crc16Util.getUltimatelyVoice("军警车免费通行", entity.getType()));//特殊车辆放行
                            carVO.getRs485_data().add(e2);
                            //led
                            Rs485Data e3 = new Rs485Data();
                            e3.setEncodetype("hex2string");
                            e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                            carVO.getRs485_data().add(e3);

                        } else if (entity.getVdcType().equals("out")) {//出口
                            //是否开闸
                            GpioData gpioData = new GpioData();
                            gpioData.setIonum("io1");
                            gpioData.setAction("on");
                            carVO.getGpio_data().add(gpioData);
                            //语音播报内容
                            Rs485Data e2 = new Rs485Data();
                            e2.setEncodetype("hex2string");
                            e2.setData(Crc16Util.getUltimatelyVoice("军警车免费通行", entity.getType()));//特殊车辆放行
                            carVO.getRs485_data().add(e2);
                            //led
                            Rs485Data e3 = new Rs485Data();
                            e3.setEncodetype("hex2string");
                            e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                            carVO.getRs485_data().add(e3);
                        }
                        //开闸记录
                        extracted(entity.getPlateNum(), entity.getVehicleType(), entity.getStartTime(), entity.getCamId(), entity.getVdcType(), entity.getTrigerType(), carInAndOutPicture, carInAndOutPicture1, entity.getCarSubLogo(), entity.getPlateColor());

                    } else {//不是特殊车辆或者是特殊车辆要收费

                        //临时车最大入场数
                        CarBasicsEntity one1 = iCarBasicsService.findOne(communityId);
                        if (one1 == null) {
                            throw new PropertyException(511, "请设置临时车最大入场数");
                        }
                        Integer maxNumber = one1.getMaxNumber();
                        CarCutOffQO carCutOffQO = new CarCutOffQO();
                        carCutOffQO.setCommunityId(communityId);
                        carCutOffQO.setState(0);//0为未完成
                        //查询临时车位的占用数量
                        long total = carCutOffService.selectPage(carCutOffQO);
                        System.out.println("最大入场数" + maxNumber);
                        System.out.println("查询临时车位的占用数量" + total);


                        if (maxNumber > total) {//还有车位的情况
                            //查询设备模式
                            CarEquipmentManageEntity carEquipmentManageEntity = equipmentManageService.equipmentOne(entity.getCamId());
                            CarPatternEntity one = iCarPatternService.findOne(carEquipmentManageEntity.getPatternId());
                            String locationPattern = one.getLocationPattern();//设备模式结果


                            if (locationPattern.equals("禁入禁出")) {//"禁入禁出"
                                boolean statusInvite = false;
                                if (entity.getVdcType().equals("in")) {//进口
                                    //查询车牌是否是业主邀请and状态为没有进入
                                    VisitorEntity visitorEntity = visitorService.selectCarNumberIsNoInvite(entity.getPlateNum(), communityId, 1);//状态 1.待入园 2.已入园 3.已出园 4.已失效
                                    if (visitorEntity != null) {
                                        long StartTime = visitorEntity.getStartTime().toEpochSecond(ZoneOffset.of("+8"));//开始时间
                                        long EndTime = visitorEntity.getEndTime().toEpochSecond(ZoneOffset.of("+8"));//结束时间
                                        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));//当前时间
                                        if(StartTime<now && EndTime>now){
                                            statusInvite=true;
                                        }
                                    }

                                } else if (entity.getVdcType().equals("out")) {//出口
                                    //查询车牌是否是业主邀请and状态为进入状态
                                    statusInvite = true;
                                }
                                //查询车牌号是否收到邀请
                                if (statusInvite) {//收到邀请可以开闸
                                    //临时车收费模式
                                    chargingMode(entity.getPlateNum(), entity.getPlateColor(), entity.getCarSubLogo(), entity.getVehicleType(), entity.getStartTime(), entity.getCamId(), entity.getVdcType(), entity.getTrigerType(), carInAndOutPicture, carInAndOutPicture1, carVO, communityId);
                                } else {
                                    //是否开闸
                                    GpioData gpioData = new GpioData();
                                    gpioData.setIonum("io1");
                                    carVO.getGpio_data().add(gpioData);
                                    //语音播报内容
                                    Rs485Data e2 = new Rs485Data();
                                    e2.setEncodetype("hex2string");
                                    e2.setData(Crc16Util.getUltimatelyVoice("禁止通行", entity.getType()));//禁止通行
                                    carVO.getRs485_data().add(e2);
                                    //led
                                    Rs485Data e3 = new Rs485Data();
                                    e3.setEncodetype("hex2string");
                                    e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                                    carVO.getRs485_data().add(e3);
                                }


                            }
                            if (locationPattern.equals("免费模式")) {//免费模式
                                //开闸记录
                                extracted(entity.getPlateNum(), entity.getVehicleType(), entity.getStartTime(), entity.getCamId(), entity.getVdcType(), entity.getTrigerType(), carInAndOutPicture, carInAndOutPicture1, entity.getCarSubLogo(), entity.getPlateColor());
                                if (entity.getVdcType().equals("in")) {//进口
                                    //是否开闸
                                    GpioData gpioData = new GpioData();
                                    gpioData.setIonum("io1");
                                    gpioData.setAction("on");
                                    carVO.getGpio_data().add(gpioData);

                                    //语音播报内容
                                    Rs485Data e2 = new Rs485Data();
                                    e2.setEncodetype("hex2string");
                                    e2.setData(Crc16Util.getUltimatelyVoice("欢迎光临", entity.getType()));//欢迎光临
                                    carVO.getRs485_data().add(e2);
                                    //led
                                    Rs485Data e3 = new Rs485Data();
                                    e3.setEncodetype("hex2string");
                                    e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                                    carVO.getRs485_data().add(e3);


                                } else if (entity.getVdcType().equals("out")) {//出口
                                    //是否开闸
                                    GpioData gpioData = new GpioData();
                                    gpioData.setIonum("io1");
                                    gpioData.setAction("on");
                                    carVO.getGpio_data().add(gpioData);

                                    Rs485Data e2 = new Rs485Data();
                                    e2.setEncodetype("hex2string");
                                    e2.setData(Crc16Util.getUltimatelyVoice("一路平安", entity.getType()));//一路平安
                                    carVO.getRs485_data().add(e2);
                                    //led
                                    Rs485Data e3 = new Rs485Data();
                                    e3.setEncodetype("hex2string");
                                    e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                                    carVO.getRs485_data().add(e3);
                                }

                            }

                            if (locationPattern.equals("收费模式")) {

                                chargingMode(entity.getPlateNum(), entity.getPlateColor(), entity.getCarSubLogo(), entity.getVehicleType(), entity.getStartTime(), entity.getCamId(), entity.getVdcType(), entity.getTrigerType(), carInAndOutPicture, carInAndOutPicture1, carVO, communityId);
                            }
                        } else {//車位不足的情況
                            //是否开闸
                            GpioData gpioData = new GpioData();
                            gpioData.setIonum("io1");
                            carVO.getGpio_data().add(gpioData);
                            //语音播报内容
                            Rs485Data e2 = new Rs485Data();
                            e2.setEncodetype("hex2string");
                            e2.setData(Crc16Util.getUltimatelyVoice("车位已满", entity.getType()));//车位已满
                            carVO.getRs485_data().add(e2);
                            //led
                            Rs485Data e3 = new Rs485Data();
                            e3.setEncodetype("hex2string");
                            e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                            carVO.getRs485_data().add(e3);
                        }
                    }
                }

            } else {//是黑名单的这辆
                String value = null;//黑名单提示信息
                if (entity.getVdcType().equals("in")) {
                    value = "禁止入场";
                }
                if (entity.getVdcType().equals("out")) {
                    value = "禁止出场";
                }

                //是否开闸
                GpioData gpioData = new GpioData();
                gpioData.setIonum("io1");
                carVO.getGpio_data().add(gpioData);
                //语音播报内容
                Rs485Data e2 = new Rs485Data();
                e2.setEncodetype("hex2string");
                e2.setData(Crc16Util.getUltimatelyVoice(value, entity.getType()));//黑名单提示信息
                carVO.getRs485_data().add(e2);
                //led
                Rs485Data e3 = new Rs485Data();
                e3.setEncodetype("hex2string");
                e3.setData(Crc16Util.getUltimatelyValue2(entity.getPlateNum(), "01", entity.getType()));//车牌号
                carVO.getRs485_data().add(e3);
            }


        }

        response.setStatus(200);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        response.setContentLength(carVO.toString().length());
        PrintWriter writer = response.getWriter();
        System.out.println(JSONArray.toJSON(carVO).toString());
        writer.write(JSONArray.toJSON(carVO).toString());
        writer.flush();
        writer.close();
    }

    //根据车牌号查询车辆的3种状态
    private Integer getInteger(CarIdentifyEntity entity, Long communityId) {
        Map map = iCarMonthlyVehicleService.selectByStatus(entity.getPlateNum(), entity.getPlateColor(), communityId);//1临时，2包月3業主
        Iterator<Integer> iterator = map.keySet().iterator();
        Integer next = null;
        while (iterator.hasNext()) {//通过迭代器输出
            next = iterator.next();
        }
        System.out.println("状态" + next);
        return next;
    }


    //临时车收费模式
    private void chargingMode(String plateNum,//车牌号
                              String plateColor,//车牌颜色
                              String carSubLogo,//车辆子品牌
                              String vehicleType,// 车辆类型
                              Long startTime,//起闸时间
                              String camId, //相机id
                              String vdcType, //进出类型
                              String trigerType,//video 表示视频触发，hwtriger 表示地感触发，swtriger 表示软触发
                              String carInAndOutPicture,//全景图
                              String carInAndOutPicture1,//车位号
                              CarVO carVO, //相应對象
                              Long communityId)//社区id
    {
        if (vdcType.equals("in")) {//进口
            //是否开闸
            GpioData gpioData = new GpioData();
            gpioData.setIonum("io1");
            gpioData.setAction("on");
            carVO.getGpio_data().add(gpioData);
            //开闸记录
            extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);


            //语音播报内容
            Rs485Data e2 = new Rs485Data();
            e2.setEncodetype("hex2string");
            e2.setData(Crc16Util.getUltimatelyVoice("欢迎光临", "online"));//欢迎光临
            carVO.getRs485_data().add(e2);
            //led
            Rs485Data e3 = new Rs485Data();
            e3.setEncodetype("hex2string");
            e3.setData(Crc16Util.getUltimatelyValue2(plateNum, "01", "online"));//车牌号
            carVO.getRs485_data().add(e3);


            //新增订单
            insterCarOrder(plateNum, communityId, LocalDateTime.now(), plateColor, 0);
            //如果是收到邀请的车辆状态改变为已经进园，查询车辆现在时间是在业主邀请名单中and时间范围之内
//            boolean statusInvite = visitorService.selectCarNumberIsNoInvite(plateNum, communityId, 1);
//            if (statusInvite) {
//                visitorService.updateCarStatus(plateNum, communityId, 2);
//            }

        } else if (vdcType.equals("out")) {//出口
            //查询最后没有支付的订单
            CarOrderEntity entity = iCarOrderService.selectCarOrderStatusNO(communityId, plateNum, 1);
            if (entity != null && entity.getOverdueState() == 0) {//正常订单
                extracted(plateNum, plateColor, carSubLogo, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carVO, communityId);

            } else {//不是正常订单
                //0：车牌识别错误 1:逾期 2：正常
                OverdueVo overdueVo = iCarMonthlyVehicleService.MonthlyOverdue(plateNum, communityId);//1包月逾期0临时车
                if (overdueVo.getState() == 1) {//包月逾期车辆
                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData(Crc16Util.getUltimatelyVoice("月租车到期请重新补交停车费", "online"));
                    carVO.getRs485_data().add(e2);
                    //led
                    Rs485Data e3 = new Rs485Data();
                    e3.setEncodetype("hex2string");
                    e3.setData(Crc16Util.getUltimatelyValue2(plateNum, "01", "online"));//车牌号
                    carVO.getRs485_data().add(e3);
                } else if (overdueVo.getState() == 2) {//临时车,逾期车辆正常缴费后变为的临时车
                    //临时车走的接口
                    extracted(plateNum, plateColor, carSubLogo, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carVO, communityId);
                } else if (overdueVo.getState() == 0) {
                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData(Crc16Util.getUltimatelyVoice("请重新进入识别区", "online"));
                    carVO.getRs485_data().add(e2);
                    //led
                    Rs485Data e3 = new Rs485Data();
                    e3.setEncodetype("hex2string");
                    e3.setData(Crc16Util.getUltimatelyValue2(plateNum, "01", "online"));//车牌号
                    carVO.getRs485_data().add(e3);
                }
            }


        }


    }

    //如果是临时车走的接口
    private void extracted(String plateNum, String plateColor, String carSubLogo, String vehicleType, Long startTime, String camId, String vdcType, String trigerType, String carInAndOutPicture, String carInAndOutPicture1, CarVO carVO, Long communityId) {
        Integer orderStatus = 0;
        //查询临时车最后订单
        CarOrderEntity carOrderEntity = iCarOrderService.selectCarOrderStatus(communityId, plateNum, 1);
        if (carOrderEntity != null) {
            orderStatus = carOrderEntity.getOrderStatus();//获取订单的状态
        }
        System.out.println("支付状态" + orderStatus);
        if (orderStatus == 0) {//未支付

            Integer plateType = 1;
            if (plateColor.equals("黄色")) {
                plateType = 0;
            }
            LocalDateTime beginTime = carOrderEntity.getBeginTime();//进入时间
            LocalDateTime now = LocalDateTime.now();//当前时间
            Integer difference = (int) Duration.between(beginTime, now).toMinutes();//时间差
            System.out.println("时间差" + difference);
            //查询临时车的免费分钟数difference
            Integer temporaryFreTime = carChargeService.selectTemporaryFreTime(communityId, plateType);

            System.out.println("免费分钟数" + temporaryFreTime);
            if (difference < temporaryFreTime) {//在免费时间内
                //是否开闸
                GpioData gpioData = new GpioData();
                gpioData.setIonum("io1");
                gpioData.setAction("on");
                carVO.getGpio_data().add(gpioData);
                //开闸记录
                extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);
                //将免费时间内的订单删除
                iCarOrderService.deletedNOpayOrder(plateNum, communityId, beginTime);

                //语音播报内容
                Rs485Data e2 = new Rs485Data();
                e2.setEncodetype("hex2string");
                e2.setData(Crc16Util.getUltimatelyVoice("一路平安", "online"));//特殊车辆放行
                carVO.getRs485_data().add(e2);
                //led
                Rs485Data e3 = new Rs485Data();
                e3.setEncodetype("hex2string");
                e3.setData(Crc16Util.getUltimatelyValue2(plateNum, "01", "online"));//车牌号
                carVO.getRs485_data().add(e3);
            } else {


                //语音播报内容
                Rs485Data e2 = new Rs485Data();
                e2.setEncodetype("hex2string");
                e2.setData(Crc16Util.getUltimatelyVoice("请缴费", "online"));//请缴费
                carVO.getRs485_data().add(e2);
                //led
                Rs485Data e3 = new Rs485Data();
                e3.setEncodetype("hex2string");
                e3.setData(Crc16Util.getUltimatelyValue2(plateNum, "01", "online"));//车牌号
                carVO.getRs485_data().add(e3);
                //计算临时车，停车金额
                BigDecimal charge = getBigDecimal(plateColor, communityId, carOrderEntity.getBeginTime());
                //led
                Rs485Data e4 = new Rs485Data();
                e4.setEncodetype("hex2string");
                e4.setData(Crc16Util.getUltimatelyValue2(charge + "元", "02", "online"));//计算临时车
                carVO.getRs485_data().add(e4);


            }

        }
        if (orderStatus == 1) {//已经支付

            //缴费获取缴费时间和允许出入的时间是多久
            CarBasicsEntity one1 = iCarBasicsService.findOne(communityId);
            LocalDateTime now = LocalDateTime.now();//当前时间
            LocalDateTime orderTime = carOrderEntity.getOrderTime();//支付时间
            Integer dwellTime = one1.getDwellTime();//允许滞留时间
            Duration duration = Duration.between(orderTime, now);//支付时间和当前时间差
            long l = duration.toMinutes();//分钟
            System.out.println("当前时间" + now);
            System.out.println("支付时间" + orderTime);
            System.out.println("允许滞留时间" + dwellTime + "分钟");
            System.out.println("时间差" + l + "分钟");

            if (l > dwellTime) {//超过了滞留时间
                //是否开闸
                GpioData gpioData = new GpioData();
                gpioData.setIonum("io1");
                carVO.getGpio_data().add(gpioData);
                //语音播报内容
                Rs485Data e2 = new Rs485Data();
                e2.setEncodetype("hex2string");
                e2.setData(Crc16Util.getUltimatelyVoice("此车已超时请重新补交停车费", "online"));//此车已超时
                carVO.getRs485_data().add(e2);
                //led
                Rs485Data e3 = new Rs485Data();
                e3.setEncodetype("hex2string");
                e3.setData(Crc16Util.getUltimatelyValue2(plateNum, "01", "online"));//车牌号
                carVO.getRs485_data().add(e3);
                //新增订单
                insterCarOrder(plateNum, communityId, orderTime, plateColor, 1);

                //查询临时车最后订单
                CarOrderEntity carOrder1 = iCarOrderService.selectCarOrderStatus(communityId, plateNum, 1);
                //查询临时车费用
                BigDecimal charge = getBigDecimal(plateColor, communityId, carOrder1.getBeginTime());
                //led
                Rs485Data e4 = new Rs485Data();
                e4.setEncodetype("hex2string");
                e4.setData(Crc16Util.getUltimatelyValue2(charge + "元", "02", "online"));//需要支付的金额
                carVO.getRs485_data().add(e4);


            }
            if (l < dwellTime) {//没有超过滞留时间
                //是否开闸
                GpioData gpioData = new GpioData();
                gpioData.setIonum("io1");
                gpioData.setAction("on");
                carVO.getGpio_data().add(gpioData);
                //开闸记录
                extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);

                //语音播报内容
                Rs485Data e2 = new Rs485Data();
                e2.setEncodetype("hex2string");
                e2.setData(Crc16Util.getUltimatelyVoice("一路平安", "online"));//一路平安
                carVO.getRs485_data().add(e2);
                //led
                Rs485Data e3 = new Rs485Data();
                e3.setEncodetype("hex2string");
                e3.setData(Crc16Util.getUltimatelyValue2(plateNum, "01", "online"));//车牌号
                carVO.getRs485_data().add(e3);
                //如果是收到邀请的车辆状态改变为已经出园
//                boolean statusInvite = visitorService.selectCarNumberIsNoInvite(plateNum, communityId, 2);
//                if (statusInvite) {
//                    visitorService.updateCarStatus(plateNum, communityId, 3);
//                }
            }

        }
    }

    //查询需要支付的额金额{"社区id,车牌颜色,入场时间,结算时间"}
    private BigDecimal getBigDecimal(String plateColor, Long communityId, LocalDateTime beginTime) {
        CarChargeQO carChargeQO = new CarChargeQO();
        carChargeQO.setCommunityId(communityId.toString());//社区id
        carChargeQO.setCarColor(plateColor);//车牌颜色
        carChargeQO.setInTime(beginTime);//订单中的开始时间
        carChargeQO.setReTime(LocalDateTime.now());//现在的时间
        BigDecimal charge = carChargeService.charge(carChargeQO);
        return charge;
    }

    //新增一个车辆订单对象
    private void insterCarOrder(String plateNum, Long communityId, LocalDateTime beginTime, String plateColor, Integer isRetention) {
        boolean  statusInvite=false;
        VisitorEntity visitorEntity=null;
        if(isRetention!=1){//不能为滞留订单的时候进入
            //查询车牌是否是业主邀请切在时间之内
             visitorEntity = visitorService.selectCarNumberIsNoInvite(plateNum, communityId, 1);//状态 1.待入园 2.已入园 3.已出园 4.已失效
            if (visitorEntity != null) {
                long StartTime = visitorEntity.getStartTime().toEpochSecond(ZoneOffset.of("+8"));//开始时间
                long EndTime = visitorEntity.getEndTime().toEpochSecond(ZoneOffset.of("+8"));//结束时间
                long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));//当前时间
                if(StartTime<now && EndTime>now){
                    statusInvite=true;
                }
            }
        }
        CarOrderEntity entity = new CarOrderEntity();//新增一个车辆订单对象
        entity.setType(1);//临时车
        entity.setPlateColor(plateColor);//颜色
        entity.setIsRetention(isRetention);//是否为滞留订单0正常1,滞留订单
        entity.setOrderNum(OrderNoUtil.getOrder());//订单编号
        entity.setBeginTime(beginTime);//进入的时间
        entity.setOrderStatus(0);//未支付
        entity.setRise("无");
        if(statusInvite){//有邀请记录就讲uid设置进去
            entity.setUid(visitorEntity.getUid());//邀请的uid
        }
        entity.setCarPlate(plateNum);//车牌号
        entity.setCommunityId(communityId);//社区id
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        entity.setMonth(month);//月份
        boolean save = iCarOrderService.save(entity);
    }

    //转换base64图片上传获取url
    private String base64GetString(String plateNum, Long startTime, String picture, String name) throws IOException {
        String s = picture.replace("-", "+");
        String s1 = s.replace("_", "/");
        String s2 = s1.replace(".", "=");
        byte[] bytes = Base64.getDecoder().decode(s2);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        MultipartFile file = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
        return MinioUtils.uploadNameByCarJPG(file, "car-in-and-out-picture", name + startTime + plateNum + ".jpg");
    }

    //新增开闸记录
    private void extracted(String plateNum, String vehicleType, Long startTime,
                           String camId, String vdcType, String trigerType,
                           String picture, String closeupPic,
                           String carSubLogo, String plateColor) {
        if (vdcType.equals("in")) {
            CarEquipmentManageEntity carEquipmentManageEntity = equipmentManageService.equipmentOne(camId);
            System.out.println("开闸记录" + vdcType);
            //开闸记录实体类对象
            CarCutOffEntity carCutOffEntity = new CarCutOffEntity();
            //车子状态  1临时车  2包月  3业主
            carCutOffEntity.setCarNumber(plateNum);
            carCutOffEntity.setCarType(vehicleType);
            carCutOffEntity.setAccess(vdcType);
            carCutOffEntity.setTrigerType(trigerType);
            carCutOffEntity.setImage(picture);
            carCutOffEntity.setCloseupPic(closeupPic);
            carCutOffEntity.setCarSublogo(carSubLogo);
            carCutOffEntity.setPlateColor(plateColor);
            //车子状态  1临时车  2包月  3业主
            Integer belong = extracted(plateNum, carEquipmentManageEntity, plateColor);
            carCutOffEntity.setBelong(belong);

            //时间戳转年月日       //进闸时间
            LocalDateTime localDateTime = new Date(startTime * 1000l).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
            carCutOffEntity.setOpenTime(localDateTime);


            System.out.println(carEquipmentManageEntity);
            CarLaneEntity carLaneOne = iCarLaneService.getCarLaneOne(carEquipmentManageEntity.getId(), carEquipmentManageEntity.getCommunityId());
            carCutOffEntity.setLaneName(carLaneOne.getLaneName());
            carCutOffEntity.setCommunityId(carEquipmentManageEntity.getCommunityId());


            //新增
            boolean b = carCutOffService.addCutOff(carCutOffEntity);
            System.out.println("新增数量" + b);

        } else {
            System.out.println("chuzadlfjdoilj ");
            CarCutOffEntity carCutOffEntity = new CarCutOffEntity();
            List<CarCutOffEntity> carCutOffEntityList = carCutOffService.selectAccess(plateNum, 0);

            System.out.println("carCutOffEntityList" + carCutOffEntityList.size());
            //时间戳转年月日       //进闸时间
            LocalDateTime localDateTime = new Date(startTime * 1000l).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
            CarEquipmentManageEntity carEquipmentManageEntity = equipmentManageService.equipmentOne(camId);
            CarLaneEntity carLaneOne = iCarLaneService.getCarLaneOne(carEquipmentManageEntity.getId(), carEquipmentManageEntity.getCommunityId());
            for (CarCutOffEntity i : carCutOffEntityList) {
                i.setStopTime(localDateTime);
                i.setState(1);
                //出闸照片
                i.setOutPic(closeupPic);
                i.setOutImage(picture);

                i.setOutLane(carLaneOne.getLaneName());
                System.out.println("*********" + carLaneOne.getLaneName());
                carCutOffService.updateCutOff(i);
            }

        }
    }

    //包月车辆剩余天数
    private Long extracted(String plateNum, String plateColor, Long CommunityId) {
        Map map = iCarMonthlyVehicleService.selectByStatus(plateNum, plateColor, CommunityId);
        Iterator<Integer> iterator = map.keySet().iterator();
        Integer status = null;
        while (iterator.hasNext()) {//通过迭代器输出
            status = iterator.next();
        }

        //1臨時，2包月   3業主
        long days = 0;
        if (status == 2) {
            CarMonthlyVehicle o = (CarMonthlyVehicle) map.get(status);
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(now, o.getEndTime());
            days = duration.toDays();
        }
        return days;
    }

    //返回的车子所属类型  1临时 2包月 3业主
    private Integer extracted(String plateNum, CarEquipmentManageEntity carEquipmentManageEntity, String plateColor) {
        Map map = iCarMonthlyVehicleService.selectByStatus(plateNum, plateColor, carEquipmentManageEntity.getCommunityId());
        Iterator<Integer> iterator = map.keySet().iterator();
        Integer status = null;
        while (iterator.hasNext()) {//通过迭代器输出
            status = iterator.next();
        }
        return status;

    }

    //获取当下剩余车位
    private Long selectResidueCarPositionCount(Long communityId) {
        //查询临时车最大入场数
        Integer maxNumber = iCarBasicsService.findOne(communityId).getMaxNumber();
        CarCutOffQO carCutOffQO = new CarCutOffQO();
        carCutOffQO.setCommunityId(communityId);
        carCutOffQO.setState(0);//0为未完成
        //查询临时车位的占用数量
        long total = carCutOffService.selectPage(carCutOffQO);
        System.out.println("最大入场数" + maxNumber);
        System.out.println("查询临时车位的占用数量" + total);
        Long residue = maxNumber - total;//剩余车位
        return residue;
    }


}

