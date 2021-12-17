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

    /**
     * @author: Pipi
     * @description: 修改户号
     * @param accountEntity:
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/10 18:41
     **/
    @PostMapping("/v2/modifyAccount")
    public CommonResult<?> modifyAccount(@RequestBody UserLivingExpensesAccountEntity accountEntity) {
        if (accountEntity.getId() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "账号ID不能为空");
        }
        accountEntity.setUid(UserUtils.getUserId());
        UserLivingExpensesAccountEntity originalAccountEntity = accountService.queryAccountById(accountEntity);
        if (originalAccountEntity == null) {
            throw new JSYException(JSYError.DATA_LOST);
        }
        if (!accountEntity.getAccount().equals(originalAccountEntity.getAccount()) || !accountEntity.getItemCode().equals(originalAccountEntity.getItemCode())) {
            // 如果户号或者项目有改变,需要重新查询账单信息,同时删除原有未缴费账单
        }
        return CommonResult.ok();
    }
}