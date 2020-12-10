package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/lease")
@Api(tags = "房屋租售控制器")
@Slf4j
@RestController
@ApiJSYController
public class HouseLeaseController {



    @ApiOperation("新增房屋租售")
    @GetMapping("/addHouseLease")
    public CommonResult<?> addLeaseSaleHouse(@RequestBody HouseLeaseQO houseLeaseQO) {
        //新增参数效验
        ValidatorUtils.validateEntity(houseLeaseQO, HouseLeaseQO.addLeaseSaleHouse.class);
        return null;
    }




}
