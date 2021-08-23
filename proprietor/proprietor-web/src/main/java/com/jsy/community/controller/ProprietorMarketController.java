package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IProprietorMarketService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.ProprietorMarketQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "社区集市")
@RestController
@RequestMapping("/proprietorMarket")
@ApiJSYController
public class ProprietorMarketController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IProprietorMarketService marketService;

    @PostMapping("/addMarket")
    @ApiOperation("假账单接口")
    @Login
    public CommonResult addMarket(@RequestBody ProprietorMarketQO marketQO){

        return CommonResult.ok("发布成功");
    }

}
