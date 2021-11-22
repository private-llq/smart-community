package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.CebBankService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.cebbank.CebQueryCityContributionCategoryQO;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CebBankController {
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private CebBankService cebBankService;

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @param categoryQO:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/11/17 17:47
     **/
    @PostMapping("/v2/cityContributionCategory")
    @Permit("community:payment:AlipayPhoneH5:v2:cityContributionCategory")
    public CommonResult queryCityContributionCategory(@RequestBody CebQueryCityContributionCategoryQO categoryQO) {
        cebBankService.queryCityContributionCategory(categoryQO);
        return CommonResult.ok();
    }

}
