package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICompanyPayConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 物业公司支付配置
 * @author: Hu
 * @create: 2021-09-10 14:23
 **/
@RestController
// @ApiJSYController
@RequestMapping("/company/config")
public class CompanyPayConfigController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;

    @LoginIgnore
    @ApiOperation("上传私钥")
    @PostMapping("/key")
    @Permit("community:property:company:config:key")
    public CommonResult uploadApiclientKey(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "apiclient-key");
        return  CommonResult.ok(upload,"上传成功");
    }
    @ApiOperation("上传公钥")
    @PostMapping("/cert")
    @Permit("community:property:company:config:cert")
    public CommonResult uploadApiclientCert(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "apiclient-cert");
        return  CommonResult.ok(upload,"上传成功");
    }

    @ApiOperation("上传apiclient_cert.p12")
    @PostMapping("/certP12")
    @Permit("community:property:company:config:certP12")
    public CommonResult uploadApiclientCertP12(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "apiclient-cert-p12");
        return  CommonResult.ok(upload,"上传成功");
    }
    
    @ApiOperation("更新基本配置")
    @PutMapping("basicConfig")
    @Permit("community:property:company:config:basicConfig")
    public CommonResult basicConfig(@RequestBody CompanyPayConfigEntity communityHardWareEntity) {
        companyPayConfigService.basicConfig(communityHardWareEntity, UserUtils.getAdminUserInfo().getCompanyId());
        return CommonResult.ok();
    }
    
    @ApiOperation("查询状态")
    @GetMapping("getConfig")
    @Permit("community:property:company:config:getConfig")
    public CommonResult getConfig() {
        CompanyPayConfigEntity entity = companyPayConfigService.getConfig(UserUtils.getAdminUserInfo().getCompanyId());
        return CommonResult.ok(entity);
    }
    
    @ApiOperation("查询退款配置状态")
    @GetMapping("getRefundConfigStatus")
    @Permit("community:property:company:config:getRefundConfigStatus")
    public CommonResult getRefundConfig() {
        Map map = companyPayConfigService.getRefundConfig(UserUtils.getAdminUserInfo().getCompanyId());
        return CommonResult.ok(map);
    }
    
    @ApiOperation("查询支付配置状态")
    @GetMapping("getBasicConfigStatus")
    @Permit("community:property:company:config:getBasicConfigStatus")
    public CommonResult getBasicConfig() {
        Map map = companyPayConfigService.getBasicConfig(UserUtils.getAdminUserInfo().getCompanyId());
        return CommonResult.ok(map);
    }
}
