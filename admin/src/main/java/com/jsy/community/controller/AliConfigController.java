package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.service.IAliConfigService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @program: com.jsy.community
 * @description: 支付宝支付配置
 * @author: DKS
 * @create: 2021-11-10 14:06
 **/
@Api(tags = "支付宝支付配置")
@RestController
@RequestMapping("/ali/config")
// @ApiJSYController
public class AliConfigController {
    
    @Resource
    private IAliConfigService aliConfigService;
    
    /**
     * @Description: 上传应用公钥证书路径
     * @author: DKS
     * @since: 2021/11/10 14:40
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传应用公钥证书路径")
    @PostMapping("/upload/cert/path")
    public CommonResult uploadCertPath(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "sys-alipay-cert-path");
        if (StringUtils.isBlank(upload)) {
            return CommonResult.ok("上传失败");
        }
        return  CommonResult.ok(upload,"上传成功");
    }
    
    /**
     * @Description: 上传支付宝公钥证书路径
     * @author: DKS
     * @since: 2021/11/10 14:41
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传支付宝公钥证书路径")
    @PostMapping("/upload/public")
    public CommonResult uploadAlipayPublicCertPath(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "sys-alipay-public-cert-path");
        if (StringUtils.isBlank(upload)) {
            return CommonResult.ok("上传失败");
        }
        return  CommonResult.ok(upload,"上传成功");
    }
    
    /**
     * @Description: 上传支付宝根证书路径
     * @author: DKS
     * @since: 2021/11/10 14:41
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传支付宝根证书路径")
    @PostMapping("/upload/root")
    public CommonResult uploadRootCertPath(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "sys-alipay-root-cert-path");
        if (StringUtils.isBlank(upload)) {
            return CommonResult.ok("上传失败");
        }
        return  CommonResult.ok(upload,"上传成功");
    }
    
    /**
     * @Description: 更新配置
     * @author: DKS
     * @since: 2021/11/10 14:46
     * @Param: [payConfigureEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @Login
    @ApiOperation("更新配置")
    @PutMapping("/basic/config")
    public CommonResult basicConfig(@RequestBody PayConfigureEntity payConfigureEntity) {
        return CommonResult.ok(aliConfigService.basicConfig(payConfigureEntity) ? "更新成功" : "更新失败");
    }
    
    /**
     * @Description: 查询证书上传状态
     * @author: DKS
     * @since: 2021/11/10 14:54
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("查询证书上传状态")
    @GetMapping("/getConfig")
    public CommonResult getConfig() {
        return CommonResult.ok(aliConfigService.getConfig(),"查询成功!");
    }
}
