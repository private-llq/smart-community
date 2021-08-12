package com.jsy.community.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarLaneService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.CarLaneEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "停车收费设置")
@RestController
@RequestMapping("carLane")
@ApiJSYController
public class CarLaneController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    public ICarLaneService carLaneService;


    @PostMapping("SaveCarLane")
    public CommonResult SaveCarLane(@RequestBody CarLaneEntity CarLaneEntity) {
        carLaneService.SaveCarLane(CarLaneEntity);
        return CommonResult.ok();
    }

    @PutMapping("UpdateCarLane")
    public CommonResult UpdateCarLane(@RequestBody CarLaneEntity carLaneEntity) {
        carLaneService.UpdateCarLane(carLaneEntity);
        return CommonResult.ok();
    }



    @DeleteMapping("DelCarLane")
    public CommonResult DelCarLane(@RequestParam("uid") String uid) {
        carLaneService.DelCarLane(uid);
        return CommonResult.ok();
    }



    @GetMapping("FindByLaneNamePage")
    public CommonResult<PageInfo> FindByLaneNamePage(@RequestBody BaseQO baseQO) {
        PageInfo pageInfo = carLaneService.FindByLaneNamePage(baseQO);
        return CommonResult.ok(pageInfo);
    }

    @GetMapping("FindByLaneNamePage2")
    public CommonResult<PageInfo> FindByLaneNamePage2(@RequestBody BaseQO baseQO) {
        PageInfo pageInfo = carLaneService.FindByLaneNamePage2(baseQO);
        return CommonResult.ok(pageInfo);
    }

}
