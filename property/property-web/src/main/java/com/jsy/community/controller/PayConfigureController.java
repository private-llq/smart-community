package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPayConfigureService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: com.jsy.community
 * @description: 支付宝支付配置
 * @author: DKS
 * @create: 2021-09-09 09:51
 **/
@Api(tags = "支付宝支付配置")
@RestController
@RequestMapping("/alipay/configure")
@ApiJSYController
public class PayConfigureController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPayConfigureService payConfigureService;
    
    @ApiOperation("上传应用公钥证书路径")
    @PostMapping("/upload/cert/path")
    @Permit("community:property:alipay:configure:upload:cert:path")
    public CommonResult uploadCertPath(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "alipay-cert-path");
        if (StringUtils.isBlank(upload)) {
            return CommonResult.ok("上传失败");
        }
        return  CommonResult.ok(upload,"上传成功");
    }
    
    @ApiOperation("上传支付宝公钥证书路径")
    @PostMapping("/upload/public")
    @Permit("community:property:alipay:configure:upload:public")
    public CommonResult uploadAlipayPublicCertPath(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "alipay-public-cert-path");
        if (StringUtils.isBlank(upload)) {
            return CommonResult.ok("上传失败");
        }
        return  CommonResult.ok(upload,"上传成功");
    }
    
    @ApiOperation("上传支付宝根证书路径")
    @PostMapping("/upload/root")
    @Permit("community:property:alipay:configure:upload:root")
    public CommonResult uploadRootCertPath(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "alipay-root-cert-path");
        if (StringUtils.isBlank(upload)) {
            return CommonResult.ok("上传失败");
        }
        return  CommonResult.ok(upload,"上传成功");
    }
    
    @Login
    @ApiOperation("更新配置")
    @PutMapping("/basic/config")
    @Permit("community:property:alipay:configure:basic:config")
    public CommonResult basicConfig(@RequestBody PayConfigureEntity payConfigureEntity) {
        UserUtils.getAdminCompanyId();
        payConfigureService.basicConfig(payConfigureEntity, UserUtils.getAdminUserInfo().getCompanyId());
        return CommonResult.ok("添加成功!");
    }
    
    @ApiOperation("查询证书上传状态")
    @GetMapping("getConfig")
    @Permit("community:property:alipay:configure:getConfig")
    public CommonResult getConfig() {
        return CommonResult.ok(payConfigureService.getConfig(UserUtils.getAdminUserInfo().getCompanyId()),"查询成功!");
    }
}
