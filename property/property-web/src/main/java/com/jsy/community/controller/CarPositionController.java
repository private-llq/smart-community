package com.jsy.community.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarPositionService;
import com.jsy.community.api.ICarPositionTypeService;
import com.jsy.community.api.IUserService;
import com.jsy.community.config.ExcelListener;
import com.jsy.community.config.ExcelUtils;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.CarPositionTypeEntity;
import com.jsy.community.qo.property.CustomerBindingQO;
import com.jsy.community.qo.property.InsterCarPositionQO;
import com.jsy.community.qo.property.MoreInsterCarPositionQO;
import com.jsy.community.qo.property.SelectCarPositionPagingQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        if (qo.getNumber()!=null) {
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
        Boolean b=    iCarPositionService.deletedCarPosition(id);
        if (b) {
            return CommonResult.ok(b, "删除成功");
        }
        return CommonResult.ok(b, "删除失败");
    }








    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    public static void main(String[] args) {

        String param = "";


        //结束时间
        String dateTimeStrEnd = "2018/07/28 14:11:15";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStrEnd, df);
        long lEnd = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        //开始时间
        String dateTimeStrStart = "2018/07/28 14:11:15";
        DateTimeFormatter dfStart = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateTimeStart = LocalDateTime.parse(dateTimeStrStart, dfStart);
        long lStart = dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();


        String CarNo = "沪A99999";
        Long IssueTime = 1L;
        Long StartTime = 1L;
        Long EndTime = 1L;
        Integer UseState = 0;
        Integer ApprovalState = 0;
        String UserName = "李粤";
        String Phone = "";

        List name = new ArrayList();
        name.add("IssueTime");
        name.add("StartTime");
        name.add("EndTime");
        name.add("UseState");
        name.add("ApprovalState");
        name.add("UserName");
        name.add("Phone");
        Collections.sort(name);
        System.out.println(name);


        String sr = CarPositionController.sendPost("http://192.168.1.100:9999/AddCarNo", param);
        System.out.println(sr);


    }

}

