package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarCutOffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cutOff")
@ApiJSYController
@Api("开闸记录")
public class CarCutOffController{
    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarCutOffService carCutOffService;


    @Login
    @PostMapping("/selectPage")
    public CommonResult selectPage(@RequestBody CarCutOffQO carCutOffQO){
        PageInfo<CarCutOffEntity> pageInfo = carCutOffService.selectPage(carCutOffQO);
        return CommonResult.ok(pageInfo,"查询成功");
    }

    @Login
    @PostMapping("/addCutOff")
    public CommonResult addCutOff(@RequestBody CarCutOffEntity carCutOffEntity){
        boolean b=  carCutOffService.addCutOff(carCutOffEntity);
        return CommonResult.ok("添加成功");
    }
    @Login
    @PostMapping("/updateCutOff")
    public CommonResult updateCutOff(@RequestBody CarCutOffEntity carCutOffEntity){
        boolean b=  carCutOffService.updateCutOff(carCutOffEntity);
        return CommonResult.ok("修改成功");
    }

    @Login
    @PostMapping("/selectAccess")
    public CommonResult selectAccess(@RequestParam("car_number") String carNumber, @RequestParam("state") Integer state){
        List<CarCutOffEntity>  carCutOffEntityList =  carCutOffService.selectAccess(carNumber,state);
        return CommonResult.ok(carCutOffEntityList,"查询成功");
    }

}
