package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.service.IWeChatConfigService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 微信支付配置
 * @author: DKS
 * @create: 2021-11-10 14:02
 **/
@RestController
@ApiJSYController
@RequestMapping("/weChat/config")
public class WeChatConfigController {

    @Resource
    private IWeChatConfigService weChatConfigService;
    
    /**
     * @Description: 上传私钥
     * @author: DKS
     * @since: 2021/11/10 14:38
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传私钥")
    @PostMapping("/key")
//    @Login
    public CommonResult uploadApiclientKey(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "sys-apiclient-key");
        return  CommonResult.ok(upload,"上传成功");
    }
    
    /**
     * @Description: 上传公钥
     * @author: DKS
     * @since: 2021/11/10 14:37
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传公钥")
    @PostMapping("/cert")
//    @Login
    public CommonResult uploadApiclientCert(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "sys-apiclient-cert");
        return  CommonResult.ok(upload,"上传成功");
    }
    
    /**
     * @Description: 上传apiClient_cert.p12
     * @author: DKS
     * @since: 2021/11/10 14:37
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传apiClient_cert.p12")
    @PostMapping("/certP12")
//    @Login
    public CommonResult uploadApiclientCertP12(@RequestParam("file") MultipartFile file) {
        String upload = MinioUtils.upload(file, "sys-apiclient-cert-p12");
        return  CommonResult.ok(upload,"上传成功");
    }
    
    /**
     * @Description: 更新基本配置
     * @author: DKS
     * @since: 2021/11/10 14:24
     * @Param: [communityHardWareEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @Login
    @ApiOperation("更新基本配置")
    @PutMapping("/basicConfig")
    public CommonResult basicConfig(@RequestBody CompanyPayConfigEntity communityHardWareEntity) {
        return CommonResult.ok(weChatConfigService.basicConfig(communityHardWareEntity) ? "更新成功" : "更新失败");
    }
    
    /**
     * @Description: 查询支付公私钥状态
     * @author: DKS
     * @since: 2021/11/10 14:28
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @Login
    @ApiOperation("查询状态")
    @GetMapping("getConfig")
    public CommonResult getConfig() {
        CompanyPayConfigEntity entity = weChatConfigService.getConfig();
        return CommonResult.ok(entity);
    }
    
    /**
     * @Description: 查询退款配置状态
     * @author: DKS
     * @since: 2021/11/10 14:29
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @Login
    @ApiOperation("查询退款配置状态")
    @GetMapping("getRefundConfigStatus")
    public CommonResult getRefundConfig() {
        Map map = weChatConfigService.getRefundConfig();
        return CommonResult.ok(map);
    }
    
    /**
     * @Description: 查询支付配置状态
     * @author: DKS
     * @since: 2021/11/10 14:30
     * @Param: []
     * @return: com.jsy.community.vo.CommonResult
     */
    @Login
    @ApiOperation("查询支付配置状态")
    @GetMapping("getBasicConfigStatus")
    public CommonResult getBasicConfig() {
        Map map = weChatConfigService.getBasicConfig();
        return CommonResult.ok(map);
    }
}
