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
@Api(tags = "生活缴费前端控制器")
@RestController
@RequestMapping("/livingpaymentquery")
@ApiJSYController
public class LivingpaymentQueryController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ILivingpaymentQueryService livingpaymentQueryService;
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayTypeService payTypeService;
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayCompanyService payCompanyService;
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayGroupService payGroupService;

    @PostMapping("/getPayDetails")
    @ApiOperation("户号和缴费单位ID生成假账单")
    public Map getPayDetails(@RequestParam("familyId")String familyId, @RequestParam("companyId")Long companyId){
        return livingpaymentQueryService.getPayDetails(familyId,companyId);
    }

    @ApiOperation("根据城市id查询其支持的缴费类型")
    @GetMapping("/getPayType")
    public CommonResult getPayType(@ApiParam("城市id") @RequestParam("cityId") Long cityId){
        List<PayTypeEntity> payType = payTypeService.getPayTypes(cityId);
        return CommonResult.ok(payType);
    }

    @ApiOperation("城市id加缴费类型id查询缴费单位")
    @PostMapping("/selectPayCompany")
    public CommonResult selectPayCompany(@RequestBody PayCompanyQO payCompanyQO){
        List<PayCompanyVO> payCompany = payTypeService.selectPayCompany(payCompanyQO);
        return CommonResult.ok(payCompany);
    }


    @ApiOperation("通过订单id查询缴费凭证")
    @GetMapping("/getOrderID")
    public CommonResult getOrderID(@ApiParam("订单id") @RequestParam("orderId") Long orderId){
        PayVoucherVO payVoucherVO=livingpaymentQueryService.getOrderID(orderId);
        return CommonResult.ok(payVoucherVO);
    }

    /**
     * 查询一条订单详情
     * @param id
     * @return
     */
    @ApiOperation("查询一条订单详情")
    @GetMapping("/selectPaymentDetailsVO")
    @Login
    public CommonResult selectPaymentDetailsVO(@RequestParam("orderId") Long orderId){
        String userId = UserUtils.getUserId();
        PaymentDetailsVO paymentDetailsVO = livingpaymentQueryService.selectPaymentDetailsVO(orderId,userId);
        return CommonResult.ok(paymentDetailsVO);
    }
    /**
     * 选择分组查询下面缴过费的水电气户号
     * @param
     * @return
     */
    @ApiOperation("选择分组查询缴过费的户号")
    @GetMapping("/selectGroup")
    @Login
    public CommonResult selectGroup(@RequestParam("groupName") String groupName){
        String userId = UserUtils.getUserId();
        List<GroupVO> voList = livingpaymentQueryService.selectGroup(groupName,userId);
        return CommonResult.ok(voList);
    }
    /**
     * 选择分组查询下面缴过费的水电气户号
     * @param
     * @return
     */
    @ApiOperation("查询缴过费的全部户号")
    @GetMapping("/selectGroupAll")
    @Login
    public CommonResult selectGroupAll(){
        String userId = UserUtils.getUserId();
        PaymentRecordsMapVO voList = livingpaymentQueryService.selectGroupAll(userId);
        return CommonResult.ok(voList);
    }
    /**
     * 查询当前登录人员自定义的分组
     * @param
     * @return
     */
    @ApiOperation("查询当前登录人员自定义的分组")
    @GetMapping("/selectUserGroup")
    @Login
    public CommonResult selectGroup(){
        String userId = UserUtils.getUserId();
        List<UserGroupVO> voList = livingpaymentQueryService.selectUserGroup(userId);
        return CommonResult.ok(voList);
    }

    /**
     * 通过组户号查询订单详情
     * @param
     * @return
     */
    @ApiOperation("查询每月的缴费详情")
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
    @ApiOperation("查询一条账单详情")
    @GetMapping("/selectOrderId")
    @Login
    public CommonResult selectOrderId(@RequestParam("orderId") Long orderId){
        TheBillingDetailsVO theBillingDetailsVO = livingpaymentQueryService.selectOrderId(orderId);
        return CommonResult.ok(theBillingDetailsVO);
    }

    /**
     * 默认查询所有缴费信息
     * @param
     * @return
     */
    @ApiOperation("默认查询所有缴费信息")
    @GetMapping("/selectList")
    @Login
    public CommonResult selectList(){
        String userId = UserUtils.getUserId();
        List<DefaultHouseOwnerVO> list = livingpaymentQueryService.selectList(userId);
        return CommonResult.ok(list);
    }



}
