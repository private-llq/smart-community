package com.jsy.community.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.config.ExcelListener;
import com.jsy.community.config.ExcelUtils;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.property.*;
import com.jsy.community.qo.property.*;
import com.jsy.community.util.CarOperation;
import com.jsy.community.util.Crc16Util;
import com.jsy.community.util.HttpClientHelper;
import com.jsy.community.util.OrderNoUtil;
import com.jsy.community.utils.MD5Util;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.car.CarVO;
import com.jsy.community.vo.car.GpioData;
import com.jsy.community.vo.car.Rs485Data;
import com.jsy.community.vo.property.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.entity.ContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

        if (qo.getCarPosStatus()==1) {//绑定为业主的时候
            long carProprietorId = SnowFlake.nextId();//外键业主车辆表
            carPositionEntity.setCarProprietorId(carProprietorId);//外键
            carPositionEntity.setCarNumber(qo.getCarNumber());//车牌号
            CarProprietorEntity entity = new CarProprietorEntity();
            entity.setCommunityId(adminCommunityId);//社区id
            entity.setCarNumber(qo.getCarNumber());//车牌号
            entity.setProprietorName(qo.getUserName());//用户名
            entity.setId(carProprietorId);//主键id
            entity.setPhone(Long.parseLong(qo.getOwnerPhone()));//电话
            iCarProprietorService.addProprietor(entity,adminCommunityId);
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
            if (byId.getCarPosStatus()==1) {
                System.out.println(byId.getCarProprietorId());
                boolean b1 = iCarProprietorService.deleteProprietor(byId.getCarProprietorId());//删除业主车辆
            }
        Boolean b = iCarPositionService.relieve(id);
        if(b){
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
    public void carBeforeRecord(@RequestParam("type") String type,//type 固定 online 或 offline online 表示正常在线传输结果，offline 表示断网续传结果
                                @RequestParam("mode") Integer mode,//mode 协议模式，数字表示 模式 5 以上才有此字段
                                @RequestParam("park_id") String parkId,//车场 ID，最大支持 60 个字符
                                @RequestParam("plate_num") String plateNum,//plate_num 车牌号码，UTF8 编码
                                @RequestParam("plate_color") String plateColor,//plate_color 车牌底色，UTF8 编码
                                @RequestParam("plate_val") boolean plateVal,// plate_val 虚假车牌信息，true 表示真牌，false 表示虚假车牌
                                @RequestParam("car_sublogo") String carSubLogo,//车辆子品牌，UTF8 编码
                                @RequestParam("vehicle_type") String vehicleType,//vehicle_type 车辆类型，UTF8 编码
                                @RequestParam("start_time") Long startTime,//start_time 车牌识别时间,1970/01/01 到现在的秒数目
                                @RequestParam("cam_id") String camId,//相机 ID 号根据配置决定是使用MAC 还是 UID
                                @RequestParam("vdc_type") String vdcType,//出入口类型，in 表示入口，out 表示出口
                                @RequestParam("is_whitelist") Boolean isWhitelist,//是否是白名单车辆，true 表示白名单，false 表示非白名
                                @RequestParam("triger_type") String trigerType,//video 表示视频触发，hwtriger 表示地感触发，swtriger 表示软触发
                                @RequestParam("picture") String picture,//全景图，BASE64 编码 为避免Http传输时URL编码意外
                                @RequestParam("closeup_pic") String closeupPic//车牌特写图，BASE64 编码 为避免Http传输时URL编码意外改变图片的 BASE64 编码，作了特殊的替换：'+'替换为'-'，'/'替换为'_'，'='替换为'.
            , HttpServletResponse response, HttpServletRequest request) throws IOException {

        //全景图
        String carInAndOutPicture = base64GetString(plateNum, startTime, picture, "全景");
        //车位号
        String carInAndOutPicture1 = base64GetString(plateNum, startTime, closeupPic, "車牌");
        CarVO carVO = new CarVO();//返回對象
        carVO.setError_num(0);
        carVO.setError_str("响应");
        carVO.setPasswd("123456");
        carVO.setGpio_data(new ArrayList<>());
        carVO.setRs485_data(new ArrayList<>());
        carVO.setWhitelist_data(new ArrayList<>());

        //查询社区代写
        Long communityId = equipmentManageService.equipmentOne(camId).getCommunityId();
        System.out.println("社区id"+communityId);
        System.out.println("相机id"+camId);
        System.out.println("进出"+vdcType);
        System.out.println("车牌号"+plateNum);
        //在小区是否是黑名单车辆
        CarBlackListEntity carBlackListEntity = iCarBlackListService.carBlackListOne(plateNum, communityId);
        if (carBlackListEntity == null) {//不是黑名单
            //根据车牌号查询车辆的3种状态
            Map map = iCarMonthlyVehicleService.selectByStatus(plateNum, plateColor, communityId);//1临时，2包月3業主
            Iterator<Integer> iterator = map.keySet().iterator();
            Integer next = null;
            while (iterator.hasNext()) {//通过迭代器输出
                next = iterator.next();
            }
            System.out.println("状态" + next);

            if (next != null && next == 3) {//业主

                if (vdcType.equals("in")) {//进口
                    //是否开闸
                    GpioData gpioData = new GpioData();
                    gpioData.setIonum("io1");
                    gpioData.setAction("on");
                    GpioData gpioData1 = new GpioData();
                    carVO.getGpio_data().add(gpioData);


                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData("0064FFFF300901BBB6D3ADBBD8BCD2737A");//欢迎回家
                    carVO.getRs485_data().add(e2);


                } else if (vdcType.equals("out")) {//出口
                    //是否开闸
                    GpioData gpioData = new GpioData();
                    gpioData.setIonum("io1");
                    gpioData.setAction("on");
                    carVO.getGpio_data().add(gpioData);
                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData("0064FFFF300901D2BBC2B7CBB3B7E79F40");//一路顺风
                    carVO.getRs485_data().add(e2);
                }
                //开闸记录
                extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);

            } else if (next != null && next == 2) {//月租车

                if (vdcType.equals("in")) {//进口
                    //是否开闸
                    GpioData gpioData = new GpioData();
                    gpioData.setIonum("io1");
                    gpioData.setAction("on");
                    carVO.getGpio_data().add(gpioData);
                    //查询出包月车辆的剩余天数
                    Long extracted = extracted(plateNum, plateColor, communityId);
                    System.out.println("包月车辆的剩余天数" + extracted);
//                    //限制8个字节
//                    String standard = Crc16Util.getStandard(extracted.intValue());
//                    String ultimatelyValue = Crc16Util.getUltimatelyValue("余天" + standard);
//
//                    //LED
//                    Rs485Data e1 = new Rs485Data();
//                    e1.setEncodetype("hex2string");
//                    e1.setData(ultimatelyValue);//余天
//                    carVO.getRs485_data().add(e1);

                    //语音播报内容
                    Rs485Data e3 = new Rs485Data();
                    e3.setEncodetype("hex2string");
                    e3.setData("0064FFFF300901BBB6D3ADBBD8BCD2737A");//欢迎回家
                    carVO.getRs485_data().add(e3);

                } else if (vdcType.equals("out")) {//出口
                    //是否开闸
                    GpioData gpioData = new GpioData();
                    gpioData.setIonum("io1");
                    gpioData.setAction("on");
                    carVO.getGpio_data().add(gpioData);
                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData("0064FFFF300901D2BBC2B7CBB3B7E79F40");//一路顺风
                    carVO.getRs485_data().add(e2);
                }
                //开闸记录
                extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);

            } else {//临时车
                // 查询特殊车辆是否收费
                CarBasicsEntity one2 = iCarBasicsService.findOne(communityId);
                Integer exceptionCar = one2.getExceptionCar();//0：不收费  1：收费
                System.out.println("特殊车辆是否收费" + exceptionCar);
                if (plateColor.equals("白色") && exceptionCar == 0) {//特殊车辆且不收费

                    if (vdcType.equals("in")) {//进口
                        //是否开闸
                        GpioData gpioData = new GpioData();
                        gpioData.setIonum("io1");
                        gpioData.setAction("on");
                        carVO.getGpio_data().add(gpioData);
                        //语音播报内容
                        Rs485Data e2 = new Rs485Data();
                        e2.setEncodetype("hex2string");
                        e2.setData("0064FFFF300901BBB6D3ADB9E2C1D93258");//欢迎光临
                        carVO.getRs485_data().add(e2);

                    } else if (vdcType.equals("out")) {//出口
                        //是否开闸
                        GpioData gpioData = new GpioData();
                        gpioData.setIonum("io1");
                        gpioData.setAction("on");
                        carVO.getGpio_data().add(gpioData);
                        //语音播报内容
                        Rs485Data e2 = new Rs485Data();
                        e2.setEncodetype("hex2string");
                        e2.setData("0064FFFF300901D2BBC2B7CBB3B7E79F40");//一路顺风
                        carVO.getRs485_data().add(e2);
                    }
                    //开闸记录
                    extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);

                } else {//不是特殊车辆或者是特殊车辆要收费
                    //最大入场数
                    Integer maxNumber = iCarBasicsService.findOne(communityId).getMaxNumber();
                    CarCutOffQO carCutOffQO = new CarCutOffQO();
                    carCutOffQO.setCommunityId(communityId);
                    carCutOffQO.setState(0);//0为未完成
                    //查询临时车位的占用数量
                    long total = carCutOffService.selectPage(carCutOffQO);

                    System.out.println("最大入场数" + maxNumber);
                    System.out.println("查询临时车位的占用数量" + total);
                    Long residue=maxNumber-total-1;//剩余车位


                    if (maxNumber > total) {//还有车位的情况
                        //设备模式
                        CarEquipmentManageEntity carEquipmentManageEntity = equipmentManageService.equipmentOne(camId);
                        CarPatternEntity one = iCarPatternService.findOne(carEquipmentManageEntity.getPatternId());
                        String locationPattern = one.getLocationPattern();


                        if (locationPattern.equals("禁入禁出")) {//"禁入禁出"
                            boolean statusInvite = false;
                            if (vdcType.equals("in")) {//进口
                                //查询车牌是否是业主邀请and状态为没有进入
                                statusInvite = visitorService.selectCarNumberIsNoInvite(plateNum, communityId, 1);
                            } else if (vdcType.equals("out")) {//出口
                                //查询车牌是否是业主邀请and状态为进入状态
                                statusInvite = visitorService.selectCarNumberIsNoInvite(plateNum, communityId, 2);
                            }
                            //查询车牌号是否收到邀请
                            if (statusInvite) {//收到邀请可以开闸
                                //临时车收费模式
                                chargingMode(plateNum, plateColor, carSubLogo, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carVO, communityId);
                            } else {
                                //是否开闸
                                GpioData gpioData = new GpioData();
                                gpioData.setIonum("io1");
                                carVO.getGpio_data().add(gpioData);
                                //语音播报内容
                                Rs485Data e2 = new Rs485Data();
                                e2.setEncodetype("hex2string");
                                e2.setData("0064FFFF300901BDFBD6B9CDA8D0D0E950");//禁止通行
                                carVO.getRs485_data().add(e2);
                            }


                        }
                        if (locationPattern.equals("免费模式")) {//免费模式
                            //开闸记录
                            extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);
                            if (vdcType.equals("in")) {//进口
                                //是否开闸
                                GpioData gpioData = new GpioData();
                                gpioData.setIonum("io1");
                                gpioData.setAction("on");
                                carVO.getGpio_data().add(gpioData);

                                Long aLong = selectResidueCarPositionCount(communityId);//查询临时车余位
                                String standard = Crc16Util.getStandard(aLong.intValue());
                                String ultimatelyValue = Crc16Util.getUltimatelyValue("余位" + standard);
                                //led
                                Rs485Data e1 = new Rs485Data();
                                e1.setEncodetype("hex2string");
                                 e1.setData(ultimatelyValue);//余位
                                carVO.getRs485_data().add(e1);

                                //语音播报内容
                                Rs485Data e2 = new Rs485Data();
                                e2.setEncodetype("hex2string");
                                e2.setData("0064FFFF300901BBB6D3ADB9E2C1D93258");//欢迎光临
                                carVO.getRs485_data().add(e2);


                            } else if (vdcType.equals("out")) {//出口
                                //是否开闸
                                GpioData gpioData = new GpioData();
                                gpioData.setIonum("io1");
                                gpioData.setAction("on");
                                carVO.getGpio_data().add(gpioData);

                                Long aLong = selectResidueCarPositionCount(communityId);//查询临时车余位
                                String standard = Crc16Util.getStandard(aLong.intValue());
                                String ultimatelyValue = Crc16Util.getUltimatelyValue("余位" + standard);
//                                //led
//                                Rs485Data e1 = new Rs485Data();
//                                e1.setEncodetype("hex2string");
//                                e1.setData(ultimatelyValue);//余位
//                                carVO.getRs485_data().add(e1);

                                //语音播报内容
                                Rs485Data e2 = new Rs485Data();
                                e2.setEncodetype("hex2string");
                                e2.setData("0064FFFF300901D2BBC2B7CBB3B7E79F40");//一路顺风
                                carVO.getRs485_data().add(e2);
                            }

                        }

                        if (locationPattern.equals("收费模式")) {
                            chargingMode(plateNum, plateColor, carSubLogo, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carVO, communityId);
                        }
                    } else {//車位不足的情況
                        //是否开闸
                        GpioData gpioData = new GpioData();
                        gpioData.setIonum("io1");
                        gpioData.setAction("on");
                        carVO.getGpio_data().add(gpioData);
                        //语音播报内容
                        Rs485Data e2 = new Rs485Data();
                        e2.setEncodetype("hex2string");
                        e2.setData("0064FFFF300901B3B5CEBBD2D1C2FAE7B2");//车位已满
                        carVO.getRs485_data().add(e2);
                    }
                }
            }

        } else {//是黑名单的这辆
            //是否开闸
            GpioData gpioData = new GpioData();
            gpioData.setIonum("io1");
            carVO.getGpio_data().add(gpioData);
            //语音播报内容
            Rs485Data e2 = new Rs485Data();
            e2.setEncodetype("hex2string");
            e2.setData("0064FFFF300901BDFBD6B9CDA8D0D0E950");//禁止通行
            carVO.getRs485_data().add(e2);
        }

