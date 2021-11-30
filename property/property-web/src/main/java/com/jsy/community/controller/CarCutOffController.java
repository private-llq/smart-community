package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarCutOffService;
import com.jsy.community.config.ExcelUtils;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.CarAccessVO;
import com.jsy.community.vo.property.CarSceneVO;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cutOff")
// @ApiJSYController
@Api("开闸记录")
public class CarCutOffController{
    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarCutOffService carCutOffService;


    /**
     * @Description: 查询临时车在场数量
     * @Param: [carCutOffQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/9/6-13:55
     **/
    @LoginIgnore
    @PostMapping("/selectPage")
    @Permit("community:property:cutOff:selectPage")
    public CommonResult selectPage(@RequestParam CarCutOffQO carCutOffQO){
        Long total = carCutOffService.selectPage(carCutOffQO);
        return CommonResult.ok(total,"查询成功");
    }

    /**
     * @Description: 查询进出记录  和在场车辆
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/9/9-14:24
     **/
    @PostMapping("/selectCarPage")
    @Permit("community:property:cutOff:selectCarPage")
    public CommonResult selectCarPage(@RequestBody BaseQO<CarCutOffQO> baseQO){
        Long communityId = UserUtils.getAdminCommunityId();
        Page<CarCutOffEntity> page = carCutOffService.selectCarPage(baseQO,communityId);
        return CommonResult.ok(page,"查询成功");
    }

    @PostMapping("/addCutOff")
    @CarOperation(operation = "新增了【开闸记录】")
    @Permit("community:property:cutOff:addCutOff")
    public CommonResult addCutOff(@RequestBody CarCutOffEntity carCutOffEntity){
        boolean b=  carCutOffService.addCutOff(carCutOffEntity);
        return CommonResult.ok("添加成功");
    }

    @PostMapping("/updateCutOff")
    @CarOperation(operation = "修改【出闸记录】")
    @Permit("community:property:cutOff:updateCutOff")
    public CommonResult updateCutOff(@RequestBody CarCutOffEntity carCutOffEntity){
        boolean b=  carCutOffService.updateCutOff(carCutOffEntity);
        return CommonResult.ok("修改成功");
    }

    @PostMapping("/selectAccess")
    @Permit("community:property:cutOff:selectAccess")
    public CommonResult selectAccess(@RequestParam("car_number") String carNumber, @RequestParam("state") Integer state){
        List<CarCutOffEntity>  carCutOffEntityList =  carCutOffService.selectAccess(carNumber,state);
        return CommonResult.ok(carCutOffEntityList,"查询成功");
    }

    @LoginIgnore
    @ApiOperation("导出模板")
    @PostMapping("/carCutOFFExport")
    @ResponseBody
    @CarOperation(operation = "导出了【进出记录模板】")
    @Permit("community:property:cutOff:carCutOFFExport")
    public void downLoadFile(@RequestBody CarCutOffQO carCutOffQO, HttpServletResponse response) throws IOException {
        Long communityId = UserUtils.getAdminCommunityId();
        if (carCutOffQO.getState()==0){
            System.out.println("在场车辆导出");
            //在场车辆
            List<CarSceneVO>  list = carCutOffService.selectCarSceneList(carCutOffQO,communityId);
            ExcelUtils.exportModule("在场车辆表", response, CarSceneVO.class, list, 2);

        }else {
            //进出记录
            List<CarAccessVO>  list = carCutOffService.selectAccessList(carCutOffQO,communityId);
            ExcelUtils.exportModule("进出记录表", response, CarAccessVO.class, list, 2);
        }

    }

}
