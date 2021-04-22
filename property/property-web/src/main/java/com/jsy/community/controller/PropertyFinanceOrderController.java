package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @ApiOperation("查询房屋所有未缴账单")
    @GetMapping("/houseCost")
    @Login
    public CommonResult houseCost(@RequestParam("houseId") Long houseId){
        AdminInfoVo userInfo = UserUtils.getAdminUserInfo();
        Map<String, Object> map=propertyFinanceOrderService.houseCost(userInfo,houseId);
        return CommonResult.ok(map);
    }


}
