package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description: 物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:35
 **/
@Api(tags = "物业房间账单")
@RestController
@RequestMapping("/financeOrder")
@ApiJSYController
public class PropertyFinanceOrderController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyFinanceOrderService propertyFinanceOrderService;

    @ApiOperation("")
    @PostMapping("/update")
    public CommonResult update(@RequestParam("communityId") String communityId){
       propertyFinanceOrderService.updateDays();
        return CommonResult.ok();
    }
}
