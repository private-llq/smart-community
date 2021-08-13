package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarChargeService;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.CarChargeEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "停车收费设置")
@RestController
@RequestMapping("/carCharge")
@ApiJSYController
public class CarChargeController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarChargeService carChargeService;


    @GetMapping("/listPage")
    public CommonResult<PageInfo> listPage(@RequestBody BaseQO baseQO){

        PageInfo pageInfo= carChargeService.listCarChargePage(baseQO);

        return  CommonResult.ok(pageInfo);
    }

    @PostMapping("/save")
    public CommonResult save(@RequestBody CarChargeEntity carChargeEntity){
        carChargeService.SaveCarCharge(carChargeEntity);
        return CommonResult.ok();
    }

    @PostMapping("/update")
    CommonResult update(@RequestBody CarChargeEntity carChargeEntity){
        carChargeService.UpdateCarCharge(carChargeEntity);
        return CommonResult.ok();

    }
    @DeleteMapping("/del")
    CommonResult delete(@RequestParam("uid") String uid){
        carChargeService.DelCarCharge(uid);
        return CommonResult.ok();
    }

    /**
     * 根据收费类型查询 0：月租 1：临时
     * @param type
     * @return
     */
    @GetMapping("/getTypeList")
    CommonResult getTypeList(@RequestParam Integer type){
        List<CarChargeEntity> list = carChargeService.selectCharge(type);
        return CommonResult.ok(list);
    }
}
