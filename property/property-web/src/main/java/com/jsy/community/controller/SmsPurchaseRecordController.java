package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ISmsPurchaseRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.SmsPurchaseRecordEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 短信购买记录
 * @author: DKS
 * @create: 2021-09-14 15:01
 **/
@Api(tags = "短信发送记录")
@RestController
@RequestMapping("/sms/purchase")
@ApiJSYController
@Login
public class SmsPurchaseRecordController {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ISmsPurchaseRecordService smsPurchaseRecordService;
    
    /**
     * @Description: 查询短信购买记录
     * @Param:
     * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.SmsPurchaseRecordEntity>>
     * @Author: DKS
     * @Date: 2021/09/14
     **/
    @Login
    @ApiOperation("查询短信发送记录")
    @PostMapping("/query")
    public CommonResult<List<SmsPurchaseRecordEntity>> queryPropertyDeposit() {
        Long companyId = UserUtils.getAdminCompanyId();
        return CommonResult.ok(smsPurchaseRecordService.querySmsPurchaseRecord(companyId));
    }
}
