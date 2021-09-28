package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarChargeService;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarChargeEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarChargeQO;
import com.jsy.community.qo.property.orderChargeDto;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = "收费设置")
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
    @CarOperation(operation = "新增了【停车收费设置】")
    public CommonResult save(@RequestBody CarChargeEntity carChargeEntity){
        carChargeService.SaveCarCharge(carChargeEntity,UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }
    @Login
    @PostMapping("/openCarCharge")
    @CarOperation(operation = "启用了【停车收费设置模板】")
    public CommonResult openCarCharge(@RequestParam("uid") String uid,@RequestParam("type") Integer type){
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        carChargeService.openCarCharge(uid,type,adminCommunityId);
        return CommonResult.ok();
    }

    @PutMapping("/update")
    @CarOperation(operation = "更新了【停车收费设置模板】")
    public CommonResult update(@RequestBody CarChargeEntity carChargeEntity){
        carChargeService.UpdateCarCharge(carChargeEntity);
        return CommonResult.ok();

    }
    @DeleteMapping("/del")
    @CarOperation(operation = "删除了【停车收费设置模板】")
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
    @CarOperation(operation = "新增了【临时停车收费设置模板】")
    public CommonResult temporaryParkingSet(@RequestBody CarChargeEntity carChargeEntity){
        Integer integer = carChargeService.temporaryParkingSet(carChargeEntity, UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }

    /**
     * 计算该项设置的收费 Test charge
     */

    @Login
    @PostMapping("/TestCharge")
    @CarOperation(operation = "计算了【模板设置的费用】")
    public CommonResult TestCharge(@RequestBody CarChargeQO carChargeQO){
       BigDecimal charge =carChargeService.testCharge(carChargeQO);
        return CommonResult.ok(charge);
    }


    /**
     * 订单支付返回收费详情
     */
    @PostMapping("/orderCharge")
    public CommonResult orderCharge(@RequestParam Long adminCommunityId,@RequestParam String carNumber){
        orderChargeDto orderCharge =carChargeService.orderCharge(adminCommunityId,carNumber);

        if (orderCharge==null) {
            return CommonResult.error(501,"暂无临时车信息");
        }
        return CommonResult.ok(orderCharge,"查询成功");
    }

    /**
     * 查询包月的所有收费标准
     */
    //todo 暂时没用到
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
    //todo 暂时没用到
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
