package com.jsy.community.controller;

import com.jsy.community.api.UserLivingExpensesOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesOrderEntity;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
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
    @LoginIgnore
//    @Permit("community:proprietor:livingExpensesOrder:v2:addOrder")
    public CommonResult addUserLivingExpensesOrder(@RequestBody UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
//        userLivingExpensesOrderEntity.setUid(UserUtils.getUserId());
        userLivingExpensesOrderEntity.setUid("123");
        String id = orderService.addUserLivingExpensesOrder(userLivingExpensesOrderEntity);
        return id == null ? CommonResult.error("添加失败") : CommonResult.ok(id, "添加成功");
    }
    
    /**
     * @Description: 查询当前用户生活缴费记录列表
     * @author: DKS
     * @since: 2021/12/29 11:22
     * @Param: [userLivingExpensesOrderEntity]
     * @return: com.jsy.community.vo.CommonResult<?>
     */
    @PostMapping("/v2/orderList")
    @LoginIgnore
//    @Permit("community:proprietor:livingExpensesOrder:v2:orderList")
    public CommonResult<?> orderList(@RequestBody UserLivingExpensesOrderEntity userLivingExpensesOrderEntity) {
//        userLivingExpensesOrderEntity.setUid(UserUtils.getUserId());
        userLivingExpensesOrderEntity.setUid("123");
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
    @LoginIgnore
//    @Permit("community:proprietor:livingExpensesOrder:v2:orderDetail")
    public CommonResult<?> orderDetail(Long id) {
        return CommonResult.ok(orderService.getById(id));
    }
}
