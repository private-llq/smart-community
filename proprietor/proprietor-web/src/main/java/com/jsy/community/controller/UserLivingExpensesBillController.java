package com.jsy.community.controller;

import com.jsy.community.api.UserLivingExpensesBillService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 生活缴费账单控制器
 * @Date: 2021/12/28 18:30
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/livingExpensesBill")
public class UserLivingExpensesBillController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private UserLivingExpensesBillService billService;

    /**
     * @author: Pipi
     * @description: 查询账单详情
     * @param billEntity:
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/28 18:55
     **/
    @PostMapping("/v2/queryBillInfo")
    public CommonResult<?> queryBillInfo(@RequestBody UserLivingExpensesBillEntity billEntity) {
        if (billEntity.getId() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM);
        }
        billEntity.setUid(UserUtils.getUserId());
        UserLivingExpensesBillEntity entity = billService.queryBill(billEntity);
        return entity != null ? CommonResult.ok(entity) : CommonResult.error("没有待缴费的账单");
    }

    /**
     * @author: Pipi
     * @description: 查询账单列表
     * @param billEntity:
     * @return: {@link CommonResult<?>}
     * @date: 2022/1/8 17:57
     **/
    @PostMapping("/v2/queryBillList")
    public CommonResult<?> queryBillList(@RequestBody UserLivingExpensesBillEntity billEntity) {
        ValidatorUtils.validateEntity(billEntity, UserLivingExpensesBillEntity.QueryBillValidateGroup.class);
        billEntity.setUid(UserUtils.getUserId());
        return CommonResult.ok(billService.queryBillList(billEntity));
    }
}
