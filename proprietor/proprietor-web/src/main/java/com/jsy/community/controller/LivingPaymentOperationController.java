package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ILivingPaymentOperationService;
import com.jsy.community.api.IPayGroupService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.livingpayment.LivingPaymentQO;
import com.jsy.community.qo.livingpayment.RemarkQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * @program: com.jsy.community
 * @description: 生活缴费增删改控制器
 * @author: Hu
 * @create: 2020-12-11 09:28
 **/
@Api(tags = "生活缴费前端控制器--增删改")
@RestController
@RequestMapping("/livingpaymentoperation")
@ApiJSYController
public class LivingPaymentOperationController {

    private final String[] img ={"jpg","png","jpeg"};

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ILivingPaymentOperationService livingPaymentOperationService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IPayGroupService payGroupService;

    @ApiOperation("新增分组")
    @PostMapping("/saveGroupName")
    @Login
    public CommonResult saveGroup(@ApiParam("组名") @RequestParam String name){
        String userId = UserUtils.getUserId();
        payGroupService.insertGroup(name,userId);
        return CommonResult.ok();
    }
    @ApiOperation("修改组名")
    @PutMapping("/updateGroupName")
    @Login
    public CommonResult updateGroup(@ApiParam("id") @RequestParam Long id,@ApiParam("name") String name){
        String userId = UserUtils.getUserId();
        payGroupService.updateGroup(id,name,userId);
        return CommonResult.ok();
    }

    @ApiOperation("删除分组")
    @DeleteMapping("/deleteGroupName")
    @Login
    public CommonResult deleteGroupName(@ApiParam("组名") @RequestParam String name){
        String userId = UserUtils.getUserId();
        payGroupService.delete(name,userId);
        return CommonResult.ok();
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
        ValidatorUtils.validateEntity(livingPaymentQO, LivingPaymentQO.LivingPaymentValidated.class);
//            if (new BigDecimal(livingPaymentQO.getAccountBalance().abs().intValue()).compareTo(livingPaymentQO.getAccountBalance().abs())==0){
//                //整数
//                if (livingPaymentQO.getAccountBalance().abs().compareTo(livingPaymentQO.getPaymentBalance()) != 0) {
//                    return CommonResult.error("缴费金额不足");
//                }
//            }else {
//                //小数
//                if (livingPaymentQO.getAccountBalance().abs().setScale(0, RoundingMode.DOWN).compareTo(livingPaymentQO.getPaymentBalance().subtract(new BigDecimal(1)))!=0) {
//                    return CommonResult.error("缴费金额不足");
//                }
//            }
        String userId = UserUtils.getUserId();
        livingPaymentQO.setUserID(userId);
        livingPaymentOperationService.add(livingPaymentQO);
        return CommonResult.ok();
    }

    /**
     * 添加订单备注
     * @param
     * @return
     */
    @ApiOperation("添加备注")
    @PostMapping("/addRemark")
    @Login
    public CommonResult addRemark(@RequestBody RemarkQO remarkQO){
        String userId = UserUtils.getUserId();
        remarkQO.setUid(userId);
        livingPaymentOperationService.addRemark(remarkQO);
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
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "bbbb");
        return CommonResult.ok(upload);
    }

}
