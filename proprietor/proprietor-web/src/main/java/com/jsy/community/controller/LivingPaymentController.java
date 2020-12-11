package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ILivingPaymentService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.LivingPaymentQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.GroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 生活缴费前端控制器
 * @author: Hu
 * @create: 2020-12-11 09:28
 **/
@Api(tags = "生活缴费前端控制器")
@RestController
@RequestMapping("/livingpayment")
@ApiJSYController
public class LivingPaymentController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ILivingPaymentService livingPaymentService;

    // 1. 根据城市id查询缴费类型  id =1  jiaofei   1 23 344

    // 2. 缴费类型id 查询缴费单位 集合 某一个公司

    //2.

    /**
     * 添加缴费记录
     * @param livingPaymentQO
     * @return
     */
    @ApiOperation("生活缴费")
    @PostMapping("/add")
    @Login
    public CommonResult add(@RequestBody LivingPaymentQO livingPaymentQO){
        String userId = UserUtils.getUserId();
        System.out.println(livingPaymentQO);
        livingPaymentQO.setUserID(userId);
        livingPaymentService.add(livingPaymentQO);
        return CommonResult.ok();
    }

    /**
     * 选择分组查询下面缴过费的水电气户号
     * @param groupQO
     * @return
     */
    @ApiOperation("选择分组查询下面缴过费的水电气户号")
    @PostMapping("/selectGroup")
    @Login
    public CommonResult selectGroup(@RequestBody GroupQO groupQO){
        String userId = UserUtils.getUserId();
        groupQO.setUserID(userId);
        List<GroupVO> voList = livingPaymentService.selectGroup(groupQO);
        return CommonResult.ok(voList);
    }

    /**
     * 通过组户号查询订单详情
     * @param paymentRecordsQO
     * @return
     */
    @ApiOperation("通过组户号查询订单详情")
    @PostMapping("/selectOrder")
    @Login
    public CommonResult selectOrder(@RequestBody PaymentRecordsQO paymentRecordsQO){
        String userId = UserUtils.getUserId();
        paymentRecordsQO.setUserID(userId);
        livingPaymentService.selectOrder(paymentRecordsQO);
        return CommonResult.ok();
    }

    /**
     * 通过组户号查询订单详情
     * @param
     * @return
     */
    @ApiOperation("通过组户号查询订单详情")
    @PostMapping("/selectList")
    @Login
    public CommonResult selectList(){
        String userId = UserUtils.getUserId();
        livingPaymentService.selectList(userId);
        return CommonResult.ok();
    }

}