////黑白名单的注册
//        WhitelistData whitelistData = new WhitelistData();
//        whitelistData.setAction("delete");
//        whitelistData.setPlateNumber("沪A99999");
//        whitelistData.setType("W");
//        whitelistData.setStart("2021/08/31 11:00:00");
//        whitelistData.setEnd("2022/12/31 23:59:59");
//        carVO.getWhitelist_data().add(whitelistData);

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

            Long aLong = selectResidueCarPositionCount(communityId);//查询临时车余位
            String standard = Crc16Util.getStandard(aLong.intValue());
            String ultimatelyValue = Crc16Util.getUltimatelyValue("余位" + standard);
            //led
            Rs485Data e1 = new Rs485Data();
            e1.setEncodetype("hex2string");
            e1.setData(ultimatelyValue);//余位
            carVO.getRs485_data().add(e1);


            //语音播报内容
            Rs485Data e2 = new Rs485Data();
            e2.setEncodetype("hex2string");
            e2.setData("0064FFFF300901BBB6D3ADB9E2C1D93258");//欢迎光临
            carVO.getRs485_data().add(e2);


            //新增订单
            insterCarOrder(plateNum, communityId, LocalDateTime.now(),plateColor,0);
            //如果是收到邀请的车辆状态改变为已经进园
            boolean statusInvite = visitorService.selectCarNumberIsNoInvite(plateNum, communityId, 1);
            if (statusInvite) {
                visitorService.updateCarStatus(plateNum, communityId, 2);
            }

        } else if (vdcType.equals("out")) {//出口
            Integer orderStatus = 0;
            //查询临时车最后订单
            System.out.println(communityId+plateNum+"+++++");
            CarOrderEntity carOrderEntity = iCarOrderService.selectCarOrderStatus(communityId, plateNum, 1);
            System.out.println(carOrderEntity);
            if (carOrderEntity != null) {
                orderStatus = carOrderEntity.getOrderStatus();//获取订单的状态
            }
            System.out.println("支付状态" + orderStatus);
            if (orderStatus == 0) {//未支付
                
                Integer plateType=1;
                if (plateColor.equals("黄色")) {
                    plateType=0;
                }
                LocalDateTime beginTime = carOrderEntity.getBeginTime();//进入时间
                LocalDateTime now = LocalDateTime.now();//当前时间
                Integer difference =(int) Duration.between(beginTime, now).toMinutes();//时间差
                System.out.println("时间差"+difference);
                //查询临时车的免费分钟数difference
                Integer temporaryFreTime = carChargeService.selectTemporaryFreTime(communityId, plateType);
                System.out.println("免费分钟数"+temporaryFreTime);
                if(difference<temporaryFreTime){//在免费时间内
                    //是否开闸
                    GpioData gpioData = new GpioData();
                    gpioData.setIonum("io1");
                    gpioData.setAction("on");
                    carVO.getGpio_data().add(gpioData);
                    //开闸记录
                    extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);
                    //将免费时间内的订单删除
                    iCarOrderService.deletedNOpayOrder(plateNum,communityId,beginTime);

                    Long aLong = selectResidueCarPositionCount(communityId);//查询临时车余位
                    String standard = Crc16Util.getStandard(aLong.intValue());
                    String ultimatelyValue = Crc16Util.getUltimatelyValue("余位" + standard);
                    //led
//                    Rs485Data e1 = new Rs485Data();
//                    e1.setEncodetype("hex2string");
//                    e1.setData(ultimatelyValue);//余位
//                    carVO.getRs485_data().add(e1);

                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData("0064FFFF300901D2BBC2B7CBB3B7E79F40");//一路顺风
                    carVO.getRs485_data().add(e2);
                }else {
                    //是否开闸
                    GpioData gpioData = new GpioData();
                    gpioData.setIonum("io1");
                    carVO.getGpio_data().add(gpioData);
                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData("0064FFFF300901BDFBD6B9CDA8D0D0E950");//禁止通行
                    carVO.getRs485_data().add(e2);
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
                    e2.setData("0064FFFF300901BDFBD6B9CDA8D0D0E950");//禁止通行
                    carVO.getRs485_data().add(e2);
                    //新增订单
                    insterCarOrder(plateNum, communityId, orderTime,plateColor,1);
                }
                if (l < dwellTime) {//没有超过滞留时间
                    //是否开闸
                    GpioData gpioData = new GpioData();
                    gpioData.setIonum("io1");
                    gpioData.setAction("on");
                    carVO.getGpio_data().add(gpioData);
                    //开闸记录
                    extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1, carSubLogo, plateColor);
                    Long aLong = selectResidueCarPositionCount(communityId);//查询临时车余位
                    String standard = Crc16Util.getStandard(aLong.intValue());
                    String ultimatelyValue = Crc16Util.getUltimatelyValue("余位" + standard);
                    //led
                    Rs485Data e1 = new Rs485Data();
                    e1.setEncodetype("hex2string");
                    e1.setData(ultimatelyValue);//余位
                    carVO.getRs485_data().add(e1);

                    //语音播报内容
                    Rs485Data e2 = new Rs485Data();
                    e2.setEncodetype("hex2string");
                    e2.setData("0064FFFF300901D2BBC2B7CBB3B7E79F40");//一路顺风
                    carVO.getRs485_data().add(e2);
                    //开闸记录

                    //如果是收到邀请的车辆状态改变为已经出园
                    boolean statusInvite = visitorService.selectCarNumberIsNoInvite(plateNum, communityId, 2);
                    if (statusInvite) {
                        visitorService.updateCarStatus(plateNum, communityId, 3);
                    }
                }

            }


        }
    }

    //新增一个车辆订单对象
    private void insterCarOrder(String plateNum, Long communityId, LocalDateTime beginTime,String plateColor,Integer isRetention) {
        CarOrderEntity entity = new CarOrderEntity();//新增一个车辆订单对象
        entity.setType(1);//临时车
        entity.setPlateColor(plateColor);
        entity.setIsRetention(isRetention);//是否为滞留订单0正常1,滞留订单
        entity.setOrderNum(OrderNoUtil.getOrder());//订单编号
        entity.setBeginTime(beginTime);//进入的时间
        entity.setOrderStatus(0);//未支付
        entity.setRise("无");
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
                System.out.println("*********"+carLaneOne.getLaneName());
                carCutOffService.updateCutOff(i);
            }

        }
    }
    @ApiOperation("心跳")
    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    public void carBeforeRecord(@RequestParam Map<String, Object> body
            , HttpServletResponse response, HttpServletRequest request) throws IOException {
        System.out.println(body.get("type")+"-----type类型");
        CarVO carVO = getType(body);

        response.setStatus(200);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        response.setContentLength(carVO.toString().length());
        PrintWriter writer = response.getWriter();
        System.out.println("\n\nJSON\n"+JSONArray.toJSON(carVO).toString());
        writer.write(JSONArray.toJSON(carVO).toString());
        writer.flush();
        writer.close();
    }

    public CarVO getType(Map<String, Object> body){
        CarVO carVO = new CarVO();
        carVO.setError_num(0);
        carVO.setError_str("noerror");
        carVO.setPasswd("123456");
        ArrayList<Rs485Data> list = new ArrayList<>();
        Rs485Data e2 = new Rs485Data();

        CarIdentifyEntity entity = JSON.parseObject(JSON.toJSONString(body), CarIdentifyEntity.class);
        System.out.println("\nentity\n"+entity);
        String type = entity.getType();
        //推送例子为识别结果
        if (type.equals("online")){
            e2.setEncodetype("hex2string");
            //识别结果的显示数据   type=0nline
            e2.setData( Crc16Util.getUltimatelyValue2("渝A5486C","00",type));//一路顺风  语音0064FFFF300901D2BBC2B7CBB3B7E79F40
            list.add(e2);

        }else {
            e2.setEncodetype("hex2string");
            //心跳结果的显示数据   type=heartbeat
            e2.setData( Crc16Util.getUltimatelyValue2("余位999个","00",type));//一路顺风  语音0064FFFF300901D2BBC2B7CBB3B7E79F40
            list.add(e2);

            e2.setEncodetype("hex2string");
            e2.setData( Crc16Util.getUltimatelyValue2("您还未缴费，请缴费在通行","01",type));//一路顺风  语音0064FFFF300901D2BBC2B7CBB3B7E79F40
            list.add(e2);
        }


            carVO.setRs485_data(list);
           return carVO;

    }




