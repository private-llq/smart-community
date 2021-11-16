package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.CebBankService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.cebbank.CebLoginQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费控制器
 * @Date: 2021/11/15 17:26
 * @Version: 1.0
 **/
@RestController
@ApiJSYController
@RequestMapping("/cebBank")
@Login
public class CebBankController {
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private CebBankService cebBankService;

    /**
     * @author: Pipi
     * @description: 光大银行用户注册
     * @param cebLoginQO:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/11/15 17:29
     **/
    @PostMapping("/v2/cebBankLogin")
    public CommonResult cebBankLogin(@RequestBody CebLoginQO cebLoginQO) {
        ValidatorUtils.validateEntity(cebLoginQO);
        cebBankService.login(cebLoginQO);
        return CommonResult.ok();
    }
}
