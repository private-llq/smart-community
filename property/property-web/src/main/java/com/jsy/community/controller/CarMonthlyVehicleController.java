package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarMonthlyVehicleService;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarMonthlyVehicle;
import com.jsy.community.qo.CarMonthlyDelayQO;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.util.POIUtil;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.POIUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(tags = "包月车辆")
@RestController
@RequestMapping("CarMonthlyVehicle")
// @ApiJSYController
public class CarMonthlyVehicleController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarMonthlyVehicleService vehicleService;

    @DubboReference(version = Const.version, group = Const.group_property,check = false)
    private IHouseService houseService;


    /**
     * 新增
     * @param carMonthlyVehicle
     * @return
     */
    @PostMapping("SaveMonthlyVehicle")
    @CarOperation(operation = "新增了【月租停车】")
    @Permit("community:property:CarMonthlyVehicle:SaveMonthlyVehicle")
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
    @CarOperation(operation = "更新了【月租停车】")
    @Permit("community:property:CarMonthlyVehicle:UpdateMonthlyVehicle")
    public CommonResult UpdateMonthlyVehicle(@RequestBody CarMonthlyVehicle carMonthlyVehicle) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        carMonthlyVehicle.setCommunityId(adminCommunityId);
        vehicleService.UpdateMonthlyVehicle(carMonthlyVehicle);
        return CommonResult.ok();
    }

    /**
     * 根据uuid删除
     * @param uid
     * @return
     */
    @LoginIgnore
    @DeleteMapping("DelMonthlyVehicle")
    @CarOperation(operation = "删除了【月租停车】")
    public CommonResult DelMonthlyVehicle(@RequestParam("uid") String uid) {
        vehicleService.DelMonthlyVehicle(uid);
        return CommonResult.ok();

    }

    /**
     * 多条件查询+分页
     * @param carMonthlyVehicleQO
     * @return
     */
    @PostMapping("FindByMultiConditionPage")
    @Permit("community:property:CarMonthlyVehicle:FindByMultiConditionPage")
    public CommonResult<PageInfo> FindByMultiConditionPage(@RequestBody CarMonthlyVehicleQO carMonthlyVehicleQO) {
        PageInfo pageInfo = vehicleService.FindByMultiConditionPage(carMonthlyVehicleQO,UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }

    /**
     * 包月延期 0 按天 1 按月
     * @param carMonthlyDelayQO
     */
    @PostMapping("delay")
    @CarOperation(operation = "包月延期了【月租停车】")
    @Permit("community:property:CarMonthlyVehicle:delay")
    public CommonResult delay(@RequestBody CarMonthlyDelayQO carMonthlyDelayQO){
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        carMonthlyDelayQO.setCommunityId(adminCommunityId);
        vehicleService.delay(carMonthlyDelayQO);
        return CommonResult.ok(0,"延期成功！");
    }


    /**
     * 同步按钮 (下发业务)
     */
    @GetMapping("issue")
    @CarOperation(operation = "同步下发了【月租停车】")
    @Permit("community:property:CarMonthlyVehicle:issue")
    public CommonResult issue(@RequestParam("uid") String uid){
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        vehicleService.issue(uid,adminCommunityId);
        return CommonResult.ok("操作成功");


    }

    /**
     * 上传模板
     */
    @LoginIgnore
    @PostMapping("uploadTemplate")
    public  String uploadTemplate(MultipartFile file){
        String path = MinioUtils.upload(file, "template");
        return path;

    }


    /**
     * 下载模板
     */
    @LoginIgnore
    @CarOperation(operation = "下载了【包月车辆导入模板】")
    @PostMapping("dataExportTemplate")
    public String dataExportTemplate(){
        return "http://222.178.212.29:9000/template/d3134ee2d696455881ab6038ea205eab";
    }


    /**
     * 数据录入2.0
     */
    @PostMapping("dataImport2")
    @CarOperation(operation = "导入了【包月车辆数据】")
    @Permit("community:property:CarMonthlyVehicle:dataImport2")
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
     * 车辆数据导出
     */
    @PostMapping("dataExport")
    @CarOperation(operation = "导出了【包月车辆数据】")
    @Permit("community:property:CarMonthlyVehicle:dataExport")
    public CommonResult dataExport(@RequestBody CarMonthlyVehicleQO carMonthlyVehicleQO, HttpServletResponse response){
        carMonthlyVehicleQO.setCommunityId(UserUtils.getAdminCommunityId());
        List<CarMonthlyVehicle> vehicleList = vehicleService.selectListCar(carMonthlyVehicleQO);
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
    @PostMapping("findByMultiConditionPage2Position")
    @Permit("community:property:CarMonthlyVehicle:findByMultiConditionPage2Position")
    public CommonResult<PageInfo> findByMultiConditionPage2Position(@RequestBody CarMonthlyVehicleQO carMonthlyVehicleQO) {
        PageInfo pageInfo = vehicleService.findByMultiConditionPage2Position(carMonthlyVehicleQO,UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }

    /**
     * 包月车位新增
     */
    @PostMapping("SaveMonthlyVehicle2Position")
    @CarOperation(operation = "新增了【包月车位数据】")
    @Permit("community:property:CarMonthlyVehicle:SaveMonthlyVehicle2Position")
    public CommonResult SaveMonthlyVehicle2Position(@RequestBody CarMonthlyVehicle carMonthlyVehicle) {
        vehicleService.SaveMonthlyVehicle2Position(carMonthlyVehicle, UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }

    /**
     * 包月车位导入
     */
    @PostMapping("dataImport2Position")
    @CarOperation(operation = "导入了【包月车位数据】")
    @Permit("community:property:CarMonthlyVehicle:dataImport2Position")
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
    @PostMapping("dataExport2Position")
    @CarOperation(operation = "导出了【包月车位数据】")
    @Permit("community:property:CarMonthlyVehicle:dataExport2Position")
    public CommonResult dataExport2Position(@RequestBody CarMonthlyVehicleQO carMonthlyVehicleQO, HttpServletResponse response){
        carMonthlyVehicleQO.setCommunityId(UserUtils.getAdminCommunityId());
        List<CarMonthlyVehicle> vehicleList = vehicleService.selectListPostion(carMonthlyVehicleQO);
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
    @LoginIgnore
    @PostMapping("dataExportTemplate2Position")
    @CarOperation(operation = "下载了【包月车位导入模板】")
    public String dataExportTemplate2Position(){
        return "http://222.178.212.29:9000/template/485336f8ed1d46fea3d26f6475770f5a";
    }


}
