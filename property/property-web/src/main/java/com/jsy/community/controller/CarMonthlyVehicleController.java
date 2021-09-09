package com.jsy.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Log;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarMonthlyVehicleService;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarMonthlyVehicle;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.util.POIUtil;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.POIUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(tags = "停车收费设置")
@RestController
@RequestMapping("CarMonthlyVehicle")
@ApiJSYController
public class CarMonthlyVehicleController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarMonthlyVehicleService vehicleService;

    @DubboReference(version = Const.version, group = Const.group_property,check = false)
    private IHouseService houseService;


    /**
     * 新增
     * @param carMonthlyVehicle
     * @return
     */

    @Login
    @PostMapping("SaveMonthlyVehicle")
    @businessLog(operation = "新增",content = "新增了【月租停车收费设置】")
    public CommonResult SaveMonthlyVehicle(@RequestBody CarMonthlyVehicle carMonthlyVehicle) {
       vehicleService.SaveMonthlyVehicle(carMonthlyVehicle, UserUtils.getAdminCommunityId());
       return CommonResult.ok();
    }

    /**
     * 只能修改 车牌号，车主姓名，电话，备注
     * @param carMonthlyVehicle
     * @return
     */
    @PutMapping("UpdateMonthlyVehicle")
    @businessLog(operation = "编辑",content = "更新了【月租停车收费设置】")
    public CommonResult UpdateMonthlyVehicle(@RequestBody CarMonthlyVehicle carMonthlyVehicle) {
        vehicleService.UpdateMonthlyVehicle(carMonthlyVehicle);
        return CommonResult.ok();
    }

    /**
     * 根据uuid删除
     * @param uid
     * @return
     */

    @DeleteMapping("DelMonthlyVehicle")
    @businessLog(operation = "删除",content = "删除了【月租停车收费设置】")
    public CommonResult DelMonthlyVehicle(@RequestParam("uid") String uid) {
        vehicleService.DelMonthlyVehicle(uid);
        return CommonResult.ok();

    }

    /**
     * 多条件查询+分页
     * @param carMonthlyVehicleQO
     * @return
     */

    @Login
    @PostMapping("FindByMultiConditionPage")
    public CommonResult<PageInfo> FindByMultiConditionPage(@RequestBody CarMonthlyVehicleQO carMonthlyVehicleQO) {
        PageInfo pageInfo = vehicleService.FindByMultiConditionPage(carMonthlyVehicleQO,UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }

    /**
     * 包月延期 0 按天 1 按月
     * @param fee
     */
    @PutMapping("delay")
    public CommonResult delay(@RequestParam("uid") String uid,
               @RequestParam("type")Integer type,
               @RequestParam("dayNum")Integer dayNum,
               @RequestParam("fee")BigDecimal fee){

        vehicleService.delay(uid,type,dayNum,fee);
        return CommonResult.ok(0,"延期成功！");
    }


    /**
     * 同步按钮 (下发业务)
     */
    @GetMapping("issue")
    @Login
    public CommonResult issue(@RequestParam("uid") String uid){
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        try {
            vehicleService.issue(uid,adminCommunityId);
            return CommonResult.ok("下发成功");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.ok("下发失败，请联系管理员");

        }

    }

    /**
     * 上传模板
     */
    @PostMapping("uploadTemplate")
    public  String uploadTemplate(MultipartFile file){
        String path = MinioUtils.upload(file, "template");
        return path;

    }


    /**
     * 下载模板
     */
    @PostMapping("dataExportTemplate")
    public String dataExportTemplate(){
        return "http://222.178.212.29:9000/template/d3134ee2d696455881ab6038ea205eab";
    }


    /**
     * 数据录入2.0
     */
    @Login
    @PostMapping("dataImport2")
    public CommonResult dataImport2(MultipartFile file){
        try {
            List<String[]> strings = POIUtils.readExcel(file);
            Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
            Map<String, Object> map =vehicleService.addLinkByExcel2(strings,communityId);
            return CommonResult.ok(map);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.ok("添加失败,请联系管理员!");
        }
    }


    /**
     * 数据导出
     */
    @RequestMapping("dataExport")
    public CommonResult dataExport( HttpServletResponse response){
        List<CarMonthlyVehicle> vehicleList = vehicleService.selectList(UserUtils.getAdminCommunityId());
        List<String[]> list=new ArrayList<>();//封装返回的数据
        String[] strings0=new String[10];
        strings0[0]=  "车牌号";
        strings0[1]= "车主姓名";
        strings0[2]= "联系电话";
        strings0[3]= "包月方式";
        strings0[4]= "开始时间";
        strings0[5]= "结束时间";
        strings0[6]= "包月费用";
        strings0[7]= "下发状态";
        strings0[8]= "备注";
        strings0[9]= "车位编号";
        list.add(strings0);//excel属性名
        for (int i = 0; i < vehicleList.size(); i++) {
            CarMonthlyVehicle carMonthlyVehicle = vehicleList.get(i);
            String[] strings1=new String[10];
            strings1[0]= String.valueOf( carMonthlyVehicle.getCarNumber());
            strings1[1]= String.valueOf(carMonthlyVehicle.getOwnerName());
            strings1[2]= String.valueOf(carMonthlyVehicle.getPhone());
            strings1[3]=String.valueOf(carMonthlyVehicle.getMonthlyMethodName());
            strings1[4]= String.valueOf(carMonthlyVehicle.getStartTime());
            strings1[5]= String.valueOf(carMonthlyVehicle.getEndTime());
            strings1[6]= String.valueOf(carMonthlyVehicle.getMonthlyFee());
            Integer distributionStatus = carMonthlyVehicle.getDistributionStatus();
            if (0==distributionStatus || distributionStatus==null){
                strings1[7]= ("未下发");
            }
            if (1==distributionStatus){
                strings1[7]= ("已下发");
            }
            strings1[8]= String.valueOf(carMonthlyVehicle.getRemarks());
            strings1[9]= String.valueOf(carMonthlyVehicle.getCarPosition());
            list.add(strings1);
        }
        try {
            POIUtil.writePoi(list,"我的文件",2,response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResult.ok();
    }

    /**
     * 多条件+分页查询包月车位
     */
    @Login
    @PostMapping("findByMultiConditionPage2Position")
    public CommonResult<PageInfo> findByMultiConditionPage2Position(@RequestBody CarMonthlyVehicleQO carMonthlyVehicleQO) {
        PageInfo pageInfo = vehicleService.findByMultiConditionPage2Position(carMonthlyVehicleQO,UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }

    /**
     * 包月车位新增
     */
    @Login
    @PostMapping("SaveMonthlyVehicle2Position")
    public CommonResult SaveMonthlyVehicle2Position(@RequestBody CarMonthlyVehicle carMonthlyVehicle) {
        vehicleService.SaveMonthlyVehicle2Position(carMonthlyVehicle, UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }

    /**
     * 包月车位导入
     */
    @Login
    @PostMapping("dataImport2Position")
    public CommonResult dataImport2Position(MultipartFile file){
        try {
            List<String[]> strings = POIUtils.readExcel(file);
            Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
            Map<String, Object> map =vehicleService.addLinkByExcel2Position(strings,communityId);
            return CommonResult.ok(map);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.ok("添加失败,请联系管理员!");
        }
    }

    /**
     * 包月车位导出
     */
    @Login
    @RequestMapping("dataExport2Position")
    public CommonResult dataExport2Position( HttpServletResponse response){
        List<CarMonthlyVehicle> vehicleList = vehicleService.selectList(UserUtils.getAdminCommunityId());
        List<String[]> list=new ArrayList<>();//封装返回的数据
        String[] strings0=new String[10];
        strings0[0]=  "车位号";
        strings0[1]= "车主姓名";
        strings0[2]= "联系电话";
        strings0[3]= "车牌号";
        strings0[4]= "开始时间";
        strings0[5]= "到期时间";
        strings0[6]= "备注";
        strings0[7]= "所属房屋";
        strings0[8]="下发状态";
        list.add(strings0);//excel属性名
        for (int i = 0; i < vehicleList.size(); i++) {
            CarMonthlyVehicle carMonthlyVehicle = vehicleList.get(i);
            String[] strings1=new String[10];
            strings1[0]= String.valueOf( carMonthlyVehicle.getCarPosition());
            strings1[1]= String.valueOf(carMonthlyVehicle.getOwnerName());
            strings1[2]= String.valueOf(carMonthlyVehicle.getPhone());
            strings1[3]=String.valueOf(carMonthlyVehicle.getCarNumber());
            strings1[4]= String.valueOf(carMonthlyVehicle.getStartTime());
            strings1[5]= String.valueOf(carMonthlyVehicle.getEndTime());
            strings1[6]= String.valueOf(carMonthlyVehicle.getRemarks());

            Long houseId = carMonthlyVehicle.getHouseId();
            HouseEntity houseEntity = houseService.getById(houseId);
            if (Objects.nonNull(houseEntity)){
                String building = houseEntity.getBuilding();
                String unit = houseEntity.getUnit();
                Integer floor = houseEntity.getFloor();
                String door = houseEntity.getDoor();
                String getBelongHouse=building+unit+floor+door;
                strings1[7]= getBelongHouse;//所属房屋
            }
            Integer distributionStatus = carMonthlyVehicle.getDistributionStatus();
            if (0==distributionStatus || distributionStatus==null){
                strings1[8]= ("未下发");
            }
            if (1==distributionStatus){
                strings1[8]= ("已下发");
            }
            list.add(strings1);
        }
        try {
            POIUtil.writePoi(list,"我的文件",2,response);
        } catch (IOException e) {
            return CommonResult.error("导出失败，请联系管理员");
        }
      return   CommonResult.ok();

    }

    /**
     * 包月车位导入模板下载
     */
    @PostMapping("dataExportTemplate2Position")
    public String dataExportTemplate2Position(){
        return "http://222.178.212.29:9000/template/dac28811cce04c92ba37f50089fc4c9f";
    }


}