//    @ApiOperation("云平台")
//    @RequestMapping(value = "/test2", method = RequestMethod.POST)


//    public void carBeforeRecord(@RequestParam("mode") Integer mode,//mode 协议模式，数字表示 模式 5 以上才有此字段
//                                @RequestParam("park_id") String parkId,//车场 ID，最大支持 60 个字符
//                                @RequestParam("cam_id") String camId,//车场 ID，最大支持 60 个字符
//                                @RequestParam("cam_ip") String camIp,//车场 ID，最大支持 60 个字符
//                                @RequestParam("cmd") String cmd,//devreg 表示设备注册
//                                @RequestParam("msg_id") String msgId,//devreg 表示设备注册
//                                @RequestParam("utc_ts") Integer utcTs,//devreg 表示设备注册
//                                @RequestParam("local_ts") Integer localTs,//devreg 表示设备注册
//                                @RequestParam("local_time") String localTime,//devreg 表示设备注册
//                                @RequestParam("version") String version//devreg 表示设备注册
//
//
//
//
//            , HttpServletResponse response, HttpServletRequest request) throws IOException {
//        System.out.println(localTs+"    localts");
//        System.out.println(cmd +"      cmd");
//        System.out.println(mode);
//        System.out.println(parkId);
//        System.out.println(camId);
//        System.out.println(camIp);
//    }

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
    private  Long selectResidueCarPositionCount(Long communityId){
        //查询临时车最大入场数
        Integer maxNumber = iCarBasicsService.findOne(communityId).getMaxNumber();
        CarCutOffQO carCutOffQO = new CarCutOffQO();
        carCutOffQO.setCommunityId(communityId);
        carCutOffQO.setState(0);//0为未完成
        //查询临时车位的占用数量
        long total = carCutOffService.selectPage(carCutOffQO);
        System.out.println("最大入场数" + maxNumber);
        System.out.println("查询临时车位的占用数量" + total);
        Long residue=maxNumber-total;//剩余车位
        return residue;
    }




    public static void main(String[] args) {

        //结束时间
        String dateTimeStrEnd = "2018/09/28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStrEnd, df);
        long lEnd = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        //开始时间
        String dateTimeStrStart = "2018/07/28 14:11:15";
        DateTimeFormatter dfStart = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateTimeStart = LocalDateTime.parse(dateTimeStrStart, dfStart);
        long lStart = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        HashMap<String, Object> values = new HashMap<>();

        values.put("CarNo", "沪A99999");//车牌号
        values.put("IssueTime", lStart); //发行日期
        values.put("StartTime", lStart);//有效期开始
        values.put("EndTime", lEnd);//有效期结束
        values.put("UseState", 1);//(0空闲中,1使用中,2报修中 ,-1已下架)
        values.put("ApprovalState", 1);//(0待审批,1审批成功,-1审批失败)
        values.put("UserName", "李粤");//用户姓名
        values.put("Phone", "13047315551");//联系电话


        String CarNo = "沪A99999";
        Long IssueTime = lStart;
        Long StartTime = lStart;
        Long EndTime = lEnd;
        Integer UseState = 1;
        Integer ApprovalState = 1;
        String UserName = "李粤";
        String Phone = "13047315551";

        List name = new ArrayList();
        name.add("CarNo");
        name.add("IssueTime");
        name.add("StartTime");
        name.add("EndTime");
        name.add("UseState");
        name.add("ApprovalState");
        name.add("UserName");
        name.add("Phone");
        Collections.sort(name);//排序

        String temporary = "";

        for (int i = 0; i < name.size(); i++) {
            String KEY = (String) name.get(i);
            String VALUE = values.get(KEY) + "";
            temporary = temporary + KEY + "=" + VALUE + "&";

        }

        temporary = temporary.substring(0, temporary.length() - 1);

        String signTemp = temporary + "&key=29bfd42e5a753522b2e77f3071901f98";

        String sign = MD5Util.getMd5Str(signTemp).toUpperCase();
        System.out.println(temporary);
        System.out.println(sign);

        String param = "{" +
                "    \"pid\":\"1\"," +
                "    \"serciceName\":\"AddCarNo\"," +
                "    \"sign\":\"" + sign + "\"," +
                "    \"timestamp\":1," +
                "    \"msgId\":2," +
                "    \"data\":\"{{" +
                "    \"ApprovalState\":\"1\"," +
                "    \"CarNo\":\"沪A99999\"," +
                "   \"EndTime\":\"1532758275000\"," +
                "   \"IssueTime\":\"1532758275000\"," +
                "   \"Phone\":\"13047315551\"," +
                "   \"StartTime\":\"1532758275000\"," +
                "   \"UseState\":\"1\"," +
                "   \"UserName\":\"李粤\"" +
                "}}\"" +
                "}";

        String url = "http://192.168.12.253:8000/AddCarNo";
        String s = HttpClientHelper.sendPost(url, param);
        System.out.println("返回" + s);


    }

}

