package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.CebBankService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @param categoryQO:
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
     * @author: Pipi
     * @description: 查询缴费项目
     * @param projectQO:
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
     * @author: Pipi
     * @description: 获取cebBankSession
     * @param :
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
