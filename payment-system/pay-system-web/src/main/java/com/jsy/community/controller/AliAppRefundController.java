package com.jsy.community.controller;

import com.jsy.community.api.IAliAppRefundService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 支付宝退款
 * @author: Hu
 * @create: 2021-12-15 16:00
 **/
@RestController
// @ApiJSYController
@Slf4j
@RequestMapping("alipay")
public class AliAppRefundController {

    @DubboReference(version = Const.version, group = Const.group_payment,check = false)
    private IAliAppRefundService aliAppRefundService;


    @PostMapping("refund")
    public CommonResult refund(@RequestBody Map<String, String> orderNo){
        AiliAppPayRecordEntity ailiAppPayRecordEntity =  aliAppRefundService.selectByOrder(orderNo.get("orderNo"));
        if (ailiAppPayRecordEntity != null) {
            aliAppRefundService.refund(ailiAppPayRecordEntity);
            return CommonResult.ok();
        } else {
            return CommonResult.error("当前订单不存在，请检查订单是否正确！");
        }
    }

    @GetMapping("getOrder")
    public CommonResult getOrder(@RequestParam String orderNo){
        return CommonResult.ok(aliAppRefundService.selectByOrder(orderNo));
    }
}
