package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.CebBankService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResponseModel;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费控制器
 * @Date: 2021/11/27 14:30
 * @Version: 1.0
 **/
@RestController
// @ApiJSYController
@RequestMapping("/cebBank")
@Slf4j
public class CebBankController {
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private CebBankService cebBankService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * @param categoryQO:
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/11/17 17:47
     **/
    @LoginIgnore
    @PostMapping("/v2/cityContributionCategory")
    public CommonResult queryCityContributionCategory(@RequestBody CebQueryCityContributionCategoryQO categoryQO) {
        ValidatorUtils.validateEntity(categoryQO);
        categoryQO.setSessionId(getCebBankSessionId());
        return CommonResult.ok(cebBankService.queryCityContributionCategory(categoryQO));
    }

    /**
     * @param projectQO:
     * @author: Pipi
     * @description: 查询缴费项目
     * @return: {@link CommonResult}
     * @date: 2021/11/23 15:33
     **/
    @LoginIgnore
    @PostMapping("/v2/queryContributionProject")
    public CommonResult queryContributionProject(@RequestBody CebQueryContributionProjectQO projectQO) {
        ValidatorUtils.validateEntity(projectQO);
        String sessionId = getCebBankSessionId();
        projectQO.setSessionId(sessionId);
        return CommonResult.ok(cebBankService.queryContributionProject(projectQO));
    }

    /**
     * @param billInfoQO:
     * @author: Pipi
     * @description: 查询缴费账单信息(作用同查询手机充值缴费账单, 但是适用于生活缴费, 不适用于手机话费)
     * @return: {@link CommonResult}
     * @date: 2021/11/23 17:14
     **/
    @LoginIgnore
    @PostMapping("/v2/queryBillInfo")
    public CommonResult queryBillInfo(@RequestBody CebQueryBillInfoQO billInfoQO) {
        ValidatorUtils.validateEntity(billInfoQO);
        if (billInfoQO.getBusinessFlow() == 1) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "直接缴费业务,请查询直缴接口");
        }
        String sessionId = getCebBankSessionId();
        billInfoQO.setSessionId(sessionId);
        return CommonResult.ok(cebBankService.queryBillInfo(billInfoQO));
    }

    /**
     * @param :
     * @author: Pipi
     * @description: 获取cebBankSession
     * @return: {@link String}
     * @date: 2021/11/23 14:59
     **/
    @LoginIgnore
    private String getCebBankSessionId() {
        String sessionId = redisTemplate.opsForValue().get("cebBank-sessionId:" + "18996226451");
        if (sessionId == null) {
            CebLoginQO cebLoginQO = new CebLoginQO();
            cebLoginQO.setUserPhone("18996226451");
            cebLoginQO.setDeviceType("1");
            sessionId = cebBankService.login(cebLoginQO);
        }
        return sessionId;
    }
}
