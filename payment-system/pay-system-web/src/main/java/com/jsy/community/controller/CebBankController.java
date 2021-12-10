package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.CebBankService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResponseModel;
import com.jsy.community.untils.cebbank.CebBankContributionUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import jodd.util.StringUtil;
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
    @PostMapping("/v2/cityContributionCategory")
    public CommonResult<?> queryCityContributionCategory(@RequestBody CebQueryCityContributionCategoryQO categoryQO) {
        ValidatorUtils.validateEntity(categoryQO);
        categoryQO.setSessionId(cebBankService.getCebBankSessionId(UserUtils.getUserInfo().getMobile(), categoryQO.getDeviceType()));
        return CommonResult.ok(cebBankService.queryCityContributionCategory(categoryQO));
    }

    /**
     * @param projectQO:
     * @author: Pipi
     * @description: 查询缴费项目
     * @return: {@link CommonResult}
     * @date: 2021/11/23 15:33
     **/
    @PostMapping("/v2/queryContributionProject")
    public CommonResult<?> queryContributionProject(@RequestBody CebQueryContributionProjectQO projectQO) {
        ValidatorUtils.validateEntity(projectQO);
        projectQO.setSessionId(cebBankService.getCebBankSessionId(UserUtils.getUserInfo().getMobile(), projectQO.getDeviceType()));
        return CommonResult.ok(cebBankService.queryContributionProject(projectQO));
    }

    /**
     * @author: Pipi
     * @description: 查询城市
     * @param deviceType: 1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/10 14:05
     **/
    @GetMapping("/v2/queryCity")
    public CommonResult<?> queryCity(@RequestParam("deviceType") String deviceType) {
        if (StringUtil.isBlank(deviceType)) {
            throw new JSYException(JSYError.REQUEST_PARAM);
        }
        CebQueryCityQO cebQueryCityQO = new CebQueryCityQO();
        cebQueryCityQO.setSessionId(cebBankService.getCebBankSessionId(UserUtils.getUserInfo().getMobile(), deviceType));
        cebQueryCityQO.setCityRange("SOME");
        cebQueryCityQO.setDeviceType(deviceType);
        return CommonResult.ok(cebBankService.queryCity(cebQueryCityQO));
    }
}
