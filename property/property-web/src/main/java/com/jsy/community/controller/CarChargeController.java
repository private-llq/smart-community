package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarChargeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarChargeEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarChargeQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = "停车收费设置")
@RestController
@RequestMapping("/carCharge")
@ApiJSYController
public class CarChargeController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarChargeService carChargeService;


    @Login
    @PostMapping("/listPage")
    public CommonResult<PageInfo> listPage(@RequestBody BaseQO baseQO){

        PageInfo pageInfo= carChargeService.listCarChargePage(baseQO, UserUtils.getAdminCommunityId());

        return  CommonResult.ok(pageInfo);
    }

    @Login
    @PostMapping("/save")
    @businessLog(operation = "新增",content = "新增了【停车收费设置】")
    public CommonResult save(@RequestBody CarChargeEntity carChargeEntity){
        carChargeService.SaveCarCharge(carChargeEntity,UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }

    @PutMapping("/update")
    @businessLog(operation = "编辑",content = "更新了【停车收费设置】")
    public CommonResult update(@RequestBody CarChargeEntity carChargeEntity){
        carChargeService.UpdateCarCharge(carChargeEntity);
        return CommonResult.ok();

    }
    @DeleteMapping("/del")
    @businessLog(operation = "删除",content = "删除了【停车收费设置】")
    public CommonResult delete(@RequestParam("uid") String uid){
        carChargeService.DelCarCharge(uid);
        return CommonResult.ok();
    }

    /**
     * 根据收费类型查询 0：月租 1：临时
     * @param type
     * @return
     */
    @Login
    @GetMapping("/getTypeList")
    public CommonResult getTypeList(@RequestParam Integer type){
        List<CarChargeEntity> list = carChargeService.selectCharge(type,UserUtils.getAdminCommunityId());
        return CommonResult.ok(list);
    }


    /**
     * 临时停车收费设置 save
     */
    @Login
    @PostMapping("/temporaryParkingSet")
    @businessLog(operation = "新增",content = "新增了【临时停车收费设置】")
    public CommonResult temporaryParkingSet(@RequestBody CarChargeEntity carChargeEntity){
        Integer integer = carChargeService.temporaryParkingSet(carChargeEntity, UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }

    /**
     * 计算该项设置的收费 Test charge
     */

    @Login
    @PostMapping("/TestCharge")
    public CommonResult TestCharge(@RequestBody CarChargeQO carChargeQO){
       BigDecimal charge =carChargeService.testCharge(carChargeQO);
        return CommonResult.ok(charge);
    }

    /**
     * 查询包月的所有收费标准
     */
    @Login
    @GetMapping("/ListCharge")
    public CommonResult ListCharge(){
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        List<CarChargeEntity> chargeEntityList= carChargeService.ListCharge(adminCommunityId);
        return CommonResult.ok(chargeEntityList);
    }

    /**
     * 查询临时停车收费标准
     */
    @Login
    @GetMapping("/ListCharge2")
    public CommonResult ListCharge2(){
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        List<CarChargeEntity> chargeEntityList= carChargeService.ListCharge2(adminCommunityId);
        return CommonResult.ok(chargeEntityList);
    }

    /**
     * 根据uid查询单个收费设置标准
     */
    @Login
    @GetMapping("/selectOneCharge")
    public CommonResult selectOneCharge(@RequestParam("uid") String uid){
       CarChargeEntity chargeEntity= carChargeService.selectOneCharge(uid,UserUtils.getAdminCommunityId());
        return CommonResult.ok(chargeEntity);
    }



}
