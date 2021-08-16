package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarCutOffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cutOff")
@ApiJSYController
@Api("开闸记录")
public class CarCutOffController{
    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarCutOffService carCutOffService;

    @Login
    @PostMapping("/selectPage")
    private CommonResult selectPage(@RequestBody CarCutOffQO carCutOffQO){
        System.out.println(carCutOffQO);
        System.out.println(carCutOffQO.getPage());
        System.out.println(carCutOffQO.getSize());
       Page<CarCutOffEntity> page =  carCutOffService.selectPage(carCutOffQO, UserUtils.getAdminCommunityId());
        return CommonResult.ok(page,"查询成功");
    }
}