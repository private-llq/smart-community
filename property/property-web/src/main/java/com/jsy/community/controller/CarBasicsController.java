package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarBasicsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarBasicsEntity;
import com.jsy.community.qo.property.CarBasicsMonthQO;
import com.jsy.community.qo.property.CarBasicsRuleQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
  * @author Tian
  * @since 2021/8/3-15:17
  * @description 车禁-基础设置
  **/
 @Api(tags = "基础设备")
 @RestController
 @RequestMapping("/carBasics")
 @ApiJSYController
public class CarBasicsController {

     @DubboReference(version = Const.version,group = Const.group_property,check = false)
     private ICarBasicsService carBasics;

     @Login
     @ApiOperation("添加或修改临时车规则")
     @PostMapping("/addBasics")
    public CommonResult addBasics(@RequestBody CarBasicsRuleQO carBasicsRuleQO){
         boolean b = carBasics.addBasics(carBasicsRuleQO,UserUtils.getUserId(),UserUtils.getAdminCommunityId());
         return CommonResult.ok("操作成功");
     }

     @Login
     @ApiOperation("查询当前社区的临时车规则")
     @GetMapping("/findOne")
     public CommonResult findSpecial(){
         System.out.println(UserUtils.getAdminCommunityId());
         CarBasicsEntity  carBasicsEntity = carBasics.findOne(UserUtils.getAdminCommunityId());
         return CommonResult.ok(carBasicsEntity);
     }

     @ApiOperation("添加火修改特殊车辆是否收费")
     @PostMapping("/exceptionCar")
     @Login
     public CommonResult addExceptionCar(@RequestParam("exceptionCar") Integer exceptionCar){
         String userId = UserUtils.getUserId();
         Long communityId = UserUtils.getAdminCommunityId();
         boolean b =  carBasics.addExceptionCar(exceptionCar,userId,communityId);
        return CommonResult.ok("操作成功");
      }
      @ApiOperation("添加或修改包月选项")
      @PostMapping("/monthlyPayment")
      @Login
     public CommonResult addMonthlyPayment(@RequestBody CarBasicsMonthQO carBasicsMonthQO){
         boolean b = carBasics.addMonthlyPayment(carBasicsMonthQO,UserUtils.getUserId(),UserUtils.getAdminCommunityId());
        return CommonResult.ok("操作成功");

     }
}
