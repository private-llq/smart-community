package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.HouseLeaseVO;
import com.jsy.lease.api.IHouseLeaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@ApiJSYController
@RestController
@Api(tags = "房屋租售控制器")
@RequestMapping("/house")
public class HouseLeaseController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseLeaseService iHouseLeaseService;


    @Login
    @PostMapping()
    @ApiOperation("新增房屋租售")
    public CommonResult<Boolean> addLeaseHouse(@RequestBody HouseLeaseQO houseLeaseQO) {
        //新增参数常规效验
        ValidatorUtils.validateEntity(houseLeaseQO, HouseLeaseQO.addLeaseSaleHouse.class);
        houseLeaseQO.setUid(UserUtils.getUserId());
        //参数效验完成 新增
        iHouseLeaseService.addLeaseSaleHouse(houseLeaseQO);
        return CommonResult.ok();
    }

    @Login
    @DeleteMapping()//delHouseLease
    @ApiOperation("删除房屋租售")
    public CommonResult<Boolean> delLeaseHouse(@RequestParam Long id) {
        return iHouseLeaseService.delLeaseHouse(id, UserUtils.getUserId()) ? CommonResult.ok() : CommonResult.error(1, "数据不存在");
    }


    @Login
    @GetMapping()
    @ApiOperation("查询房屋出租数据")
    public CommonResult<List<HouseLeaseVO>> queryHouseLeaseByList(@RequestBody BaseQO<HouseLeaseQO> baseQO) {
        ValidatorUtils.validatePageParam(baseQO);
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseByList(baseQO));
    }


    @Login
    @GetMapping("/details")
    @ApiOperation("查询房屋出租数据单条详情")
    public CommonResult<HouseLeaseVO> houseLeaseDetails(@RequestParam Long houseId) {
        return CommonResult.ok(iHouseLeaseService.queryHouseLeaseOne(houseId));
    }



    public static void main(String[] args) {
        System.out.println(20 | 7);
    }



}
