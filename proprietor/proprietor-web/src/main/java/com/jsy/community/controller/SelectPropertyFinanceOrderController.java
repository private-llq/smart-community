package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ISelectPropertyFinanceOrderService;
import com.jsy.community.constant.Const;
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
@Api(tags = "社区趣事控制器")
@RestController
@RequestMapping("/FinanceOrder")
@ApiJSYController
@Login
public class SelectPropertyFinanceOrderController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ISelectPropertyFinanceOrderService selectPropertyFinanceOrderService;


    @ApiOperation("查询物业账单")
    @GetMapping("/list")
    public CommonResult getFinanceOrder(@RequestParam Long communityId){
        String userId = UserUtils.getUserId();
        return CommonResult.ok(selectPropertyFinanceOrderService.list(userId,communityId),"查询成功");
    }


}
