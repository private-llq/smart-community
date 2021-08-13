package com.jsy.community.controller;

import com.alibaba.excel.EasyExcel;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarMonthlyVehicleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.CarMonthlyVehicle;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.utils.ExcelListener;
import com.jsy.community.utils.POIUtil;
import com.jsy.community.utils.POIUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = "停车收费设置")
@RestController
@RequestMapping("CarMonthlyVehicle")
@ApiJSYController
public class CarMonthlyVehicleController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarMonthlyVehicleService vehicleService;

    /**
     * 新增
     * @param carMonthlyVehicle
     * @return
     */

    @PostMapping("SaveMonthlyVehicle")
    public CommonResult SaveMonthlyVehicle(@RequestBody CarMonthlyVehicle carMonthlyVehicle) {
       vehicleService.SaveMonthlyVehicle(carMonthlyVehicle);
       return CommonResult.ok();
    }

    /**
     * 只能修改 车牌号，车主姓名，电话，备注
     * @param carMonthlyVehicle
     * @return
     */
    @PutMapping("UpdateMonthlyVehicle")
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
    public CommonResult DelMonthlyVehicle(@RequestParam("uid") String uid) {
        vehicleService.DelMonthlyVehicle(uid);
        return CommonResult.ok();

    }

    /**
     * 多条件查询+分页
     * @param carMonthlyVehicleQO
     * @return
     */

    @GetMapping("FindByMultiConditionPage")
    public CommonResult<PageInfo> FindByMultiConditionPage(@RequestBody CarMonthlyVehicleQO carMonthlyVehicleQO) {
        PageInfo pageInfo = vehicleService.FindByMultiConditionPage(carMonthlyVehicleQO);
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
     * 包月变更： 0:地上 1：地下
     */
    @PutMapping("monthlyChange")
    public CommonResult monthlyChange(@RequestParam("uid") String uid,@RequestParam("type") Integer type){
        vehicleService.monthlyChange(uid,type);
        return CommonResult.ok();
    }

    /**
     * 数据录入
     */
    @PostMapping("dataImport")
    public CommonResult dataImport(MultipartFile file){
        try {
            List<String[]> strings = POIUtil.ReadMessage(file);
            Map<String,Object> map= vehicleService.addLinkByExcel(strings);
            return CommonResult.ok(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return CommonResult.ok(-1,"服务器异常！");
    }

    /**
     * 数据导出
     */
    @RequestMapping("dataExport")
    public CommonResult dataExport( HttpServletResponse response){
        List<CarMonthlyVehicle> vehicleList = vehicleService.selectList();
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

            Integer monthlyMethod = carMonthlyVehicle.getMonthlyMethod();
            if (0==monthlyMethod){
                strings1[3]= ("地上");
            }
            if (1==monthlyMethod){
                strings1[3]= ("地下");
            }
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

}
