package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ILivingPaymentService;
import com.jsy.community.api.IPayTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayCompanyEntity;
import com.jsy.community.entity.PayTypeEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.GroupQO;
import com.jsy.community.qo.proprietor.LivingPaymentQO;
import com.jsy.community.qo.proprietor.PaymentRecordsQO;
import com.jsy.community.qo.proprietor.RemarkQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayTypeService payTypeService;
    
    @ApiOperation("根据城市id查询其支持的缴费类型")
    @GetMapping("/getPayType")
    public CommonResult getPayType(@ApiParam("城市id") @RequestParam Long id){
        List<PayTypeEntity> payType = payTypeService.getPayTypes(id);
        return CommonResult.ok(payType);
    }
    
	@ApiOperation("查询支持的缴费单位")
	@PostMapping("/getPayCompany")
	public CommonResult<PageInfo<PayCompanyEntity>> getPayCompany(@RequestBody BaseQO<PayCompanyEntity> baseQO,
                                                @ApiParam("缴费类型id") @RequestParam Long type,
                                                @ApiParam("城市id") @RequestParam Long cityId){
        PageInfo<PayCompanyEntity> pageInfo = payTypeService.getPayCompany(baseQO,type,cityId);
        return CommonResult.ok(pageInfo);
	}


	@ApiOperation("通过订单id查询缴费凭证")
	@PostMapping("/getOrderID")
	public CommonResult getOrderID(@ApiParam("缴费类型id") @RequestParam Long id){
        PayVoucherVO payVoucherVO=livingPaymentService.getOrderID(id);
        return CommonResult.ok(payVoucherVO);
	}
	

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
        PaymentDetailsVO paymentDetailsVO=livingPaymentService.add(livingPaymentQO);
        return CommonResult.ok(paymentDetailsVO);
    }

    /**
     * 选择分组查询下面缴过费的水电气户号
     * @param groupQO
     * @return
     */
    @ApiOperation("选择分组查询缴过费的户号")
    @PostMapping("/selectGroup")
    @Login
    public CommonResult selectGroup(@RequestBody GroupQO groupQO){
        String userId = UserUtils.getUserId();
        groupQO.setUserID(userId);
        List<GroupVO> voList = livingPaymentService.selectGroup(groupQO);
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
        List<GroupVO> voList = livingPaymentService.selectGroupAll(userId);
        return CommonResult.ok(voList);
    }
    /**
     * 查询当前登录人员自定义的分组
     * @param
     * @return
     */
    @ApiOperation("查询当前登录人员自定义的分组")
    @PostMapping("/selectUserGroup")
    @Login
    public CommonResult selectGroup(){
        String userId = UserUtils.getUserId();
        List<UserGroupVO> voList = livingPaymentService.selectUserGroup(userId);
        return CommonResult.ok(voList);
    }

    /**
     * 通过组户号查询订单详情
     * @param baseQO
     * @return
     */
    @ApiOperation("查询每月的缴费详情")
    @PostMapping("/selectOrder")
    @Login
    public CommonResult selectOrder(@RequestBody BaseQO<PaymentRecordsQO> baseQO){
        String userId = UserUtils.getUserId();
        System.out.println(userId);
        baseQO.getQuery().setUserID(userId);
        Map<String, List<PaymentRecordsVO>> map = livingPaymentService.selectOrder(baseQO);
        return CommonResult.ok(map);
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
        List list = livingPaymentService.selectList(userId);
        return CommonResult.ok(list);
    }


    /**
     * 添加订单备注
     * @param
     * @return
     */
    @ApiOperation("添加订单备注")
    @PostMapping("/addRemark")
    @Login
    public CommonResult addRemark(@RequestBody RemarkQO remarkQO){
        String userId = UserUtils.getUserId();
        remarkQO.setUid(userId);
        livingPaymentService.addRemark(remarkQO);
        return CommonResult.ok();
    }
    /**
     * 添加备注图片
     * @param
     * @return
     */
    @ApiOperation("添加备注图片")
    @PostMapping("/addRemarkImg")
    @Login
    public CommonResult addRemarkImg(@RequestParam("file") MultipartFile file){
        String upload = MinioUtils.upload(file, "bbbb");
        return CommonResult.ok(upload);
    }


}
