package com.jsy.community.controller;


import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarBasicsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarBasicsEntity;
import com.jsy.community.qo.property.CarBasicsMonthQO;
import com.jsy.community.qo.property.CarBasicsRuleQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
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
 // @ApiJSYController
public class CarBasicsController {

     @DubboReference(version = Const.version,group = Const.group_property,check = false)
     private ICarBasicsService carBasics;

     @ApiOperation("添加或修改临时车规则")
     @PostMapping("/addBasics")
     @businessLog(operation = "编辑",content = "更新了【临时车规则】")
     @CarOperation(operation = "更新了【临时车规则】")
     @Permit("community:property:carBasics:addBasics")
    public CommonResult addBasics(@RequestBody CarBasicsRuleQO carBasicsRuleQO){
         boolean b = carBasics.addBasics(carBasicsRuleQO,UserUtils.getUserId(),UserUtils.getAdminCommunityId());
         return CommonResult.ok("操作成功");
     }

     @ApiOperation("查询当前社区的临时车规则")
     @GetMapping("/findOne")
     @CarOperation(operation = "临时车规则")
     @Permit("community:property:carBasics:findOne")
     public CommonResult findSpecial(){
         System.out.println(UserUtils.getAdminCommunityId());
         String userId = UserUtils.getUserId();
         CarBasicsEntity  carBasicsEntity = carBasics.findOne(UserUtils.getAdminCommunityId());
         return CommonResult.ok(carBasicsEntity);
     }

     @ApiOperation("添加火修改特殊车辆是否收费")
     @PostMapping("/exceptionCar")
     @businessLog(operation = "编辑",content = "更新了【特殊车辆是否收费】")
     @CarOperation(operation = "更新了【特殊车辆是否收费】")
     @Permit("community:property:carBasics:exceptionCar")
     public CommonResult addExceptionCar(@RequestParam("exceptionCar") Integer exceptionCar){
         String userId = UserUtils.getUserId();
         Long communityId = UserUtils.getAdminCommunityId();
         boolean b =  carBasics.addExceptionCar(exceptionCar,userId,communityId);
        return CommonResult.ok("操作成功");
      }
      
      @ApiOperation("添加或修改包月选项")
      @PostMapping("/monthlyPayment")
      @businessLog(operation = "编辑",content = "更新了【包月选项】")
      @CarOperation(operation = "更新了【包月选项】")
      @Permit("community:property:carBasics:monthlyPayment")
     public CommonResult addMonthlyPayment(@RequestBody CarBasicsMonthQO carBasicsMonthQO){
         boolean b = carBasics.addMonthlyPayment(carBasicsMonthQO,UserUtils.getUserId(),UserUtils.getAdminCommunityId());
        return CommonResult.ok("操作成功");

     }
    @ApiOperation("添加或修改包月选项")
    @PostMapping("/addBasics2")
    @businessLog(operation = "编辑",content = "更新了【包月选项】")
    @CarOperation(operation = "更新了【包月选项】")
    @Permit("community:property:carBasics:addBasics2")
    public CommonResult addBasics2(){
        String userId = UserUtils.getUserId();
        Long communityId = UserUtils.getAdminCommunityId();
        boolean b = carBasics.addBasics2(userId,communityId);
        return CommonResult.ok("操作成功");

    }
}
