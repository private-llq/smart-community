package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ILivingpaymentQueryService;
import com.jsy.community.api.IPayCompanyService;
import com.jsy.community.api.IPayGroupService;
import com.jsy.community.api.IPayTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayTypeEntity;
import com.jsy.community.qo.livingpayment.PayCompanyQO;
import com.jsy.community.qo.livingpayment.PaymentRecordsQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.livingpayment.*;
import com.jsy.community.vo.shop.PaymentRecordsMapVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  用于查询
 * @author: Hu
 * @create: 2021-02-26 13:57
 **/
@Api(tags = "生活缴费前端控制器--查询")
@RestController
@RequestMapping("/livingpaymentquery")
@ApiJSYController
public class LivingPaymentQueryController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ILivingpaymentQueryService livingpaymentQueryService;
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayTypeService payTypeService;
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayCompanyService payCompanyService;
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayGroupService payGroupService;

    @PostMapping("/getPayDetails")
    @ApiOperation("假账单接口")
    @Login
    public CommonResult getPayDetails(@RequestParam("familyId")String familyId, @RequestParam("companyId")Long companyId){
        Map payDetails = livingpaymentQueryService.getPayDetails(familyId, companyId);
        return CommonResult.ok(payDetails);
    }

    @ApiOperation("缴费类型")
    @GetMapping("/getPayType")
    @Login
    public CommonResult getPayType(@ApiParam("城市id") @RequestParam("cityId") Long cityId){
        List<PayTypeEntity> payType = payTypeService.getPayTypes(cityId);
        return CommonResult.ok(payType);
    }

    @ApiOperation("查询全部户号")
    @GetMapping("/selectFamilyId")
    @Login
    public CommonResult selectFamilyId(){
        String userId = UserUtils.getUserId();
        List<FamilyIdVO> familyIdVOS = livingpaymentQueryService.selectFamilyId(userId);
        return CommonResult.ok(familyIdVOS);
    }

    @ApiOperation("根据城市和缴费类型查询缴费单位")
    @PostMapping("/selectPayCompany")
    @Login
    public CommonResult selectPayCompany(@RequestBody PayCompanyQO payCompanyQO){
        List<PayCompanyVO> payCompany = payTypeService.selectPayCompany(payCompanyQO);
        return CommonResult.ok(payCompany);
    }


    /**
     * 查询当前登录人员自定义的分组
     * @param
     * @return
     */
    @ApiOperation("查询自定义分组")
    @GetMapping("/selectUserGroup")
    @Login
    public CommonResult selectGroup(){
        String userId = UserUtils.getUserId();
        List<UserGroupVO> voList = livingpaymentQueryService.selectUserGroup(userId);
        return CommonResult.ok(voList);
    }
    /**
     * 默认查询所有缴费信息
     * @param
     * @return
     */
    @ApiOperation("我的缴费")
    @GetMapping("/selectList")
    @Login
    public CommonResult selectList(){
        String userId = UserUtils.getUserId();
        List<DefaultHouseOwnerVO> list = livingpaymentQueryService.selectList(userId);
        return CommonResult.ok(list);
    }


    @ApiOperation("缴费凭证")
    @GetMapping("/getOrderID")
    @Login
    public CommonResult getOrderID(@ApiParam("订单id") @RequestParam("orderId") Long orderId){
        String uid = UserUtils.getUserId();
        PayVoucherVO payVoucherVO=livingpaymentQueryService.getOrderID(orderId,uid);
        return CommonResult.ok(payVoucherVO);
    }

    @ApiOperation("缴费详情")
    @GetMapping("/selectPaymentDetailsVO")
    @Login
    public CommonResult selectPaymentDetailsVO(@RequestParam("orderId") Long orderId){
        String userId = UserUtils.getUserId();
        PaymentDetailsVO paymentDetailsVO = livingpaymentQueryService.selectPaymentDetailsVO(orderId,userId);
        return CommonResult.ok(paymentDetailsVO);
    }

    @ApiOperation("户号管理")
    @GetMapping("/selectGroupAll")
    @Login
    public CommonResult selectGroupAll(){
        String userId = UserUtils.getUserId();
        PaymentRecordsMapVO voList = livingpaymentQueryService.selectGroupAll(userId);
        return CommonResult.ok(voList);
    }

    @ApiOperation("缴费记录")
    @PostMapping("/selectOrder")
    @Login
    public CommonResult selectOrder(@RequestBody PaymentRecordsQO paymentRecordsQO){
        String userId = UserUtils.getUserId();
        paymentRecordsQO.setUserID(userId);
        PaymentRecordsMapVO map = livingpaymentQueryService.selectOrder(paymentRecordsQO);
        return CommonResult.ok(map);
    }
    /**
     * 通过组户号查询订单详情
     * @param
     * @return
     */
    @ApiOperation("账单详情")
    @GetMapping("/selectOrderId")
    @Login
    public CommonResult selectOrderId(@RequestParam("orderId") Long orderId){
        String uid = UserUtils.getUserId();
        TheBillingDetailsVO theBillingDetailsVO = livingpaymentQueryService.selectOrderId(orderId,uid);
        return CommonResult.ok(theBillingDetailsVO);
    }





}
