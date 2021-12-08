package com.jsy.community.controller;

import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserLivingExpensesAccountService;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstError;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
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
 * @Description:
 * @Date: 2021/12/3 16:49
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/livingExpensesAccount")
public class UserLivingExpensesAccountController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private UserLivingExpensesAccountService accountService;

    /**
     * @author: Pipi
     * @description: 添加户号
     * @param accountEntity:
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/3 16:54
     **/
    @PostMapping("/v2/addAccount")
    public CommonResult<?> addAccount(@RequestBody UserLivingExpensesAccountEntity accountEntity) {
        ValidatorUtils.validateEntity(accountEntity);
        if (accountEntity.getBusinessFlow() == 1) {
            throw new JSYException(ConstError.BAD_REQUEST, "直缴业务不用绑定,请走直接缴费接口");
        }
        accountEntity.setUid(UserUtils.getUserId());
        accountEntity.setMobile(UserUtils.getUserInfo().getMobile());
        UserLivingExpensesAccountEntity userLivingExpensesAccountEntity = accountService.queryAccount(accountEntity);
        if (userLivingExpensesAccountEntity != null) {
            throw new JSYException(JSYError.DUPLICATE_KEY);
        }
        return accountService.addAccount(accountEntity) == 1 ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
    }
}
