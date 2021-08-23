package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IProprietorMarketLabelService;
import com.jsy.community.api.IProprietorMarketService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "社区集市")
@RestController
@RequestMapping("/proprietorMarket")
@ApiJSYController
public class ProprietorMarketLabelController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IProprietorMarketLabelService labelService;


}
