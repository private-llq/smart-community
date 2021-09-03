package com.jsy.community.controller;


import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpException;
import cn.hutool.json.JSON;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.config.ExcelListener;
import com.jsy.community.config.ExcelUtils;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.*;
import com.jsy.community.qo.property.*;
import com.jsy.community.util.Base64UtilsTest;
import com.jsy.community.util.HttpClientHelper;
import com.jsy.community.util.HttpUtil;
import com.jsy.community.utils.MD5Util;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.car.CarVO;
import com.jsy.community.vo.car.GpioData;
import com.jsy.community.vo.car.Rs485Data;
import com.jsy.community.vo.car.WhitelistData;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private IUserService iUserService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionTypeService iCarPositionTypeService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarCutOffService carCutOffService;
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarEquipmentManageService equipmentManageService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarBlackListService icarBlackListService;




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
    @RequestMapping(value = "/insterCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> insterCarPosition(@RequestBody InsterCarPositionQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        Boolean boo = iCarPositionService.insterCarPosition(qo, adminCommunityId);
        return CommonResult.ok(boo, "新增成功");
    }

    @ApiOperation("批量新增车位信息")
    @Login
    @RequestMapping(value = "/moreInsterCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> moreInsterCarPosition(@RequestBody MoreInsterCarPositionQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id

        Boolean boo = iCarPositionService.moreInsterCarPosition(qo, adminCommunityId);


        return CommonResult.ok(boo, "新增成功");
    }

    @ApiOperation("绑定用户")
    @Login
    @RequestMapping(value = "/customerBinding", method = RequestMethod.POST)
    public CommonResult<Boolean> customerBinding(@RequestBody CustomerBindingQO qo) {
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
        boolean b = iCarPositionService.updateById(carPositionEntity);
        if (b) {
            return CommonResult.ok(b, "绑定成功");
        }
        return CommonResult.ok(b, "绑定失败");
    }

    @ApiOperation("解除绑定")
    @Login
    @RequestMapping(value = "/relieve", method = RequestMethod.POST)
    public CommonResult<Boolean> relieve(Long id) {
        Boolean b = iCarPositionService.relieve(id);
        if (b) {
            return CommonResult.ok(b, "解绑成功");
        }
        return CommonResult.ok(b, "解绑失败");
    }

    @ApiOperation("删除车位")
    @Login
    @RequestMapping(value = "/deletedCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> deletedCarPosition(Long id) {
        Boolean b = iCarPositionService.deletedCarPosition(id);
        if (b) {
            return CommonResult.ok(b, "删除成功");
        }
        return CommonResult.ok(b, "删除失败");
    }

    @ApiOperation("编辑车位")
    @Login
    @RequestMapping(value = "/updateCarPosition", method = RequestMethod.POST)
    public CommonResult<Boolean> updateCarPosition(@RequestBody UpdateCarPositionQO qo) {

        Boolean b = iCarPositionService.updateCarPosition(qo);

        if (b) {
            return CommonResult.ok(b, "更新成功");
        }
        return CommonResult.ok(b, "更新失败");
    }
    @ApiOperation("开闸")
    @Login
    @RequestMapping(value = "/open", method = RequestMethod.POST)
    public void updateCarPosition() {
        HttpClientHelper.sendPost("192.168.12.253:8000","{\"error_num\":0,\"error_str\":\"noerror\",\"gpio_data\":[{\"ionum\":\"io1\",\"action\":\"on\"}]}");

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
            , HttpServletResponse response) throws IOException {





        System.out.println(type);
        System.out.println(mode);
        System.out.println(parkId);
        System.out.println(plateNum);
        System.out.println(plateColor);
        System.out.println(plateVal);
        System.out.println(carSubLogo);
        System.out.println(vehicleType);
        System.out.println(startTime);
        System.out.println(camId);
        System.out.println(vdcType);
        System.out.println(isWhitelist);
        System.out.println(trigerType);











        String s = picture.replace("-", "+");
        String s1 = s.replace("_", "/");
        String s2 = s1.replace(".", "=");
        byte[] bytes = Base64.getDecoder().decode(s2);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        MultipartFile file = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
        String carInAndOutPicture = MinioUtils.uploadNameByCarJPG(file, "car-in-and-out-picture", "全景"+startTime+plateNum+".jpg" );
        System.out.println(carInAndOutPicture);




        String c = closeupPic.replace("-", "+");
        String c1 = c.replace("_", "/");
        String c2 = c1.replace(".", "=");
        byte[] bytes1 = Base64.getDecoder().decode(c2);
        InputStream inputStream1 = new ByteArrayInputStream(bytes1);
        MultipartFile file1 = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream1);
        String carInAndOutPicture1 = MinioUtils.uploadNameByCarJPG(file1, "car-in-and-out-picture", "車牌"+startTime+plateNum+".jpg" );
        System.out.println(carInAndOutPicture1);


        //新增开闸记录 和 关闸记录的时间
        extracted(plateNum, vehicleType, startTime, camId, vdcType, trigerType, carInAndOutPicture, carInAndOutPicture1,carSubLogo,plateColor);



        CarVO carVO = new CarVO();
        carVO.setError_num(0);
        carVO.setError_str("响应");
        carVO.setPasswd("123456");
        GpioData gpioData = new GpioData();
        gpioData.setIonum("io1");
        gpioData.setAction("on");
        carVO.setGpio_data(new ArrayList<>());
        carVO.getGpio_data().add(gpioData);
        carVO.setRs485_data(new ArrayList<>());


        Rs485Data e2 = new Rs485Data();
        e2.setEncodetype("hex2string");
        e2.setData("0064FFFF300901BBB6D3ADB9E2C1D93258");
        carVO.getRs485_data().add(e2);



        WhitelistData whitelistData = new WhitelistData();
        whitelistData.setAction("add");
        whitelistData.setPlateNumber("沪A99999");
        whitelistData.setType("W");
        whitelistData.setStart("2021/08/31 11:00:00");
        whitelistData.setEnd("2022/12/31 23:59:59");
        carVO.setWhitelist_data(new ArrayList<>());
        carVO.getWhitelist_data().add(whitelistData);

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




    //新增开闸记录和结算出闸时间
    private void extracted(String plateNum, String vehicleType, Long startTime,
                           String camId, String vdcType, String trigerType, String picture, String closeupPic,String carSubLogo,String plateColor) {
        if (vdcType.equals("in")){
            //开闸记录实体类对象
            CarCutOffEntity carCutOffEntity = new CarCutOffEntity();
            carCutOffEntity.setCarNumber(plateNum);
            carCutOffEntity.setCarType(vehicleType);
            carCutOffEntity.setAccess(vdcType);
            carCutOffEntity.setTrigerType(trigerType);
            carCutOffEntity.setImage(picture);
            carCutOffEntity.setCloseupPic(closeupPic);
            carCutOffEntity.setCarSublogo(carSubLogo);
            carCutOffEntity.setPlateColor(plateColor);

            //时间戳转年月日       //进闸时间
            LocalDateTime localDateTime = new Date(startTime*1000l).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
            carCutOffEntity.setOpenTime(localDateTime);

            //通过mac地址 获取社区id和设备名称   还有临时车模式
            CarEquipmentManageEntity carEquipmentManageEntity = equipmentManageService.equipmentOne(camId);

            System.out.println(carEquipmentManageEntity);

            carCutOffEntity.setLaneName(carEquipmentManageEntity.getEquipmentName());
            carCutOffEntity.setCommunityId(carEquipmentManageEntity.getCommunityId());

            boolean b = carCutOffService.addCutOff(carCutOffEntity);

        }else {
            CarCutOffEntity carCutOffEntity = new CarCutOffEntity();
            List<CarCutOffEntity> carCutOffEntityList = carCutOffService.selectAccess(plateNum, 0);
            //时间戳转年月日       //进闸时间
            LocalDateTime localDateTime = new Date(startTime*1000l).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
            for (CarCutOffEntity i:carCutOffEntityList) {
                i.setStopTime(localDateTime);
                i.setState(1);
                //出闸照片
                i.setOutPic(closeupPic);
                i.setOutImage(picture);
                carCutOffService.updateCutOff(i);
            }

        }
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

        String url ="http://192.168.12.253:8000";
        String s = HttpClientHelper.sendPost(url, param);
        System.out.println("返回"+s);


//        LocalDateTime localDateTime =
//                LocalDateTime.ofInstant(Instant.ofEpochMilli(1630563772), ZoneId.systemDefault());
        Date date = new Date();
        date.setTime(1630563772*1000);
        String format = new SimpleDateFormat().format(date);

        System.out.println();
        LocalDateTime localDateTime = new Date(1630563772*1000l).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        System.out.println(localDateTime);
        //HttpUtil.post(url,param);
    }

}

