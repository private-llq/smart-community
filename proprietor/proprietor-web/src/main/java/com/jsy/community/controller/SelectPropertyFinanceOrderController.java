package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ISelectPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-07-05 10:53
 **/
@Api(tags = "物业账单")
@RestController
@RequestMapping("/FinanceOrder")
// @ApiJSYController
@Login
public class SelectPropertyFinanceOrderController {
    
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ISelectPropertyFinanceOrderService selectPropertyFinanceOrderService;

    @ApiOperation("查询物业账单")
    @GetMapping("/list")
    public CommonResult getFinanceOrder(@RequestParam Long communityId,Integer orderStatus){
        PropertyFinanceOrderEntity qo = new PropertyFinanceOrderEntity();
        qo.setUid(UserUtils.getUserId());
        qo.setCommunityId(communityId);
        qo.setOrderStatus(orderStatus);
        return CommonResult.ok(selectPropertyFinanceOrderService.listV2(qo),"查询成功");
    }
    @ApiOperation("查询物业账单（详情）")
    @GetMapping("/findOne")
    public CommonResult findOne(@RequestParam("id") String id,Integer orderStatus){
        return CommonResult.ok(selectPropertyFinanceOrderService.findOne(id,orderStatus),"查询成功");
    }

}
