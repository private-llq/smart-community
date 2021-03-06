package com.jsy.community.controller;

import com.jsy.community.api.UserLivingExpensesOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CebCashierDeskVO;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: DKS
 * @Description: 用户生活缴费订单表服务
 * @Date: 2021/12/29 11:05
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/livingExpensesOrder")
public class UserLivingExpensesOrderController {
    
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private UserLivingExpensesOrderService orderService;
    
    /**
     * @Description: 新增生活缴费订单记录
     * @author: DKS
     * @since: 2021/12/29 11:09
     * @Param: [userLivingExpensesOrderEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @PostMapping("/v2/addOrder")
    @Deprecated
//    @Permit("community:proprietor:livingExpensesOrder:v2:addOrder")
    public CommonResult addUserLivingExpensesOrder(@RequestBody UserLivingExpensesBillEntity billEntity) {
        ValidatorUtils.validateEntity(UserLivingExpensesBillEntity.AddOrderValidateGroup.class);
        billEntity.setUid(UserUtils.getUserId());
        CebCashierDeskVO cebCashierDeskVO = orderService.addUserLivingExpensesOrder(billEntity, UserUtils.getUserInfo().getMobile());
        return CommonResult.ok(cebCashierDeskVO, "下单成功");
    }
    
    /**
     * @Description: 查询当前用户生活缴费记录列表
     * @author: DKS
     * @since: 2021/12/29 11:22
     * @Param: [userLivingExpensesOrderEntity]
     * @return: com.jsy.community.vo.CommonResult<?>
     */
    @PostMapping("/v2/orderList")
//    @Permit("community:proprietor:livingExpensesOrder:v2:orderList")
    public CommonResult<?> orderList(@RequestBody UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
        userLivingExpensesOrderEntity.setUid(UserUtils.getUserId());
        return CommonResult.ok(orderService.getListOfUserLivingExpensesOrder(userLivingExpensesOrderEntity));
    }
    
    /**
     * @Description: 查询生活缴费记录详情
     * @author: DKS
     * @since: 2021/12/29 14:08
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult<?>
     */
    @GetMapping("/v2/orderDetail")
//    @Permit("community:proprietor:livingExpensesOrder:v2:orderDetail")
    public CommonResult<?> orderDetail(Long id) {
        return CommonResult.ok(orderService.getById(id));
    }
}
